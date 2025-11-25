package com.boda.springboot.mapper;

import com.boda.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    /**
     * 根据邮箱查询用户信息
     * @param email
     * @return
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(String email);

    /**
     * 保存用户信息
     * @param user
     */
    void save(User user);

    /**
     * 更新用户密码
     * @param userId
     * @param newPassword
     */
    @Update("UPDATE user SET password = #{newPassword}, update_time = NOW() WHERE user_id = #{userId}")
    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
