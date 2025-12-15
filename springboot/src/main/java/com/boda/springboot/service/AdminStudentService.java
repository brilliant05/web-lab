package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.StudentPageQueryDTO;
import com.boda.springboot.entity.User; // 复用User实体，代表学生（role='STUDENT'）

public interface AdminStudentService {

    /**
     * 新增学生（复用 User 表，role='STUDENT'）
     * @param student 学生信息
     */
    void saveStudent(User student);

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

}