package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.MultipartFileCompleteDTO;
import edu.wust.qrz.dto.media.QueryMediaParamsDTO;
import edu.wust.qrz.dto.media.UploadFileDTO;
import edu.wust.qrz.dto.media.UploadInitDTO;
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

    /**
     * 上传课程文件
     * @param companyId 公司ID
     * @param file 上传的文件
     * @param uploadFileDTO 上传文件的DTO对象，包含文件相关信息
     * @return 上传结果
    **/
    @PostMapping(value = "/upload/coursefile" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadCourseFile(@RequestParam("companyId") Long companyId,
                                   @RequestPart(value = "file") MultipartFile file,
                                   @Valid @ModelAttribute UploadFileDTO uploadFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return mediaFilesService.uploadCourseFile(companyId, file, uploadFileDTO);
    }

    /**
     * 分页查询媒资文件
     * @param companyId 公司ID
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param queryMediaParamsDto 查询媒资文件的参数DTO，包含文件名、文件类型、审核状态等信息
     * @return 分页查询结果
     */
    @PostMapping("/files/{companyId}")
    public Result getFilesByPage(@PathVariable("companyId") Long companyId,
                                 @RequestParam("pageNum") Integer pageNum,
                                 @RequestParam("pageSize") Integer pageSize,
                                 @RequestBody(required = false) QueryMediaParamsDTO queryMediaParamsDto){
        return mediaFilesService.getFilesByPage(companyId, pageNum, pageSize, queryMediaParamsDto);
    }


    /**
     * 大文件分片上传任务初始化
     * @param uploadInitDTO 上传初始化DTO，包含文件名、文件大小和文件类型等信息
     * @return 上传初始化结果
     */
    @PostMapping("upload/init")
    public Result initUpload(@Valid @RequestBody UploadInitDTO uploadInitDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return mediaFilesService.initUpload(uploadInitDTO);
    }

    @PostMapping("/upload/complete")
    public Result completeUpload(@Valid @RequestBody MultipartFileCompleteDTO multipartFileCompleteDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return mediaFilesService.completeUpload(multipartFileCompleteDTO);
    }



}
