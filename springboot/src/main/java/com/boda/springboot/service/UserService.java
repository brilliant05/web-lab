package com.boda.springboot.service;

import com.boda.springboot.dto.LoginRequest;
import com.boda.springboot.entity.User;

public interface UserService {
   /**
    * 用户登录
    * @param user
    * @return
    */
   User login(LoginRequest user);

   User selectByUsername(String username);

}
