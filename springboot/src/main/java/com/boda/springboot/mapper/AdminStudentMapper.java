package com.boda.springboot.mapper;

import com.boda.springboot.dto.StudentPageQueryDTO;
import com.boda.springboot.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminStudentMapper {

    /**
     * 更新学生信息
     * @param student 学生信息
     */
    void updateStudent(User student);

    /**
     * 分页查询学生
     * @param studentPageQueryDTO 查询条件
     * @return 分页结果
     */
    Page<User> pageQuery(StudentPageQueryDTO studentPageQueryDTO);

    /**
     * 根据ID查询学生 用于启用禁用和删除学生操作
     * @param studentId 学生ID
     * @return 学生信息
     */
    User getById(@Param("studentId") Long studentId);
}
