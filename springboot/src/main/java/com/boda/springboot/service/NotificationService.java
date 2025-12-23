package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.NotificationPageQueryDTO;

/**
 * 通知Service接口
 */
public interface NotificationService {

    /**
     * 分页查询通知列表
     */
    PageResult pageQuery(NotificationPageQueryDTO queryDTO, Long userId);

    /**
     * 获取未读通知数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 标记通知已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知已读
     */
    void markAllAsRead(Long userId);

    /**
     * 创建问题被回答的通知
     * @param studentId 学生ID
     * @param teacherId 教师ID
     * @param questionId 问题ID
     * @param questionTitle 问题标题
     */
    void createAnswerNotification(Long studentId, Long teacherId, Long questionId, String questionTitle);

    /**
     * 创建系统通知
     */
    void createSystemNotification(Long userId, String title, String content);

    /**
     * 创建新问题通知（通知教师）
     * @param teacherId 教师ID
     * @param studentId 学生ID
     * @param questionId 问题ID
     * @param questionTitle 问题标题
     * @param courseName 课程名称
     */
    void createQuestionNotification(Long teacherId, Long studentId, Long questionId, String questionTitle, String courseName);
}

