package com.boda.springboot.service.imp;

import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.TeacherPageQueryDTO;
import com.boda.springboot.entity.User;
import com.boda.springboot.mapper.AdminTeacherMapper;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.AdminTeacherService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminTeacherServiceImpl implements AdminTeacherService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AdminTeacherMapper adminTeacherMapper;
    @Autowired
    UserMapper userMapper;
    /**
     * 保存教师信息
     *
     * @param teacher 教师信息
     */
    public void saveTeacher(User teacher) {
        if(userMapper.selectByUsername(teacher.getUsername()) != null){
            throw new RuntimeException("用户名已存在");
        }
        teacher.setRole(Constant.ROLE_TEACHER);
        teacher.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        userMapper.save(teacher);
    }

    /**
     * 更新教师信息
     *
     * @param teacher 教师信息
     */
    @Override
    public void updateTeacher(User teacher) {
        if (teacher.getUserId() == null) {
            throw new RuntimeException("添加失败");
        }

        // 如果有新密码，进行加密
        if (teacher.getPassword() != null && !teacher.getPassword().trim().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        }

        // 调用 Mapper 更新
        userMapper.update(teacher);
    }

    /**
     * 分页查询教师
     *
     * @param teacherPageQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(TeacherPageQueryDTO teacherPageQueryDTO) {
        // 1. 开启分页（PageHelper 会自动拦截下一条 SQL 查询）
        PageHelper.startPage(teacherPageQueryDTO.getPageNum(), teacherPageQueryDTO.getPageSize());

        // 2. 执行查询（PageHelper 会自动分页）
        Page<User> page = adminTeacherMapper.pageQuery(teacherPageQueryDTO);

        // 3. 封装分页结果
        return new PageResult(page.getTotal(), page.getResult());
    }
}
