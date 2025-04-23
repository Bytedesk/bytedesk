/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-21 18:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 18:35:00
 * @Description: Spring AI Observability配置
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.config;

import org.springframework.ai.aot.AiRuntimeHints;
import org.springframework.ai.chat.metadata.DefaultChatGenerationMetadata;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.chat.observation.ChatObservation;
import org.springframework.ai.embedding.observation.EmbeddingObservation;
import org.springframework.ai.observation.AiOperationContext;
import org.springframework.ai.observation.DefaultAiOperationListener;
import org.springframework.ai.observation.annotation.AiOperationLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ImportRuntimeHints(AiRuntimeHints.class)
public class ObservabilityConfig {

    @Bean
    public AiOperationLogger aiOperationLogger() {
        return new AiOperationLogger();
    }

    @Bean
    public ObservationRegistry observationRegistry() {
        ObservationRegistry registry = ObservationRegistry.create();
        
        // 添加自定义监听器
        registry.observationConfig().observationHandler(new DefaultAiOperationListener() {
            @Override
            public void onSuccess(AiOperationContext aiOperationContext) {
                // 处理成功完成的AI操作
                if (aiOperationContext.getObservation() instanceof ChatObservation chatObservation) {
                    log.info("Chat operation completed successfully. Duration: {} ms", 
                             chatObservation.getObservation().getDuration().toMillis());
                    
                    // 获取元数据
                    if (chatObservation.getResponse().getMetadata() instanceof DefaultChatGenerationMetadata metadata) {
                        RateLimit rateLimit = metadata.getRateLimit();
                        if (rateLimit != null) {
                            log.info("Rate limit - Requests: {}, Tokens: {}, Reset: {} s", 
                                    rateLimit.getRemaining() + "/" + rateLimit.getLimit(),
                                    rateLimit.getRemainingTokens() + "/" + rateLimit.getLimitTokens(),
                                    rateLimit.getResetDuration().toSeconds());
                        }
                    }
                }
                else if (aiOperationContext.getObservation() instanceof EmbeddingObservation embeddingObservation) {
                    log.info("Embedding operation completed successfully. Duration: {} ms", 
                             embeddingObservation.getObservation().getDuration().toMillis());
                }
            }
            
            @Override
            public void onError(AiOperationContext aiOperationContext, Throwable error) {
                log.error("AI operation failed: {}", error.getMessage(), error);
            }
        });
        
        return registry;
    }
}
