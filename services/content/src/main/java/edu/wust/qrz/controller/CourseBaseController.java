package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseCreateDTO;
import edu.wust.qrz.dto.content.CourseQueryDTO;
import edu.wust.qrz.service.CourseBaseService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseBaseController {

    @Resource
    CourseBaseService courseBaseService;

    /**
     * 分页查询课程信息
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     * @param courseQueryDTO 查询条件
     * @return Result对象，包含查询结果
     */
    @PostMapping("/list")
    public Result getCourseByPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestBody(required = false) CourseQueryDTO courseQueryDTO) {
        return  courseBaseService.getCourseByPage(pageNum, pageSize, courseQueryDTO);
    }

    /**
     * 创建课程
     * @param companyId 机构ID
     * @param courseCreateDTO 课程创建数据传输对象
     * @return Result对象
     */
    @PostMapping
    public Result createCourse(@RequestParam Long companyId,
                               @Valid @RequestBody CourseCreateDTO courseCreateDTO){
        return courseBaseService.createCourse(companyId,courseCreateDTO);
    }

    /**
     * 根据课程ID查询课程信息
     * @param courseId 课程ID
     * @return Result对象，返回CourseVO
     */
    @GetMapping("/{courseId}")
    public Result getCourseById(@PathVariable Long courseId) {
        return courseBaseService.getCourseById(courseId);
    }

}
