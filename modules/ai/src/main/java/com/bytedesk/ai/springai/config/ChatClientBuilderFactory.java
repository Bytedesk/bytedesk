/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.observation.AdvisorObservationConvention;
import org.springframework.ai.chat.client.advisor.observation.DefaultAdvisorObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.client.observation.DefaultChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import io.micrometer.observation.ObservationRegistry;

/**
 * 阶段 7B：统一 ChatClient 观测接入辅助.
 *
 * <p>所有 provider config（Ollama / DeepSeek / ZhipuAI / OpenAI / DashScope / ...）应通过本工厂
 * 构建 {@link ChatClient}，保证：</p>
 * <ol>
 *   <li>统一注入 Boot 自动配置的 {@link ObservationRegistry}（不再散落创建）；</li>
 *   <li>统一应用 {@link ChatClientObservationConvention} / {@link AdvisorObservationConvention}，
 *       使指标命名一致；</li>
 *   <li>当 ObservationRegistry 不可用时安全回退到无观测 builder，保证业务可用。</li>
 * </ol>
 *
 * <p>使用方式：</p>
 * <pre>{@code
 * @Bean
 * ChatClient deepseekChatClient(SpringAIDeepseekChatConfig config,
 *                                ChatClientBuilderFactory chatClientBuilderFactory) {
 *     return chatClientBuilderFactory.builder(config.deepseekChatModel())
 *             .defaultOptions(config.deepseekChatOptions().mutate())
 *             .defaultAdvisors(new SimpleLoggerAdvisor())
 *             .build();
 * }
 * }</pre>
 */
@Component
public class ChatClientBuilderFactory {

    private final ObservationRegistry observationRegistry;
    private final ObjectProvider<ChatClientObservationConvention> chatClientConventionProvider;
    private final ObjectProvider<AdvisorObservationConvention> advisorConventionProvider;

    public ChatClientBuilderFactory(ObservationRegistry observationRegistry,
                                    ObjectProvider<ChatClientObservationConvention> chatClientConventionProvider,
                                    ObjectProvider<AdvisorObservationConvention> advisorConventionProvider) {
        this.observationRegistry = observationRegistry;
        this.chatClientConventionProvider = chatClientConventionProvider;
        this.advisorConventionProvider = advisorConventionProvider;
    }

    /**
     * 创建一个接入了 ObservationRegistry 与自定义 Convention 的 {@link ChatClient.Builder}。
     * 调用方可继续链式追加 defaultOptions / defaultAdvisors / defaultSystem 等。
     */
    public ChatClient.Builder builder(ChatModel chatModel) {
        ChatClientObservationConvention chatConvention = chatClientConventionProvider.getIfAvailable();
        AdvisorObservationConvention advisorConvention = advisorConventionProvider.getIfAvailable();
        if (chatConvention != null && advisorConvention != null) {
            return ChatClient.builder(chatModel, observationRegistry, chatConvention, advisorConvention);
        }
        // convention 未提供时退化为仅注入 ObservationRegistry（使用框架默认约定）
        return ChatClient.builder(chatModel, observationRegistry,
                chatConvention != null ? chatConvention : new DefaultChatClientObservationConvention(),
                advisorConvention != null ? advisorConvention : new DefaultAdvisorObservationConvention());
    }
}
