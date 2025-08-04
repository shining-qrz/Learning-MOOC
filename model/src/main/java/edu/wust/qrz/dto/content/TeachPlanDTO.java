package edu.wust.qrz.dto.content;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TeachPlanDTO {
    // 课程计划ID
    private Integer id;

    // 父节点ID
    @NotNull(message = "父节点ID为空")
    @Min(value = 0, message = "父节点ID小于0")
    private Long parentid;

    // 层级
    @NotNull(message = "课程层级为空")
    @Min(value = 1, message = "层级小于0")
    @Max(value = 3, message = "层级大于3")
    private Integer grade;

    // 章节名称
    @NotBlank(message = "章节名称为空")
    private String pname;

    //所属课程Id
    @NotNull(message = "课程ID为空")
    @Min(value = 1, message = "课程ID小于1")
    private Long courseId;

    // 课程类型
    private String mediaType;

    // 课程发布状态
    private Long coursePubId;

    //是否支持试看
    private String isPreview;
}
