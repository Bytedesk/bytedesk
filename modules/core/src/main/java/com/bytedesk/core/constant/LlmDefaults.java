/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 16:35:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 10:43:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

/**
 * LLM 默认配置常量
 */
public class LlmDefaults {
    private LlmDefaults() {}
    
    // 默认 智谱AI
    
    // 默认文字对话模型提供商
    public static final String DEFAULT_TEXT_PROVIDER = LlmProviderConstants.ZHIPUAI;
    // 默认文字对话模型
    public static final String DEFAULT_TEXT_MODEL = "glm-4-flash";
    
    // 默认Vision提供商
    public static final String DEFAULT_VISION_PROVIDER = LlmProviderConstants.ZHIPUAI;
    // 默认Vision模型
    public static final String DEFAULT_VISION_MODEL = "llava:latest";
    
    // 默认Speech提供商
    public static final String DEFAULT_AUDIO_PROVIDER = LlmProviderConstants.ZHIPUAI;
    // 默认Speech模型
    public static final String DEFAULT_AUDIO_MODEL = "mxbai-tts:latest";
    
    // 默认Embedding提供商
    public static final String DEFAULT_EMBEDDING_PROVIDER = LlmProviderConstants.ZHIPUAI;
    // 默认Embedding模型
    public static final String DEFAULT_EMBEDDING_MODEL = "embedding-2";
    
    // 默认Rerank提供商
    public static final String DEFAULT_RERANK_PROVIDER = LlmProviderConstants.ZHIPUAI;
    // 默认Rerank模型
    public static final String DEFAULT_RERANK_MODEL = "linux6200/bge-reranker-v2-m3:latest";
    
    // 默认rewrite提供商
    public static final String DEFAULT_REWRITE_PROVIDER = LlmProviderConstants.ZHIPUAI;
    // 默认rewrite模型
    public static final String DEFAULT_REWRITE_MODEL = "glm-4-flash";
}