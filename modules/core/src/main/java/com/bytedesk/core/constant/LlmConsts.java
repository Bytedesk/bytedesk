/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 16:35:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 14:29:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

public class LlmConsts {
    private LlmConsts() {}
    
    // 已上线
    public static final String OLLAMA = "ollama";
    public static final String ZHIPU = "zhipu";
    public static final String DEEPSEEK = "deepseek";
    public static final String DASHSCOPE = "dashscope";
    public static final String SILICONFLOW = "silicon";
    public static final String GITEE = "gitee";
    public static final String TENCENT = "tencent";
    public static final String BAIDU = "baidu";
    public static final String VOLCENGINE = "volcengine";

    // 开发中
    public static final String XINGHUO = "xinghuo";
    public static final String MOONSHOT = "moonshot";
    public static final String BAICHUAN = "baichuan";
    public static final String MINIMAX = "minimax";
    public static final String YI = "yi";
    public static final String STEPFUN = "stepfun";
    public static final String OPENROUTER = "openrouter";
    public static final String GROQ = "groq";
    public static final String ANTHROPIC = "anthropic";
    public static final String OPENAI = "openai";
    public static final String GEMINI = "gemini";
    public static final String AIHUBMIX = "aihubmix";

    // 默认提供商
    public static final String DEFAULT_PROVIDER = OLLAMA;
    // 默认模型
    public static final String DEFAULT_MODEL = "qwen2.5:1.5b";
    // 默认嵌入提供商
    public static final String DEFAULT_EMBEDDING_PROVIDER = OLLAMA;
    // 默认嵌入模型
    public static final String DEFAULT_EMBEDDING_MODEL = "nomic-embed-text:latest";

    
}
