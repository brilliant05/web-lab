package com.boda.springboot.config;

import com.boda.springboot.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置 - 配置拦截器和跨域
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/auth/login",              // 登录接口不拦截
                        "/auth/register",           // 注册接口不拦截
                        "/auth/reset-password",     // 重置密码接口不拦截
                        "/test/**",                 // 测试接口不拦截(开发阶段)
                        "/error",                   // 错误页面
                        // Knife4j API文档相关路径
                        "/doc.html",                // Knife4j文档首页
                        "/swagger-ui.html",         // Swagger UI
                        "/swagger-ui/**",           // Swagger UI资源
                        "/v3/api-docs/**",          // OpenAPI 3文档
                        "/swagger-resources/**",    // Swagger资源
                        "/webjars/**",              // WebJars资源
                        "/favicon.ico"              // 网站图标
                );
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 修复:使用allowedOriginPatterns代替allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)      // 允许携带凭证
                .maxAge(3600);
    }
}
