/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 14:09:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 阿里云 Dashscope 嵌入向量模型配置
 * 
 * 参考：
 * https://github.com/alibaba/spring-ai-alibaba/blob/main/spring-ai-alibaba-autoconfigure/src/main/java/com/alibaba/cloud/ai/autoconfigure/dashscope/DashScopeEmbeddingProperties.java
 * https://docs.spring.io/spring-ai/reference/api/embeddings/openai-embeddings.html
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.dashscope.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeEmbeddingConfig {

    @Value("${spring.ai.dashscope.base-url:https://dashscope.aliyuncs.com/compatible-mode}")
    private String baseUrl;

    @Value("${spring.ai.dashscope.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.dashscope.embedding.options.model:text-embedding-v1}")
    private String model;

    @Bean("bytedeskDashscopeEmbeddingApi")
    OpenAiApi bytedeskDashscopeEmbeddingApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("bytedeskDashscopeEmbeddingOptions")
    OpenAiEmbeddingOptions bytedeskDashscopeEmbeddingOptions() {
        return OpenAiEmbeddingOptions.builder()
                .model(model)
                .build();
    }

    @Bean("bytedeskDashscopeEmbeddingModel")
    EmbeddingModel bytedeskDashscopeEmbeddingModel() {
        return new OpenAiEmbeddingModel(bytedeskDashscopeEmbeddingApi(), MetadataMode.EMBED, bytedeskDashscopeEmbeddingOptions());
    }

} 