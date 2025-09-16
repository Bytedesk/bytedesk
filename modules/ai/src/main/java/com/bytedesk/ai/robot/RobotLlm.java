/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:02:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 10:10:41
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
import com.bytedesk.core.constant.LlmDefaults;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html
@Embeddable
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RobotLlm {

    // 默认启用llm问答
    @Builder.Default
    @Column(name = "is_llm_enabled")
    private Boolean enabled = true;

    @Builder.Default
    @Column(name = "is_thinking_enabled")
    private Boolean enableThinking = false;

    // 启用搜索
    @Builder.Default
    @Column(name = "is_search_enabled")
    private Boolean enableSearch = false;

    // 文本对话模型提供商
    @Builder.Default
    @Column(name = "llm_text_provider")
    private String textProvider = LlmDefaults.DEFAULT_TEXT_PROVIDER;

    @Column(name = "llm_text_provider_uid")
    private String textProviderUid;
    
    // text chat model name
    @Builder.Default
    @Column(name = "llm_text_model")
    private String textModel = LlmDefaults.DEFAULT_TEXT_MODEL; 

    // 视觉模型
    // 启用vision
    @Builder.Default
    @Column(name = "is_vision_enabled")
    private Boolean visionEnabled = false;

    // 视觉模型提供商
    @Builder.Default
    @Column(name = "llm_vision_provider")
    private String visionProvider = LlmDefaults.DEFAULT_VISION_PROVIDER;

    @Column(name = "llm_vision_provider_uid")
    private String visionProviderUid;

    // 视觉模型名称
    @Builder.Default
    @Column(name = "llm_vision_model")
    private String visionModel = LlmDefaults.DEFAULT_VISION_MODEL;

    // 语音模型
    @Builder.Default
    @Column(name = "is_audio_enabled")
    private Boolean audioEnabled = false;

    // 语音模型提供商
    @Builder.Default
    @Column(name = "llm_audio_provider")
    private String audioProvider = LlmDefaults.DEFAULT_AUDIO_PROVIDER;

    @Column(name = "llm_audio_provider_uid")
    private String audioProviderUid;

    // 语音模型名称
    @Builder.Default
    @Column(name = "llm_audio_model")
    private String audioModel = LlmDefaults.DEFAULT_AUDIO_MODEL;

    // 启用rerank
    @Builder.Default
    @Column(name = "is_rerank_enabled")
    private Boolean rerankEnabled = false;

    // rerank model provider
    @Builder.Default
    @Column(name = "llm_rerank_provider")
    private String rerankProvider = LlmDefaults.DEFAULT_RERANK_PROVIDER;

    @Column(name = "llm_rerank_provider_uid")
    private String rerankProviderUid;

    // rerank model name
    @Builder.Default
    @Column(name = "llm_rerank_model")
    private String rerankModel = LlmDefaults.DEFAULT_RERANK_MODEL;

    // rewrite
    @Builder.Default
    @Column(name = "llm_rewrite_enabled")
    private Boolean rewriteEnabled = false;

    // rewrite model provider
    @Builder.Default
    @Column(name = "llm_rewrite_provider")
    private String rewriteProvider = LlmDefaults.DEFAULT_REWRITE_PROVIDER;

    @Column(name = "llm_rewrite_provider_uid")
    private String rewriteProviderUid;

    // rewrite model name
    @Builder.Default
    @Column(name = "llm_rewrite_model")
    private String rewriteModel = LlmDefaults.DEFAULT_REWRITE_MODEL;

    // rewrite prompt
    @Builder.Default
    @Column(name = "llm_rewrite_prompt", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String rewritePrompt = RobotConsts.ROBOT_LLM_DEFAULT_REWRITE_PROMPT;

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
