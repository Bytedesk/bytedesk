/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 16:35:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 10:42:01
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
 * LLM 提供商常量字符串 - 专门用于注解等需要编译时常量的场景
 * 这些常量与 LlmProviderType 枚举的值保持一致
 */
public class LlmProviderConstants {
    private LlmProviderConstants() {}
    
    // 已上线
    public static final String OLLAMA = "ollama";
    public static final String ZHIPUAI = "zhipuai";
    public static final String DEEPSEEK = "deepseek";
    public static final String DASHSCOPE = "dashscope";
    public static final String SILICONFLOW = "silicon";
    public static final String GITEE = "gitee";
    public static final String TENCENT = "tencent";
    public static final String BAIDU = "baidu";
    public static final String VOLCENGINE = "volcengine";
    public static final String MINIMAX = "minimax";

    // 开发中
    public static final String XINGHUO = "xinghuo";
    public static final String MOONSHOT = "moonshot";
    public static final String BAICHUAN = "baichuan";
    public static final String YI = "yi";
    public static final String STEPFUN = "stepfun";
    public static final String OPENROUTER = "openrouter";
    public static final String GROQ = "groq";
    public static final String ANTHROPIC = "anthropic";
    public static final String OPENAI = "openai";
    public static final String GEMINI = "gemini";
    public static final String AIHUBMIX = "aihubmix";

    // 自定义模型提供商
    public static final String CUSTOM = "custom";

    // 第三方知识库
    public static final String COZE = "coze";
    public static final String DIFY = "dify";
    public static final String N8N = "n8n";
    public static final String MAXKB = "maxkb";
    public static final String RAGFLOW = "ragflow";
    public static final String FASTGPT = "fastgpt";
    public static final String WEKNORA = "weknora";
}