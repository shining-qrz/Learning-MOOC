package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.TeachPlanDTO;
import edu.wust.qrz.entity.content.Teachplan;
import jakarta.validation.Valid;

public interface TeachPlanService extends IService<Teachplan> {
    Result getTeachPlanTreeNodes(Long courseId);

    Result createTeachPlan(@Valid TeachPlanDTO teachPlanDTO);

    Result updateTeachPlan(@Valid TeachPlanDTO teachPlanDTO);

    Result deleteTeachPlan(Long id);

    Result moveUpTeachPlan(Long id);

    Result moveDownTeachPlan(Long id);
}
