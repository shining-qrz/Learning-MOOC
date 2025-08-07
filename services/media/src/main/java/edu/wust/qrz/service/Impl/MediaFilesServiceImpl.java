package edu.wust.qrz.service.Impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UploadFileDTO;
import edu.wust.qrz.entity.media.MediaFiles;
import edu.wust.qrz.exception.BadRequestException;
import edu.wust.qrz.exception.DatabaseOperateException;
import edu.wust.qrz.mapper.MediaFilesMapper;
import edu.wust.qrz.service.MediaFilesService;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Override
    public Result uploadCourseFile(Long companyId, MultipartFile file, UploadFileDTO uploadFileDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(file == null || file.isEmpty()) {
            throw new BadRequestException("上传的文件不能为空");
        }

        String fileId = UUID.randomUUID().toString();
        String filePath = getFilePath(file,fileId);
        String url = MINIO_FILE_BUCKET + "/" + filePath;

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

        //构建实体对象
        MediaFiles mediaFile = getMediaFile(companyId, file, uploadFileDTO, fileId, filePath, url);

        //存入数据库
        boolean success = save(mediaFile);
        if(!success) {
            throw new DatabaseOperateException("文件信息保存失败");
        }

        return Result.ok("上传成功");
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
