/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.n8n;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * RAGFlow 配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bytedesk.n8n")
@ConditionalOnProperty(name = "bytedesk.n8n.enabled", havingValue = "true", matchIfMissing = false)
public class N8nConfig {

    /**
     * 是否启用 RAGFlow
     */
    private boolean enabled = false;

    /**
     * RAGFlow API 地址
     */
    private String apiUrl = "http://localhost:9380";

    /**
     * RAGFlow API 密钥
     */
    private String apiKey;

    /**
     * 默认模型名称
     */
    private String defaultModel = "model";

    /**
     * 是否默认使用流式响应
     */
    private boolean defaultStream = false;

    /**
     * 请求超时时间（毫秒）
     */
    private int timeout = 30000;

    /**
     * 最大重试次数
     */
    private int maxRetries = 3;

    /**
     * 是否启用调试日志
     */
    private boolean debugEnabled = false;
}
