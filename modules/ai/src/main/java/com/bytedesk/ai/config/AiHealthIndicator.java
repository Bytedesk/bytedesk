/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-05 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * AI模块健康检查
 * 监控AI大模型服务的可用性
 */
@Slf4j
@Component
public class AiHealthIndicator implements HealthIndicator {

    @Value("${spring.ai.model.chat:none}")
    private String chatModel;

    @Value("${spring.ai.model.embedding:none}")
    private String embeddingModel;

    @Value("${spring.ai.model.image:none}")
    private String imageModel;

    @Autowired(required = false)
    private ChatModel primaryChatModel;

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();
            
            // 基本信息
            builder.withDetail("chat-model", chatModel)
                   .withDetail("embedding-model", embeddingModel)
                   .withDetail("image-model", imageModel);

            // 检查ChatModel是否可用
            if (primaryChatModel != null) {
                builder.withDetail("chat-model-status", "Available");
                
                // 尝试简单的健康检查调用
                try {
                    // 简单的ping测试，不实际调用大模型
                    builder.withDetail("chat-model-type", primaryChatModel.getClass().getSimpleName());
                } catch (Exception e) {
                    log.warn("Chat model health check warning: {}", e.getMessage());
                    builder.withDetail("chat-model-warning", e.getMessage());
                }
            } else {
                if (!"none".equals(chatModel)) {
                    builder.down()
                           .withDetail("chat-model-status", "Not Available")
                           .withDetail("error", "Chat model configured but not initialized");
                } else {
                    builder.withDetail("chat-model-status", "Not Configured");
                }
            }

            return builder.build();
            
        } catch (Exception e) {
            log.error("AI health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("chat-model", chatModel)
                    .build();
        }
    }
}
