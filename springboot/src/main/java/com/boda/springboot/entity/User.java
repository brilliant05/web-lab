package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 存储系统所有用户信息(管理员、教师、学生)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID,主键
     */
    private Long userId;

    /**
     * 用户名,唯一
     */
    private String username;

    /**
     * 密码(BCrypt加密)
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱,唯一
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 角色: ADMIN-管理员, TEACHER-教师, STUDENT-学生
     */
    private String role;

    /**
     * 账号状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 学号(仅学生有)
     */
    private String studentId;

    /**
     * 所属学院(学生)
     */
    private String college;

    /**
     * 职称(教师): 助教、讲师、副教授、教授
     */
    private String jobTitle;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer isDeleted;
}
