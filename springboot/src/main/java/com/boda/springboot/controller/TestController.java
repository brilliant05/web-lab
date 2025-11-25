package com.boda.springboot.controller;

import com.boda.springboot.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 测试控制器 - 用于验证Knife4j配置
 */
@Tag(name = "测试接口", description = "用于测试API文档和接口调用")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "Hello测试", description = "测试接口是否正常访问(无需登录)")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, Knife4j!");
    }

    @Operation(summary = "带参数测试", description = "测试带参数的GET请求")
    @GetMapping("/greet")
    public Result<String> greet(
            @Parameter(description = "用户名", required = true)
            @RequestParam String name) {
        return Result.success("Hello, " + name + "!");
    }

    @Operation(summary = "需要认证的接口", description = "测试JWT Token认证(需要登录)")
    @GetMapping("/auth")
    public Result<String> authTest() {
        return Result.success("您已通过JWT认证!");
    }
}
