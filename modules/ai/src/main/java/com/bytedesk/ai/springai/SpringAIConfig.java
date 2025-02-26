/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-12 12:09:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 15:35:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class SpringAIConfig {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Bean("defaultChatClientBuilder")
    ChatClient.Builder chatClientBuilder() {
        return ChatClient.builder(ollamaChatModel);
    }

    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @Bean("defaultChatClient")
    ChatClient defaultChatClient(ChatClient.Builder defaultChatClientBuilder) {
        return defaultChatClientBuilder
                .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
                .build();
    }

    // chatMemory
    @Bean("defaultChatMemory")
    InMemoryChatMemory defaultChatMemory() {
        return new InMemoryChatMemory();
    }

    // 客服助手
    // https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory
    // The bean 'vectorStore', defined in class path resource [org/springframework/ai/autoconfigure/vectorstore/weaviate/WeaviateVectorStoreAutoConfiguration.class], could not be registered. A bean with that name has already been defined in class path resource [org/springframework/ai/autoconfigure/vectorstore/redis/RedisVectorStoreAutoConfiguration.class] and overriding is disabled.
    @Bean("customerSupportAssistant")
    public ChatClient customerSupportAssistant(ChatClient.Builder defaultChatClientBuilder,
            InMemoryChatMemory defaultChatMemory, VectorStore ollamaRedisVectorStore) {

        return defaultChatClientBuilder
                .defaultSystem("""
                        You are a customer chat support agent of an airline named "Funnair". Respond in a friendly,
                        helpful, and joyful manner.

                        Before providing information about a booking or cancelling a booking, you MUST always
                        get the following information from the user: booking number, customer first name and last name.

                        Before changing a booking you MUST ensure it is permitted by the terms.

                        If there is a charge for the change, you MUST ask the user to consent before proceeding.
                        """)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(defaultChatMemory), // CHAT MEMORY
                        new QuestionAnswerAdvisor(ollamaRedisVectorStore), // RAG
                        new SimpleLoggerAdvisor())
                .defaultTools("getBookingDetails", "changeBooking", "cancelBooking") // FUNCTION CALLING
                .build();
    }

}
