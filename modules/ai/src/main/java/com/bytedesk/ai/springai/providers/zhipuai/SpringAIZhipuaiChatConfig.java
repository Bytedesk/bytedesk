/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 13:41:23
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
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
/**
 * @deprecated 使用ZhipuaiChatConfig
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 * ZhiPuAI Chat Configuration
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIZhipuaiChatConfig {

    @Value("${spring.ai.zhipuai.api-key:}")
    String zhipuaiApiKey;

    @Value("${spring.ai.zhipuai.base-url:https://open.bigmodel.cn/api/paas}")
    String zhipuaiBaseUrl;

    @Value("${spring.ai.zhipuai.chat.options.model:glm-4.5-flash}")
    String zhipuaiApiModel;

    @Value("${spring.ai.zhipuai.chat.options.temperature:0.7}")
    double zhipuaiApiTemperature;

    @Bean("bytedeskZhipuaiApi")
    ZhiPuAiApi bytedeskZhipuaiApi() {
        // return new ZhiPuAiApi(zhipuaiBaseUrl, zhipuaiApiKey);
        return ZhiPuAiApi.builder()
                .apiKey(zhipuaiApiKey)
                .build();
    }

    @Bean("bytedeskZhipuaiChatOptions")
    ZhiPuAiChatOptions bytedeskZhipuaiChatOptions() {
        return ZhiPuAiChatOptions.builder()
                .model(zhipuaiApiModel)
                .temperature(zhipuaiApiTemperature)
                .build();
    }

    // https://open.bigmodel.cn/dev/api/normal-model/glm-4
    @Bean("bytedeskZhipuaiChatModel")
    ZhiPuAiChatModel bytedeskZhipuaiChatModel() {
        return new ZhiPuAiChatModel(bytedeskZhipuaiApi(), bytedeskZhipuaiChatOptions());
    }

    @Bean("bytedeskZhipuaiChatClientBuilder")
    ChatClient.Builder bytedeskZhipuaiChatClientBuilder() {
        return ChatClient.builder(bytedeskZhipuaiChatModel());
    }

    @Bean("bytedeskZhipuaiChatClient")
    ChatClient bytedeskZhipuaiChatClient() {
        return bytedeskZhipuaiChatClientBuilder()
                .defaultOptions(bytedeskZhipuaiChatOptions())
                .defaultAdvisors(new SimpleLoggerAdvisor())
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

} 