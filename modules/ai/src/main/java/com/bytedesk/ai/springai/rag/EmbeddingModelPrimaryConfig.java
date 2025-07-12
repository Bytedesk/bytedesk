/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-11 10:19:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;

/**
 * 根据spring.ai.model.embedding配置动态设置Primary的EmbeddingModel
 * 支持的值：zhipuai, ollama, none
 */
@Slf4j
@Configuration
public class EmbeddingModelPrimaryConfig {

    @Value("${spring.ai.model.embedding:none}")
    private String embeddingModel;

    @Autowired(required = false)
    @Qualifier("bytedeskZhipuaiEmbeddingModel")
    private EmbeddingModel zhipuaiEmbeddingModel;

    @Autowired(required = false)
    @Qualifier("bytedeskOllamaEmbeddingModel")
    private EmbeddingModel ollamaEmbeddingModel;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.ai.model.embedding", havingValue = "zhipuai")
    public EmbeddingModel primaryZhipuaiEmbeddingModel() {
        log.info("Setting ZhiPuAI embedding model as Primary");
        if (zhipuaiEmbeddingModel == null) {
            throw new IllegalStateException("ZhiPuAI embedding model is not available. Please check if spring.ai.zhipuai.embedding.enabled=true");
        }
        return zhipuaiEmbeddingModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.ai.model.embedding", havingValue = "ollama")
    public EmbeddingModel primaryOllamaEmbeddingModel() {
        log.info("Setting Ollama embedding model as Primary");
        if (ollamaEmbeddingModel == null) {
            throw new IllegalStateException("Ollama embedding model is not available. Please check if spring.ai.ollama.embedding.enabled=true");
        }
        return ollamaEmbeddingModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.ai.model.embedding", havingValue = "none", matchIfMissing = true)
    public EmbeddingModel noPrimaryEmbeddingModel() {
        log.warn("No embedding model configured as Primary. Set spring.ai.model.embedding to 'zhipuai' or 'ollama' to use embedding features.");
        // 返回一个空的EmbeddingModel实现，或者抛出异常
        throw new IllegalStateException("No embedding model configured. Please set spring.ai.model.embedding to 'zhipuai' or 'ollama'");
    }
} 