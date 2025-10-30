/*
 * RobotLlmEntity: 独立大模型配置实体（由原 @Embeddable RobotLlm 迁移而来）
 */
package com.bytedesk.ai.robot_settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.llm.LlmDefaults;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ai_robot_llm")
public class RobotLlmEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Column(name = "is_llm_enabled")
    private Boolean enabled = true;

    @Builder.Default
    @Column(name = "is_thinking_enabled")
    private Boolean enableThinking = true;

    @Builder.Default
    @Column(name = "is_streaming_enabled")
    private Boolean enableStreaming = false;

    @Builder.Default
    @Column(name = "is_search_enabled")
    private Boolean enableSearch = false;

    @Builder.Default
    @Column(name = "llm_text_provider")
    private String textProvider = LlmDefaults.DEFAULT_CHAT_PROVIDER;

    @Column(name = "llm_text_provider_uid")
    private String textProviderUid;

    @Builder.Default
    @Column(name = "llm_text_model")
    private String textModel = LlmDefaults.DEFAULT_CHAT_MODEL;

    @Builder.Default
    @Column(name = "is_audio_enabled")
    private Boolean audioEnabled = false;

    @Builder.Default
    @Column(name = "llm_audio_provider")
    private String audioProvider = LlmDefaults.DEFAULT_AUDIO_PROVIDER;

    @Column(name = "llm_audio_provider_uid")
    private String audioProviderUid;

    @Builder.Default
    @Column(name = "llm_audio_model")
    private String audioModel = LlmDefaults.DEFAULT_AUDIO_MODEL;

    @Builder.Default
    @Column(name = "is_rerank_enabled")
    private Boolean rerankEnabled = false;

    @Builder.Default
    @Column(name = "llm_rerank_provider")
    private String rerankProvider = LlmDefaults.DEFAULT_RERANK_PROVIDER;

    @Column(name = "llm_rerank_provider_uid")
    private String rerankProviderUid;

    @Builder.Default
    @Column(name = "llm_rerank_model")
    private String rerankModel = LlmDefaults.DEFAULT_RERANK_MODEL;

    @Builder.Default
    @Column(name = "llm_rewrite_enabled")
    private Boolean rewriteEnabled = false;

    @Builder.Default
    @Column(name = "llm_rewrite_provider")
    private String rewriteProvider = LlmDefaults.DEFAULT_REWRITE_PROVIDER;

    @Column(name = "llm_rewrite_provider_uid")
    private String rewriteProviderUid;

    @Builder.Default
    @Column(name = "llm_rewrite_model")
    private String rewriteModel = LlmDefaults.DEFAULT_REWRITE_MODEL;

    @Builder.Default
    @Column(name = "llm_rewrite_prompt", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String rewritePrompt = "";

    @Builder.Default
    @Column(name = "llm_search_type")
    private String searchType = "FULLTEXT";

    @Builder.Default
    @Column(name = "llm_top_k")
    private Integer topK = 3;

    @Builder.Default
    @Column(name = "llm_score_threshold")
    private Double scoreThreshold = 0.5;

    @Builder.Default
    @Column(name = "llm_do_sample")
    private Boolean doSample = true;

    @Builder.Default
    @Column(name = "llm_max_tokens")
    private Integer maxTokens = 1024;

    @Builder.Default
    @Column(name = "llm_temperature")
    private Double temperature = 0.0;

    @Builder.Default
    @Column(name = "llm_top_p")
    private Double topP = 0.7;

    @Builder.Default
    @Column(name = "llm_tools")
    private List<String> tools = new ArrayList<>();

    @Builder.Default
    @Column(name = "llm_tool_choice")
    private String toolChoice = "auto";

    @Builder.Default
    @Column(name = "llm_stops")
    private List<String> stops = new ArrayList<>();

    @Builder.Default
    @Column(name = "llm_response_format")
    private String responseFormat = "text";

    @Column(name = "llm_request_id")
    private String requestId;

    @Column(name = "llm_user_id")
    private String userId;

    @Builder.Default
    @Column(name = "llm_prompt", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String prompt = I18Consts.I18N_ROBOT_DEFAULT_REPLY;

    @Builder.Default
    @Column(name = "llm_context_msg_count")
    private Integer contextMsgCount = 0;

    @Builder.Default
    private String defaultReply = I18Consts.I18N_ROBOT_DEFAULT_REPLY;
}
