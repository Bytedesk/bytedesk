/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 14:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 14:23:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.freeswitch;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch健康检查器
 * 为Spring Boot Actuator提供FreeSwitch连接状态监控
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchHealthIndicator implements HealthIndicator {

    private final FreeSwitchProperties freeSwitchProperties;

    @Override
    public Health health() {
        try {
            // 测试基本网络连接
            boolean isConnectable = testNetworkConnection();
            
            if (isConnectable) {
                return Health.up()
                        .withDetail("server", freeSwitchProperties.getServer())
                        .withDetail("port", freeSwitchProperties.getEslPort())
                        .withDetail("status", "可连接")
                        .withDetail("lastCheck", getCurrentTime())
                        .withDetail("message", "FreeSwitch ESL端口可访问")
                        .build();
            } else {
                return Health.down()
                        .withDetail("server", freeSwitchProperties.getServer())
                        .withDetail("port", freeSwitchProperties.getEslPort())
                        .withDetail("status", "连接失败")
                        .withDetail("lastCheck", getCurrentTime())
                        .withDetail("message", "无法连接到FreeSwitch ESL端口")
                        .withDetail("troubleshooting", "检查FreeSwitch服务状态和网络连接")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("server", freeSwitchProperties.getServer())
                    .withDetail("port", freeSwitchProperties.getEslPort())
                    .withDetail("status", "检查异常")
                    .withDetail("lastCheck", getCurrentTime())
                    .withDetail("error", e.getMessage())
                    .withDetail("message", "健康检查过程中发生异常")
                    .build();
        }
    }

    /**
     * 测试网络连接
     */
    private boolean testNetworkConnection() {
        String server = freeSwitchProperties.getServer();
        int port = freeSwitchProperties.getEslPort();
        
        try (Socket socket = new Socket()) {
            // 设置较短的超时时间用于健康检查
            socket.connect(new java.net.InetSocketAddress(server, port), 3000);
            return socket.isConnected();
        } catch (IOException e) {
            log.debug("FreeSwitch健康检查连接失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前时间字符串
     */
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取详细的连接状态信息
     */
    public ConnectionStatus getDetailedStatus() {
        String server = freeSwitchProperties.getServer();
        int port = freeSwitchProperties.getEslPort();
        
        ConnectionStatus status = new ConnectionStatus();
        status.setServer(server);
        status.setPort(port);
        status.setPassword(freeSwitchProperties.getEslPassword());
        status.setEnabled(freeSwitchProperties.isEnabled());
        status.setCheckTime(getCurrentTime());
        
        try (Socket socket = new Socket()) {
            long startTime = System.currentTimeMillis();
            socket.connect(new java.net.InetSocketAddress(server, port), 5000);
            long endTime = System.currentTimeMillis();
            
            status.setConnectable(true);
            status.setResponseTime(endTime - startTime);
            status.setMessage("连接成功");
            
            // 尝试读取FreeSwitch响应
            try {
                socket.setSoTimeout(2000);
                byte[] buffer = new byte[512];
                int bytesRead = socket.getInputStream().read(buffer);
                
                if (bytesRead > 0) {
                    String response = new String(buffer, 0, bytesRead).trim();
                    status.setServerResponse(response);
                    
                    if (response.contains("rude-rejection")) {
                        status.setMessage("连接被拒绝 - ACL限制");
                        status.setTroubleshooting("检查FreeSwitch的ACL配置");
                    } else if (response.contains("auth/request")) {
                        status.setMessage("服务正常 - 等待认证");
                    }
                }
            } catch (IOException e) {
                status.setMessage("连接成功但读取响应失败: " + e.getMessage());
            }
            
        } catch (IOException e) {
            status.setConnectable(false);
            status.setMessage("连接失败: " + e.getMessage());
            
            if (e.getMessage().contains("Connection refused")) {
                status.setTroubleshooting("检查FreeSwitch服务状态和端口配置");
            } else if (e.getMessage().contains("timeout")) {
                status.setTroubleshooting("检查网络连接和防火墙设置");
            }
        }
        
        return status;
    }

    /**
     * 连接状态信息类
     */
    public static class ConnectionStatus {
        private String server;
        private int port;
        private String password;
        private boolean enabled;
        private boolean connectable;
        private long responseTime;
        private String message;
        private String serverResponse;
        private String troubleshooting;
        private String checkTime;

        // Getters and Setters
        public String getServer() { return server; }
        public void setServer(String server) { this.server = server; }
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public boolean isConnectable() { return connectable; }
        public void setConnectable(boolean connectable) { this.connectable = connectable; }
        
        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getServerResponse() { return serverResponse; }
        public void setServerResponse(String serverResponse) { this.serverResponse = serverResponse; }
        
        public String getTroubleshooting() { return troubleshooting; }
        public void setTroubleshooting(String troubleshooting) { this.troubleshooting = troubleshooting; }
        
        public String getCheckTime() { return checkTime; }
        public void setCheckTime(String checkTime) { this.checkTime = checkTime; }
    }
}
