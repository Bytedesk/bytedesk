/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 14:29:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.zhipuai;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.SpringAIModels;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
 * https://open.bigmodel.cn/overview
 * ZhiPuAI Embedding Configuration
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIZhipuaiEmbeddingConfig {

    @Value("${spring.ai.zhipuai.api-key:}")
    String zhipuaiApiKey;

    @Value("${spring.ai.zhipuai.base-url:https://open.bigmodel.cn/api/paas}")
    String zhipuaiBaseUrl;

    @Value("${spring.ai.zhipuai.embedding.api-key:${spring.ai.zhipuai.api-key:}}")
    String zhipuaiEmbeddingApiKey;

    @Value("${spring.ai.zhipuai.embedding.options.model:embedding-2}")
    String zhipuaiEmbeddingModel;

    @Bean("bytedeskZhipuaiEmbeddingApi")
    ZhiPuAiApi bytedeskZhipuaiEmbeddingApi() {
        if (zhipuaiEmbeddingApiKey != null && !zhipuaiEmbeddingApiKey.isEmpty()) {
            return ZhiPuAiApi.builder()
                    .apiKey(zhipuaiEmbeddingApiKey)
                    .build();
        } else {
            return ZhiPuAiApi.builder()
                    .apiKey(zhipuaiApiKey)
                    .build();
        }
    }

    // https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
    // https://open.bigmodel.cn/overview
    @Bean("bytedeskZhipuaiEmbeddingOptions")
    ZhiPuAiEmbeddingOptions bytedeskZhipuaiEmbeddingOptions() {
        return ZhiPuAiEmbeddingOptions.builder()
                .model(zhipuaiEmbeddingModel)
                .build();
    }

    @Bean("zhiPuAiEmbeddingModel")
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = SpringAIModels.ZHIPUAI, matchIfMissing = false)
    ZhiPuAiEmbeddingModel zhiPuAiEmbeddingModel() {
        return new ZhiPuAiEmbeddingModel(bytedeskZhipuaiEmbeddingApi(), MetadataMode.EMBED, bytedeskZhipuaiEmbeddingOptions());
    }

} 