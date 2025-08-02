package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.entity.content.CourseCategory;
import edu.wust.qrz.mapper.CourseCategoryMapper;
import edu.wust.qrz.service.CourseCategoryService;
import edu.wust.qrz.vo.content.CourseCategoryTreeVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {


    @Override
    public Result getCategoryByTree() {
        List<CourseCategory> courseCategories = list();

        Map<String, CourseCategoryTreeVO> voMap = courseCategories.stream()
                .map(courseCategory ->
                        new CourseCategoryTreeVO(courseCategory.getId(),
                                courseCategory.getName(),
                                courseCategory.getParentid()))
                .collect(Collectors.toMap(CourseCategoryTreeVO::getId, courseCategoryTreeVO -> courseCategoryTreeVO));

        ArrayList<CourseCategoryTreeVO> courseCategoryTreeVOList = new ArrayList<>();

        for(CourseCategoryTreeVO courseCategoryTreeVO : voMap.values()) {
            if (courseCategoryTreeVO.getParentid() == null || courseCategoryTreeVO.getParentid().isEmpty() || "1".equals(courseCategoryTreeVO.getParentid())) {
                courseCategoryTreeVOList.add(courseCategoryTreeVO);
            } else {
                CourseCategoryTreeVO parent = voMap.get(courseCategoryTreeVO.getParentid());
                if (parent != null ) {
                    parent.getChildren().add(courseCategoryTreeVO);
                }
            }
        }

        return Result.ok(courseCategoryTreeVOList);
    }
}
