/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29
 * @Description: 意图转换历史记录
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention.intention;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 记录会话中意图的转换历史
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntentionTransition {
    
    /**
     * 会话ID
     */
    private String threadId;
    
    /**
     * 转换时间戳
     */
    private long timestamp;
    
    /**
     * 之前的意图
     */
    private String previousIntention;
    
    /**
     * 之前意图的置信度
     */
    private double previousConfidence;
    
    /**
     * 当前/新的意图
     */
    private String currentIntention;
    
    /**
     * 当前意图的置信度
     */
    private double currentConfidence;
    
    /**
     * 触发转换的消息内容
     */
    private String triggerMessage;
    
    /**
     * 转换类型
     * INITIAL: 初始意图设置
     * AUTO_DETECT: 自动检测
     * MANUAL: 人工设置
     * CLARIFICATION: 基于用户澄清
     */
    private String transitionType;
    
    /**
     * 转换原因描述
     */
    private String reason;
    
    /**
     * 转换后采取的动作
     * 例如：回复、转人工、提问澄清等
     */
    private String actionTaken;
    
    /**
     * 会话阶段
     * 例如：开始、中间、结束
     */
    private String conversationSintentionSettingse;
}