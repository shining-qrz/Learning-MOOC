package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UploadFileDTO;
import edu.wust.qrz.service.MediaFilesService;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@Validated
public class MediaFilesController {

    @Resource
    private MediaFilesService mediaFilesService;

    @PostMapping(value = "/upload/coursefile" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadCourseFile(@RequestParam("companyId") Long companyId,
                                   @RequestPart(value = "file") MultipartFile file,
                                   @Valid @ModelAttribute UploadFileDTO uploadFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        return mediaFilesService.uploadCourseFile(companyId, file, uploadFileDTO);
    }

}
