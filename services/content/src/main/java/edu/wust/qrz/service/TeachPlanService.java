package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.entity.content.Teachplan;

public interface TeachPlanService extends IService<Teachplan> {
    Result getTeachPlanTreeNodes(Long courseId);
}
