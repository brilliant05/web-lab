package com.boda.springboot.service.imp;

import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.StudentPageQueryDTO;
import com.boda.springboot.entity.User;
import com.boda.springboot.mapper.AdminStudentMapper;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.AdminStudentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminStudentServiceImpl implements AdminStudentService {

    @Autowired
    private AdminStudentMapper adminStudentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入密码加密器

    /**
     *保存学生信息
     */
    public void saveStudent(User student) {
        // 校验用户名是否重复
        if (userMapper.selectByUsername(student.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 设置学生角色+默认密码
        student.setRole(Constant.ROLE_STUDENT);
        student.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        userMapper.save(student); // 复用UserMapper的save方法
    }

    /**
     * 分页查询学生
     */
    @Override
    public PageResult pageQuery(StudentPageQueryDTO studentPageQueryDTO) {
        log.info("分页查询学生，条件：{}", studentPageQueryDTO);
        PageHelper.startPage(studentPageQueryDTO.getPageNum(), studentPageQueryDTO.getPageSize());
        Page<User> page = adminStudentMapper.pageQuery(studentPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 获取学生详情
     */
    @Override
    public User getById(Long studentId) {
        log.info("查询学生详情，ID：{}", studentId);
        // 先校验ID是否为空
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        User student = adminStudentMapper.getById(studentId);
        if (student != null && !Constant.ROLE_STUDENT.equals(student.getRole())) {
            throw new IllegalArgumentException("该用户不是学生");
        }
        return student;
    }

    /**
     * 更新学生信息
     */
    @Override
    public void updateStudent(User student) {
        log.info("更新学生信息：{}", student);
        if (student.getUserId() == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        // 密码加密
        if (student.getPassword() != null && !student.getPassword().trim().isEmpty()) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        // 确保角色为学生
        student.setRole(Constant.ROLE_STUDENT);
        // 调用Mapper更新
        adminStudentMapper.updateStudent(student);
    }
}