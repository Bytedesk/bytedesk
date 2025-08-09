/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:14:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:53:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Call配置类
 * 
 * 该配置类用于设置Call ESL客户端连接和事件监听器。
 * https://github.com/esl-client/esl-client?tab=readme-ov-file
 * 
 * @author jackning
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ComponentScan("com.bytedesk.freeswitch")
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class CallConfig {
        
    private final CallProperties freeSwitchProperties;
    
    private final CallEventListener freeSwitchEventListener;

    /**
     * 配置Call ESL客户端
     */
    @Bean
    public Client eslClient() {
        Client inboundClient = new Client();
        
        // 连接重试配置
        int maxRetries = 3;
        int retryDelayMs = 2000;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("第{}次尝试连接Call ESL: {}:{}", 
                        attempt, freeSwitchProperties.getServer(), freeSwitchProperties.getEslPort());
                        
                // 设置更长的超时时间
                inboundClient.connect(
                    freeSwitchProperties.getServer(), 
                    freeSwitchProperties.getEslPort(), 
                    freeSwitchProperties.getEslPassword(),
                    20); // 增加超时时间到20秒
                    
                // 验证连接是否真正建立
                if (inboundClient.canSend()) {
                    // 注册事件监听器
                    inboundClient.addEventListener(freeSwitchEventListener);
                    
                    // 订阅所有事件
                    inboundClient.setEventSubscriptions("plain", "all");
                    
                    log.info("Call ESL连接成功，服务器: {}:{}", 
                            freeSwitchProperties.getServer(), freeSwitchProperties.getEslPort());
                    
                    // 连接成功，跳出重试循环
                    break;
                } else {
                    log.warn("ESL连接建立但无法发送命令，连接可能不稳定");
                    throw new InboundConnectionFailure("Connection established but cannot send commands");
                }
                
            } catch (InboundConnectionFailure e) {
                log.error("第{}次ESL连接失败: {}", attempt, e.getMessage());
                
                // 检查具体的错误类型
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("rude-rejection") || e.getMessage().contains("Access Denied")) {
                        log.error("Call ESL拒绝连接 - 可能的原因:");
                        log.error("1. ESL密码错误 (当前密码: {})", freeSwitchProperties.getEslPassword());
                        log.error("2. IP地址不在Call的访问控制列表(ACL)中");
                        log.error("3. Call的event_socket.conf.xml配置限制了外部连接");
                        log.error("4. 防火墙阻止了连接");
                        
                        // ACL拒绝错误通常不需要重试
                        if (attempt == maxRetries) {
                            log.error("所有连接尝试都被拒绝，请检查Call的ESL配置");
                        }
                    } else if (e.getMessage().contains("Connection refused") || e.getMessage().contains("timeout")) {
                        log.error("网络连接问题 - 可能的原因:");
                        log.error("1. Call服务未运行");
                        log.error("2. 端口{}未开放或被防火墙阻止", freeSwitchProperties.getEslPort());
                        log.error("3. 网络连接超时");
                    }
                }
                
                // 如果不是最后一次尝试，等待后重试
                if (attempt < maxRetries) {
                    try {
                        log.info("等待{}毫秒后重试...", retryDelayMs);
                        Thread.sleep(retryDelayMs);
                        retryDelayMs *= 2; // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("连接重试被中断");
                        break;
                    }
                } else {
                    log.error("Call ESL连接最终失败，已尝试{}次", maxRetries);
                }
            }
        }
        
        return inboundClient;
    }
}
