package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 通知分页查询DTO
 */
@Data
public class NotificationPageQueryDTO implements Serializable {

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
     * 是否已读（0-未读，1-已读）
     */
    private Integer isRead;

    /**
     * 通知类型
     */
    private String notificationType;
}

