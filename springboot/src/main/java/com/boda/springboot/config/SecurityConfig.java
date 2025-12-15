package com.boda.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 使用 JWT,禁用 Session
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                // 禁用 CSRF(使用 JWT 不需要)
                .csrf(csrf -> csrf.disable())
                // 禁用 Session(使用 JWT 不需要 Session)
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置请求授权(全部放行,由我们自己的拦截器处理)
                .authorizeHttpRequests(auth -> 
                        auth.anyRequest().permitAll())
                // 禁用默认登录页
                .formLogin(form -> form.disable())
                // 禁用 HTTP Basic
                .httpBasic(basic -> basic.disable());
        
        return http.build();
    }
}
