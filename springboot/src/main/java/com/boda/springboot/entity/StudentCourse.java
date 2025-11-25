package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生选课关系实体类
 * 记录学生与课程的多对多关系，明确学生跟随哪个教师上课
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 学生ID,逻辑外键关联user表
     */
    private Long studentId;

    /**
     * 课程ID,逻辑外键关联course表
     */
    private Long courseId;

    /**
     * 授课教师ID,逻辑外键关联user表
     */
    private Long teacherId;

    /**
     * 加入方式: ADMIN_ASSIGN-管理员分配, INVITE_CODE-邀请码加入
     */
    private String joinMethod;

    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime joinTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
