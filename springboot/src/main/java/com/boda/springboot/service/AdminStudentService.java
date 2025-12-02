package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.AssignCourseDTO;
import com.boda.springboot.dto.StudentPageQueryDTO;
import com.boda.springboot.entity.User; // 复用User实体，代表学生（role='STUDENT'）

public interface AdminStudentService {
    /**
     * 分页查询学生   筛选user表中role='STUDENT'的记录
     */
    PageResult pageQuery(StudentPageQueryDTO studentPageQueryDTO);

    /**
     * 获取学生详情   查询user表中role='STUDENT'的记录
     */
    User getById(Long studentId);

    /**
     * 更新学生信息   更新user表中role='STUDENT'的记录） 用于做启用禁用和删除操作
     */
    void updateStudent(User student);

    /**
     * 管理员为学生分配课程
     * @param studentId 学生ID
     * @param assignCourseDTO 分配课程信息（课程ID、教师ID）
     */
    void assignCourse(Long studentId, AssignCourseDTO assignCourseDTO);

    /**
     * 管理员移除学生课程
     * @param studentId 学生ID
     * @param courseId 课程ID
     */
    void removeCourse(Long studentId, Long courseId);
}