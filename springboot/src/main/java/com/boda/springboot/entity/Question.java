package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问题实体类
 * 存储学生向具体教师提问的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问题ID,主键
     */
    private Long questionId;

    /**
     * 所属课程ID,逻辑外键关联course表
     */
    private Long courseId;

    /**
     * 提问学生ID,逻辑外键关联user表
     */
    private Long studentId;

    /**
     * 被提问的教师ID,逻辑外键关联user表
     */
    private Long teacherId;

    /**
     * 问题标题
     */
    private String questionTitle;

    /**
     * 问题内容
     */
    private String questionContent;

    /**
     * 图片附件URL,多个用逗号分隔
     */
    private String imageUrls;

    /**
     * 问题标签,逗号分隔
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
     * 是否已回答: 0-未回答, 1-已回答
     */
    private Integer isAnswered;

    /**
     * 状态: 0-已关闭, 1-正常
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

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer isDeleted;
}
