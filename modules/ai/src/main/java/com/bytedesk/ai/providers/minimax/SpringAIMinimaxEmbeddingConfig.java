/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-18 13:35:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 08:39:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.minimax;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.providers.openai.OpenAiCompatibleModelFactory;
import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.Data;

/**
 * 阿里云 DashScope 向量嵌入配置
 * https://java2ai.com/docs/dev/get-started/
 * 阿里云百炼大模型获取api key：https://bailian.console.aliyun.com/?apiKey=1#/api-key
 * 阿里云百炼大模型模型列表：https://bailian.console.aliyun.com/?spm=a2c4g.11186623.0.0.11c67980m5X2VR#/model-market
 * 阿里云百炼支持的embedding模型：text-embedding-v1, text-embedding-v2, text-embedding-v3
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.minimax.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIMinimaxEmbeddingConfig {

    @Value("${spring.ai.minimax.base-url:https://api.minimax.chat}")
    private String minimaxBaseUrl;

    @Value("${spring.ai.minimax.api-key:}")
    private String minimaxApiKey;

    @Value("${spring.ai.minimax.embedding.api-key:${spring.ai.minimax.api-key:}}")
    private String minimaxEmbeddingApiKey;

    @Value("${spring.ai.minimax.embedding.options.model:text-embedding-v1}")
    private String minimaxEmbeddingModel;

    @Bean("minimaxEmbeddingOptions")
    OpenAiEmbeddingOptions minimaxEmbeddingOptions() {
        String resolvedApiKey = (minimaxEmbeddingApiKey != null && !minimaxEmbeddingApiKey.isEmpty())
                ? minimaxEmbeddingApiKey
                : minimaxApiKey;
        return OpenAiEmbeddingOptions.builder()
                .baseUrl(minimaxBaseUrl)
                .apiKey(resolvedApiKey)
                .model(minimaxEmbeddingModel)
                .build();
    }

    @Bean("minimaxEmbeddingModel")
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.MINIMAX, matchIfMissing = false)
    OpenAiEmbeddingModel minimaxEmbeddingModel() {
        return OpenAiCompatibleModelFactory.embeddingModel(minimaxEmbeddingOptions(), MetadataMode.EMBED);
    }

}
