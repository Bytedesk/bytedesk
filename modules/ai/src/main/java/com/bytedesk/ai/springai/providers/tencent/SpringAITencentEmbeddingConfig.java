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
package com.bytedesk.ai.springai.providers.tencent;

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
 * 腾讯混元 Embedding Configuration (OpenAI-compatible)
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.tencent.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAITencentEmbeddingConfig {

    @Value("${spring.ai.tencent.base-url:https://api.hunyuan.cloud.tencent.com}")
    private String baseUrl;

    @Value("${spring.ai.tencent.api-key:}")
    private String apiKey;

    @Value("${spring.ai.tencent.embedding.api-key:${spring.ai.tencent.api-key:}}")
    private String embeddingApiKey;

    @Value("${spring.ai.tencent.embedding.options.model:hunyuan-embedding}")
    private String embeddingModel;

    @Value("${spring.ai.tencent.embedding.options.dimensions:0}")
    private Integer embeddingDimensions;

    @Bean("tencentEmbeddingApi")
    OpenAiApi tencentEmbeddingApi() {
        String resolvedApiKey = (embeddingApiKey != null && !embeddingApiKey.isEmpty()) ? embeddingApiKey : apiKey;
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(resolvedApiKey)
                .build();
    }

    @Bean("tencentEmbeddingOptions")
    OpenAiEmbeddingOptions tencentEmbeddingOptions() {
        OpenAiEmbeddingOptions.Builder builder = OpenAiEmbeddingOptions.builder()
                .model(embeddingModel);
        if (embeddingDimensions != null && embeddingDimensions > 0) {
            builder.dimensions(embeddingDimensions);
        }
        return builder.build();
    }

    @Bean("tencentEmbeddingModel")
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.TENCENT, matchIfMissing = false)
    OpenAiEmbeddingModel tencentEmbeddingModel() {
        return new OpenAiEmbeddingModel(tencentEmbeddingApi(), MetadataMode.EMBED, tencentEmbeddingOptions());
    }

}
