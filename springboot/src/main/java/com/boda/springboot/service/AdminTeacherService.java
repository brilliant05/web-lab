package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.TeacherPageQueryDTO;
import com.boda.springboot.entity.User;

import java.util.List;

public interface AdminTeacherService {

    /**
     * 添加教师
     * @param teacher 教师信息
     * @param courseIds 要分配的课程ID列表（可选）
     */
    void saveTeacher(User teacher, List<Long> courseIds);

    /**
     * 更新教师信息
     * @param teacher 教师信息
     */
    void updateTeacher(User teacher);

    /**
     * 分页查询教师
     * @param teacherPageQueryDTO 查询条件
     * @return 分页结果
     */
    PageResult pageQuery(TeacherPageQueryDTO teacherPageQueryDTO);
}
