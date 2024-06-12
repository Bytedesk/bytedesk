/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-31 11:17:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

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

/**
 * https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
 */
@Configuration
public class ZhipuAiConfig {

    @Value("${spring.ai.zhipu.api-key}")
    String zhiPuAiApiKey;

    @Bean
    ZhiPuAiApi zhipuaiApi() {
        // return new ZhiPuAiApi(System.getenv("ZHIPU_AI_API_KEY"));
        return new ZhiPuAiApi(zhiPuAiApiKey);
    }

    @Bean
    ZhiPuAiEmbeddingModel zhipuaiEmbeddingModel() {
        return new ZhiPuAiEmbeddingModel(zhipuaiApi());
    }

    // https://open.bigmodel.cn/overview
    @Bean
    ZhiPuAiEmbeddingOptions ZhiPuAiEmbeddingOptions() {
        return ZhiPuAiEmbeddingOptions.builder()
                .withModel(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
                // .withModel("GLM-4")
                .build();
    }

    @Bean
    ZhiPuAiChatModel zhipuaiChatModel() {
        return new ZhiPuAiChatModel(zhipuaiApi(), ZhiPuAiChatOptions.builder()
                .withModel(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
                .withTemperature(0.4f)
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
