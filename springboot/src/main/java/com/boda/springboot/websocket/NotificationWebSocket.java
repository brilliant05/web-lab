package com.boda.springboot.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 服务端点
 * 用于实时推送通知给前端
 */
@ServerEndpoint("/ws/notification/{userId}")
@Component
@Slf4j
public class NotificationWebSocket {

    /**
     * 存储所有在线用户的 WebSocket 会话
     * Key: userId, Value: Session
     */
    private static final Map<Long, Session> ONLINE_USERS = new ConcurrentHashMap<>();

    /**
     * JSON 转换工具
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        ONLINE_USERS.put(userId, session);
        log.info("WebSocket 连接建立 - 用户ID: {}, 当前在线人数: {}", userId, ONLINE_USERS.size());

        // 发送连接成功消息
        sendMessage(userId, createMessage("connection", "WebSocket 连接成功"));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        ONLINE_USERS.remove(userId);
        log.info("WebSocket 连接关闭 - 用户ID: {}, 当前在线人数: {}", userId, ONLINE_USERS.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        log.info("收到用户 {} 的消息: {}", userId, message);
        // 这里可以处理客户端发来的消息，比如心跳检测
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误", error);
    }

    /**
     * 向指定用户发送通知
     *
     * @param userId 用户ID
     * @param notification 通知内容
     */
    public static void sendNotification(Long userId, Map<String, Object> notification) {
        Session session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String message = OBJECT_MAPPER.writeValueAsString(notification);
                session.getBasicRemote().sendText(message);
                log.info("向用户 {} 发送通知成功", userId);
            } catch (IOException e) {
                log.error("向用户 {} 发送通知失败", userId, e);
            }
        } else {
            log.debug("用户 {} 不在线，无法发送 WebSocket 通知", userId);
        }
    }

    /**
     * 向指定用户发送消息
     */
    private void sendMessage(Long userId, String message) {
        Session session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("发送消息失败", e);
            }
        }
    }

    /**
     * 创建消息
     */
    private String createMessage(String type, String content) {
        try {
            Map<String, String> message = Map.of(
                "type", type,
                "content", content,
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            return OBJECT_MAPPER.writeValueAsString(message);
        } catch (Exception e) {
            log.error("创建消息失败", e);
            return "{\"type\":\"error\",\"content\":\"消息创建失败\"}";
        }
    }

    /**
     * 获取在线用户数量
     */
    public static int getOnlineCount() {
        return ONLINE_USERS.size();
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isUserOnline(Long userId) {
        Session session = ONLINE_USERS.get(userId);
        return session != null && session.isOpen();
    }
}

