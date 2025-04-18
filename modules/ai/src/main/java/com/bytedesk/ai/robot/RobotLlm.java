/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:02:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 14:33:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RobotLlm {

    // @Value("${spring.ai.ollama.chat.options.model:qwen2.5:1.5b}")
    // private static String defaultModel;

    // @Value("${spring.ai.ollama.embedding.options.model:qwen2.5:1.5b}")
    // private static String defaultEmbeddingModel;

    // 默认启用llm问答
    @Builder.Default
    @Column(name = "is_llm_enabled")
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "llm_top_k")
    private int topK = 3;

    @Builder.Default
    @Column(name = "llm_score_threshold")
    private Double scoreThreshold = 0.5;

    @Builder.Default
    @Column(name = "llm_provider")
    private String provider = LlmConsts.DEFAULT_PROVIDER;
    // private String provider = "ollama";
    
    @Builder.Default
    @Column(name = "llm_model")
    private String model = LlmConsts.DEFAULT_MODEL; 
    //"glm-4-flash";
    // private String model = defaultModel;

    // 迁移到kbase
    // @Builder.Default
    // @Column(name = "llm_embedding_provider")
    // private String embeddingProvider = "zhipu";
    
    // @Builder.Default
    // @Column(name = "llm_embedding_model")
    // private String embeddingModel = "embedding-v2";
    // // private String embeddingModel = defaultEmbeddingModel;

    // 温度越低，回答结果越固定，随机性越低
    @Builder.Default
    @Column(name = "llm_temperature")
    private float temperature = 0.0f;

    @Builder.Default
    @Column(name = "llm_top_p")
    private float topP = 0.7f;

    @Builder.Default
    @Column(name = "llm_prompt", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String prompt = RobotConsts.ROBOT_LLM_DEFAULT_PROMPT;
    // private String prompt = "";
    // private String promptTemplate =
    // "请根据上下文信息回答问题：\n\n上下文信息：\n{context}\n\n问题：{question}\n\n答案：";

    // 上下文消息数，默认3条。一同传递给大模型
    @Builder.Default
    @Column(name = "llm_context_msg_count")
    private int contextMsgCount = 3;
}
