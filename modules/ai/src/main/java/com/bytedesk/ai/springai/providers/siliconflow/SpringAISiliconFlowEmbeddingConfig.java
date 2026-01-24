/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.providers.siliconflow;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.Data;

/**
 * SiliconFlow Embedding Configuration (OpenAI-compatible)
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.siliconflow.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAISiliconFlowEmbeddingConfig {

    @Value("${spring.ai.siliconflow.base-url:https://api.siliconflow.cn}")
    private String baseUrl;

    @Value("${spring.ai.siliconflow.api-key:}")
    private String apiKey;

    @Value("${spring.ai.siliconflow.embedding.api-key:${spring.ai.siliconflow.api-key:}}")
    private String embeddingApiKey;

    @Value("${spring.ai.siliconflow.embedding.options.model:BAAI/bge-m3}")
    private String embeddingModel;

    @Value("${spring.ai.siliconflow.embedding.options.dimensions:0}")
    private Integer embeddingDimensions;

    @Bean("siliconFlowEmbeddingApi")
    OpenAiApi siliconFlowEmbeddingApi() {
        String resolvedApiKey = (embeddingApiKey != null && !embeddingApiKey.isEmpty()) ? embeddingApiKey : apiKey;
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(resolvedApiKey)
                .build();
    }

    @Bean("siliconFlowEmbeddingOptions")
    OpenAiEmbeddingOptions siliconFlowEmbeddingOptions() {
        OpenAiEmbeddingOptions.Builder builder = OpenAiEmbeddingOptions.builder()
                .model(embeddingModel);
        if (embeddingDimensions != null && embeddingDimensions > 0) {
            builder.dimensions(embeddingDimensions);
        }
        return builder.build();
    }

    @Bean("siliconFlowEmbeddingModel")
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.SILICONFLOW, matchIfMissing = false)
    OpenAiEmbeddingModel siliconFlowEmbeddingModel() {
        return new OpenAiEmbeddingModel(siliconFlowEmbeddingApi(), MetadataMode.EMBED, siliconFlowEmbeddingOptions());
    }

}
