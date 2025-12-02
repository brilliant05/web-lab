package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.common.Result;
import com.boda.springboot.dto.NotificationPageQueryDTO;
import com.boda.springboot.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知管理 Controller
 */
@RestController
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取通知列表（分页）
     * GET /notifications?pageNum=1&pageSize=10&isRead=0
     *
     * 权限：所有登录用户
     */
    @GetMapping
    public Result<PageResult> getNotificationList(NotificationPageQueryDTO queryDTO, HttpServletRequest request) {
        log.info("接收到通知列表查询请求 - 查询条件: {}", queryDTO);

        Long userId = (Long) request.getAttribute("userId");
        PageResult pageResult = notificationService.pageQuery(queryDTO, userId);

        return Result.success(pageResult);
    }

    /**
     * 获取未读通知数量
     * GET /notifications/unread-count
     *
     * 权限：所有登录用户
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount(HttpServletRequest request) {
        log.info("接收到获取未读通知数量请求");

        Long userId = (Long) request.getAttribute("userId");
        Integer count = notificationService.getUnreadCount(userId);

        Map<String, Integer> result = new HashMap<>();
        result.put("unreadCount", count);

        return Result.success(result);
    }

    /**
     * 标记通知为已读
     * PUT /notifications/1/read
     *
     * 权限：所有登录用户
     */
    @PutMapping("/{notificationId}/read")
    public Result<Void> markAsRead(@PathVariable Long notificationId, HttpServletRequest request) {
        log.info("接收到标记通知已读请求 - 通知ID: {}", notificationId);

        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAsRead(notificationId, userId);

        return Result.success("通知已标记为已读");
    }

    /**
     * 标记所有通知为已读
     * PUT /notifications/read-all
     *
     * 权限：所有登录用户
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        log.info("接收到标记所有通知已读请求");

        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllAsRead(userId);

        return Result.success("所有通知已标记为已读");
    }

    /**
     * 创建系统通知（仅管理员）
     * POST /notifications/system
     * Content-Type: application/json
     *
     * Body:
     * {
     *   "userId": 1,        // 接收通知的用户ID，null表示全体用户
     *   "title": "系统通知标题",
     *   "content": "系统通知内容"
     * }
     */
    @PostMapping("/system")
    @RequireRole(Constant.ROLE_ADMIN)
    public Result<Void> createSystemNotification(@RequestBody Map<String, Object> params) {
        log.info("接收到创建系统通知请求 - 参数: {}", params);

        Long userId = params.get("userId") != null ?
            Long.valueOf(params.get("userId").toString()) : null;
        String title = (String) params.get("title");
        String content = (String) params.get("content");

        // 参数校验
        if (title == null || title.trim().isEmpty()) {
            return Result.error("通知标题不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            return Result.error("通知内容不能为空");
        }

        notificationService.createSystemNotification(userId, title, content);

        return Result.success("系统通知创建成功");
    }
}

