package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseTeacherCreateDTO;
import edu.wust.qrz.dto.content.CourseTeacherUpdateDTO;
import edu.wust.qrz.service.CourseTeacherService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courseTeacher")
@Validated
public class CourseTeacherController {
    @Resource
    private CourseTeacherService courseTeacherService;

    /**
     * 根据课程Id查询教师
     * @param courseId 课程ID
     * @return Result对象
     */
    @GetMapping("/list/{courseId}")
    public Result getCourseTeacherWithCourseId(@PathVariable Long courseId) {
        return courseTeacherService.getCourseTeacherWithCourseId(courseId);
    }

    /**
     * 新增课程教师
     * @param courseTeacherCreateDTO 课程教师数据传输对象
     * @return Result对象
     */
    @PostMapping
    public Result createCourseTeacher(@Valid @RequestBody CourseTeacherCreateDTO courseTeacherCreateDTO) {
        return courseTeacherService.createCourseTeacher(courseTeacherCreateDTO);
    }

    /**
     * 更新课程教师信息
     * @param courseTeacherUpdateDTO 课程教师更新数据传输对象
     * @return Result对象
     */
    @PatchMapping
    public Result updateCourseTeacher(@Valid @RequestBody CourseTeacherUpdateDTO courseTeacherUpdateDTO) {
        return courseTeacherService.updateCourseTeacher(courseTeacherUpdateDTO);
    }

    /**
     * 删除指定课程的教师信息
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return Result对象
     */
    @DeleteMapping("/course/{courseId}/{teacherId}")
    public Result deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        return courseTeacherService.deleteCourseTeacher(courseId, teacherId);
    }
}
