package com.boda.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * SPA 路由兜底转发，将前端路由交给 index.html 处理
 */
@Controller
public class ForwardController {

    /**
     * SPA 路由兜底转发（排除带.的静态资源和后端接口前缀）
     * 注意：需要确保构建后的前端 index.html 位于 classpath:/static/index.html
     */
    @RequestMapping(
            method = {RequestMethod.GET, RequestMethod.HEAD},
            value = {
            "/",
            "/login",
            "/register",
            "/admin/**",
            "/student/**",
            "/teacher/**"
            }
    )
    public String forwardToIndex() {
        // 如果 dist 下有 index.html，请保持 dist 目录；否则把 index.html 平铺到 static 根
        return "forward:/dist/index.html";
    }
}


