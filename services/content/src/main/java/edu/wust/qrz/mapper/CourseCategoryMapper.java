package edu.wust.qrz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.wust.qrz.entity.content.CourseCategory;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
}
