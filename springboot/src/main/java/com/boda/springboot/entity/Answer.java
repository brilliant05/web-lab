package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 回答实体类
 * 存储教师回答信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回答ID,主键
     */
    private Long answerId;

    /**
     * 问题ID,逻辑外键关联question表
     */
    private Long questionId;

    /**
     * 回答教师ID,逻辑外键关联user表
     */
    private Long teacherId;

    /**
     * 回答内容
     */
    private String answerContent;

    /**
     * 图片附件URL,多个用逗号分隔
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
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer isDeleted;
}
