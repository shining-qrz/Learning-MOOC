package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UploadSuccessConfirmDTO;
import edu.wust.qrz.entity.media.MediaProcess;
import edu.wust.qrz.exception.BadRequestException;
import edu.wust.qrz.exception.DatabaseOperateException;
import edu.wust.qrz.mapper.MediaProcessMapper;
import edu.wust.qrz.service.MediaProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {

    @Override
    public Result uploadSuccessConfirm(UploadSuccessConfirmDTO uploadSuccessConfirmDTO) {
        MediaProcess mediaProcess = new MediaProcess();
        BeanUtils.copyProperties(uploadSuccessConfirmDTO, mediaProcess);
        mediaProcess.setCreateDate(LocalDateTime.now());
        try {
            save(mediaProcess);
        } catch (Exception e) {
            throw new DatabaseOperateException("上传成功确认失败",e);
        }
        return Result.ok("上传成功确认成功");
    }

    @Override
    public Result getUploadProgress(String fileId) {
        if(fileId == null || fileId.isEmpty()) {
            throw new BadRequestException("文件标识不能为空");
        }

        QueryWrapper<MediaProcess> mediaProcessQueryWrapper = new QueryWrapper<>();
        mediaProcessQueryWrapper.eq("file_id", fileId)
                        .eq("bucket", "videos");
        List<MediaProcess> mediaProcesses = list(mediaProcessQueryWrapper);

        if(mediaProcesses == null || mediaProcesses.isEmpty()) {
            return Result.ok("查询成功", new ArrayList<>());
        }

        ArrayList<Integer> uploadSuccessParts = new ArrayList<>();

        for(MediaProcess mediaProcess : mediaProcesses) {
            uploadSuccessParts.add(mediaProcess.getPartNumber());
        }

        return Result.ok("查询成功", uploadSuccessParts);
    }
}
