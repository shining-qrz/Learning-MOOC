package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.MultipartFileCompleteDTO;
import edu.wust.qrz.dto.media.QueryMediaParamsDTO;
import edu.wust.qrz.dto.media.UploadFileDTO;
import edu.wust.qrz.dto.media.UploadInitDTO;
import edu.wust.qrz.entity.media.EtagObject;
import edu.wust.qrz.entity.media.MediaFiles;
import edu.wust.qrz.exception.BadRequestException;
import edu.wust.qrz.exception.DatabaseOperateException;
import edu.wust.qrz.exception.MinioOperateException;
import edu.wust.qrz.mapper.MediaFilesMapper;
import edu.wust.qrz.service.MediaFilesService;
import edu.wust.qrz.vo.media.UploadInitVO;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static edu.wust.qrz.constant.MediaConstant.*;

@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {

    @Resource
    private MinioClient minioClient;

    @Lazy
    @Resource
    private MediaFilesService proxy;

    @Resource
    private S3Client s3Client;

    @Resource
    private S3Presigner s3Presigner;

    @Override
    public Result uploadCourseFile(Long companyId, MultipartFile file, UploadFileDTO uploadFileDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        System.out.println("uploadCourseFile事务是否激活: " + TransactionSynchronizationManager.isActualTransactionActive());
//        long txStart = System.currentTimeMillis();
        if(file == null || file.isEmpty()) {
            throw new BadRequestException("上传的文件不能为空");
        }

        //md5文件签名
        String fileId = DigestUtils.md5DigestAsHex(file.getInputStream());
        if(fileId.isEmpty()) {
            throw new BadRequestException("文件签名生成失败");
        }
        String filePath = getFilePath(file,fileId);
        String url = MINIO_FILE_BUCKET + "/" + filePath;


        uploadFileToMinIO(file, filePath);

        //构建实体对象
        MediaFiles mediaFile = getMediaFile(companyId, file, uploadFileDTO, fileId, filePath, url);

        //代理对象调用，优化事务处理
        proxy.saveFileToDB(mediaFile);

//        long txEnd = System.currentTimeMillis();
//        System.out.println("接口耗时(ms): " + (txEnd - txStart));

        return Result.ok("上传成功");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFileToDB(MediaFiles mediaFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        System.out.println("事务是否激活: " + TransactionSynchronizationManager.isActualTransactionActive());
//        long txStart = System.currentTimeMillis();
        long count = count(new QueryWrapper<MediaFiles>().eq("id", mediaFile.getId()));
        if(count > 0) {
            throw new BadRequestException("文件已存在");
        }
        //存入数据库
        try {
            save(mediaFile);
        }catch (Exception e){
            //确保Minio与数据库信息一致
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(MINIO_FILE_BUCKET)
                    .object(mediaFile.getFilePath())
                    .build());
            throw new DatabaseOperateException("文件信息保存失败");
        }
//        long txEnd = System.currentTimeMillis();
//        System.out.println("数据库事务耗时(ms): " + (txEnd - txStart));
    }

    @Override
    public Result getFilesByPage(Long companyId, Integer pageNum, Integer pageSize, QueryMediaParamsDTO queryMediaParamsDto) {
        if(companyId == null || companyId <= 0) {
            throw new BadRequestException("公司ID不能为空或负数");
        }

        if (pageNum == null || pageNum <= 0 || pageSize == null || pageSize <= 0) {
            throw new BadRequestException("页码或每页记录数不能为空或负数");
        }

        Page<MediaFiles> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MediaFiles> mediaFilesQueryWrapper = getMediaFilesQueryWrapper(companyId, queryMediaParamsDto);

        Page<MediaFiles> pageResult = page(page, mediaFilesQueryWrapper);

        return Result.ok("查询成功", pageResult);
    }

    @Override
    public Result initUpload(UploadInitDTO uploadInitDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filename = uploadInitDTO.getFilename();
        Long fileSize = uploadInitDTO.getFileSize();
        String fileType = uploadInitDTO.getFileType();

        String fileId = DigestUtils.md5DigestAsHex(filename.getBytes(StandardCharsets.UTF_8));
        CreateMultipartUploadResponse multipartUpload = s3Client.createMultipartUpload(CreateMultipartUploadRequest
                .builder()
                .bucket(MINIO_VIDEO_BUCKET)
                .key(fileId + "." + fileType)
                .build());
        String uploadId = multipartUpload.uploadId();

        Integer totalPart = (int) Math.ceil((double) fileSize / MINIO_FILE_MULTIPART_SIZE_MAX);

        ArrayList<String> presignUrls = new ArrayList<>();

        for (int partNum = 1; partNum <= totalPart; partNum++) {
            //构建uploadPartRequest
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .uploadId(uploadId)//指定上传任务id
                    .partNumber(partNum)//指定分片索引
                    .bucket(MINIO_VIDEO_BUCKET)//指定桶
                    .key(fileId + "." + fileType)//指定Minio对象名
                    .build();

            //生成预签名上传请求
            PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner.presignUploadPart(UploadPartPresignRequest.builder()
                    .uploadPartRequest(uploadPartRequest)
                    .signatureDuration(Duration.ofDays(1))
                    .build());

            //获取预签名URL
            String presignUrl = presignedUploadPartRequest.url().toString();
            presignUrls.add(presignUrl);
        }

        //构建VO返回对象
        UploadInitVO uploadInitVO = new UploadInitVO();
        uploadInitVO.setUploadId(uploadId);
        uploadInitVO.setTotalPart(totalPart);
        uploadInitVO.setPresignedUrls(presignUrls);

        return Result.ok("初始化分片任务成功", uploadInitVO);
    }

    @Override
    public Result completeUpload(MultipartFileCompleteDTO multipartFileCompleteDTO) {
        String uploadId = multipartFileCompleteDTO.getUploadId();
        String fileId = multipartFileCompleteDTO.getFileId();
        List<EtagObject> etagObjects = multipartFileCompleteDTO.getEtags();

        //操作Minio进行分片的合成
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(MINIO_VIDEO_BUCKET)
                .key(fileId + ".mp4")
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder()
                        .parts(etagObjects.stream()
                                .map(etagObject -> CompletedPart.builder()
                                        .partNumber(etagObject.getPartNumber())
                                        .eTag(etagObject.getETag())
                                        .build()).toList())
                        .build())
                .build();

        try {
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (Exception e) {
            throw new MinioOperateException("合并分片文件失败", e);
        }

        return Result.ok("合并分片文件成功");
        //TODO 将文件信息写入数据库
    }

    @NotNull
    private static QueryWrapper<MediaFiles> getMediaFilesQueryWrapper(Long companyId, QueryMediaParamsDTO queryMediaParamsDto) {
        QueryWrapper<MediaFiles> mediaFilesQueryWrapper = new QueryWrapper<>();
        mediaFilesQueryWrapper.eq("company_id", companyId);

        if(queryMediaParamsDto != null) {
            if (queryMediaParamsDto.getFilename() != null && !queryMediaParamsDto.getFilename().isEmpty()) {
                mediaFilesQueryWrapper.like("filename", queryMediaParamsDto.getFilename());
            }
            if (queryMediaParamsDto.getFileType() != null && !queryMediaParamsDto.getFileType().isEmpty()) {
                mediaFilesQueryWrapper.eq("file_type", queryMediaParamsDto.getFileType());
            }
            if (queryMediaParamsDto.getAuditStatus() != null && !queryMediaParamsDto.getAuditStatus().isEmpty()) {
                mediaFilesQueryWrapper.eq("audit_status", queryMediaParamsDto.getAuditStatus());
            }
        }
        return mediaFilesQueryWrapper;
    }

    public void uploadFileToMinIO(MultipartFile file, String filePath) throws InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException, InsufficientDataException, ErrorResponseException, InternalException {
        //将文件上传至minio
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(MINIO_FILE_BUCKET)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .object(filePath)
                .build());
        if(objectWriteResponse==null) {
            throw new InternalException("文件上传失败", null);
        }
    }

    @NotNull
    private static MediaFiles getMediaFile(Long companyId, MultipartFile file, UploadFileDTO uploadFileDTO, String fileId, String filePath, String url) {
        MediaFiles mediaFile = new MediaFiles();
        BeanUtils.copyProperties(uploadFileDTO, mediaFile);
        mediaFile.setId(fileId);
        mediaFile.setFileId(fileId);
        mediaFile.setCompanyId(companyId);
        mediaFile.setBucket(MINIO_FILE_BUCKET);
        mediaFile.setFilePath(filePath);
        mediaFile.setUrl(url);
        mediaFile.setCreateDate(LocalDateTime.now());
        mediaFile.setAuditStatus(MEDIA_OBJECT_AUDIT_TRUE);
        mediaFile.setFileSize(file.getSize());
        return mediaFile;
    }

    //获取文件存储路径
    @NotNull
    private static String getFilePath(MultipartFile file, String fileId) {
        String date_str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        //获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return date_str + "/" + fileId + ext;
    }
}
