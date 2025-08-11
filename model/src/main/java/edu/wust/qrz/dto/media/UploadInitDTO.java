package edu.wust.qrz.dto.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadInitDTO {
    @NotBlank
    private String filename;

    @NotNull
    private Long fileSize;

    @NotBlank
    private String fileType;
}
