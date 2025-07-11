/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-17 14:44:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.zhipuai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
/**
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 * https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIZhipuaiConfig {

    @Value("${spring.ai.zhipuai.api-key:}")
    String zhipuaiApiKey;

    @Value("${spring.ai.zhipuai.chat.options.model:glm-4-flash}")
    String zhipuaiApiModel;

    @Value("${spring.ai.zhipuai.chat.options.temperature:0.7}")
    double zhipuaiApiTemperature;

    @Value("${spring.ai.zhipuai.embedding.options.model:embedding-2}")
    String zhipuaiEmbeddingModel;

    @Bean("bytedeskZhipuaiApi")
    ZhiPuAiApi bytedeskZhipuaiApi() {
        return new ZhiPuAiApi(zhipuaiApiKey);
    }

    @Bean("bytedeskZhipuaiChatOptions")
    ZhiPuAiChatOptions bytedeskZhipuaiChatOptions() {
        return ZhiPuAiChatOptions.builder()
                .model(zhipuaiApiModel)
                .temperature(zhipuaiApiTemperature)
                .build();
    }

    // https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
    // https://open.bigmodel.cn/overview
    @Bean("bytedeskZhipuaiEmbeddingOptions")
    ZhiPuAiEmbeddingOptions bytedeskZhipuaiEmbeddingOptions() {
        return ZhiPuAiEmbeddingOptions.builder()
                .model(zhipuaiEmbeddingModel)
                .build();
    }

    // https://open.bigmodel.cn/dev/api/normal-model/glm-4
    @Bean("bytedeskZhipuaiChatModel")
    ZhiPuAiChatModel bytedeskZhipuaiChatModel() {
        return new ZhiPuAiChatModel(bytedeskZhipuaiApi(), bytedeskZhipuaiChatOptions());
    }

    @Bean("bytedeskZhipuaiEmbeddingModel")
    @ConditionalOnProperty(prefix = "spring.ai.zhipuai.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
    ZhiPuAiEmbeddingModel bytedeskZhipuaiEmbeddingModel(org.springframework.core.env.Environment env) {
        // Only mark as @Primary if this is the selected embedding provider
        boolean isPrimary = "zhipuai".equals(env.getProperty("spring.ai.model.embedding"));
        ZhiPuAiEmbeddingModel model = new ZhiPuAiEmbeddingModel(bytedeskZhipuaiApi(), MetadataMode.EMBED, bytedeskZhipuaiEmbeddingOptions());
        if (isPrimary) {
            // Use a proxy or custom annotation processor if needed, or document that only one embedding should be enabled
        }
        return model;
    }

    @Bean("bytedeskZhipuaiChatClientBuilder")
    ChatClient.Builder bytedeskZhipuaiChatClientBuilder() {
        return ChatClient.builder(bytedeskZhipuaiChatModel());
    }

    @Bean("bytedeskZhipuaiChatClient")
    ChatClient bytedeskZhipuaiChatClient() {
        return bytedeskZhipuaiChatClientBuilder()
                .defaultOptions(bytedeskZhipuaiChatOptions())
                .build();
    }

    @Bean("bytedeskZhipuaiImageApi")
    ZhiPuAiImageApi bytedeskZhipuaiImageApi() {
        return new ZhiPuAiImageApi(zhipuaiApiKey);
    }

    @Bean("bytedeskZhipuaiImageModel")
    ZhiPuAiImageModel bytedeskZhipuaiImageModel() {
        return new ZhiPuAiImageModel(bytedeskZhipuaiImageApi());
    }

    // 注意：RedisVectorStore相关配置已移至VectorStoreConfig类中统一管理
}
