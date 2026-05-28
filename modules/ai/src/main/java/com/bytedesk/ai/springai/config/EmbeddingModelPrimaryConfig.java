/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-05-28 00:00:00
 * @LastEditors: GitHub Copilot
 * @LastEditTime: 2026-05-28 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.SpringAIModels;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据 spring.ai.model.embedding 配置显式导出 Primary EmbeddingModel，
 * 避免多个 provider 同时启用时按类型注入出现歧义。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class EmbeddingModelPrimaryConfig {

    private final ApplicationContext applicationContext;

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = SpringAIModels.ZHIPUAI)
    public EmbeddingModel primaryZhipuaiEmbeddingModel() {
        return requireEmbeddingModel("zhiPuAiEmbeddingModel", "ZhiPuAI", "spring.ai.zhipuai.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = SpringAIModels.OLLAMA)
    public EmbeddingModel primaryOllamaEmbeddingModel() {
        return requireEmbeddingModel("OllamaEmbeddingModel", "Ollama", "spring.ai.ollama.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.DASHSCOPE)
    public EmbeddingModel primaryDashscopeEmbeddingModel() {
        return requireEmbeddingModel("dashscopeEmbeddingModel", "Dashscope", "spring.ai.dashscope.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.BAIDU)
    public EmbeddingModel primaryBaiduEmbeddingModel() {
        return requireEmbeddingModel("baiduEmbeddingModel", "Baidu", "spring.ai.baidu.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.TENCENT)
    public EmbeddingModel primaryTencentEmbeddingModel() {
        return requireEmbeddingModel("tencentEmbeddingModel", "Tencent", "spring.ai.tencent.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.VOLCENGINE)
    public EmbeddingModel primaryVolcengineEmbeddingModel() {
        return requireEmbeddingModel("volcengineEmbeddingModel", "Volcengine", "spring.ai.volcengine.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = SpringAIModels.OPENAI)
    public EmbeddingModel primaryOpenaiEmbeddingModel() {
        return requireEmbeddingModel("openaiEmbeddingModel", "OpenAI", "spring.ai.openai.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.OPENROUTER)
    public EmbeddingModel primaryOpenrouterEmbeddingModel() {
        return requireEmbeddingModel("openrouterEmbeddingModel", "OpenRouter", "spring.ai.openrouter.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.SILICONFLOW)
    public EmbeddingModel primarySiliconflowEmbeddingModel() {
        return requireEmbeddingModel("siliconFlowEmbeddingModel", "SiliconFlow", "spring.ai.siliconflow.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.GITEE)
    public EmbeddingModel primaryGiteeEmbeddingModel() {
        return requireEmbeddingModel("giteeEmbeddingModel", "Gitee", "spring.ai.gitee.embedding.enabled=true");
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = LlmProviderConstants.MINIMAX)
    public EmbeddingModel primaryMinimaxEmbeddingModel() {
        return requireEmbeddingModel("minimaxEmbeddingModel", "MiniMax", "spring.ai.minimax.embedding.enabled=true");
    }

    private EmbeddingModel requireEmbeddingModel(String beanName, String provider, String enableHint) {
        if (!BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, EmbeddingModel.class).toString().isEmpty()) {
            log.info("Setting {} embedding model as Primary", provider);
        }
        if (!applicationContext.containsBean(beanName)) {
            throw new IllegalStateException(provider + " embedding model is not available. Please check if " + enableHint);
        }
        return applicationContext.getBean(beanName, EmbeddingModel.class);
    }
}