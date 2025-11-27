package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.CoursePageQueryDTO;
import com.boda.springboot.entity.Course;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 保存课程信息
     * @param course 课程信息
     */
    void saveCourse(Course course);

    /**
     * 分页查询课程列表
     * @param coursePageQueryDTO 查询条件
     * @return 分页结果
     */
    PageResult pageQuery(CoursePageQueryDTO coursePageQueryDTO);

    /**
     * 根据课程ID获取课程信息
     * @param courseId 课程ID
     * @return 课程信息
     */
    Course getCourseById(Long courseId);

    /**
     * 更新课程信息
     * @param course 课程信息
     */
    void updateCourse(Course course);
}
