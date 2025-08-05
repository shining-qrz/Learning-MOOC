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

    /**
     * 新增教学计划
     * @param teachPlanDTO 教学计划数据传输对象
     * @return Result对象
     */
    @PostMapping
    public Result createTeachPlan(@Valid @RequestBody TeachPlanDTO teachPlanDTO) {
        return teachPlanService.createTeachPlan(teachPlanDTO);
    }

    /**
     * 更新教学计划
     * @param teachPlanDTO
     * @return
     */
    @PatchMapping
    public Result updateTeachPlan(@Valid @RequestBody TeachPlanDTO teachPlanDTO){
        return teachPlanService.updateTeachPlan(teachPlanDTO);
    }

    /**
     * 删除教学计划
     * @param id 教学计划ID
     * @return Result对象
     */
    @DeleteMapping("/{id}")
    public Result deleteTeachPlan(@PathVariable Long id) {
        return teachPlanService.deleteTeachPlan(id);
    }

    /**
     * 将教学计划上移
     * @param id 教学计划ID
     * @return Result对象
     */
    @PostMapping("/moveup/{id}")
    public Result moveUpTeachPlan(@PathVariable Long id) {
        return teachPlanService.moveUpTeachPlan(id);
    }

    @PostMapping("/movedown/{id}")
    public Result moveDownTeachPlan(@PathVariable Long id) {
        return teachPlanService.moveDownTeachPlan(id);
    }
}
