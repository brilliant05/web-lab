package com.boda.springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生课程关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 授课教师ID
     */
    private Long teacherId;

    /**
     * 加入方式: ADMIN_ASSIGN-管理员分配, INVITE_CODE-邀请码加入
     */
    private String joinMethod;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

