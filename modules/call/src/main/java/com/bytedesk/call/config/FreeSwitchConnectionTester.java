/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 14:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch连接测试工具
 * 在应用程序启动时自动测试ESL连接
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchConnectionTester implements CommandLineRunner {

    private final FreeSwitchProperties freeSwitchProperties;

    @Override
    public void run(String... args) {
        log.info("开始FreeSwitch连接测试...");
        testConnection();
    }

    /**
     * 测试FreeSwitch连接
     */
    public void testConnection() {
        String server = freeSwitchProperties.getServer();
        int port = freeSwitchProperties.getEslPort();
        
        log.info("测试连接到FreeSwitch ESL: {}:{}", server, port);
        
        try (Socket socket = new Socket()) {
            // 设置5秒连接超时
            socket.connect(new java.net.InetSocketAddress(server, port), 5000);
            
            if (socket.isConnected()) {
                log.info("✅ 网络连接成功: {}:{}", server, port);
                
                // 尝试读取FreeSwitch的欢迎消息
                try {
                    socket.setSoTimeout(3000); // 3秒读取超时
                    byte[] buffer = new byte[1024];
                    int bytesRead = socket.getInputStream().read(buffer);
                    
                    if (bytesRead > 0) {
                        String response = new String(buffer, 0, bytesRead);
                        log.info("收到FreeSwitch响应: {}", response.trim());
                        
                        if (response.contains("rude-rejection")) {
                            log.error("❌ FreeSwitch ESL拒绝连接 - Access Control List (ACL) 限制");
                            log.error("解决方案:");
                            log.error("1. 修改FreeSwitch的 event_socket.conf.xml 文件");
                            log.error("2. 在ACL配置中添加允许当前IP地址的规则");
                            log.error("3. 或者移除 apply-inbound-acl 参数以允许所有连接");
                        } else if (response.contains("auth/request")) {
                            log.info("✅ FreeSwitch ESL服务正常，等待认证");
                        }
                    }
                } catch (SocketTimeoutException e) {
                    log.warn("⚠️ 读取FreeSwitch响应超时，但连接已建立");
                } catch (IOException e) {
                    log.error("❌ 读取FreeSwitch响应失败: {}", e.getMessage());
                }
            }
            
        } catch (IOException e) {
            log.error("❌ 连接失败: {}", e.getMessage());
            
            if (e.getMessage().contains("Connection refused")) {
                log.error("可能的原因:");
                log.error("1. FreeSwitch服务未运行");
                log.error("2. 端口{}未开放", port);
                log.error("3. 防火墙阻止了连接");
            } else if (e.getMessage().contains("timeout")) {
                log.error("可能的原因:");
                log.error("1. 网络连接超时");
                log.error("2. 服务器地址不正确: {}", server);
                log.error("3. 路由或网络配置问题");
            }
            
            log.error("当前配置: 服务器={}, 端口={}, 密码={}", 
                    server, port, freeSwitchProperties.getEslPassword());
        }
        
        log.info("FreeSwitch连接测试完成");
    }

    /**
     * 提供连接诊断信息
     */
    public void printDiagnosticInfo() {
        log.info("=== FreeSwitch ESL 连接诊断信息 ===");
        log.info("服务器地址: {}", freeSwitchProperties.getServer());
        log.info("ESL端口: {}", freeSwitchProperties.getEslPort());
        log.info("ESL密码: {}", freeSwitchProperties.getEslPassword());
        log.info("启用状态: {}", freeSwitchProperties.isEnabled());
        
        log.info("\n=== 故障排除指南 ===");
        log.info("1. 检查FreeSwitch服务状态:");
        log.info("   sudo systemctl status freeswitch");
        log.info("   或 ps aux | grep freeswitch");
        
        log.info("\n2. 检查端口监听:");
        log.info("   netstat -tlnp | grep 8021");
        log.info("   或 ss -tlnp | grep 8021");
        
        log.info("\n3. 测试端口连通性:");
        log.info("   telnet {} {}", freeSwitchProperties.getServer(), freeSwitchProperties.getEslPort());
        
        log.info("\n4. 检查FreeSwitch配置:");
        log.info("   配置文件: conf/autoload_configs/event_socket.conf.xml");
        log.info("   ACL配置: conf/autoload_configs/acl.conf.xml");
        
        log.info("\n5. 查看FreeSwitch日志:");
        log.info("   tail -f /usr/local/freeswitch/log/freeswitch.log");
        
        log.info("=====================================");
    }
}
