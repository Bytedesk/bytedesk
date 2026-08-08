/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-23 10:31:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 16:27:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.config;

import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.observability.CustomChatClientObservationConvention;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;

/**
 * Bytedesk AI Observability 基础设施配置.
 *
 * <p>阶段 7A 调整：不再通过 {@code ObservationRegistry.create()} 创建独立 Registry，
 * 而是注入 Spring Boot 自动配置的 {@link ObservationRegistry}，确保所有观测指标
 * （ChatClient / ChatModel / EmbeddingModel / VectorStore / Tool Calling）
 * 都汇聚到同一个 Registry，供 Prometheus、Zipkin 等后端统一采集。</p>
 */
@Configuration
public class ObservationConfig {

    /**
     * 支持 {@code @Observed} 注解的 AOP 切面，复用 Boot 自动配置的 ObservationRegistry。
     */
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    /**
     * 自定义 ChatClient 观测命名约定，注册为 Spring Bean 后由 Spring AI 自动发现。
     */
    @Bean
    public ChatClientObservationConvention chatClientObservationConvention() {
        return new CustomChatClientObservationConvention();
    }
}
