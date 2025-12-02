package com.boda.springboot.mapper;

import com.boda.springboot.entity.TeacherCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 教师课程关联 Mapper
 */
@Mapper
public interface TeacherCourseMapper {

    /**
     * 添加教师课程关联
     */
    void save(TeacherCourse teacherCourse);

    /**
     * 删除教师课程关联
     */
    void delete(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);

    /**
     * 根据课程ID删除所有关联
     */
    void deleteByCourseId(Long courseId);

    /**
     * 检查教师是否已关联课程
     */
    TeacherCourse selectByTeacherAndCourse(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);

    /**
     * 统计课程关联的教师数量
     */
    Integer countByCourseId(Long courseId);
}

