package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseQueryDTO;
import edu.wust.qrz.service.CourseBaseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

    @Resource
    CourseBaseService courseBaseService;

    @PostMapping("/course/list")
    public Result getCourseByPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestBody(required = false) CourseQueryDTO courseQueryDTO) {
        return  courseBaseService.getCourseByPage(pageNum, pageSize, courseQueryDTO);
    }

}
