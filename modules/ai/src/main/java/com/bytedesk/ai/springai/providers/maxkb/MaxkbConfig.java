/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.maxkb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * MaxKB 配置类
 * https://maxkb.cn/docs/v1/dev_manual/APIKey_chat/#1-openai-api
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bytedesk.maxkb")
@ConditionalOnProperty(name = "bytedesk.maxkb.enabled", havingValue = "true", matchIfMissing = false)
public class MaxkbConfig {

    /**
     * 是否启用 MaxKB 集成
     */
    private boolean enabled = false;

    /**
     * MaxKB API 基础地址
     * 例如：https://maxkb.fit2cloud.com
     */
    private String apiUrl = "https://maxkb.fit2cloud.com";

    /**
     * MaxKB API 密钥
     * 从 MaxKB 控制台获取，格式：application-xxxxxxxxf00e21a7530d1177c20967
     */
    private String apiKey;

    /**
     * 默认模型名称
     */
    private String defaultModel = "gpt-3.5-turbo";

    /**
     * 默认是否使用流式响应
     */
    private Boolean defaultStream = false;

    /**
     * 请求超时时间（毫秒）
     */
    private Integer timeout = 30000;

    /**
     * 最大重试次数
     */
    private Integer maxRetries = 3;

    /**
     * 是否启用调试模式
     */
    private Boolean debugEnabled = false;

    /**
     * 连接池最大连接数
     */
    private Integer maxConnections = 100;

    /**
     * 连接池最大空闲连接数
     */
    private Integer maxIdleConnections = 10;

    /**
     * 连接保持时间（秒）
     */
    private Integer keepAliveDuration = 300;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 30000;

    /**
     * 写入超时时间（毫秒）
     */
    private Integer writeTimeout = 30000;

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 10000;
}
