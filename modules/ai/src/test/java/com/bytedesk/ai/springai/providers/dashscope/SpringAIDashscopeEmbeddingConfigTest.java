/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-14 17:22:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring AI Dashscope Embedding配置测试
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.dashscope.embedding.enabled=true",
    "spring.ai.dashscope.api-key=test-key",
    "spring.ai.dashscope.embedding.options.model=text-embedding-v1"
})
class SpringAIDashscopeEmbeddingConfigTest {

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeEmbeddingApi")
    private OpenAiApi dashscopeEmbeddingApi;

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeEmbeddingOptions")
    private OpenAiEmbeddingOptions dashscopeEmbeddingOptions;

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeEmbeddingModel")
    private EmbeddingModel dashscopeEmbeddingModel;

    @Test
    void testDashscopeEmbeddingApiBean() {
        assertNotNull(dashscopeEmbeddingApi, "Dashscope Embedding API should be created");
    }

    @Test
    void testDashscopeEmbeddingOptionsBean() {
        assertNotNull(dashscopeEmbeddingOptions, "Dashscope Embedding Options should be created");
        assertEquals("text-embedding-v1", dashscopeEmbeddingOptions.getModel(), "Model should be set correctly");
    }

    @Test
    void testDashscopeEmbeddingModelBean() {
        assertNotNull(dashscopeEmbeddingModel, "Dashscope Embedding Model should be created");
        assertTrue(dashscopeEmbeddingModel.getClass().getSimpleName().contains("OpenAi"), 
                  "Should be an OpenAi embedding model");
    }

    @Test
    void testEmbeddingModelFunctionality() {
        if (dashscopeEmbeddingModel != null) {
            try {
                // 测试embedding功能（需要真实的API key才能成功）
                var embedding = dashscopeEmbeddingModel.embed("test");
                assertNotNull(embedding, "Embedding should not be null");
                assertTrue(embedding.length > 0, "Embedding should have dimensions");
            } catch (Exception e) {
                // 如果API key无效，这是预期的行为
                assertTrue(e.getMessage().contains("401") || e.getMessage().contains("Unauthorized") || 
                          e.getMessage().contains("Invalid"), "Should fail with authentication error");
            }
        }
    }
} 