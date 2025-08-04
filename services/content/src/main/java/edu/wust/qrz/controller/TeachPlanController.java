package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.service.TeachPlanService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teachplan")
public class TeachPlanController {

    @Resource
    private TeachPlanService teachPlanService;

    @GetMapping("/{courseId}/tree-nodes")
    public Result getTeachPlanTreeNodes(@PathVariable Long courseId) {
        return teachPlanService.getTeachPlanTreeNodes(courseId);
    }
}
