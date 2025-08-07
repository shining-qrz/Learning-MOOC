package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UpdateFileDTO;
import edu.wust.qrz.service.MediaFilesService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
public class MediaFilesController {

    @Resource
    private MediaFilesService mediaFilesService;

    @PostMapping(value = "/upload/coursefile" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadCourseFile(@RequestParam("companyId") Long companyId,
                                   @RequestPart("file") MultipartFile file,
                                   @Valid @ModelAttribute UpdateFileDTO updateFileDTO)  {

        return mediaFilesService.uploadCourseFile(companyId, file, updateFileDTO);
    }

}
