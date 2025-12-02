package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新问题DTO
 */
@Data
public class QuestionUpdateDTO implements Serializable {

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
     * 问题标签
     */
    private String tags;
}
