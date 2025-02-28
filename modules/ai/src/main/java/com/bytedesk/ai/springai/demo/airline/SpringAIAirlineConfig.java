/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 11:15:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 10:21:49
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
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIAirlineConfig {

    // https://java2ai.com/docs/1.0.0-M5.1/practices/playground-flight-booking/?spm=4347728f.3651acb5.0.0.491e3bbcfEUpsq
     // private String customerSupportAssistantSystemPrompt = """
    //                     You are a customer chat support agent of an airline named "Funnair". Respond in a friendly,
    //                     helpful, and joyful manner.

    //                     Before providing information about a booking or cancelling a booking, you MUST always
    //                     get the following information from the user: booking number, customer first name and last name.

    //                     Before changing a booking you MUST ensure it is permitted by the terms.

    //                     If there is a charge for the change, you MUST ask the user to consent before proceeding.
    //                     """;

    private String customerSupportAssistantSystemPrompt = """
						您是"Funnair"航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
						您正在通过在线聊天系统与客户互动。
						您能够支持已有机票的预订详情查询、机票日期改签、机票预订取消等操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
					   在提供有关机票预订详情查询、机票日期改签、机票预订取消等操作之前，您必须始终从用户处获取以下信息：预订号、客户姓名。
					   在询问用户之前，请检查消息历史记录以获取预订号、客户姓名等信息，尽量避免重复询问给用户造成困扰。
					   在更改预订之前，您必须确保条款允许这样做。
					   如果更改需要收费，您必须在继续之前征得用户同意。
					   使用提供的功能获取预订详细信息、更改预订和取消预订。
					   如果需要，您可以调用相应函数辅助完成。
					   请讲中文。
					   今天的日期是 {current_date}.
					""";

    // 客服助手
    // https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory
    // The bean 'vectorStore', defined in class path resource [org/springframework/ai/autoconfigure/vectorstore/weaviate/WeaviateVectorStoreAutoConfiguration.class], could not be registered. A bean with that name has already been defined in class path resource [org/springframework/ai/autoconfigure/vectorstore/redis/RedisVectorStoreAutoConfiguration.class] and overriding is disabled.
    @Bean("ollamaCustomerSupportAssistant")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
    public ChatClient ollamaCustomerSupportAssistant(
            ChatClient.Builder defaultChatClientBuilder,
            InMemoryChatMemory defaultChatMemory, 
            VectorStore ollamaRedisVectorStore) {

        return defaultChatClientBuilder
                .defaultSystem(customerSupportAssistantSystemPrompt)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(defaultChatMemory),
                        new QuestionAnswerAdvisor(ollamaRedisVectorStore),
                        new SimpleLoggerAdvisor())
                .defaultTools("getBookingDetails", "changeBooking", "cancelBooking")
                .build();
    }

    @Bean("dashScopeCustomerSupportAssistant")
    @ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
    public ChatClient dashScopeCustomerSupportAssistant(
            ChatClient.Builder dashScopeChatClientBuilder,
            InMemoryChatMemory defaultChatMemory, 
            VectorStore ollamaRedisVectorStore) {

        return dashScopeChatClientBuilder
                .defaultSystem(customerSupportAssistantSystemPrompt)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(defaultChatMemory),
                        new QuestionAnswerAdvisor(ollamaRedisVectorStore),
                        new SimpleLoggerAdvisor())
                .defaultTools("getBookingDetails", "changeBooking", "cancelBooking")
                .build();
    }
}
