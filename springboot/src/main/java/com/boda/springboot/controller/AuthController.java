package com.boda.springboot.controller;

import com.boda.springboot.common.Result;
import com.boda.springboot.dto.LoginData;
import com.boda.springboot.dto.LoginRequest;
import com.boda.springboot.entity.User;
import com.boda.springboot.service.UserService;
import com.boda.springboot.utils.JwtUtil;
import com.boda.springboot.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result<LoginData> login(@RequestBody LoginRequest req) {
        log.info("登录请求: username={}", req.getUsername());
        // 不要对前端传来的明文密码进行二次加密，这会导致校验失败
        User userInfo = userService.login(req);
        // 生成 JWT Token（包含用户ID、用户名、角色）
        String token = jwtUtil.generateToken(userInfo.getUserId(), userInfo.getUsername(), userInfo.getRole());
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userInfo, userVo);
        LoginData data = new LoginData(token, userVo);
        return Result.success("登录成功", data);
    }

}
