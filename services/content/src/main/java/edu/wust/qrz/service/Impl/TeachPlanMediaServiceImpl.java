package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.entity.content.TeachplanMedia;
import edu.wust.qrz.mapper.TeachplanMediaMapper;
import edu.wust.qrz.service.TeachPlanMediaService;
import edu.wust.qrz.service.TeachPlanService;
import org.springframework.stereotype.Service;

@Service
public class TeachPlanMediaServiceImpl extends ServiceImpl<TeachplanMediaMapper, TeachplanMedia>  implements TeachPlanMediaService {
}
