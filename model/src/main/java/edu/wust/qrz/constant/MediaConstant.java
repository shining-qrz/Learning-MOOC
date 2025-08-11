package edu.wust.qrz.constant;

public class MediaConstant {
    //大文件分片最大值--10MB
    public static final Long MINIO_FILE_MULTIPART_SIZE_MAX = 30 * 1024 * 1024L;

    public static final String MINIO_FILE_BUCKET = "files";
    public static final String MINIO_VIDEO_BUCKET = "videos";

    // 文件类型--图片
    public static final String MEDIA_TYPE_PHOTO = "001001";
    // 文件类型--视频
    public static final String MEDIA_TYPE_VIDEO = "001002";
    // 文件类型--其他
    public static final String MEDIA_TYPE_OTHER = "001003";

    //文件对象未审核
    public static final String MEDIA_OBJECT_AUDIT_NOT = "002002";
    //文件对象审核未通过
    public static final String MEDIA_OBJECT_AUDIT_FALSE = "002001";
    //文件对象审核通过
    public static final String MEDIA_OBJECT_AUDIT_TRUE = "002003";
}
