/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-27 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 08:49:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import com.bytedesk.core.base.LlmProviderConfigDefault;
import com.bytedesk.core.constant.LlmConsts;

class LlmConfigUtilsTest {

    @Test
    void testGetLlmProviderConfigDefault_WithZhipuai() {
        // Given
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.ai.model.chat", "zhipuai");
        environment.setProperty("spring.ai.model.embedding", "zhipuai");
        environment.setProperty("spring.ai.model.vision", "zhipuai");
        environment.setProperty("spring.ai.model.audio", "zhipuai");
        environment.setProperty("spring.ai.model.rerank", "zhipuai");
        environment.setProperty("spring.ai.zhipuai.chat.options.model", "glm-4-flash");
        environment.setProperty("spring.ai.zhipuai.embedding.options.model", "embedding-2");

        // When
        LlmProviderConfigDefault config = LlmConfigUtils.getLlmProviderConfigDefault(environment);

        // Then
        assertNotNull(config);
        assertEquals("zhipuai", config.getDefaultChatProvider());
        assertEquals("glm-4-flash", config.getDefaultChatModel());
        assertEquals("zhipuai", config.getDefaultEmbeddingProvider());
        assertEquals("embedding-2", config.getDefaultEmbeddingModel());
        assertEquals("zhipuai", config.getDefaultVisionProvider());
        assertEquals("llava:latest", config.getDefaultVisionModel());
        assertEquals("zhipuai", config.getDefaultVoiceProvider());
        assertEquals("mxbai-tts:latest", config.getDefaultVoiceModel());
        assertEquals("zhipuai", config.getDefaultRerankProvider());
        assertEquals("linux6200/bge-reranker-v2-m3:latest", config.getDefaultRerankModel());
    }

    @Test
    void testGetLlmProviderConfigDefault_WithOllama() {
        // Given
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.ai.model.chat", "ollama");
        environment.setProperty("spring.ai.model.embedding", "ollama");
        environment.setProperty("spring.ai.model.vision", "ollama");
        environment.setProperty("spring.ai.model.audio", "ollama");
        environment.setProperty("spring.ai.model.rerank", "ollama");
        environment.setProperty("spring.ai.ollama.chat.options.model", "qwen3:0.6b");
        environment.setProperty("spring.ai.ollama.embedding.options.model", "bge-m3:latest");

        // When
        LlmProviderConfigDefault config = LlmConfigUtils.getLlmProviderConfigDefault(environment);

        // Then
        assertNotNull(config);
        assertEquals("ollama", config.getDefaultChatProvider());
        assertEquals("qwen3:0.6b", config.getDefaultChatModel());
        assertEquals("ollama", config.getDefaultEmbeddingProvider());
        assertEquals("bge-m3:latest", config.getDefaultEmbeddingModel());
        assertEquals("ollama", config.getDefaultVisionProvider());
        assertEquals("llava:latest", config.getDefaultVisionModel());
        assertEquals("ollama", config.getDefaultVoiceProvider());
        assertEquals("mxbai-tts:latest", config.getDefaultVoiceModel());
        assertEquals("ollama", config.getDefaultRerankProvider());
        assertEquals("linux6200/bge-reranker-v2-m3:latest", config.getDefaultRerankModel());
    }

    @Test
    void testGetLlmProviderConfigDefault_WithDefaults() {
        // Given
        MockEnvironment environment = new MockEnvironment();

        // When
        LlmProviderConfigDefault config = LlmConfigUtils.getLlmProviderConfigDefault(environment);

        // Then
        assertNotNull(config);
        assertEquals(LlmConsts.DEFAULT_TEXT_PROVIDER, config.getDefaultChatProvider());
        assertEquals(LlmConsts.DEFAULT_TEXT_MODEL, config.getDefaultChatModel());
        assertEquals(LlmConsts.DEFAULT_EMBEDDING_PROVIDER, config.getDefaultEmbeddingProvider());
        assertEquals(LlmConsts.DEFAULT_EMBEDDING_MODEL, config.getDefaultEmbeddingModel());
        assertEquals(LlmConsts.DEFAULT_VISION_PROVIDER, config.getDefaultVisionProvider());
        assertEquals(LlmConsts.DEFAULT_VISION_MODEL, config.getDefaultVisionModel());
        assertEquals(LlmConsts.DEFAULT_AUDIO_PROVIDER, config.getDefaultVoiceProvider());
        assertEquals(LlmConsts.DEFAULT_AUDIO_MODEL, config.getDefaultVoiceModel());
        assertEquals(LlmConsts.DEFAULT_RERANK_PROVIDER, config.getDefaultRerankProvider());
        assertEquals(LlmConsts.DEFAULT_RERANK_MODEL, config.getDefaultRerankModel());
    }

    @Test
    void testGetLlmProviderConfigDefault_WithDeepseek() {
        // Given
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.ai.model.chat", "deepseek");
        environment.setProperty("spring.ai.deepseek.chat.options.model", "deepseek-chat");

        // When
        LlmProviderConfigDefault config = LlmConfigUtils.getLlmProviderConfigDefault(environment);

        // Then
        assertNotNull(config);
        assertEquals("deepseek", config.getDefaultChatProvider());
        assertEquals("deepseek-chat", config.getDefaultChatModel());
    }
} 