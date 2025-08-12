package edu.wust.qrz.vo.media;

import lombok.Data;

@Data
public class presignedUrlVO {
    private String url;
    private String partNumber; // 分片索引
    private String XAmzAlgorithm;
    private String XAmzDate;
    private String XAmzSignedHeaders;
    private String XAmzCredential;
    private String XAmzExpires;
    private String XAmzSignature;

}
