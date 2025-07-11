/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 10:17:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.baidu;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 百度智能云配置
 * https://console.bce.baidu.com/iam/#/iam/apikey/list
 * https://cloud.baidu.com/doc/WENXINWORKSHOP/s/Fm2vrveyu
 * 百度智能云嵌入配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.baidu.embedding.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIBaiduEmbeddingConfig {

    @Value("${spring.ai.baidu.base-url:https://qianfan.baidubce.com/v2}")
    private String baseUrl;

    @Value("${spring.ai.baidu.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.baidu.embedding.options.model:text-embedding-001}")
    private String model;

    @Bean("baiduEmbeddingApi")
    OpenAiApi baiduEmbeddingApi() {
        // 使用BaiduApi工厂方法创建API实例，自动配置正确的路径
        return BaiduApi.create(baseUrl, apiKey);
    }

    @Bean("baiduEmbeddingOptions")
    OpenAiEmbeddingOptions baiduEmbeddingOptions() {
        return OpenAiEmbeddingOptions.builder()
                .model(model)
                .build();
    }

    @Bean("baiduEmbeddingModel")
    @Primary
    EmbeddingModel baiduEmbeddingModel() {
        return new OpenAiEmbeddingModel(baiduEmbeddingApi(), baiduEmbeddingOptions());
    }

} 