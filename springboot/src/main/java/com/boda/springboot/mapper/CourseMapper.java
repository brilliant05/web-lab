package com.boda.springboot.mapper;

import com.boda.springboot.dto.CoursePageQueryDTO;
import com.boda.springboot.entity.Course;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 课程 Mapper 接口
 */
@Mapper
public interface CourseMapper {

    /**
     * 根据课程编号查询课程
     * @param courseCode 课程编号
     * @return 课程信息
     */
    @Select("SELECT * FROM course WHERE course_code = #{courseCode} AND is_deleted = 0")
    Course selectByCourseCode(String courseCode);

    /**
     * 保存课程信息
     * @param course 课程信息
     */
    void save(Course course);

    /**
     * 分页查询课程列表
     * @param coursePageQueryDTO 查询条件
     * @return 分页结果
     */
    Page<Course> pageQuery(CoursePageQueryDTO coursePageQueryDTO);
    /**
     * 根据课程ID查询课程
     * @param courseId 课程ID
     * @return 课程信息
     */
    @Select("SELECT * FROM course WHERE course_id = #{courseId} AND is_deleted = 0")
    Course selectById(Long courseId);

    /**
     * 动态更新课程信息
     * @param course 课程信息
     */
    void update(Course course);
}
