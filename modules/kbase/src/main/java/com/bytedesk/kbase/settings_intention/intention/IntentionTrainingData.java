/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29
 * @Description: 意图训练数据模型
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention.intention;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * 用于训练意图识别模型的数据结构
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentionTrainingData {
    
    /**
     * 用户输入文本
     */
    private String text;
    
    /**
     * 主意图标签
     */
    private String intention;
    
    /**
     * 子意图标签（如果有）
     */
    private String subIntention;
    
    /**
     * 是否为真实客户对话数据
     */
    @Builder.Default
    private Boolean isRealData = false;
    
    /**
     * 上下文消息列表
     * 在进行上下文敏感的意图识别时使用
     */
    private List<String> contextMessages;
    
    /**
     * 标注的意图实体
     * 键为实体类型，值为实体值
     * 例如：{"订单号": "123456", "产品名称": "智能音箱"}
     */
    @Builder.Default
    private Map<String, String> entities = new HashMap<>();
    
    /**
     * 训练数据权重
     * 默认为1.0，值越大对模型的影响越大
     */
    @Builder.Default
    private double weight = 1.0;
    
    /**
     * 训练数据来源
     */
    private String source;
    
    /**
     * 数据标注人员ID
     */
    private String labeledBy;
    
    /**
     * 数据质量评分（1-5）
     */
    @Builder.Default
    private Integer qualityScore = 5;
    
    /**
     * 创建时间戳
     */
    private long createdAt;
    
    /**
     * 更新时间戳
     */
    private long updatedAt;
}