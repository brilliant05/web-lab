package com.boda.springboot.service;

import com.boda.springboot.dto.LoginRequest;
import com.boda.springboot.dto.UpdatePassword;
import com.boda.springboot.entity.User;

public interface UserService {
   /**
    * 用户登录
    * @param user
    * @return
    */
   User login(LoginRequest user);

   /**
    * 用户注册信息保存
    * @param user
    */
   void saveStudent(User user);

   /**
    * 修改密码
    * @param updatePassword
    */
   void updatePassword(UpdatePassword updatePassword);

   /**
    * 检查用户名是否存在
    * @param username
    * @return
    */
   boolean existsByUsername(String username);

   /**
    * 检查邮箱是否存在
    * @param email
    * @return
    */
   boolean existsByEmail(String email);
   /**
    * 重置密码
    * @param updatePassword
    */
   void resetPassword(UpdatePassword updatePassword);
}
