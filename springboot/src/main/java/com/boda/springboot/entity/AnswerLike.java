package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 回答点赞实体类
 * 记录用户对回答的点赞
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerLike implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 回答ID,逻辑外键关联answer表
     */
    private Long answerId;

    /**
     * 点赞用户ID,逻辑外键关联user表
     */
    private Long userId;

    /**
     * 点赞时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
