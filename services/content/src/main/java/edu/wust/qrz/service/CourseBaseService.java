package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseQueryDTO;
import edu.wust.qrz.entity.content.CourseBase;

public interface CourseBaseService extends IService<CourseBase> {
    Result getCourseByPage(Integer pageNum, Integer pageSize, CourseQueryDTO courseQueryDTO);
}
