package edu.wust.qrz.dto.content;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseCreateDTO {
    @NotBlank(message = "收费类型不能为空")
    private String charge;

    private String description;

    @NotBlank(message = "课程等级不能为空")
    private String grade;

    @NotBlank(message = "课程分类不能为空")
    private String mt;

    @NotBlank(message = "课程名称不能为空")
    private String name;

    @NotBlank(message = "课程图片不能为空")
    private String pic;

    @DecimalMin(value = "0" , message = "现价不能小于0")
    private BigDecimal price;

    @NotBlank(message = "课程二级分类不能为空")
    private String st;

    private String tags;

    @NotBlank(message = "教学模式不能为空")
    private String teachmode;

    @NotBlank(message = "适用人群不能为空")
    private String users;

    @DecimalMin(value = "0" , message = "原价不能小于0")
    private BigDecimal originalPrice;

    private String qq;

    private String wechat;

    private String phone;

    private Integer validDays;



}
