package edu.wust.qrz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.wust.qrz.entity.content.CourseBase;
import edu.wust.qrz.entity.content.CoursePublish;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CoursePublishMapper extends BaseMapper<CoursePublish> {
}
