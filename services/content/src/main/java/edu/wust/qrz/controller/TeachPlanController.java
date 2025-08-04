package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.TeachPlanDTO;
import edu.wust.qrz.service.TeachPlanService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachplan")
@Validated
public class TeachPlanController {

    @Resource
    private TeachPlanService teachPlanService;

    /**
     * 获取课程教学计划
     * @param courseId 课程ID
     * @return Result对象，包含课程教学计划树形结构
     */
    @GetMapping("/{courseId}/tree-nodes")
    public Result getTeachPlanTreeNodes(@PathVariable Long courseId) {
        return teachPlanService.getTeachPlanTreeNodes(courseId);
    }

    @PostMapping
    public Result createTeachPlan(@Valid @RequestBody TeachPlanDTO teachPlanDTO) {
        return teachPlanService.createTeachPlan(teachPlanDTO);
    }
}
