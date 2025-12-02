package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 提交回答DTO
 */
@Data
public class AnswerSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回答内容
     */
    private String answerContent;

    /**
     * 图片URLs（多个用逗号分隔）
     */
    private String imageUrls;
}

