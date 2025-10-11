/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 17:02:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.node;

/**
 * 工作流节点类型枚举
 */
public enum WorkflowNodeTypeEnum {
    
    /**
     * 开始节点
     */
    START("start"),
    
    /**
     * 结束节点
     */
    END("end"),
    
    /**
     * 条件节点
     */
    CONDITION("condition"),
    
    /**
     * 循环节点
     */
    LOOP("loop"),
    
    /**
     * LLM节点
     */
    LLM("llm"),
    
    /**
     * 文本节点
     */
    TEXT("text"),
    
    /**
     * 注释节点
     */
    COMMENT("comment"),
    
    /**
     * 分组节点
     */
    GROUP("group"),

    // === 新增：网关 ===
    EXCLUSIVE_GATEWAY("exclusiveGateway"),
    PARALLEL_GATEWAY("parallelGateway"),
    INCLUSIVE_GATEWAY("inclusiveGateway"),

    // === 新增：用户任务 ===
    USER_TASK("userTask"),

    // === 新增：事件 ===
    TIMER_EVENT("timerEvent"),
    MESSAGE_EVENT("messageEvent"),
    SIGNAL_EVENT("signalEvent"),

    // === 新增：DMN 决策 ===
    DECISION_DMN("decisionDmn");
    
    private final String value;
    
    WorkflowNodeTypeEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 根据值获取枚举
     */
    public static WorkflowNodeTypeEnum fromValue(String value) {
        for (WorkflowNodeTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown node type: " + value);
    }
    
} 