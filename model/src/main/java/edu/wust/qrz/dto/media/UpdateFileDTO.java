package edu.wust.qrz.dto.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateFileDTO {

    @NotBlank(message = "文件名不能为空")
    private String filename;

    private String fileType;

    private Long fileSize;

    private String tags;

    private String username;

    private String remark;

}
