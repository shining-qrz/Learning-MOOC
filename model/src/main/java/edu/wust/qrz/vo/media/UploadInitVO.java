package edu.wust.qrz.vo.media;

import lombok.Data;

import java.util.List;

/**
 * 大文件分片上传VO
 */

@Data
public class UploadInitVO {
    //上传任务id
    private String uploadId;
    //分片总数
    private Integer totalPart;
    //预签名url
    private List<String> presignedUrls;
}
