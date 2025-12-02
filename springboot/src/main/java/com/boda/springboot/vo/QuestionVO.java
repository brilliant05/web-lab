package com.boda.springboot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问题列表VO
 * 用于问题列表展示
 */
@Data
public class QuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问题ID
     */
    private Long questionId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 提问学生ID
     */
    private Long studentId;

    /**
     * 提问学生姓名
     */
    private String studentName;

    /**
     * 被提问教师ID
     */
    private Long teacherId;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 问题标题
     */
    private String questionTitle;

    /**
     * 问题内容
     */
    private String questionContent;

    /**
     * 图片URLs
     */
    private String imageUrls;

    /**
     * 问题标签
     */
    private String tags;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 回答数量
     */
    private Integer answerCount;

    /**
     * 是否已回答
     */
    private Integer isAnswered;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 提问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

