package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseTeacherCreateDTO;
import edu.wust.qrz.dto.content.CourseTeacherUpdateDTO;
import edu.wust.qrz.entity.content.CourseTeacher;
import jakarta.validation.Valid;

public interface CourseTeacherService extends IService<CourseTeacher> {
    Result getCourseTeacherWithCourseId(Long courseId);

    Result createCourseTeacher(@Valid CourseTeacherCreateDTO courseTeacherCreateDTO);

    Result updateCourseTeacher(@Valid CourseTeacherUpdateDTO courseTeacherUpdateDTO);

    Result deleteCourseTeacher(Long courseId, Long teacherId);
}
