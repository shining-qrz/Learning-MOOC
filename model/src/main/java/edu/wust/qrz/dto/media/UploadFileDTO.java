package edu.wust.qrz.dto.media;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.xml.sax.helpers.AttributeListImpl;

import java.util.List;

@Data
public class UploadFileDTO {

    @NotBlank(message = "文件名不能为空")
    private String filename;

    private String fileType;

    private String tags;

    private String username;

    private String remark;

}
