package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.MultipartFileCompleteDTO;
import edu.wust.qrz.dto.media.QueryMediaParamsDTO;
import edu.wust.qrz.dto.media.UploadFileDTO;
import edu.wust.qrz.dto.media.UploadInitDTO;
import edu.wust.qrz.entity.media.MediaFiles;
import io.minio.errors.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MediaFilesService extends IService<MediaFiles> {
    Result uploadCourseFile(Long companyId, MultipartFile file, @Valid UploadFileDTO uploadFileDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void saveFileToDB(MediaFiles mediaFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    Result getFilesByPage(Long companyId,Integer pageNum, Integer pageSize, QueryMediaParamsDTO queryMediaParamsDto);

    Result initUpload(@Valid UploadInitDTO uploadInitDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    Result completeUpload(@Valid MultipartFileCompleteDTO multipartFileCompleteDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
