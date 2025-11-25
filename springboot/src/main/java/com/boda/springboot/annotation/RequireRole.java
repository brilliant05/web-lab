package com.boda.springboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解 - 标记需要特定角色才能访问的方法
 * 使用示例:
 * @RequireRole({"ADMIN", "TEACHER"})  // 管理员或教师可访问
 * @RequireRole("STUDENT")             // 仅学生可访问
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();  // 允许的角色列表
}
