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
 * 决策模型演示中单个问题的答案视图。
 * kind：yes_no / choice / score / refusal，各类型只填充对应字段。
 * https://docs.spring.io/spring-ai/reference/api/decision.html#_reading_answers
 */
@Data
public class DecisionAnswerView {

    private String name;

    private String text;

    private String kind;

    private boolean refused;

    /** refusal */
    private String reason;

    /** yes_no：为「是」的概率；isYes 使用默认阈值 0.5，isYesStrict 使用 0.9 */
    private Double probability;

    private Boolean isYes;

    private Boolean isYesStrict;

    /** choice */
    private String value;

    private List<String> optionLabels;

    private List<String> rankedOptions;

    /** choice：option -> probability */
    private Map<String, Double> probabilities;

    /** score：level index -> probability，与 expectedLevelLabels 一一对应 */
    private List<Double> levelProbabilities;

    /** choice / score：提供方报告的整体置信度，可能为 null */
    private Double confidence;

    /** score：最可能等级（从 0 开始）及其标签 */
    private Integer nearestLevel;

    private String nearestLevelLabel;

    /** score：期望等级四舍五入后的等级及其标签 */
    private Integer roundedLevel;

    private String roundedLevelLabel;

    /** score：按概率加权的期望等级，可为小数 */
    private Double expectedValue;

    private List<String> expectedLevelLabels;

}
