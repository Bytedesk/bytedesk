/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 13:19:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

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
import org.springframework.context.annotation.Primary;

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
public class SpringAiZhipuaiConfig {

    @Value("${spring.ai.zhipuai.api-key:}")
    String zhipuaiApiKey;

    @Value("${spring.ai.zhipuai.chat.options.model:glm-4-flash}")
    String zhipuaiApiModel;

    @Value("${spring.ai.zhipuai.chat.options.temperature:0.7}")
    double zhipuaiApiTemperature;

    @Bean
    ZhiPuAiApi zhipuaiApi() {
        return new ZhiPuAiApi(zhipuaiApiKey);
    }

    @Bean
    ZhiPuAiChatOptions zhipuaiChatOptions() {
        return ZhiPuAiChatOptions.builder()
                .model(zhipuaiApiModel)
                .temperature(zhipuaiApiTemperature)
                .build();
    }

    // https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
    // https://open.bigmodel.cn/overview
    @Bean
    ZhiPuAiEmbeddingOptions ZhiPuAiEmbeddingOptions() {
        return ZhiPuAiEmbeddingOptions.builder()
                .model(ZhiPuAiApi.EmbeddingModel.Embedding_2.getValue())
                .build();
    }

    @Bean
    @Primary
    ZhiPuAiEmbeddingModel zhipuaiEmbeddingModel(ZhiPuAiApi zhipuaiApi) {
        return new ZhiPuAiEmbeddingModel(zhipuaiApi);
    }

    // https://open.bigmodel.cn/dev/api/normal-model/glm-4
    @Bean
    @Primary
    ZhiPuAiChatModel zhipuaiChatModel(ZhiPuAiApi zhipuaiApi, ZhiPuAiChatOptions zhipuaiChatOptions) {
        return new ZhiPuAiChatModel(zhipuaiApi, zhipuaiChatOptions);
    }

    @Bean
    ZhiPuAiImageApi zhipuaiImageApi() {
        return new ZhiPuAiImageApi(zhipuaiApiKey);
    }

    @Bean
    ZhiPuAiImageModel zhiPuAiImageModel(ZhiPuAiImageApi zhipuaiImageApi) {
        return new ZhiPuAiImageModel(zhipuaiImageApi);
    }

    @Bean
    ClientV4 client() {
        return new ClientV4.Builder(zhipuaiApiKey).build();
    }

}
