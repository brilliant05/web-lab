package com.boda.springboot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程展示 VO
 * 用于教师课程列表查询
 */
@Data
public class CourseVO {
    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 开课学院
     */
    private String college;

    /**
     * 课程封面图片URL
     */
    private String coverImage;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 课程状态: 0-关闭, 1-开放
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 学生数量
     */
    private Long studentCount;
}

