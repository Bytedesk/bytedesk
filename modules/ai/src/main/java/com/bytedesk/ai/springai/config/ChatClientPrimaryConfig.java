/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 13:58:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.SpringAIModels;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 根据spring.ai.model.chat配置动态设置Primary的ChatClient
 * 支持的值：zhipuai, ollama, dashscope, deepseek, baidu, tencent, volcengine, openai, openrouter, siliconflow, gitee, none
 */
@Slf4j
@Configuration
public class ChatClientPrimaryConfig {

    public ChatClientPrimaryConfig(
            @Qualifier("bytedeskZhipuaiChatClient") ObjectProvider<ChatClient> zhipuaiChatClientProvider,
            @Qualifier("bytedeskOllamaChatClient") ObjectProvider<ChatClient> ollamaChatClientProvider,
            @Qualifier("bytedeskDashscopeChatClient") ObjectProvider<ChatClient> dashscopeChatClientProvider,
            @Qualifier("deepseekChatClient") ObjectProvider<ChatClient> deepseekChatClientProvider,
            @Qualifier("baiduChatClient") ObjectProvider<ChatClient> baiduChatClientProvider,
            @Qualifier("tencentChatClient") ObjectProvider<ChatClient> tencentChatClientProvider,
            @Qualifier("volcengineChatClient") ObjectProvider<ChatClient> volcengineChatClientProvider,
            @Qualifier("openaiChatClient") ObjectProvider<ChatClient> openaiChatClientProvider,
            @Qualifier("openrouterChatClient") ObjectProvider<ChatClient> openrouterChatClientProvider,
            @Qualifier("siliconFlowChatClient") ObjectProvider<ChatClient> siliconflowChatClientProvider,
            @Qualifier("giteeChatClient") ObjectProvider<ChatClient> giteeChatClientProvider) {
        this.zhipuaiChatClient = zhipuaiChatClientProvider.getIfAvailable();
        this.ollamaChatClient = ollamaChatClientProvider.getIfAvailable();
        this.dashscopeChatClient = dashscopeChatClientProvider.getIfAvailable();
        this.deepseekChatClient = deepseekChatClientProvider.getIfAvailable();
        this.baiduChatClient = baiduChatClientProvider.getIfAvailable();
        this.tencentChatClient = tencentChatClientProvider.getIfAvailable();
        this.volcengineChatClient = volcengineChatClientProvider.getIfAvailable();
        this.openaiChatClient = openaiChatClientProvider.getIfAvailable();
        this.openrouterChatClient = openrouterChatClientProvider.getIfAvailable();
        this.siliconflowChatClient = siliconflowChatClientProvider.getIfAvailable();
        this.giteeChatClient = giteeChatClientProvider.getIfAvailable();
    }


    // @Value("${spring.ai.model.chat:none}")
    // private String chatModel;

    private final ChatClient zhipuaiChatClient;

    private final ChatClient ollamaChatClient;

    private final ChatClient dashscopeChatClient;

    private final ChatClient deepseekChatClient;

    private final ChatClient baiduChatClient;

    private final ChatClient tencentChatClient;

    private final ChatClient volcengineChatClient;

    private final ChatClient openaiChatClient;

    private final ChatClient openrouterChatClient;

    private final ChatClient siliconflowChatClient;

    private final ChatClient giteeChatClient;

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.ZHIPUAI)
    public ChatClient primaryZhipuaiChatClient() {
        log.info("Setting ZhiPuAI chat client as Primary");
        if (zhipuaiChatClient == null) {
            throw new IllegalStateException("ZhiPuAI chat client is not available. Please check if spring.ai.zhipuai.chat.enabled=true");
        }
        return zhipuaiChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.OLLAMA)
    public ChatClient primaryOllamaChatClient() {
        log.info("Setting Ollama chat client as Primary");
        if (ollamaChatClient == null) {
            throw new IllegalStateException("Ollama chat client is not available. Please check if spring.ai.ollama.chat.enabled=true");
        }
        return ollamaChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.DASHSCOPE)
    public ChatClient primaryDashscopeChatClient() {
        log.info("Setting Dashscope chat client as Primary");
        if (dashscopeChatClient == null) {
            throw new IllegalStateException("Dashscope chat client is not available. Please check if spring.ai.dashscope.chat.enabled=true");
        }
        return dashscopeChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.DEEPSEEK)
    public ChatClient primaryDeepseekChatClient() {
        log.info("Setting Deepseek chat client as Primary");
        if (deepseekChatClient == null) {
            throw new IllegalStateException("Deepseek chat client is not available. Please check if spring.ai.deepseek.chat.enabled=true");
        }
        return deepseekChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.BAIDU)
    public ChatClient primaryBaiduChatClient() {
        log.info("Setting Baidu chat client as Primary");
        if (baiduChatClient == null) {
            throw new IllegalStateException("Baidu chat client is not available. Please check if spring.ai.baidu.chat.enabled=true");
        }
        return baiduChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.TENCENT)
    public ChatClient primaryTencentChatClient() {
        log.info("Setting Tencent chat client as Primary");
        if (tencentChatClient == null) {
            throw new IllegalStateException("Tencent chat client is not available. Please check if spring.ai.tencent.chat.enabled=true");
        }
        return tencentChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.VOLCENGINE)
    public ChatClient primaryVolcengineChatClient() {
        log.info("Setting Volcengine chat client as Primary");
        if (volcengineChatClient == null) {
            throw new IllegalStateException("Volcengine chat client is not available. Please check if spring.ai.volcengine.chat.enabled=true");
        }
        return volcengineChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.OPENAI)
    public ChatClient primaryOpenaiChatClient() {
        log.info("Setting OpenAI chat client as Primary");
        if (openaiChatClient == null) {
            throw new IllegalStateException("OpenAI chat client is not available. Please check if spring.ai.openai.chat.enabled=true");
        }
        return openaiChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.OPENROUTER)
    public ChatClient primaryOpenrouterChatClient() {
        log.info("Setting OpenRouter chat client as Primary");
        if (openrouterChatClient == null) {
            throw new IllegalStateException("OpenRouter chat client is not available. Please check if spring.ai.openrouter.chat.enabled=true");
        }
        return openrouterChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.SILICONFLOW)
    public ChatClient primarySiliconflowChatClient() {
        log.info("Setting SiliconFlow chat client as Primary");
        if (siliconflowChatClient == null) {
            throw new IllegalStateException("SiliconFlow chat client is not available. Please check if spring.ai.siliconflow.chat.enabled=true");
        }
        return siliconflowChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmProviderConstants.GITEE)
    public ChatClient primaryGiteeChatClient() {
        log.info("Setting Gitee chat client as Primary");
        if (giteeChatClient == null) {
            throw new IllegalStateException("Gitee chat client is not available. Please check if spring.ai.gitee.chat.enabled=true");
        }
        return giteeChatClient;
    }

} 