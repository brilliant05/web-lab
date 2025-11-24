package com.boda.springboot.service.imp;

import com.boda.springboot.dto.LoginRequest;
import com.boda.springboot.entity.User;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

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
        User userInfo = userMapper.selectByUsername(req.getUsername());
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
    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
