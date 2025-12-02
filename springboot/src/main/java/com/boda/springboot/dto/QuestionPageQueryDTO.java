package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 问题分页查询DTO
 */
@Data
public class QuestionPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 课程ID（筛选条件）
     */
    private Long courseId;

    /**
     * 教师ID（筛选条件）
     */
    private Long teacherId;

    /**
     * 是否已回答（0-未回答，1-已回答）
     */
    private Integer isAnswered;

    /**
     * 关键词（搜索标题、内容）
     */
    private String keyword;
}

