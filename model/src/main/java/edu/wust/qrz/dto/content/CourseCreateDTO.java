package edu.wust.qrz.dto.content;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseCreateDTO {
    @NotBlank
    private String charge;

    private String description;

    @NotBlank
    private String grade;

    @NotBlank
    private String mt;

    @NotBlank
    private String name;

    @NotBlank
    private String pic;

    private BigDecimal price;

    @NotBlank
    private String st;

    private String tags;

    @NotBlank
    private String teachmode;

    @NotBlank
    private String users;

    private BigDecimal originalPrice;

    private String qq;

    private String wechat;

    private String phone;

    private Integer validDays;



}
