package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.dto.content.TeachPlanDTO;
import edu.wust.qrz.entity.content.CourseBase;
import edu.wust.qrz.entity.content.Teachplan;
import edu.wust.qrz.entity.content.TeachplanMedia;
import edu.wust.qrz.exception.BadRequestException;
import edu.wust.qrz.exception.DatabaseOperateException;
import edu.wust.qrz.mapper.TeachplanMapper;
import edu.wust.qrz.service.CourseBaseService;
import edu.wust.qrz.service.TeachPlanMediaService;
import edu.wust.qrz.service.TeachPlanService;
import edu.wust.qrz.vo.content.TeachPlanVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.wust.qrz.constant.DataDictionaryConstant.COURSE_AUDIT_NOT_SUBMIT;

@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachPlanService {

    @Resource
    TeachPlanMediaService teachPlanMediaService;

    @Lazy
    @Resource
    CourseBaseService courseBaseService;

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

    @Override
    public Result createTeachPlan(TeachPlanDTO teachPlanDTO) {
        CourseBase courseBase = courseBaseService.getById(teachPlanDTO.getCourseId());
        if(courseBase == null) {
            throw new BadRequestException("课程不存在");
        }


        if (teachPlanDTO.getParentid() != 0) {
            QueryWrapper<Teachplan> parentIdWrapper = new QueryWrapper<>();
            parentIdWrapper.eq("id", teachPlanDTO.getParentid());
            long parentCount = count(parentIdWrapper);
            if(parentCount==0)
                throw new BadRequestException("父节点ID不存在");
        }

        Teachplan teachplan = new Teachplan();
        BeanUtils.copyProperties(teachPlanDTO, teachplan);
        teachplan.setStatus(1);
        teachplan.setCreateDate(LocalDateTime.now());

        //将新增章节排序至最后
        QueryWrapper<Teachplan> orderWrapper = new QueryWrapper<Teachplan>()
                .eq("grade", teachPlanDTO.getGrade())
                .eq("course_id", teachPlanDTO.getCourseId())
                .eq("parentid", teachPlanDTO.getParentid());
        long count = count(orderWrapper);
        teachplan.setOrderby((int) count + 1);

        boolean success = save(teachplan);
        if(!success) {
            throw new DatabaseOperateException("新增教学计划失败");
        }

        return Result.ok("添加教学计划成功");
    }

    @Override
    public Result updateTeachPlan(TeachPlanDTO teachPlanDTO) {
        //参数校验
        QueryWrapper<Teachplan> teachplanQueryWrapper = new QueryWrapper<>();
        teachplanQueryWrapper.eq("id", teachPlanDTO.getId())
                .eq("course_id", teachPlanDTO.getCourseId())
                .eq("grade", teachPlanDTO.getGrade())
                .eq("parentid", teachPlanDTO.getParentid());
        Teachplan teachplan = getOne(teachplanQueryWrapper);
        if(teachplan == null)
            throw new BadRequestException("请求参数错误");

        BeanUtils.copyProperties(teachPlanDTO, teachplan);
        teachplan.setChangeDate(LocalDateTime.now());
        boolean success = updateById(teachplan);
        if(!success)
            throw new DatabaseOperateException("请求计划更新失败");

        return Result.ok("更新成功");
    }

    @Transactional
    @Override
    public Result deleteTeachPlan(Long id) {
        //参数校验
        Teachplan teachplan = getById(id);
        if(teachplan == null) {
            throw new BadRequestException("教学计划不存在");
        }

        //课程未提交校验
        QueryWrapper<CourseBase> courseBaseQueryWrapper = new QueryWrapper<>();
        courseBaseQueryWrapper.eq("id", teachplan.getCourseId())
                .eq("audit_status", COURSE_AUDIT_NOT_SUBMIT);
        CourseBase courseBase = courseBaseService.getOne(courseBaseQueryWrapper);
        if(courseBase == null) {
            throw new BadRequestException("课程不存在或课程已提交审核，无法删除教学计划");
        }

        //章节空校验
        Integer grade = teachplan.getGrade();
        if(grade == 1) {
            QueryWrapper<Teachplan> childQueryWrapper = new QueryWrapper<>();
            childQueryWrapper.eq("parentid", id);
            long count = count(childQueryWrapper);
            if(count > 0) {
                throw new BadRequestException("章节下存在子章节，无法删除");
            }
        }

        //删除教学计划
        boolean success = removeById(id);
        if(!success) {
            throw new DatabaseOperateException("删除教学计划失败");
        }

        //删除关联的教学计划媒体
        if(grade == 2){
            QueryWrapper<TeachplanMedia> teachplanMediaQueryWrapper = new QueryWrapper<>();
            teachplanMediaQueryWrapper.eq("teachplan_id", id);
            teachPlanMediaService.remove(teachplanMediaQueryWrapper);
        }

        return Result.ok("删除教学计划成功");

    }

    @Transactional
    @Override
    public Result moveUpTeachPlan(Long id) {
        //参数校验
        Teachplan teachplan = getById(id);
        if(teachplan == null) {
            throw new BadRequestException("教学计划不存在");
        }

        //获取上一个章节/小节
        QueryWrapper<Teachplan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", teachplan.getCourseId())
                .eq("grade", teachplan.getGrade())
                .eq("parentid", teachplan.getParentid())
                .lt("orderby", teachplan.getOrderby())
                .orderByDesc("orderby")
                .last("limit 1");
        Teachplan previousTeachPlan = getOne(queryWrapper);
        if(previousTeachPlan == null) {
            throw new BadRequestException("当前章节/小节已是最前面，无法上移");
        }

        //交换顺序
        return exchangeOrder(teachplan, previousTeachPlan, 0);
    }

    @Override
    public Result moveDownTeachPlan(Long id) {
        //参数校验
        Teachplan teachplan = getById(id);
        if(teachplan == null) {
            throw new BadRequestException("教学计划不存在");
        }

        //获取下一个章节/小节
        QueryWrapper<Teachplan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", teachplan.getCourseId())
                .eq("grade", teachplan.getGrade())
                .eq("parentid", teachplan.getParentid())
                .gt("orderby", teachplan.getOrderby())
                .orderByAsc("orderby")
                .last("limit 1");
        Teachplan downTeachPlan = getOne(queryWrapper);
        if(downTeachPlan == null) {
            throw new BadRequestException("当前章节/小节已是最前面，无法下移");
        }

        //交换顺序
        return exchangeOrder(teachplan, downTeachPlan, 1);
    }

    private Result exchangeOrder(Teachplan originTeachplan, Teachplan targetTeachplan, Integer type) {
        //type = 0表示上移，type = 1表示下移
        int tempOrderby = originTeachplan.getOrderby();
        originTeachplan.setOrderby(targetTeachplan.getOrderby());
        targetTeachplan.setOrderby(tempOrderby);
        boolean success = updateById(originTeachplan) && updateById(targetTeachplan);
        String msg = "";
        if(type == 0)
            msg = "上移";
        else if (type == 1) {
            msg = "下移";
        }

        if(!success) {
            throw new DatabaseOperateException(msg + "教学计划失败");
        }

        return Result.ok(msg +"成功");
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

