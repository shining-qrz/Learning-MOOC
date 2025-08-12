package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UploadSuccessConfirmDTO;
import edu.wust.qrz.entity.media.MediaProcess;
import jakarta.validation.Valid;

public interface MediaProcessService extends IService<MediaProcess> {
    Result uploadSuccessConfirm(@Valid UploadSuccessConfirmDTO uploadSuccessConfirmDTO);

    Result getUploadProgress(String fileId);
}
