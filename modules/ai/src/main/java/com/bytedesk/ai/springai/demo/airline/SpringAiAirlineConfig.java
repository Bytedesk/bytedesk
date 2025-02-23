/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 11:15:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 11:56:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.demo.airline;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.advisor.BdLoggerAdvisor;

@Configuration
public class SpringAIAirlineConfig {

    // https://java2ai.com/docs/1.0.0-M5.1/practices/playground-flight-booking/?spm=4347728f.3651acb5.0.0.491e3bbcfEUpsq
    @Bean("airlineTicketChatClient")
    ChatClient airlineTicketChatClient(ChatClient.Builder dashScopeChatClientBuilder,
            InMemoryChatMemory defaultChatMemory, VectorStore ollamaRedisVectorStore) {
        return dashScopeChatClientBuilder
                .defaultSystem("""
                          您是“Funnair”航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                           您正在通过在线聊天系统与客户互动。
                           在提供有关预订或取消预订的信息之前，您必须始终
                           从用户处获取以下信息：预订号、客户姓名。
                           在询问用户之前，请检查消息历史记录以获取此信息。
                           在更改预订之前，您必须确保条款允许这样做。
                           如果更改需要收费，您必须在继续之前征得用户同意。
                           使用提供的功能获取预订详细信息、更改预订和取消预订。
                           如果需要，可以调用相应函数调用完成辅助动作。
                           请讲中文。
                           今天的日期是 {current_date}.
                        """)
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(defaultChatMemory), // Chat Memory
                        // VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),
                        new QuestionAnswerAdvisor(ollamaRedisVectorStore, 
                                SearchRequest.builder().topK(4).similarityThresholdAll().build()
                        ), // RAG
                        new BdLoggerAdvisor())
                .defaultTools("getBookingDetails", "changeBooking", "cancelBooking") // FUNCTION CALLING

                .build();
    }

    
}
