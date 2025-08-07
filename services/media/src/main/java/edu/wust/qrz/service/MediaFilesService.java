package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.media.UpdateFileDTO;
import edu.wust.qrz.entity.media.MediaFiles;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface MediaFilesService extends IService<MediaFiles> {
    Result uploadCourseFile(Long companyId, MultipartFile file, @Valid UpdateFileDTO updateFileDTO);
}
