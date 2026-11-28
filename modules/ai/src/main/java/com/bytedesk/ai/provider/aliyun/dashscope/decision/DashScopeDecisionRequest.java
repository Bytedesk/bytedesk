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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 阿里云百炼决策模型请求，字段与官方接口一一对应：
 * {@code POST /compatible-mode/v1/systemone}，body = { model, state, questions }。
 *
 * <p>{@code state} 为待决策的业务状态（文本 / 对象 / 数组）；{@code questions} 的 key 为自定义问题 id，
 * value 含 type（choice / noul / score）、instructions、criteria。</p>
 *
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeDecisionRequest {

    /** 可选：覆盖默认模型 decision-model / decision-model-preview。 */
    @Pattern(regexp = "[A-Za-z0-9._:-]{1,100}", message = "model contains invalid characters")
    private String model;

    /** 可选：按 provider uid 取 apiKey/baseUrl，为空时用全局 spring.ai.dashscope.* 配置。 */
    @Size(max = 64, message = "providerUid must be at most 64 characters")
    private String providerUid;

    /**
     * 待决策的业务状态。字符串按纯文本处理；Map / List 按结构化对象序列化后送模型。
     */
    @NotNull(message = "state must not be null")
    private Object state;

    /** 问题表：key 为问题 id，一次请求最多 16 个问题。 */
    @NotEmpty(message = "questions must not be empty")
    @Size(max = 16, message = "questions must contain at most 16 items")
    @Valid
    @Builder.Default
    private Map<String, Question> questions = new LinkedHashMap<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Question {

        public static final String TYPE_CHOICE = "choice";

        public static final String TYPE_NOUL = "noul";

        public static final String TYPE_SCORE = "score";

        @NotNull(message = "type must not be null")
        @Pattern(regexp = "choice|noul|score", message = "type must be one of choice, noul, score")
        private String type;

        /** 问题描述或评判标准。 */
        @Size(max = 2000, message = "instructions must be at most 2000 characters")
        private String instructions;

        /**
         * choice：选项名 → 选项描述（1–255 项）；noul：可选 {"true":…, "false":…}；score：从低到高的等级描述数组（2–255 级）。
         */
        private Object criteria;

        public static Question choice(String instructions, Map<String, String> options) {
            return Question.builder().type(TYPE_CHOICE).instructions(instructions).criteria(options).build();
        }

        public static Question noul(String instructions) {
            return Question.builder().type(TYPE_NOUL).instructions(instructions).build();
        }

        public static Question score(String instructions, List<String> levels) {
            return Question.builder().type(TYPE_SCORE).instructions(instructions).criteria(levels).build();
        }
    }
}
