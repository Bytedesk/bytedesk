/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 09:44:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 09:47:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import org.springframework.core.env.Environment;

import com.bytedesk.core.base.LlmModelConfigResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LlmConfigUtils {

    public LlmModelConfigResponse getDefaultModelConfig(Environment environment) {
        // Get the default chat provider and model
        String defaultChatProvider = environment.getProperty("spring.ai.model.chat", "ollama");
        String defaultChatModel = "";
        if ("zhipuai".equals(defaultChatProvider)) {
            defaultChatModel = environment.getProperty("spring.ai.zhipuai.chat.options.model", "glm-4-flash");
        } else if ("ollama".equals(defaultChatProvider)) {
            defaultChatModel = environment.getProperty("spring.ai.ollama.chat.options.model", "qwen3:0.6b");
        } else {
            defaultChatProvider = "ollama";
            defaultChatModel = "qwen3:0.6b";
        }

        // Get the default embedding provider and model
        String defaultEmbeddingProvider = environment.getProperty("spring.ai.model.embedding", "ollama");
        String defaultEmbeddingModel = "";
        if ("zhipuai".equals(defaultEmbeddingProvider)) {
            defaultEmbeddingModel = environment.getProperty("spring.ai.zhipuai.embedding.options.model", "embedding-2");
        } else if ("ollama".equals(defaultEmbeddingProvider)) {
            defaultEmbeddingModel = environment.getProperty("spring.ai.ollama.embedding.options.model", "bge-m3:latest");
        } else {
            defaultEmbeddingProvider = "ollama";
            defaultEmbeddingModel = "bge-m3:latest";
        }

        return LlmModelConfigResponse.builder()
                .defaultChatProvider(defaultChatProvider)
                .defaultChatModel(defaultChatModel)
                .defaultEmbeddingProvider(defaultEmbeddingProvider)
                .defaultEmbeddingModel(defaultEmbeddingModel)
                .build();
    }
} 