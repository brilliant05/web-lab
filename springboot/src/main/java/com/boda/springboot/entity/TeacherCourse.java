package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师授课关系实体类
 * 记录教师与课程的多对多关系
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教师ID,逻辑外键关联user表
     */
    private Long teacherId;

    /**
     * 课程ID,逻辑外键关联course表
     */
    private Long courseId;

    /**
     * 该教师为该课程生成的邀请码
     */
    private String inviteCode;

    /**
     * 邀请码过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime inviteCodeExpireTime;

    /**
     * 班级名称(可选),如"2024级1班"
     */
    private String className;

    /**
     * 分配时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime assignTime;

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
}
