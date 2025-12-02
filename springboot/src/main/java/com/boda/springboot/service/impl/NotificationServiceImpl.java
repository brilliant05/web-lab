package com.boda.springboot.service.impl;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.NotificationPageQueryDTO;
import com.boda.springboot.entity.Notification;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.NotificationMapper;
import com.boda.springboot.service.NotificationService;
import com.boda.springboot.vo.NotificationVO;
import com.boda.springboot.websocket.NotificationWebSocket;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知Service实现类
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public PageResult pageQuery(NotificationPageQueryDTO queryDTO, Long userId) {
        log.info("分页查询通知列表 - 用户ID: {}, 查询条件: {}", userId, queryDTO);

        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<NotificationVO> page = (Page<NotificationVO>) notificationMapper.selectPageList(
                userId,
                queryDTO.getIsRead(),
                queryDTO.getNotificationType()
        );

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        log.info("查询未读通知数量 - 用户ID: {}", userId);
        return notificationMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.info("标记通知已读 - 通知ID: {}, 用户ID: {}", notificationId, userId);

        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new ServiceException("通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此通知");
        }

        notificationMapper.markAsRead(notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("标记所有通知已读 - 用户ID: {}", userId);
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void createAnswerNotification(Long studentId, Long questionId, String questionTitle) {
        log.info("创建问题被回答通知 - 学生ID: {}, 问题ID: {}", studentId, questionId);

        Notification notification = new Notification();
        notification.setUserId(studentId);
        notification.setNotificationType("ANSWER_REPLY");
        notification.setTitle("您的问题有新回答");
        notification.setContent("您提问的《" + questionTitle + "》已被教师回答，点击查看详情");
        notification.setRelatedId(questionId);
        notification.setRelatedType("QUESTION");

        notificationMapper.save(notification);

        // 🚀 通过 WebSocket 实时推送通知给在线用户
        pushNotificationViaWebSocket(studentId, notification);
    }

    @Override
    @Transactional
    public void createSystemNotification(Long userId, String title, String content) {
        log.info("创建系统通知 - 用户ID: {}, 标题: {}", userId, title);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setNotificationType("SYSTEM");
        notification.setTitle(title);
        notification.setContent(content);

        notificationMapper.save(notification);

        // 🚀 通过 WebSocket 实时推送通知给在线用户
        pushNotificationViaWebSocket(userId, notification);
    }

    /**
     * 通过 WebSocket 推送通知
     */
    private void pushNotificationViaWebSocket(Long userId, Notification notification) {
        try {
            // 检查用户是否在线
            if (NotificationWebSocket.isUserOnline(userId)) {
                Map<String, Object> message = new HashMap<>();
                message.put("type", "notification");
                message.put("notificationType", notification.getNotificationType());
                message.put("title", notification.getTitle());
                message.put("content", notification.getContent());
                message.put("relatedId", notification.getRelatedId());
                message.put("relatedType", notification.getRelatedType());
                message.put("timestamp", System.currentTimeMillis());

                // 发送 WebSocket 消息
                NotificationWebSocket.sendNotification(userId, message);
                log.info("WebSocket 通知推送成功 - 用户ID: {}", userId);
            } else {
                log.debug("用户 {} 不在线，WebSocket 通知未推送", userId);
            }
        } catch (Exception e) {
            log.error("WebSocket 通知推送失败 - 用户ID: {}", userId, e);
            // WebSocket 推送失败不影响主业务
        }
    }
}

