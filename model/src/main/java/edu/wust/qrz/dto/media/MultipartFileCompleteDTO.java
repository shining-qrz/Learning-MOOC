package edu.wust.qrz.dto.media;

import edu.wust.qrz.entity.media.EtagObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MultipartFileCompleteDTO extends UploadFileDTO{
    private Long companyId; // 机构id
    private Long fileSize; // 文件大小
    private String uploadId; // 上传任务id
    private String fileId; // 文件id
    private List<EtagObject> etags; // 分片的ETag列表
}
