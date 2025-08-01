package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.entity.content.CourseBase;
import edu.wust.qrz.mapper.CourseBaseMapper;
import edu.wust.qrz.service.ICourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements ICourseService{


}
