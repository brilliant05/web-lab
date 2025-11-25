package com.boda.springboot.aspect;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.exception.NotLoginException;
import com.boda.springboot.exception.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * 权限校验切面 - 检查用户角色权限
 */
@Aspect
@Component
public class RoleCheckAspect {

    
    @Around("@annotation(com.boda.springboot.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取请求信息");
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取当前用户角色(从拦截器设置的属性中获取)
        String userRole = (String) request.getAttribute("role");
        
        if (userRole == null) {
            throw new NotLoginException("未登录或登录已过期");
        }
        
        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequireRole requireRole = signature.getMethod().getAnnotation(RequireRole.class);
        
        // 获取允许的角色列表
        String[] allowedRoles = requireRole.value();
        
        // 检查用户角色是否在允许列表中
        boolean hasPermission = Arrays.asList(allowedRoles).contains(userRole);
        
        if (!hasPermission) {
            throw new PermissionException("无访问权限");
        }
        
        // 权限校验通过,继续执行
        return joinPoint.proceed();
    }
}
