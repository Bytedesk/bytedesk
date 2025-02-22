/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:30:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 09:23:29
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
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

/**
 * Spring AI Alibaba
 * https://java2ai.com/docs/dev/get-started/
 * https://github.com/alibaba/spring-ai-alibaba
 * https://java2ai.com/docs/dev/tutorials/basics/chat-client/?spm=4347728f.63599dc2.0.0.8c026e97YbEM8P
 * Examples:
 * https://github.com/springaialibaba/spring-ai-alibaba-examples
 * 阿里云百炼大模型获取api key：
 * https://bailian.console.aliyun.com/?apiKey=1#/api-key
 * 阿里云百炼大模型模型列表：
 * https://bailian.console.aliyun.com/?spm=a2c4g.11186623.0.0.11c67980m5X2VR#/model-market
 */
@Configuration
public class SpringAiAlibabaConfig {

    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    @Value("${spring.ai.dashscope.api-key:}")
    private String dashScopeApiKey;

    @Value("${spring.ai.dashscope.chat.options.model:deepseek-r1}")
    private String dashScopeChatOptionsModel;

    @Value("${spring.ai.dashscope.chat.options.temperature:0.7}")
    private double dashScopeChatOptionsTemperature;

    @Value("${spring.ai.dashscope.chat.options.topP:3}")
    private double dashScopeChatOptionsTopP;

    @Bean("dashScopeApi")
    DashScopeApi dashScopeApi() {
        return new DashScopeApi(dashScopeApiKey);
    }

    @Bean("dashScopeChatClientBuilder")
    ChatClient.Builder dashScopeChatClientBuilder(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel);
    }

    @Bean("dashScopeChatClient")
    ChatClient dashScopeChatClient(ChatClient.Builder dashScopeChatClientBuilder,
            DashScopeChatOptions dashScopeChatOptions) {
        return dashScopeChatClientBuilder
                .defaultSystem(DEFAULT_PROMPT)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(dashScopeChatOptions)
                .build();
    }

    // https://java2ai.com/docs/1.0.0-M5.1/practices/playground-flight-booking/?spm=4347728f.3651acb5.0.0.491e3bbcfEUpsq
    @Bean("airlineTicketChatClient")
    ChatClient airlineTicketChatClient(ChatClient.Builder dashScopeChatClientBuilder,
            InMemoryChatMemory defaultChatMemory, VectorStore vectorStore) {
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
                        VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build()), // RAG
                        new SimpleLoggerAdvisor())
                .defaultTools("getBookingDetails", "changeBooking", "cancelBooking") // FUNCTION CALLING

                .build();
    }

    @Bean("dashScopeChatOptions")
    DashScopeChatOptions dashScopeChatOptions() {
        return DashScopeChatOptions.builder()
                .withModel(dashScopeChatOptionsModel)
                .withTopP(dashScopeChatOptionsTopP)
                .withTemperature(dashScopeChatOptionsTemperature)
                .build();
    }

    @Bean("dashScopeChatModel")
    DashScopeChatModel dashScopeChatModel(DashScopeApi dashScopeApi, DashScopeChatOptions dashScopeChatOptions) {
        return new DashScopeChatModel(dashScopeApi, dashScopeChatOptions);
    }

}
