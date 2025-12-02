package com.boda.springboot.controller;

import com.boda.springboot.common.Result;
import com.boda.springboot.dto.UserInfoUpdateDTO;
import com.boda.springboot.entity.User;
import com.boda.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/current")
    public Result<User> getUserInfo(HttpServletRequest request) {
        log.info("获取当前登录用户信息,id={},role={}", request.getAttribute("userId"), request.getAttribute("role"));
        Long  userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        User user = userService.selectByIdAndRole(userId,role);

        return Result.success(user);
    }
    /**
     * 更新用户信息
     * @param userInfoUpdateDTO 用户信息
     * @return 更新结果
     */
    @PutMapping("/profile")
    public Result updateUserInfo(@RequestBody UserInfoUpdateDTO userInfoUpdateDTO, HttpServletRequest request) {
        Long  userId = (Long) request.getAttribute("userId");
        log.info("更新用户信息:{}", userInfoUpdateDTO);
        userService.updateUserInfo(userId, userInfoUpdateDTO);
        return Result.success("用户信息更新成功");

    }



}
