package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知实体类
 * 存储系统通知信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID,主键
     */
    private Long notificationId;

    /**
     * 接收通知的用户ID,逻辑外键关联user表
     */
    private Long userId;

    /**
     * 通知类型: ANSWER_REPLY-问题被回答, RESOURCE_AUDIT-资源审核, SYSTEM-系统公告
     */
    private String notificationType;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 关联ID(如问题ID、资源ID)
     */
    private Long relatedId;

    /**
     * 关联类型: QUESTION, RESOURCE, COURSE
     */
    private String relatedType;

    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 阅读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readTime;
}
