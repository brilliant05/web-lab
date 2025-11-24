package com.boda.springboot.interceptor;

import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器 - 验证请求中的 Token
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // OPTIONS 请求直接放行(CORS 预检请求)
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取 Authorization 请求头
        String authHeader = request.getHeader("Authorization");

        // 如果没有 Token 或者格式不正确
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("未登录或登录已过期，拒绝访问: {}", request.getRequestURI());
            throw new ServiceException("401", "未登录或登录已过期，请先登录");
        }

        // 提取 Token (去掉前缀 "Bearer ")
        String token = authHeader.substring(7);

        // 验证 Token
        try {
            if (!jwtUtil.validateToken(token)) {
                log.info("Token 无效或已过期，拒绝访问: {}", request.getRequestURI());
                throw new ServiceException("401", "Token无效或已过期，请重新登录");
            }

            // 将用户信息存入请求属性,供后续使用
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("role", role);

            log.debug("Token 验证成功 - 用户: {}, 角色: {}", username, role);
            return true;
        } catch (ServiceException se) {
            // 继续抛出业务异常给全局异常处理器
            throw se;
        } catch (Exception e) {
            log.error("Token 解析失败, token={}", token, e);
            throw new ServiceException("401", "Token解析失败，请重新登录");
        }
    }
}
