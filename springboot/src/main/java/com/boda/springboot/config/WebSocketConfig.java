package com.boda.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置类
 * 启用 WebSocket 功能
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig {

    /**
     * 注入 ServerEndpointExporter
     * 这个Bean会自动注册使用@ServerEndpoint注解的WebSocket端点
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

