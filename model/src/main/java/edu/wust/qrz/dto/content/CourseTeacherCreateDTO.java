package edu.wust.qrz.dto.content;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseTeacherCreateDTO {
    // 课程ID
    @NotNull(message = "课程ID不能为空")
    @Min(value = 0, message = "课程ID不能小于0")
    private Long courseId;

    // 教师姓名
    @NotBlank(message = "教师姓名不能为空")
    private String teacherName;

    // 教师职位
    @NotBlank(message = "教师职位不能为空")
    private String position;

    @NotBlank(message = "教师简介不能为空")
    // 教师简介
    private String introduction;

    // 教师照片
    private String photograph;
}
