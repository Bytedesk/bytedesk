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

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.SpringAIModels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.bytedesk.core.constant.LlmConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 根据spring.ai.model.chat配置动态设置Primary的ChatClient
 * 支持的值：zhipuai, ollama, dashscope, deepseek, baidu, tencent, volcengine, openai, openrouter, siliconflow, gitee, none
 */
@Slf4j
@Configuration
public class ChatClientPrimaryConfig {

    @Value("${spring.ai.model.chat:none}")
    private String chatModel;

    @Autowired(required = false)
    @Qualifier("bytedeskZhipuaiChatClient")
    private ChatClient zhipuaiChatClient;

    @Autowired(required = false)
    @Qualifier("bytedeskOllamaChatClient")
    private ChatClient ollamaChatClient;

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeChatClient")
    private ChatClient dashscopeChatClient;

    @Autowired(required = false)
    @Qualifier("deepseekChatClient")
    private ChatClient deepseekChatClient;

    @Autowired(required = false)
    @Qualifier("baiduChatClient")
    private ChatClient baiduChatClient;

    @Autowired(required = false)
    @Qualifier("tencentChatClient")
    private ChatClient tencentChatClient;

    @Autowired(required = false)
    @Qualifier("volcengineChatClient")
    private ChatClient volcengineChatClient;

    @Autowired(required = false)
    @Qualifier("openaiChatClient")
    private ChatClient openaiChatClient;

    @Autowired(required = false)
    @Qualifier("openrouterChatClient")
    private ChatClient openrouterChatClient;

    @Autowired(required = false)
    @Qualifier("siliconFlowChatClient")
    private ChatClient siliconflowChatClient;

    @Autowired(required = false)
    @Qualifier("giteeChatClient")
    private ChatClient giteeChatClient;

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.ZHIPUAI)
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
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.DASHSCOPE)
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
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.BAIDU)
    public ChatClient primaryBaiduChatClient() {
        log.info("Setting Baidu chat client as Primary");
        if (baiduChatClient == null) {
            throw new IllegalStateException("Baidu chat client is not available. Please check if spring.ai.baidu.chat.enabled=true");
        }
        return baiduChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.TENCENT)
    public ChatClient primaryTencentChatClient() {
        log.info("Setting Tencent chat client as Primary");
        if (tencentChatClient == null) {
            throw new IllegalStateException("Tencent chat client is not available. Please check if spring.ai.tencent.chat.enabled=true");
        }
        return tencentChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.VOLCENGINE)
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
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.OPENROUTER)
    public ChatClient primaryOpenrouterChatClient() {
        log.info("Setting OpenRouter chat client as Primary");
        if (openrouterChatClient == null) {
            throw new IllegalStateException("OpenRouter chat client is not available. Please check if spring.ai.openrouter.chat.enabled=true");
        }
        return openrouterChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.SILICONFLOW)
    public ChatClient primarySiliconflowChatClient() {
        log.info("Setting SiliconFlow chat client as Primary");
        if (siliconflowChatClient == null) {
            throw new IllegalStateException("SiliconFlow chat client is not available. Please check if spring.ai.siliconflow.chat.enabled=true");
        }
        return siliconflowChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.GITEE)
    public ChatClient primaryGiteeChatClient() {
        log.info("Setting Gitee chat client as Primary");
        if (giteeChatClient == null) {
            throw new IllegalStateException("Gitee chat client is not available. Please check if spring.ai.gitee.chat.enabled=true");
        }
        return giteeChatClient;
    }

    // 当没有配置chat model时，不创建任何Primary bean，避免冲突
    // @Bean
    // @Primary
    // @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = "none", matchIfMissing = true)
    // public ChatClient noPrimaryChatClient() {
    //     log.warn("No chat client configured as Primary. Set spring.ai.model.chat to 'zhipuai', 'ollama', 'dashscope', 'deepseek', 'baidu', 'tencent', 'volcengine', 'openai', 'openrouter', 'siliconflow', or 'gitee' to use chat features.");
    //     throw new IllegalStateException("No chat client configured. Please set spring.ai.model.chat to 'zhipuai', 'ollama', 'dashscope', 'deepseek', 'baidu', 'tencent', 'volcengine', 'openai', 'openrouter', 'siliconflow', or 'gitee'");
    // }
} 