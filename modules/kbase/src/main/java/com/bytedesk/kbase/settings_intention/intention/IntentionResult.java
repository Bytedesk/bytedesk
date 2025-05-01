/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29
 * @Description: 意图检测结果
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention.intention;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentionResult {
    
    /**
     * 主要意图
     */
    private String primaryIntention;
    
    /**
     * 主要意图的置信度
     */
    private double primaryConfidence;
    
    /**
     * 子意图（如果有）
     */
    private String subIntention;
    
    /**
     * 子意图置信度
     */
    private double subConfidence;
    
    /**
     * 所有意图及其置信度
     */
    private Map<String, Double> allIntentions;
    
    /**
     * 推荐回复
     */
    private String recommendedResponse;
    
    /**
     * 是否需要澄清
     */
    @Builder.Default
    private boolean needClarification = false;
    
    /**
     * 推荐的澄清问题
     */
    private String clarificationQuestion;
    
    /**
     * 相关的意图实体（如订单编号、产品名称等）
     */
    private Map<String, String> entities;
    
    /**
     * 意图标签
     */
    private List<String> intentionSettingss;
    
    /**
     * 意图优先级
     */
    private int priority;
    
    /**
     * 是否建议转人工
     */
    @Builder.Default
    private boolean suggestHumanTransfer = false;
    
    /**
     * 意图是否确定
     * 当主意图置信度高于阈值时为true
     */
    @Builder.Default
    private boolean isConfident = true;
    
    /**
     * 意图是否改变
     * 与上一次检测结果相比是否改变
     */
    @Builder.Default
    private boolean isIntentionChanged = false;
}