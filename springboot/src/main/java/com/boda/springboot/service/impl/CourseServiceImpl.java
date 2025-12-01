package com.boda.springboot.service.impl;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.CoursePageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.CourseMapper;
import com.boda.springboot.service.CourseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 保存课程信息
     * @param course 课程信息
     */
    @Override
    public void saveCourse(Course course) {
        Course existCourse = courseMapper.selectByCourseCode(course.getCourseCode());
        if (existCourse != null) {
            throw new ServiceException("400", "课程编号已存在");
        }
        courseMapper.save(course);
    }

    /**
     * 分页查询课程列表
     * @param coursePageQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(CoursePageQueryDTO coursePageQueryDTO) {
        // 1. 开启分页（PageHelper 会自动拦截下一条 SQL 查询）
        PageHelper.startPage(coursePageQueryDTO.getPageNum(), coursePageQueryDTO.getPageSize());

        // 2. 执行查询（PageHelper 会自动分页）
        Page<Course> page = courseMapper.pageQuery(coursePageQueryDTO);

        // 3. 封装分页结果
        return new PageResult(page.getTotal(), page.getResult());
    }
    /**
     * 根据课程ID查询课程信息
     * @param courseId 课程ID
     * @return 课程信息
     */
    @Override
    public Course getCourseById(Long courseId) {
        if (courseId == null) {
            throw new ServiceException("400", "课程ID不能为空");
        }

        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("404", "课程不存在");
        }

        return course;
    }

    /**
     * 动态更新课程信息
     * @param course 课程信息
     */
    @Override
    public void updateCourse(Course course) {
        // 1. 校验课程ID
        if (course.getCourseId() == null) {
            throw new ServiceException("400", "课程ID不能为空");
        }

        // 2. 检查课程是否存在
        Course existCourse = courseMapper.selectById(course.getCourseId());
        if (existCourse == null) {
            throw new ServiceException("404", "课程不存在");
        }

        // 3. 如果要更新课程编号，检查新编号是否已被其他课程使用
        if (course.getCourseCode() != null && !course.getCourseCode().equals(existCourse.getCourseCode())) {
            Course checkCourse = courseMapper.selectByCourseCode(course.getCourseCode());
            if (checkCourse != null && !checkCourse.getCourseId().equals(course.getCourseId())) {
                throw new ServiceException("400", "课程编号已被其他课程使用");
            }
        }

        // 4. 执行动态更新
        courseMapper.update(course);
    }
}
