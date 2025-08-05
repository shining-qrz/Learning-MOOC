package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseTeacherCreateDTO;
import edu.wust.qrz.dto.content.CourseTeacherUpdateDTO;
import edu.wust.qrz.entity.content.CourseBase;
import edu.wust.qrz.entity.content.CourseTeacher;
import edu.wust.qrz.exception.BadRequestException;
import edu.wust.qrz.exception.DatabaseOperateException;
import edu.wust.qrz.mapper.CourseTeacherMapper;
import edu.wust.qrz.service.CourseBaseService;
import edu.wust.qrz.service.CourseTeacherService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Lazy
    @Resource
    private CourseBaseService courseBaseService;

    @Override
    public Result getCourseTeacherWithCourseId(Long courseId) {
        QueryWrapper<CourseTeacher> courseTeacherQueryWrapper = new QueryWrapper<>();
        courseTeacherQueryWrapper.eq("course_id", courseId);
        CourseTeacher courseTeacher = getOne(courseTeacherQueryWrapper);
        if (courseTeacher == null) {
            throw new BadRequestException("课程不存在");
        }

        return Result.ok(courseTeacher);
    }

    @Override
    public Result createCourseTeacher(CourseTeacherCreateDTO courseTeacherCreateDTO) {
        //参数校验
        QueryWrapper<CourseBase> courseBaseQueryWrapper = new QueryWrapper<>();
        courseBaseQueryWrapper.eq("id", courseTeacherCreateDTO.getCourseId());
        long count = courseBaseService.count(courseBaseQueryWrapper);
        if(count<1) {
            throw new BadRequestException("课程不存在");
        }

        //新增教师
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacherCreateDTO, courseTeacher);
        courseTeacher.setCreateDate(LocalDateTime.now());
        save(courseTeacher);

        return Result.ok("新增教师成功");
    }

    @Override
    public Result updateCourseTeacher(CourseTeacherUpdateDTO courseTeacherUpdateDTO) {
        //校验id
        CourseTeacher courseTeacher = getById(courseTeacherUpdateDTO.getId());
        if(courseTeacher == null) {
            throw new BadRequestException("教师不存在");
        }

        //校验courseId
        QueryWrapper<CourseBase> courseBaseQueryWrapper = new QueryWrapper<>();
        courseBaseQueryWrapper.eq("id", courseTeacherUpdateDTO.getCourseId());
        long count = courseBaseService.count(courseBaseQueryWrapper);
        if(count<1) {
            throw new BadRequestException("课程不存在");
        }

        BeanUtils.copyProperties(courseTeacherUpdateDTO, courseTeacher);

        boolean success = updateById(courseTeacher);
        if(!success)
            throw new DatabaseOperateException("更新教师信息失败");

        return Result.ok("更新教师信息成功");
    }

    @Override
    public Result deleteCourseTeacher(Long courseId, Long teacherId) {
        //校验id
        if(teacherId == null || teacherId < 1) {
            throw new BadRequestException("教师ID为空或小于1");
        }
        CourseTeacher courseTeacher = getById(teacherId);
        if(courseTeacher == null) {
            throw new BadRequestException("教师不存在");
        }

        //校验courseId
        if(courseId == null || courseId < 1) {
            throw new BadRequestException("课程ID为空或小于1");
        }
        QueryWrapper<CourseBase> courseBaseQueryWrapper = new QueryWrapper<>();
        courseBaseQueryWrapper.eq("id", courseId);
        long count = courseBaseService.count(courseBaseQueryWrapper);
        if(count<1) {
            throw new BadRequestException("课程不存在");
        }

        boolean success = removeById(teacherId);
        if (!success)
            throw new DatabaseOperateException("删除教师信息失败");

        return Result.ok("删除教师信息成功");
    }
}
