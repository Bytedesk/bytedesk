/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:02:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 14:21:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.constant.I18Consts;
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

    // 默认启用llm问答
    @Builder.Default
    @Column(name = "is_llm_enabled")
    private Boolean enabled = true;

    // stream流式输出
    @Builder.Default
    @Column(name = "is_stream_enabled")
    private Boolean streamEnabled = true;

    // 文字对话模型提供商
    @Builder.Default
    @Column(name = "llm_chat_provider")
    private String chatProvider = LlmConsts.DEFAULT_CHAT_PROVIDER;
    
    // chat model name
    @Builder.Default
    @Column(name = "llm_chat_model")
    private String chatModel = LlmConsts.DEFAULT_CHAT_MODEL; 

    // 视觉模型
    // 启用vision
    @Builder.Default
    @Column(name = "is_vision_enabled")
    private Boolean visionEnabled = false;

    // 视觉模型提供商
    @Builder.Default
    @Column(name = "llm_vision_provider")
    private String visionProvider = LlmConsts.DEFAULT_VISION_PROVIDER;

    // 视觉模型名称
    @Builder.Default
    @Column(name = "llm_vision_model")
    private String visionModel = LlmConsts.DEFAULT_VISION_MODEL;

    // 语音模型
    // 启用speech
    @Builder.Default
    @Column(name = "is_speech_enabled")
    private Boolean speechEnabled = false;

    // 语音模型提供商
    @Builder.Default
    @Column(name = "llm_speech_provider")
    private String speechProvider = LlmConsts.DEFAULT_SPEECH_PROVIDER;

    // 语音模型名称
    @Builder.Default
    @Column(name = "llm_speech_model")
    private String speechModel = LlmConsts.DEFAULT_SPEECH_MODEL;

    // 启用rerank
    @Builder.Default
    @Column(name = "is_rerank_enabled")
    private Boolean rerankEnabled = false;

    // rerank model provider
    @Builder.Default
    @Column(name = "llm_rerank_provider")
    private String rerankProvider = LlmConsts.DEFAULT_RERANK_PROVIDER;

    // rerank model name
    @Builder.Default
    @Column(name = "ll_rerank_model")
    private String rerankModel = LlmConsts.DEFAULT_RERANK_MODEL;

    // search type
    @Builder.Default
    @Column(name = "llm_search_type")
    private String searchType = RobotSearchTypeEnum.FULLTEXT.name();

    @Builder.Default
    @Column(name = "llm_top_k")
    private Integer topK = 3;

    @Builder.Default
    @Column(name = "llm_score_threshold")
    private Double scoreThreshold = 0.5;

    // 温度越低，回答结果越固定，随机性越低
    @Builder.Default
    @Column(name = "llm_temperature")
    private Double temperature = 0.0;  // 从float改为Double

    @Builder.Default
    @Column(name = "llm_top_p")
    private Double topP = 0.7;  // 从float改为Double

    @Builder.Default
    @Column(name = "llm_prompt", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String prompt = RobotConsts.ROBOT_LLM_DEFAULT_PROMPT;
    
    // 上下文消息数，默认0条。一同传递给大模型
    @Builder.Default
    @Column(name = "llm_context_msg_count")
    private Integer contextMsgCount = 0;

    // 如果未匹配到关键词，默认回复内容
    @Builder.Default
    private String defaultReply = I18Consts.I18N_ROBOT_DEFAULT_REPLY;
    
}
