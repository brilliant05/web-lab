package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 提交问题DTO
 */
@Data
public class QuestionSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问题标题
     */
    private String questionTitle;

    /**
     * 问题内容
     */
    private String questionContent;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 向哪位教师提问
     */
    private Long teacherId;

    /**
     * 问题标签
     */
    private String tags;

    /**
     * 图片URLs (多个用逗号分隔)
     */
    private String imageUrls;
}
