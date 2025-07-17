/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-18 07:05:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 07:08:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.dashscope;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 Dashscope 聊天配置 - 使用官方 spring-ai-alibaba-starter-dashscope
 * 
 * 当 spring.ai.dashscope.chat.enabled=true 时，spring-ai-alibaba-starter-dashscope 会自动配置：
 * - DashScopeChatModel
 * - DashScopeChatOptions
 * - DashScopeChatClient
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.dashscope.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AlibabaDashscopeChatConfig {

    @Value("${spring.ai.dashscope.chat.options.model:deepseek-r1}")
    private String model;

    @Value("${spring.ai.dashscope.chat.options.temperature:0.7}")
    private Double temperature;

    /**
     * 使用官方的 DashScopeChatModel
     * 当 spring.ai.dashscope.chat.enabled=true 时，spring-ai-alibaba-starter-dashscope 会自动配置
     */
    @Bean("alibabaDashscopeChatModel")
    ChatModel alibabaDashscopeChatModel() {
        // spring-ai-alibaba-starter-dashscope 会自动创建 DashScopeChatModel
        // 这里返回的是 ChatModel 接口，实际实现是 DashScopeChatModel
        return null; // 让自动配置处理
    }

    /**
     * 使用官方的 DashScopeChatClient
     */
    @Bean("alibabaDashscopeChatClient")
    ChatClient alibabaDashscopeChatClient() {
        // spring-ai-alibaba-starter-dashscope 会自动创建 ChatClient
        return null; // 让自动配置处理
    }

} 