package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.service.CourseCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course-category")
public class CourseCategoryController {
    @Resource
    private CourseCategoryService courseCategoryService;

    @GetMapping("tree-nodes")
    public Result getCategoryByTree() {
        return courseCategoryService.getCategoryByTree();
    }
}
