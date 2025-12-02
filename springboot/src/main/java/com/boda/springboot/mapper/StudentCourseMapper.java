package com.boda.springboot.mapper;

import com.boda.springboot.entity.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 学生课程关联 Mapper
 */
@Mapper
public interface StudentCourseMapper {

    /**
     * 添加学生课程关联
     */
    void save(StudentCourse studentCourse);

    /**
     * 删除学生课程关联
     */
    void delete(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据课程ID删除所有关联
     */
    void deleteByCourseId(Long courseId);

    /**
     * 检查学生是否已加入课程
     */
    StudentCourse selectByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 统计课程关联的学生数量
     */
    Integer countByCourseId(Long courseId);
}

