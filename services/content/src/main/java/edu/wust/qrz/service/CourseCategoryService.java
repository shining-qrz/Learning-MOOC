package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.entity.content.CourseCategory;

public interface CourseCategoryService extends IService<CourseCategory> {
    Result getCategoryByTree();
}
