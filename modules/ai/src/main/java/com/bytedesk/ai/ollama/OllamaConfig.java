/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-31 11:11:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
 */
@Configuration
public class OllamaConfig {

    @Bean
    OllamaApi ollamaApi() {
        return new OllamaApi("http://localhost:11434");
    }

    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("You are a friendly chat bot that answers question")
                .build();
    }

    @Bean
    OllamaChatModel chatModel() {
        return new OllamaChatModel(ollamaApi(),
                OllamaOptions.create()
                        // .withModel(OllamaOptions.DEFAULT_MODEL)
                        .withModel("qwen:7b")
                        .withTemperature(0.9f));
    }

    // https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
    @Bean
    OllamaEmbeddingModel ollamaEmbeddingModel() {
        return new OllamaEmbeddingModel(ollamaApi());
    }
    

}
