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
package com.bytedesk.ai.springai.providers.volcengine;

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
 * 火山引擎 Embedding Configuration (OpenAI-compatible)
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.volcengine.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIVolcengineEmbeddingConfig {

    @Value("${spring.ai.volcengine.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${spring.ai.volcengine.api-key:}")
    private String apiKey;

    @Value("${spring.ai.volcengine.embedding.api-key:${spring.ai.volcengine.api-key:}}")
    private String embeddingApiKey;

    @Value("${spring.ai.volcengine.embedding.options.model:doubao-embedding}")
    private String embeddingModel;

    @Value("${spring.ai.volcengine.embedding.options.dimensions:0}")
    private Integer embeddingDimensions;

    @Bean("volcengineEmbeddingApi")
    OpenAiApi volcengineEmbeddingApi() {
        String resolvedApiKey = (embeddingApiKey != null && !embeddingApiKey.isEmpty()) ? embeddingApiKey : apiKey;
        return VolcengineApi.create(baseUrl, resolvedApiKey);
    }

    @Bean("volcengineEmbeddingOptions")
    OpenAiEmbeddingOptions volcengineEmbeddingOptions() {
        OpenAiEmbeddingOptions.Builder builder = OpenAiEmbeddingOptions.builder()
                .model(embeddingModel);
        if (embeddingDimensions != null && embeddingDimensions > 0) {
            builder.dimensions(embeddingDimensions);
        }
        return builder.build();
    }

    @Bean("volcengineEmbeddingModel")
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.VOLCENGINE, matchIfMissing = false)
    OpenAiEmbeddingModel volcengineEmbeddingModel() {
        return new OpenAiEmbeddingModel(volcengineEmbeddingApi(), MetadataMode.EMBED, volcengineEmbeddingOptions());
    }

}
