/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.provider.ollama.decision;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.util.StringUtils;

/**
 * 决策模型演示请求。
 * type 决定输入类型与问题集：triage(text)/fields/conversation/guardrail，缺省 triage。
 * model 为可选的运行时选项，用于演示按请求切换模型。
 * https://docs.spring.io/spring-ai/reference/api/decision.html
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDemoRequest {

    @Builder.Default
    private String type = "triage";

    /** 文本输入；fields 类型下作为 message 字段 */
    @NotBlank(message = "message must not be blank")
    @Size(max = 1000, message = "message must be at most 1000 characters")
    private String message;

    /** conversation 类型的两条消息 */
    @Size(max = 1000, message = "userMessage must be at most 1000 characters")
    private String userMessage;

    @Size(max = 1000, message = "assistantMessage must be at most 1000 characters")
    private String assistantMessage;

    /** fields 类型的数值字段 */
    @Min(value = 0, message = "orders must be >= 0")
    private Integer orders;

    @Min(value = 0, message = "lateDeliveries must be >= 0")
    private Integer lateDeliveries;

    /** 可选：覆盖默认决策模型 d1-omni:600m */
    @Pattern(regexp = "[A-Za-z0-9._:/-]{1,100}", message = "model contains invalid characters")
    private String model;

    /**
     * conversation 类型必须同时提供 userMessage 与 assistantMessage。
     * 用 Bean Validation 而非手动断言，使非法请求映射为 400 而不是 500。
     */
    @AssertTrue(message = "userMessage and assistantMessage are required when type is conversation")
    public boolean isConversationInputValid() {
        return !"conversation".equals(type)
                || (StringUtils.hasText(userMessage) && StringUtils.hasText(assistantMessage));
    }

}