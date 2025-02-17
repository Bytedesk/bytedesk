/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 13:33:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * https://ollama.com/
 * https://www.promptingguide.ai/
 * https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
 */
@Data
@Configuration
public class SpringAiOllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Bean("ollamaApi")
    OllamaApi ollamaApi() {
        return new OllamaApi(ollamaBaseUrl);
    }

    @Bean("ollamaOptions")
    OllamaOptions ollamaOptions() {
        return OllamaOptions.builder()
        .model(OllamaModel.QWEN_2_5_7B.id())
        .build();
    }

    @Bean("ollamaChatModel")
    OllamaChatModel ollamaChatModel(OllamaApi ollamaApi, OllamaOptions ollamaOptions) {
        return OllamaChatModel.builder()
        .ollamaApi(ollamaApi)
        .defaultOptions(ollamaOptions)
        .build();
    }

    @Bean("ollamaEmbeddingModel")
    OllamaEmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi, OllamaOptions ollamaOptions) {
        return OllamaEmbeddingModel.builder()
        .ollamaApi(ollamaApi)
        .defaultOptions(ollamaOptions)
        .build();
    }

}
