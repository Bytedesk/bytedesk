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

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 决策模型演示结果：输入摘要、每个问题的答案、模型与用量、耗时。
 * 概率为模型输出，未经校准，不可跨模型比较，也不代表正确率。
 * https://docs.spring.io/spring-ai/reference/api/decision.html
 */
@Data
public class DecisionDemoResponse {

    private String type;

    private InputView input;

    private List<DecisionAnswerView> questions;

    private String model;

    private UsageView usage;

    private Long durationMs;

    @Data
    public static class InputView {

        private String type;

        private String preview;

        /** fields 输入的数值字段 */
        private Map<String, Object> fields;

    }

    @Data
    public static class UsageView {

        private Integer promptTokens;

        private Integer completionTokens;

        private Integer totalTokens;

    }

}
