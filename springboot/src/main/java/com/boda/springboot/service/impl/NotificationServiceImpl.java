package com.boda.springboot.service.impl;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.NotificationPageQueryDTO;
import com.boda.springboot.entity.Notification;
import com.boda.springboot.entity.User;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.NotificationMapper;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.NotificationService;
import com.boda.springboot.vo.NotificationVO;
import com.boda.springboot.websocket.NotificationWebSocket;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.boda.springboot.common.Constant;

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

    @Autowired
    private UserMapper userMapper;

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
    public void createAnswerNotification(Long studentId, Long teacherId, Long questionId, String questionTitle) {
        log.info("创建问题被回答通知 - 学生ID: {}, 教师ID: {}, 问题ID: {}", studentId, teacherId, questionId);

        // 查询学生信息
        User student = userMapper.selectById(studentId);
        if (student == null) {
            log.warn("学生不存在，无法创建通知 - 学生ID: {}", studentId);
            return;
        }

        // 查询教师信息
        User teacher = userMapper.selectById(teacherId);
        if (teacher == null) {
            log.warn("教师不存在，无法创建通知 - 教师ID: {}", teacherId);
            return;
        }

        // 获取学生姓名（优先使用真实姓名，否则使用用户名）
        String studentName = student.getRealName() != null && !student.getRealName().trim().isEmpty() 
            ? student.getRealName() 
            : student.getUsername();

        // 获取教师姓名（优先使用真实姓名，否则使用用户名）
        String teacherName = teacher.getRealName() != null && !teacher.getRealName().trim().isEmpty() 
            ? teacher.getRealName() 
            : teacher.getUsername();

        // 生成通知内容，格式：同学某某某 你的什么什么问题 被哪个老师回答
        String content = String.format("同学%s，你的问题「%s」已被%s回答", 
            studentName, questionTitle, teacherName);

        Notification notification = new Notification();
        notification.setUserId(studentId);
        notification.setNotificationType(Constant.NOTIFICATION_TYPE_ANSWER_REPLY);
        notification.setTitle("您的问题有新回答");
        notification.setContent(content);
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
        notification.setNotificationType(Constant.NOTIFICATION_TYPE_SYSTEM);
        notification.setTitle(title);
        notification.setContent(content);

        notificationMapper.save(notification);

        // 🚀 通过 WebSocket 实时推送通知给在线用户
        pushNotificationViaWebSocket(userId, notification);
    }

    @Override
    @Transactional
    public void createQuestionNotification(Long teacherId, Long studentId, Long questionId, String questionTitle, String courseName) {
        log.info("创建新问题通知 - 教师ID: {}, 学生ID: {}, 问题ID: {}", teacherId, studentId, questionId);

        // 查询学生信息
        User student = userMapper.selectById(studentId);
        String studentName = (student != null && student.getRealName() != null) ? student.getRealName() : "学生";

        Notification notification = new Notification();
        notification.setUserId(teacherId);
        notification.setTitle("新问题提醒");
        notification.setContent("学生 " + studentName + " 在课程《" + courseName + "》中提出了新问题：" + questionTitle);
        notification.setNotificationType(Constant.NOTIFICATION_TYPE_NEW_QUESTION);
        notification.setRelatedId(questionId);
        notification.setIsRead(0);

        notificationMapper.save(notification);

        // 发送WebSocket通知
        Map<String, Object> wsData = new HashMap<>();
        wsData.put("type", "notification");
        wsData.put("title", notification.getTitle());
        wsData.put("content", notification.getContent());
        wsData.put("relatedId", questionId);
        wsData.put("notificationId", notification.getNotificationId());

        NotificationWebSocket.sendNotification(teacherId, wsData);
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

