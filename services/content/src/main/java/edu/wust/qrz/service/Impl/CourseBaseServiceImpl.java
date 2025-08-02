package edu.wust.qrz.service.Impl;

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

        return Result.ok();
    }
}
