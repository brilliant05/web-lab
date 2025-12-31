package com.boda.springboot.service.impl;

import com.boda.springboot.common.Constant;
import com.boda.springboot.dto.LoginRequest;
import com.boda.springboot.dto.UpdatePassword;
import com.boda.springboot.entity.User;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     *
     * @param req 登录请求
     * @return 已认证用户信息
     */
    public User login(LoginRequest req) {
        String username = req.getUsername();
        String studentId = req.getStudentId();
        User userInfo = null;
        // 根据用户名或学号查询用户
        if(username != null){
            userInfo = userMapper.selectByUsername(req.getUsername());
        }

        if(userInfo == null && studentId != null){
            userInfo = userMapper.selectByStudentId(req.getStudentId());
        }
        if (userInfo == null) {
            throw new ServiceException("401", "用户不存在");
        }
        // 使用 BCrypt 校验密码（数据库中存的是加密串）

        if (!passwordEncoder.matches(req.getPassword(), userInfo.getPassword())) {
            throw new ServiceException("401", "用户名或密码错误");
        }
        // 可以根据状态等做额外校验
        if (userInfo.getStatus() != null && userInfo.getStatus() == 0) {
            throw new ServiceException("403", "账号已被禁用");
        }
        return userInfo;
    }


    @Override
    public void saveStudent(User user) {
        // 检查用户名是否已存在（包括已删除的）
        User existingUser = userMapper.selectByUsernameIncludeDeleted(user.getUsername());
        
        if (existingUser != null) {
            // 如果用户已存在但被逻辑删除，则恢复该用户
            if (existingUser.getIsDeleted() != null && existingUser.getIsDeleted() == 1) {
                // 恢复用户：更新密码、学号等信息，并恢复删除标记
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                existingUser.setStudentId(user.getStudentId());
                existingUser.setIsDeleted(0); // 恢复删除标记
                existingUser.setStatus(1); // 设置为启用状态
                userMapper.update(existingUser);
                return;
            } else {
                // 用户存在且未删除，抛出错误
                throw new ServiceException("400", "用户名已存在");
            }
        }
        
        // 新用户注册
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置角色为学生
        user.setRole(Constant.ROLE_STUDENT);
        // 设置默认状态
        user.setStatus(1);
        user.setIsDeleted(0);

        userMapper.save(user);
    }

    @Override
    public void updatePassword(UpdatePassword updatePassword) {
        // 1. 查询用户是否存在
        User user = userMapper.selectByUsername(updatePassword.getUsername());
        if (user == null) {
            throw new ServiceException("404", "用户不存在");
        }

        // 2. 验证原密码是否正确
        if (!passwordEncoder.matches(updatePassword.getOldPassword(), user.getPassword())) {
            throw new ServiceException("400", "原密码错误");
        }

        // 3. 加密新密码
        String newEncodedPassword = passwordEncoder.encode(updatePassword.getNewPassword());

        // 4. 更新密码
        userMapper.updatePassword(user.getUserId(), newEncodedPassword);
    }

    @Override
    public boolean existsByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        return user != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        User user = userMapper.selectByEmail(email);
        return user != null;
    }

    /**
     * 重置密码
     * @param updatePassword
     */
    @Override
    public void resetPassword(UpdatePassword updatePassword) {
        // 1. 查询用户是否存在
        User user = userMapper.selectByUsername(updatePassword.getUsername());
        if (user == null) {
            throw new ServiceException("404", "用户不存在");
        }
        // 验证是否和原密码相同
        if (passwordEncoder.matches(updatePassword.getNewPassword(), user.getPassword())) {
            throw new ServiceException("400", "新密码不能与原密码相同");
        }
        // 2. 加密新密码
        String newEncodedPassword = passwordEncoder.encode(updatePassword.getNewPassword());

        // 3. 更新密码
        userMapper.updatePassword(user.getUserId(), newEncodedPassword);
    }
}
