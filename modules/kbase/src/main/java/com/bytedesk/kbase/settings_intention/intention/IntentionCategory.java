/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29
 * @Description: 意图分类类别
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention.intention;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentionCategory {
    
    /**
     * 意图名称
     */
    private String name;
    
    /**
     * 意图描述
     */
    private String description;
    
    /**
     * 父意图（如果是子意图）
     */
    private String parentIntention;
    
    /**
     * 子意图列表
     */
    @Builder.Default
    private List<IntentionCategory> subIntentions = new ArrayList<>();
    
    /**
     * 意图示例表达式
     */
    @Builder.Default
    private List<String> examples = new ArrayList<>();
    
    /**
     * 意图标签
     */
    @Builder.Default
    private List<String> intentionSettingss = new ArrayList<>();
    
    /**
     * 意图优先级，数字越小优先级越高
     */
    @Builder.Default
    private Integer priority = 0;
    
    /**
     * 默认回复模板
     */
    private String responseTemplate;
    
    /**
     * 关联的实体类型
     * 例如："订单查询"意图可能关联"订单号"实体
     */
    @Builder.Default
    private List<String> relatedEntityTypes = new ArrayList<>();
    
    /**
     * 是否需要转人工
     */
    @Builder.Default
    private boolean requiresHumanTransfer = false;
    
    /**
     * 意图触发次数统计
     */
    @Builder.Default
    private Integer triggerCount = 0;
    
    /**
     * 意图满意度评分（平均值）
     */
    @Builder.Default
    private double satisfactionScore = 0.0;
}