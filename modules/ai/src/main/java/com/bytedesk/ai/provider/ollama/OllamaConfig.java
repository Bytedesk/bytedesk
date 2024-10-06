/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-17 22:24:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://ollama.com/
 * https://www.promptingguide.ai/
 * https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
 */
@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaChatModel;

    @Value("${spring.ai.ollama.embedding.options.model}")
    private String ollamaEmbeddingModel;

    // @Value("${spring.ai.vectorstore.pgvector.dimensions}")
    // private int pgVectorDimensions;

    // @Autowired
    // VectorStore vectorStore;

    @Bean
    OllamaApi ollamaApi() {
        return new OllamaApi(ollamaBaseUrl);
    }

    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    // @Bean
    // ChatClient chatClient(ChatClient.Builder builder) {
    // // return builder.defaultSystem("You are a friendly chat bot that answers
    // question").build();
    // return builder
    // .defaultSystem("""
    // You are a customer chat support agent. Respond in a friendly, helpful, and
    // joyful manner.
    // """)
    // // .defaultAdvisors(
    // // // new PromptChatMemoryAdvisor(chatMemory),
    // // // new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
    // // // new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults())
    // // // new LoggingAdvisor()
    // // ) // RAG
    // // .defaultFunctions("getBookingDetails", "changeBooking", "cancelBooking")
    // // FUNCTION CALLING
    // .build();
    // }
    @Bean
    ChatClient ollamaChatClient() {
        return ChatClient.create(ollamaChatModel());
    }

    @Bean
    OllamaChatModel ollamaChatModel() {
        return new OllamaChatModel(ollamaApi(), OllamaOptions.create().withModel(
                ollamaChatModel).withTemperature(0.9));
    }

    // https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
    @Bean
    OllamaEmbeddingModel ollamaEmbeddingModel() {
        return new OllamaEmbeddingModel(ollamaApi(), OllamaOptions.create().withModel(
                ollamaEmbeddingModel).withTemperature(0.9));
    }

}
