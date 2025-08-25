/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:22:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:22:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Dify 配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bytedesk.dify")
@ConditionalOnProperty(name = "bytedesk.dify.enabled", havingValue = "true", matchIfMissing = false)
public class DifyConfig {

    /**
     * 是否启用 Dify
     */
    private boolean enabled = false;

    /**
     * Dify API 地址
     */
    private String apiUrl = "https://api.dify.ai";

    /**
     * Dify API 密钥
     */
    private String apiKey;

    /**
     * 默认响应模式
     */
    private String defaultResponseMode = "blocking";

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
