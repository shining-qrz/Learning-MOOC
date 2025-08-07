package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UpdateFileDTO;
import edu.wust.qrz.entity.media.MediaFiles;
import edu.wust.qrz.mapper.MediaFilesMapper;
import edu.wust.qrz.service.MediaFilesService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {
    @Override
    public Result uploadCourseFile(Long companyId, MultipartFile file, UpdateFileDTO updateFileDTO) {
        //将文件上传至minio

        //将文件信息保存至media_files表
        return Result.ok();
    }
}
