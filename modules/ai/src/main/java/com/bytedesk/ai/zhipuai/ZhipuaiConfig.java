/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 11:45:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zhipu.oapi.ClientV4;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;

/**
 * 智谱AI配置类
 * 使用 oapi-java-sdk 的 ClientV4
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 */
@Slf4j
@Data
@Configuration
@ConditionalOnProperty(prefix = "zhipuai", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiConfig {

    @Value("${zhipuai.api-key:}")
    private String apiKey;

    @Value("${zhipuai.model:glm-4}")
    private String model;

    @Value("${zhipuai.temperature:0.7}")
    private double temperature;

    @Value("${zhipuai.top-p:0.9}")
    private double topP;

    @Value("${zhipuai.max-tokens:4096}")
    private int maxTokens;

    @Value("${zhipuai.connection-timeout:30}")
    private int connectionTimeout;

    @Value("${zhipuai.read-timeout:10}")
    private int readTimeout;

    @Value("${zhipuai.write-timeout:10}")
    private int writeTimeout;

    @Value("${zhipuai.ping-interval:10}")
    private int pingInterval;

    @Value("${zhipuai.max-idle-connections:8}")
    private int maxIdleConnections;

    @Value("${zhipuai.keep-alive-duration:1}")
    private int keepAliveDuration;

    /**
     * 创建智谱AI客户端
     * 配置网络参数和连接池
     */
    @Bean("zhipuaiClient")
    public ClientV4 zhipuaiClient() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Zhipuai API key is not configured");
            return null;
        }

        log.info("Initializing Zhipuai client with model: {}", model);
        
        return new ClientV4.Builder(apiKey)
                .enableTokenCache()
                .networkConfig(
                    connectionTimeout, 
                    readTimeout, 
                    writeTimeout, 
                    pingInterval, 
                    TimeUnit.SECONDS
                )
                .connectionPool(new ConnectionPool(
                    maxIdleConnections, 
                    keepAliveDuration, 
                    TimeUnit.SECONDS
                ))
                .build();
    }
} 