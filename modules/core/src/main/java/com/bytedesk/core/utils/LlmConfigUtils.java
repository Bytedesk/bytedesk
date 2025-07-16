/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 09:44:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 15:37:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import org.springframework.core.env.Environment;

import com.bytedesk.core.base.LlmProviderConfigDefault;
import com.bytedesk.core.constant.LlmConsts;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LlmConfigUtils {

    public LlmProviderConfigDefault getLlmProviderConfigDefault(Environment environment) {
        // Get the default chat provider and model
        String defaultChatProvider = environment.getProperty("spring.ai.model.chat", LlmConsts.DEFAULT_CHAT_PROVIDER);
        String defaultChatModel = getChatModel(environment, defaultChatProvider);

        // Get the default embedding provider and model
        String defaultEmbeddingProvider = environment.getProperty("spring.ai.model.embedding", LlmConsts.DEFAULT_EMBEDDING_PROVIDER);
        String defaultEmbeddingModel = getEmbeddingModel(environment, defaultEmbeddingProvider);

        // Get the default vision provider and model
        String defaultVisionProvider = environment.getProperty("spring.ai.model.vision", LlmConsts.DEFAULT_VISION_PROVIDER);
        String defaultVisionModel = getVisionModel(environment, defaultVisionProvider);

        // Get the default voice provider and model
        String defaultVoiceProvider = environment.getProperty("spring.ai.model.voice", LlmConsts.DEFAULT_VOICE_PROVIDER);
        String defaultVoiceModel = getVoiceModel(environment, defaultVoiceProvider);

        // Get the default rerank provider and model
        String defaultRerankProvider = environment.getProperty("spring.ai.model.rerank", LlmConsts.DEFAULT_RERANK_PROVIDER);
        String defaultRerankModel = getRerankModel(environment, defaultRerankProvider);

        return LlmProviderConfigDefault.builder()
                .defaultChatProvider(defaultChatProvider)
                .defaultChatModel(defaultChatModel)
                .defaultEmbeddingProvider(defaultEmbeddingProvider)
                .defaultEmbeddingModel(defaultEmbeddingModel)
                .defaultVisionProvider(defaultVisionProvider)
                .defaultVisionModel(defaultVisionModel)
                .defaultVoiceProvider(defaultVoiceProvider)
                .defaultVoiceModel(defaultVoiceModel)
                .defaultRerankProvider(defaultRerankProvider)
                .defaultRerankModel(defaultRerankModel)
                .build();
    }

    private String getChatModel(Environment environment, String provider) {
        switch (provider) {
            case LlmConsts.ZHIPUAI:
                return environment.getProperty("spring.ai.zhipuai.chat.options.model", "glm-4-flash");
            case LlmConsts.OLLAMA:
                return environment.getProperty("spring.ai.ollama.chat.options.model", "qwen3:0.6b");
            case LlmConsts.DEEPSEEK:
                return environment.getProperty("spring.ai.deepseek.chat.options.model", "deepseek-chat");
            case LlmConsts.DASHSCOPE:
                return environment.getProperty("spring.ai.dashscope.chat.options.model", "deepseek-r1");
            case LlmConsts.SILICONFLOW:
                return environment.getProperty("spring.ai.siliconflow.chat.options.model", "Qwen/QwQ-32B");
            case LlmConsts.GITEE:
                return environment.getProperty("spring.ai.gitee.chat.options.model", "Qwen/QwQ-32B");
            case LlmConsts.TENCENT:
                return environment.getProperty("spring.ai.tencent.chat.options.model", "hunyuan-t1-latest");
            case LlmConsts.BAIDU:
                return environment.getProperty("spring.ai.baidu.chat.options.model", "ernie-x1-32k-preview");
            case LlmConsts.VOLCENGINE:
                return environment.getProperty("spring.ai.volcengine.chat.options.model", "doubao-1-5-pro-32k-250115");
            case LlmConsts.OPENAI:
                return environment.getProperty("spring.ai.openai.chat.options.model", "gpt-4o");
            case LlmConsts.OPENROUTER:
                return environment.getProperty("spring.ai.openrouter.chat.options.model", "");
            case LlmConsts.MOONSHOT:
                return environment.getProperty("spring.ai.moonshot.chat.options.model", "moonshot-v1-8k");
            case LlmConsts.MINIMAX:
                return environment.getProperty("spring.ai.minimax.chat.options.model", "minimax-v1");
            default:
                return LlmConsts.DEFAULT_CHAT_MODEL;
        }
    }

    private String getEmbeddingModel(Environment environment, String provider) {
        switch (provider) {
            case LlmConsts.ZHIPUAI:
                return environment.getProperty("spring.ai.zhipuai.embedding.options.model", "embedding-2");
            case LlmConsts.OLLAMA:
                return environment.getProperty("spring.ai.ollama.embedding.options.model", "bge-m3:latest");
            case LlmConsts.DASHSCOPE:
                return environment.getProperty("spring.ai.dashscope.embedding.options.model", "");
            default:
                return LlmConsts.DEFAULT_EMBEDDING_MODEL;
        }
    }

    private String getVisionModel(Environment environment, String provider) {
        switch (provider) {
            case LlmConsts.ZHIPUAI:
                return environment.getProperty("spring.ai.zhipuai.vision.options.model", "llava:latest");
            case LlmConsts.OLLAMA:
                return environment.getProperty("spring.ai.ollama.vision.options.model", "llava:latest");
            case LlmConsts.OPENAI:
                return environment.getProperty("spring.ai.openai.vision.options.model", "gpt-4o");
            default:
                return LlmConsts.DEFAULT_VISION_MODEL;
        }
    }

    private String getVoiceModel(Environment environment, String provider) {
        switch (provider) {
            case LlmConsts.ZHIPUAI:
                return environment.getProperty("spring.ai.zhipuai.voice.options.model", "mxbai-tts:latest");
            case LlmConsts.OLLAMA:
                return environment.getProperty("spring.ai.ollama.voice.options.model", "mxbai-tts:latest");
            case LlmConsts.OPENAI:
                return environment.getProperty("spring.ai.openai.audio.voice.options.model", "tts-1");
            default:
                return LlmConsts.DEFAULT_VOICE_MODEL;
        }
    }

    private String getRerankModel(Environment environment, String provider) {
        switch (provider) {
            case LlmConsts.ZHIPUAI:
                return environment.getProperty("spring.ai.zhipuai.rerank.options.model", "linux6200/bge-reranker-v2-m3:latest");
            case LlmConsts.OLLAMA:
                return environment.getProperty("spring.ai.ollama.embedding.options.model.rerank", "linux6200/bge-reranker-v2-m3:latest");
            default:
                return LlmConsts.DEFAULT_RERANK_MODEL;
        }
    }
} 