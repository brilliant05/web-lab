package com.boda.springboot.service.impl;

import com.boda.springboot.mapper.CourseMapper;
import com.boda.springboot.mapper.ResourceMapper;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.StatisticsService;
import com.boda.springboot.vo.StatisticsOverviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public StatisticsOverviewVO getOverview() {
        StatisticsOverviewVO overview = new StatisticsOverviewVO();

        // 统计学生数量
        Integer studentCount = userMapper.countStudents();
        overview.setTotalStudents(studentCount != null ? Long.valueOf(studentCount) : 0L);

        // 统计教师数量
        Integer teacherCount = userMapper.countTeachers();
        overview.setTotalTeachers(teacherCount != null ? Long.valueOf(teacherCount) : 0L);

        // 计算用户总数
        overview.setTotalUsers(overview.getTotalStudents() + overview.getTotalTeachers());

        // 统计课程数量
        Integer courseCount = courseMapper.countCourses();
        overview.setTotalCourses(courseCount != null ? Long.valueOf(courseCount) : 0L);

        // 统计资源数量
        Integer resourceCount = resourceMapper.countResources();
        overview.setTotalResources(resourceCount != null ? Long.valueOf(resourceCount) : 0L);

        // 问题总数和回答总数暂时设为0，如果后续需要可以添加对应的Mapper方法
        overview.setTotalQuestions(0L);
        overview.setTotalAnswers(0L);

        return overview;
    }
}

