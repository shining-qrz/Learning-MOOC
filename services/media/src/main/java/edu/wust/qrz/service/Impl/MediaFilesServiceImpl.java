package edu.wust.qrz.service.Impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.QueryMediaParamsDto;
import edu.wust.qrz.dto.media.UploadFileDTO;
import edu.wust.qrz.entity.media.MediaFiles;
import edu.wust.qrz.exception.BadRequestException;
import edu.wust.qrz.exception.DatabaseOperateException;
import edu.wust.qrz.mapper.MediaFilesMapper;
import edu.wust.qrz.service.MediaFilesService;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static edu.wust.qrz.constant.MediaConstant.MEDIA_OBJECT_AUDIT_TRUE;
import static edu.wust.qrz.constant.MediaConstant.MINIO_FILE_BUCKET;

@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {

    @Resource
    private MinioClient minioClient;

    @Lazy
    @Resource
    private MediaFilesService proxy;

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
    public Result getFilesByPage(Long companyId, Integer pageNum, Integer pageSize, QueryMediaParamsDto queryMediaParamsDto) {
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

    @NotNull
    private static QueryWrapper<MediaFiles> getMediaFilesQueryWrapper(Long companyId, QueryMediaParamsDto queryMediaParamsDto) {
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
