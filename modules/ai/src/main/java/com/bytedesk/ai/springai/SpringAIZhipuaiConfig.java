/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 14:10:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zhipu.oapi.ClientV4;

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
public class SpringAIZhipuaiConfig {

    @Value("${spring.ai.zhipuai.api-key:}")
    String zhipuaiApiKey;

    @Value("${spring.ai.zhipuai.chat.options.model:glm-4-flash}")
    String zhipuaiApiModel;

    @Value("${spring.ai.zhipuai.chat.options.temperature:0.7}")
    double zhipuaiApiTemperature;

    @Value("${spring.ai.zhipuai.embedding.options.model:embedding-2}")
    String zhipuaiEmbeddingModel;

    @Bean("zhipuaiApi")
    ZhiPuAiApi zhipuaiApi() {
        return new ZhiPuAiApi(zhipuaiApiKey);
    }

    @Bean("zhipuaiChatOptions")
    ZhiPuAiChatOptions zhipuaiChatOptions() {
        return ZhiPuAiChatOptions.builder()
                .model(zhipuaiApiModel)
                .temperature(zhipuaiApiTemperature)
                .build();
    }

    // https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
    // https://open.bigmodel.cn/overview
    @Bean("zhipuaiEmbeddingOptions")
    ZhiPuAiEmbeddingOptions ZhiPuAiEmbeddingOptions() {
        return ZhiPuAiEmbeddingOptions.builder()
                .model(zhipuaiEmbeddingModel)
                .build();
    }

    // https://open.bigmodel.cn/dev/api/normal-model/glm-4
    @Bean("zhipuaiChatModel")
    ZhiPuAiChatModel zhipuaiChatModel(ZhiPuAiApi zhipuaiApi, ZhiPuAiChatOptions zhipuaiChatOptions) {
        return new ZhiPuAiChatModel(zhipuaiApi, zhipuaiChatOptions);
    }

    @Bean("zhipuaiEmbeddingModel")
    ZhiPuAiEmbeddingModel zhipuaiEmbeddingModel(ZhiPuAiApi zhipuaiApi, ZhiPuAiEmbeddingOptions zhipuaiEmbeddingOptions) {
        return new ZhiPuAiEmbeddingModel(zhipuaiApi, MetadataMode.EMBED, zhipuaiEmbeddingOptions);
    }

    @Bean("zhipuaiChatClientBuilder")
    ChatClient.Builder zhipuaiChatClientBuilder(ZhiPuAiChatModel zhipuaiChatModel) {
        return ChatClient.builder(zhipuaiChatModel);
    }

    @Bean("zhipuaiChatClient")
    ChatClient zhipuaiChatClient(ChatClient.Builder zhipuaiChatClientBuilder, ZhiPuAiChatOptions zhipuaiChatOptions) {
        return zhipuaiChatClientBuilder
                .defaultOptions(zhipuaiChatOptions)
                .build();
    }

    @Bean("zhipuaiImageApi")
    ZhiPuAiImageApi zhipuaiImageApi() {
        return new ZhiPuAiImageApi(zhipuaiApiKey);
    }

    @Bean("zhipuaiImageModel")
    ZhiPuAiImageModel zhipuaiImageModel(ZhiPuAiImageApi zhipuaiImageApi) {
        return new ZhiPuAiImageModel(zhipuaiImageApi);
    }

    @Bean("zhipuaiClient")
    ClientV4 zhipuaiClient() {
        return new ClientV4.Builder(zhipuaiApiKey).build();
    }

}
