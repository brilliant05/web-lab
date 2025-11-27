package com.boda.springboot.service;

import com.boda.springboot.dto.UserInfoUpdateDTO;
import com.boda.springboot.entity.User;

public interface UserService {
    /**
     * 根据用户ID查询用户信息
     * @param userId
     * @return
     */
    User selectById(Long userId);

    /**
     * 根据用户ID和角色查询用户信息
     * @param userId
     * @param role
     * @return
     */
    User selectByIdAndRole(Long userId, String role);
    /**
     * 更新用户信息
     * @param userId
     * @param userInfoUpdateDTO
     */
    void updateUserInfo(Long userId, UserInfoUpdateDTO userInfoUpdateDTO);
}
