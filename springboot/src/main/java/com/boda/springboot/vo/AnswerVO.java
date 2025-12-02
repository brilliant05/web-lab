package com.boda.springboot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 回答VO
 * 用于回答展示
 */
@Data
public class AnswerVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回答ID
     */
    private Long answerId;

    /**
     * 问题ID
     */
    private Long questionId;

    /**
     * 回答教师ID
     */
    private Long teacherId;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 教师头像
     */
    private String teacherAvatar;

    /**
     * 教师职称
     */
    private String jobTitle;

    /**
     * 回答内容
     */
    private String answerContent;

    /**
     * 图片URLs
     */
    private String imageUrls;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回答时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;
}

