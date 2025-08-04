package edu.wust.qrz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.constant.DataDictionaryConstant;
import edu.wust.qrz.dto.content.CourseCreateDTO;
import edu.wust.qrz.dto.content.CourseQueryDTO;
import edu.wust.qrz.entity.content.CourseBase;
import edu.wust.qrz.entity.content.CourseMarket;
import edu.wust.qrz.handler.BadRequestException;
import edu.wust.qrz.mapper.CourseBaseMapper;
import edu.wust.qrz.service.CourseBaseService;
import edu.wust.qrz.service.CourseMarketService;
import edu.wust.qrz.vo.content.CourseVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static edu.wust.qrz.constant.DataDictionaryConstant.*;

@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    @Resource
    CourseMarketService courseMarketService;

    @Override
    public Result getCourseByPage(Integer pageNum, Integer pageSize, CourseQueryDTO courseQueryDTO) {
        if(pageNum == null || pageSize == null || pageNum <= 0 || pageSize <= 0) {
            return Result.fail("Invalid page number or page size");
        }

        Page<CourseBase> page = new Page<>(pageNum,pageSize);

        if(courseQueryDTO == null){
            Page<CourseBase> courseBasePage = page(page);

            return Result.ok(courseBasePage);
        }

        QueryWrapper<CourseBase> queryWrapper = getCourseBaseQueryWrapper(courseQueryDTO);

        Page<CourseBase> result = page(page, queryWrapper);

        return Result.ok(result);
    }

    @Transactional
    @Override
    public Result createCourse(Long companyId, CourseCreateDTO courseCreateDTO) {
        //校验课程收费参数
        if(!(courseCreateDTO.getCharge().equals(COURSE_CHARGE_TRUE) ||
                courseCreateDTO.getCharge().equals(COURSE_CHARGE_FALSE))){
            throw new BadRequestException("课程收费参数错误");
        }

        //校验课程等级参数
        if(!(courseCreateDTO.getGrade().equals(COURSE_GRADE_BEGINNER) ||
                courseCreateDTO.getGrade().equals(COURSE_GRADE_INTERMEDIATE) ||
                courseCreateDTO.getGrade().equals(COURSE_GRADE_ADVANCED))){
            throw new BadRequestException("课程等级参数错误");
        }

        //校验课程教学模式
        if(!(courseCreateDTO.getTeachmode().equals(COURSE_MODE_RECORD) ||
                courseCreateDTO.getTeachmode().equals(COURSE_MODE_LIVE))){
            throw new BadRequestException("课程教学模式参数错误");
        }

        //保存课程基本信息
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(courseCreateDTO, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setAuditStatus(COURSE_AUDIT_FALSE); // 默认审核状态为未提交
        courseBase.setStatus(COURSE_PUBLISH_FALSE); // 默认发布状态为未发布
        LocalDateTime now = LocalDateTime.now();
        courseBase.setCreateDate(now);
        boolean isSuccess = save(courseBase);
        if(!isSuccess)
            return Result.fail("保存课程基本信息失败");

        // 如果课程收费
        if (courseCreateDTO.getCharge().equals(COURSE_CHARGE_TRUE)) {
            //查询课程Id
            QueryWrapper<CourseBase> courseBaseQueryWrapper = new QueryWrapper<>();

            courseBaseQueryWrapper.eq("name", courseBase.getName())
                                   .eq("company_id", courseBase.getCompanyId())
                                   .eq("audit_status", courseBase.getAuditStatus())
                                   .eq("status", courseBase.getStatus())
                                    .eq("grade", courseBase.getGrade())
                                    .eq("mt", courseBase.getMt())
                                    .eq("st", courseBase.getSt());

            Long courseId = getOne(courseBaseQueryWrapper).getId();

            //保存课程营销信息
            CourseMarket courseMarket = new CourseMarket();
            BeanUtils.copyProperties(courseCreateDTO, courseMarket);
            courseMarket.setId(courseId);//确保课程营销信息的ID与课程基本信息的ID一致
            boolean success = courseMarketService.save(courseMarket);
            if(!success)
                return Result.fail("保存课程营销信息失败");
        }

        return Result.ok();
    }

    @Override
    public Result getCourseById(Long courseId) {
        if (courseId == null || courseId <= 0) {
            return Result.fail("无效ID");
        }

        CourseVO courseVO = new CourseVO();

        //查询CourseBase
        CourseBase courseBase = getById(courseId);

        if (courseBase == null) {
            return Result.fail("课程不存在");
        }

        BeanUtils.copyProperties(courseBase, courseVO);

        //查询CourseMarket
        CourseMarket courseMarket = courseMarketService.getById(courseId);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseVO);
        }else {
            // 如果没有课程营销信息，设置默认值
            courseVO.setCharge("201000"); // 默认免费
            courseVO.setPrice(BigDecimal.ZERO);
            courseVO.setOriginalPrice(BigDecimal.ZERO);
            courseVO.setValidDays(0);
        }

        return Result.ok(courseVO);
    }




    private QueryWrapper<CourseBase> getCourseBaseQueryWrapper(CourseQueryDTO courseQueryDTO) {
        QueryWrapper<CourseBase> queryWrapper = new QueryWrapper<>();

        if (courseQueryDTO.getCourseName() != null && !courseQueryDTO.getCourseName().isEmpty()) {
            queryWrapper.like("name", courseQueryDTO.getCourseName());
        }

        if (courseQueryDTO.getAuditStatus() != null && !courseQueryDTO.getAuditStatus().isEmpty()) {
            queryWrapper.eq("audit_status", courseQueryDTO.getAuditStatus());
        }

        if (courseQueryDTO.getPublishStatus() != null && !courseQueryDTO.getPublishStatus().isEmpty()) {
            queryWrapper.eq("status", courseQueryDTO.getPublishStatus());
        }
        return queryWrapper;
    }
}
