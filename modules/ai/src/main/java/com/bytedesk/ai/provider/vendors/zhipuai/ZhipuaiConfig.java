/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-10 23:30:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.zhipuai;

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

/**
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 * https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
 */
@Configuration
public class ZhipuaiConfig {

    @Value("${spring.ai.zhipuai.api-key}")
    String zhiPuAiApiKey;

    @Value("${spring.ai.zhipuai.chat.options.model}")
    String zhiPuAiApiModel;

    @Bean
    ZhiPuAiApi zhipuaiApi() {
        return new ZhiPuAiApi(zhiPuAiApiKey);
    }

    // https://open.bigmodel.cn/overview
    @Bean
    ZhiPuAiEmbeddingOptions ZhiPuAiEmbeddingOptions() {
        return ZhiPuAiEmbeddingOptions.builder()
                .withModel(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
                .build();
    }

    @Bean
    @Primary
    ZhiPuAiEmbeddingModel zhipuaiEmbeddingModel() {
        return new ZhiPuAiEmbeddingModel(zhipuaiApi());
    }

    @Bean
    @Primary
    ZhiPuAiChatModel zhipuaiChatModel() {
        return new ZhiPuAiChatModel(zhipuaiApi(), ZhiPuAiChatOptions.builder()
                // .withModel(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
                .withModel(zhiPuAiApiModel)
                .withTemperature(0.4)
                .withMaxTokens(200)
                .build());
    }

    @Bean
    ZhiPuAiImageApi zhipuaiImageApi() {
        return new ZhiPuAiImageApi(zhiPuAiApiKey);
    }

    @Bean
    ZhiPuAiImageModel zhiPuAiImageModel() {
        return new ZhiPuAiImageModel(zhipuaiImageApi());
    }

    @Bean
    ClientV4 client() {
        return new ClientV4.Builder(zhiPuAiApiKey).build();
    }

}
