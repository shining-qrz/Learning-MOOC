package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseQueryDTO;
import edu.wust.qrz.entity.content.CourseBase;
import edu.wust.qrz.mapper.CourseBaseMapper;
import edu.wust.qrz.service.CourseBaseService;
import org.springframework.stereotype.Service;

@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    @Override
    public Result getCourseByPage(Integer pageNum, Integer pageSize, CourseQueryDTO courseQueryDTO) {
        if(pageNum == null || pageSize == null || pageNum <= 0 || pageSize <= 0) {
            return Result.fail("Invalid page number or page size");
        }

        Page<CourseBase> page = new Page<>(pageNum,pageSize);

        if(courseQueryDTO == null){
            Page<CourseBase> courseBasePage = page(page);

            return Result.ok(courseBasePage);
        }

        QueryWrapper<CourseBase> queryWrapper = getCourseBaseQueryWrapper(courseQueryDTO);

        Page<CourseBase> result = page(page, queryWrapper);

        return Result.ok(result);
    }

    private QueryWrapper<CourseBase> getCourseBaseQueryWrapper(CourseQueryDTO courseQueryDTO) {
        QueryWrapper<CourseBase> queryWrapper = new QueryWrapper<>();

        if (courseQueryDTO.getCourseName() != null && !courseQueryDTO.getCourseName().isEmpty()) {
            queryWrapper.like("name", courseQueryDTO.getCourseName());
        }

        if (courseQueryDTO.getAuditStatus() != null && !courseQueryDTO.getAuditStatus().isEmpty()) {
            queryWrapper.eq("audit_status", courseQueryDTO.getAuditStatus());
        }

        if (courseQueryDTO.getPublishStatus() != null && !courseQueryDTO.getPublishStatus().isEmpty()) {
            queryWrapper.eq("status", courseQueryDTO.getPublishStatus());
        }
        return queryWrapper;
    }
}
