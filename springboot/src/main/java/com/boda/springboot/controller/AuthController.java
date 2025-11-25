package com.boda.springboot.controller;

import com.boda.springboot.common.Result;
import com.boda.springboot.dto.LoginData;
import com.boda.springboot.dto.LoginRequest;
import com.boda.springboot.dto.UpdatePassword;
import com.boda.springboot.entity.User;
import com.boda.springboot.service.UserService;
import com.boda.springboot.utils.JwtUtil;
import com.boda.springboot.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController()
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;

    /**
     * 登录
     * @param req
     * @return
     */
    @PostMapping("/login")
    public Result<LoginData> login(@RequestBody LoginRequest req) {
        log.info("登录请求: username={}", req.getUsername());
        // 不对前端传来的明文密码进行二次加密，这会导致校验失败
        User userInfo = userService.login(req);
        // 生成 JWT Token（包含用户ID、用户名、角色）
        String token = jwtUtil.generateToken(userInfo.getUserId(), userInfo.getUsername(), userInfo.getRole());
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userInfo, userVo);
        LoginData data = new LoginData(token, userVo);
        return Result.success("登录成功", data);
    }

    /**
     * 学生注册
     * @param user 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        log.info("用户注册: {}", user.getUsername());

        // 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }

        userService.saveStudent(user);
        return Result.success("注册成功");
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping("/logout")
    public Result logout(){
        log.info("用户退出登录");
        return Result.success("退出成功");
    }

    /**
     * 修改密码
     * 注意：此接口需要登录认证，JWT 拦截器会自动验证 Token 并注入用户信息
     *
     * @param updatePassword 修改密码请求参数
     * @param request HttpServletRequest，用于获取拦截器注入的用户信息
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result updatePassword(
        @RequestBody UpdatePassword updatePassword, HttpServletRequest request) {
        // 从 JWT 拦截器注入的 request 属性中获取当前登录用户信息
        String username = (String) request.getAttribute("username");
        Long userId = (Long) request.getAttribute("userId");

        log.info("用户 {} (ID:{}) 修改密码", username, userId);

        // 参数校验
        if (updatePassword.getOldPassword() == null || updatePassword.getOldPassword().trim().isEmpty()) {
            return Result.error("原密码不能为空");
        }
        if (updatePassword.getNewPassword() == null || updatePassword.getNewPassword().trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        if (updatePassword.getNewPassword().length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }

        // 验证新密码和确认密码是否一致（如果有 confirmPassword 字段）
        if (updatePassword.getConfirmPassword() != null &&
            !updatePassword.getNewPassword().equals(updatePassword.getConfirmPassword())) {
            return Result.error("两次输入的新密码不一致");
        }

        // 设置用户名并调用服务层修改密码
        updatePassword.setUsername(username);
        userService.updatePassword(updatePassword);
        return Result.success("密码修改成功");
    }

    /**
     * 重置密码
     * @param updatePassword
     * @param request
     * @return
     */
    @PutMapping("/reset-password")
    public Result resetPassword(@RequestBody UpdatePassword updatePassword, HttpServletRequest request) {
        // 从 JWT 拦截器注入的 request 属性中获取当前登录用户信息
        String username = (String) request.getAttribute("username");
        Long userId = (Long) request.getAttribute("userId");
        log.info("用户 {} (ID:{}) 重置密码", username, userId);
        // 参数校验
        if (updatePassword.getNewPassword() == null || updatePassword.getNewPassword().trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        if (updatePassword.getNewPassword().length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        // 验证新密码和确认密码是否一致（如果有 confirmPassword 字段）
        if (updatePassword.getConfirmPassword() != null &&
            !updatePassword.getNewPassword().equals(updatePassword.getConfirmPassword())) {
            return Result.error("两次输入的新密码不一致");
        }
        // 设置用户名并调用服务层修改密码
        updatePassword.setUsername(username);
        userService.resetPassword(updatePassword);
        return Result.success("密码重置成功");
    }
}
