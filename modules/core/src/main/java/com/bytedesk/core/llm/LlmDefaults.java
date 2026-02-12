/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 16:35:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 13:21:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.llm;

/**
 * LLM 默认配置常量
 * https://bailian.console.aliyun.com/cn-beijing/?tab=model#/model-market/all
 * 默认使用阿里云百炼DashScope模型
 */
public class LlmDefaults {
    private LlmDefaults() {}
    
    // 默认对话模型提供商
    public static final String DEFAULT_CHAT_PROVIDER = LlmProviderConstants.DASHSCOPE;
    public static final String DEFAULT_CHAT_MODEL = "qwen3-max";
    
    // 默认文字对话模型提供商
    public static final String DEFAULT_TEXT_PROVIDER = LlmProviderConstants.DASHSCOPE;
    public static final String DEFAULT_TEXT_MODEL = "qwen3-max";
    
    // 默认视觉对话模型提供商
    public static final String DEFAULT_VISION_PROVIDER = LlmProviderConstants.DASHSCOPE;
    public static final String DEFAULT_VISION_MODEL = "qwen3-vl-plus";
    
    // 默认文本转语音提供商
    public static final String DEFAULT_AUDIO_PROVIDER = LlmProviderConstants.DASHSCOPE;
    public static final String DEFAULT_AUDIO_MODEL = "qwen3-tts-instruct-flash-realtime";
    
    // 默认Embedding提供商
    public static final String DEFAULT_EMBEDDING_PROVIDER = LlmProviderConstants.DASHSCOPE;
    public static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-v4";
    
    // 默认Rerank提供商
    public static final String DEFAULT_RERANK_PROVIDER = LlmProviderConstants.DASHSCOPE;
    public static final String DEFAULT_RERANK_MODEL = "qwen3-rerank";
    
    // 默认rewrite提供商
    public static final String DEFAULT_REWRITE_PROVIDER = LlmProviderConstants.DASHSCOPE;
    public static final String DEFAULT_REWRITE_MODEL = "qwen3-max";
}