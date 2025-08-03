package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.entity.content.CourseMarket;
import edu.wust.qrz.mapper.CourseMarketMapper;
import edu.wust.qrz.service.CourseMarketService;
import org.springframework.stereotype.Service;

@Service
public class CourseMarketServiceImpl extends ServiceImpl<CourseMarketMapper, CourseMarket> implements CourseMarketService {
}
