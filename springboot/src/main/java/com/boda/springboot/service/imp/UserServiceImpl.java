package com.boda.springboot.service.imp;

import com.boda.springboot.dto.UserInfoUpdateDTO;
import com.boda.springboot.entity.User;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public User selectById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    /**
     * 根据用户ID和角色查询用户信息
     *
     * @param userId 用户ID
     * @param role   用户角色
     * @return 用户信息
     */
    public User selectByIdAndRole(Long userId, String role) {
        User user = new User();
        //默认查询管理员信息
        user = userMapper.selectById(userId);
        if (role.equals("STUDENT"))
        {
            user = userMapper.selectStudentById(userId);
        } else if (role.equals("TEACHER"))
        {
            user = userMapper.selectTeacherById(userId);
        }
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param userId             用户ID
     * @param userInfoUpdateDTO  用户信息更新DTO
     */
    public void updateUserInfo(Long userId, UserInfoUpdateDTO userInfoUpdateDTO) {
        User user = new User();
        user.setUserId(userId);
        BeanUtils.copyProperties(userInfoUpdateDTO, user);
        userMapper.update(user);
    }
}
