package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseCreateDTO;
import edu.wust.qrz.dto.content.CourseQueryDTO;
import edu.wust.qrz.entity.content.CourseBase;
import jakarta.validation.Valid;

public interface CourseBaseService extends IService<CourseBase> {
    Result getCourseByPage(Integer pageNum, Integer pageSize, CourseQueryDTO courseQueryDTO);

    Result createCourse(Long companyId, @Valid CourseCreateDTO courseCreateDTO);

    Result getCourseById(Long courseId);

    Result updateCourse(Long courseId, Long companyId, @Valid CourseCreateDTO courseCreateDTO);

    Result deleteCourse(Long id);
}
