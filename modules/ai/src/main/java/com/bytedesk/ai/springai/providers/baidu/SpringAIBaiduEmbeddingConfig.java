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
package com.bytedesk.ai.springai.providers.baidu;

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
 * 百度智能云 Embedding Configuration (OpenAI-compatible)
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.baidu.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIBaiduEmbeddingConfig {

    @Value("${spring.ai.baidu.base-url:https://qianfan.baidubce.com/v2}")
    private String baseUrl;

    @Value("${spring.ai.baidu.api-key:}")
    private String apiKey;

    @Value("${spring.ai.baidu.embedding.api-key:${spring.ai.baidu.api-key:}}")
    private String embeddingApiKey;

    @Value("${spring.ai.baidu.embedding.options.model:embedding-v1}")
    private String embeddingModel;

    @Value("${spring.ai.baidu.embedding.options.dimensions:0}")
    private Integer embeddingDimensions;

    @Bean("baiduEmbeddingApi")
    OpenAiApi baiduEmbeddingApi() {
        String resolvedApiKey = (embeddingApiKey != null && !embeddingApiKey.isEmpty()) ? embeddingApiKey : apiKey;
        return BaiduApi.create(baseUrl, resolvedApiKey);
    }

    @Bean("baiduEmbeddingOptions")
    OpenAiEmbeddingOptions baiduEmbeddingOptions() {
        OpenAiEmbeddingOptions.Builder builder = OpenAiEmbeddingOptions.builder()
                .model(embeddingModel);
        if (embeddingDimensions != null && embeddingDimensions > 0) {
            builder.dimensions(embeddingDimensions);
        }
        return builder.build();
    }

    @Bean("baiduEmbeddingModel")
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.BAIDU, matchIfMissing = false)
    OpenAiEmbeddingModel baiduEmbeddingModel() {
        return new OpenAiEmbeddingModel(baiduEmbeddingApi(), MetadataMode.EMBED, baiduEmbeddingOptions());
    }

}
