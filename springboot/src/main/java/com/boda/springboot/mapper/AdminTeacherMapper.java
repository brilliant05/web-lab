package com.boda.springboot.mapper;

import com.boda.springboot.dto.TeacherPageQueryDTO;
import com.boda.springboot.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminTeacherMapper {

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
    Page<User> pageQuery(TeacherPageQueryDTO teacherPageQueryDTO);
}
