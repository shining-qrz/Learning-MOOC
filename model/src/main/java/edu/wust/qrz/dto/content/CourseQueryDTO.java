package edu.wust.qrz.dto.content;

import lombok.Data;

@Data
public class CourseQueryDTO {
    private String auditStatus; // 审核状态
    private String courseName; // 课程名称
    private String publishStatus; // 发布状态
}
