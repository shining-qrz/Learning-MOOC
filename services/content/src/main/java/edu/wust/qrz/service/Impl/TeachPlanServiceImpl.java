package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.entity.content.Teachplan;
import edu.wust.qrz.entity.content.TeachplanMedia;
import edu.wust.qrz.handler.BadRequestException;
import edu.wust.qrz.mapper.TeachplanMapper;
import edu.wust.qrz.service.TeachPlanMediaService;
import edu.wust.qrz.service.TeachPlanService;
import edu.wust.qrz.vo.content.TeachPlanVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachPlanService {

    @Resource
    TeachPlanMediaService teachPlanMediaService;

    @Override
    public Result getTeachPlanTreeNodes(Long courseId) {
        //查询该课程所有教学计划
        QueryWrapper<Teachplan> teachplanQueryWrapper = new QueryWrapper<>();
        teachplanQueryWrapper.eq("course_id", courseId);
        List<Teachplan> teachplans = list(teachplanQueryWrapper);

        if (teachplans.isEmpty()) {
            throw new BadRequestException("课程计划不存在");
        }

        //类型转换：Teachplan -> TeachPlanVOMap: TeachPlanId : TeachPlanVO
        //转换为map方便根据查找父亲节点
        Map<Long, TeachPlanVO> teachPlanVOMap = teachplans.stream().map(teachplan -> {
            TeachPlanVO teachPlanVO = new TeachPlanVO();
            BeanUtils.copyProperties(teachplan, teachPlanVO);

            QueryWrapper<TeachplanMedia> teachplanMediaQueryWrapper = new QueryWrapper<>();
            teachplanMediaQueryWrapper.eq("teachplan_id", teachplan.getId());
            TeachplanMedia teachplanMedia = teachPlanMediaService.getOne(teachplanMediaQueryWrapper);
            if (teachplanMedia != null) {
                teachPlanVO.setTeachplanMedia(teachplanMedia);
            }
            return teachPlanVO;
        }).collect(Collectors.toMap(TeachPlanVO::getId, teachPlanVO -> teachPlanVO));

        //构建树形结构
        List<TeachPlanVO> results = new ArrayList<>();
        for(TeachPlanVO teachPlanVO: teachPlanVOMap.values()) {
            if(teachPlanVO.getParentid() == 0)
                results.add(teachPlanVO);
            else {
                //依据Map找到父VO并将当前teachPlanVO加入父VO的children列表
                teachPlanVOMap.get(teachPlanVO.getParentid()).getChildren().add(teachPlanVO);
            }
        }

        //根据order属性对结果进行排序
        sortTeachPlanVOList(results);
        return Result.ok(results);
    }


    //排序
    private void sortTeachPlanVOList(List<TeachPlanVO> teachPlanVOList) {
        //非空校验
        if (teachPlanVOList == null || teachPlanVOList.isEmpty())
            return;

        //排序
        teachPlanVOList.sort(Comparator.comparingInt(Teachplan::getOrderby));
        //遍历List，对每个VO的孩子VO排序
        for (TeachPlanVO teachPlan : teachPlanVOList) {
            sortTeachPlanVOList(teachPlan.getChildren());
        }
    }
}

