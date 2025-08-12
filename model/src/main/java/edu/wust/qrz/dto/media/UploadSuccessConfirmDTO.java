package edu.wust.qrz.dto.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadSuccessConfirmDTO {
    @NotBlank
    private String fileId;
    @NotBlank
    private String bucket;
    @NotBlank
    private String filePath;
    @NotNull
    private Integer partNumber;
}
