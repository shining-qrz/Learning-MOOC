package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.CourseQueryDTO;
import edu.wust.qrz.service.CourseBaseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseBaseController {

    @Resource
    CourseBaseService courseBaseService;

    /**
     * 分页查询课程信息
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     * @param courseQueryDTO 查询条件
     * @return Result对象，包含查询结果
     */
    @PostMapping("/list")
    public Result getCourseByPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestBody(required = false) CourseQueryDTO courseQueryDTO) {
        return  courseBaseService.getCourseByPage(pageNum, pageSize, courseQueryDTO);
    }


}
