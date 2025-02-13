/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-13 09:08:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 09:10:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

/**
 * https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory
 */
@Service
public class CustomerSupportAssistant {

    private final ChatClient chatClient;

    public CustomerSupportAssistant(ChatClient.Builder builder, VectorStore vectorStore, ChatMemory chatMemory) {

        this.chatClient = builder
            .defaultSystem("""
                    You are a customer chat support agent of an airline named "Funnair". Respond in a friendly,
                    helpful, and joyful manner.

                    Before providing information about a booking or cancelling a booking, you MUST always
                    get the following information from the user: booking number, customer first name and last name.

                    Before changing a booking you MUST ensure it is permitted by the terms.

                    If there is a charge for the change, you MUST ask the user to consent before proceeding.
                    """)
            .defaultAdvisors(
                    new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
                    new QuestionAnswerAdvisor(vectorStore), // RAG
                    new SimpleLoggerAdvisor())
            .defaultTools("getBookingDetails", "changeBooking", "cancelBooking") // FUNCTION CALLING
            .build();
    }

    public Flux<String> chat(String chatId, String userMessageContent) {

        return this.chatClient.prompt()
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream().content();
    }

}
