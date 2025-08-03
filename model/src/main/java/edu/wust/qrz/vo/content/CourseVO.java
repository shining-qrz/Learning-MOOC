package edu.wust.qrz.vo.content;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 只包含课程信息的VO
 */
@Data
public class CourseVO {
    private String name;
    private String users;
    private String tags;
    private String mt;
    private String st;
    private String grade;
    private String teachmode;
    private String description;
    private String pic;
    private String auditStatus;
    private String status;
    private String charge;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer validDays;
}
