package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UploadSuccessConfirmDTO;
import edu.wust.qrz.service.MediaFilesService;
import edu.wust.qrz.service.MediaProcessService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.simpleframework.xml.Path;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class MediaProcessController {

    @Resource
    private MediaProcessService mediaProcessService;

    @PostMapping("/upload/info")
    public Result uploadSuccessConfirm(@Valid @RequestBody UploadSuccessConfirmDTO uploadSuccessConfirmDTO) {
        return mediaProcessService.uploadSuccessConfirm(uploadSuccessConfirmDTO);
    }

    @GetMapping("/upload/progress/{fileId}")
    public Result getUploadProgress(@PathVariable String fileId) {
        return mediaProcessService.getUploadProgress(fileId);
    }
}
