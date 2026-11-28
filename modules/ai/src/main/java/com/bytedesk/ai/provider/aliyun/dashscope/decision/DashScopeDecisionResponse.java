/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-26 15:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-26 15:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.aliyun.dashscope.decision;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 阿里云百炼决策模型响应，字段与官方返回保持一致：
 * model / request_id / answers / usage.input_tokens / latency_ms。
 *
 * <p>answers 的 key 与请求中的问题 id 一一对应；choice 与 score 额外返回 confidence。</p>
 *
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeDecisionResponse {

    private String model;

    private String requestId;

    @Builder.Default
    private Map<String, Answer> answers = new LinkedHashMap<>();

    private Usage usage;

    /** 服务端耗时（毫秒）。 */
    private Double latencyMs;

    /** 客户端总耗时（毫秒），含网络与反序列化。 */
    private Long durationMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {

        /** choice / noul / score，与问题类型一致。 */
        private String type;

        /** choice：选中的选项名。 */
        private String choice;

        /** noul：P(yes)，0=否 1=是。 */
        private Double noul;

        /** score：等级索引的概率加权期望，可落在两级之间（如 1.43）。 */
        private Double score;

        /** 每个选项 / 等级的概率（和为 1）。 */
        private Map<String, Double> probabilities;

        /** choice / score：该答案的置信度。 */
        private Double confidence;

        /** score：等级索引 → 等级描述。 */
        private Map<String, String> legend;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {

        private Integer inputTokens;

        private Integer outputTokens;

        private Integer totalTokens;
    }

    /**
     * 工单分流示例的请求体，便于直接演示「派单团队 choice + 是否升级 noul + 严重度 score」三问合一。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketRoutingDemoRequest {

        @NotBlank(message = "content must not be blank")
        @Size(max = 4000, message = "content must be at most 4000 characters")
        private String content;

        private String ticketId;

        private String model;

        private String providerUid;

        public Map<String, Object> toState() {
            Map<String, Object> state = new LinkedHashMap<>();
            if (ticketId != null && !ticketId.isBlank()) {
                state.put("ticket_id", ticketId);
            }
            state.put("content", content);
            return state;
        }

        public Map<String, DashScopeDecisionRequest.Question> toQuestions() {
            Map<String, DashScopeDecisionRequest.Question> questions = new LinkedHashMap<>();
            questions.put("department", DashScopeDecisionRequest.Question.choice("应该由哪个团队处理？",
                    Map.of("billing", "支付、退款和账单问题", "technical", "产品故障和集成问题", "other", "其他问题")));
            questions.put("escalate", DashScopeDecisionRequest.Question.noul("是否需要立即通知值班人员？"));
            questions.put("severity", DashScopeDecisionRequest.Question.score("这个问题有多严重？",
                    List.of("轻微问题，不影响功能", "部分功能受影响，但存在替代方案", "核心功能不可用，没有替代方案", "造成严重业务或安全影响")));
            return questions;
        }
    }
}
