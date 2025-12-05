/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 11:27:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_edge;

/**
 * 工作流边类型枚举
 * 定义不同类型的边连接
 */
public enum WorkflowEdgeTypeEnum {
    
    /**
     * 默认连接 - 普通的节点间连接
     */
    DEFAULT("default"),
    
    /**
     * 条件连接 - 条件节点的分支连接
     */
    CONDITION("condition"),
    
    /**
     * 循环连接 - 循环节点的连接
     */
    LOOP("loop"),
    
    /**
     * 数据连接 - 数据传输连接
     */
    DATA("data"),
    
    /**
     * 控制连接 - 控制流连接
     */
    CONTROL("control"),
    
    /**
     * 错误连接 - 异常处理连接
     */
    ERROR("error"),
    
    /**
     * 线程连接 - 并行线程连接
     */
    THREAD("thread"),
    
    /**
     * 客户连接 - 客户相关连接
     */
    CUSTOMER("customer"),
    
    /**
     * 工单连接 - 工单相关连接
     */
    TICKET("ticket");

    private final String value;

    WorkflowEdgeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据值获取枚举
     */
    public static WorkflowEdgeTypeEnum fromValue(String value) {
        for (WorkflowEdgeTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown edge type: " + value);
    }

    /**
     * 是否为条件类型边
     */
    public boolean isConditional() {
        return this == CONDITION || this == ERROR;
    }

    /**
     * 是否为控制类型边
     */
    public boolean isControl() {
        return this == CONTROL || this == LOOP || this == CONDITION;
    }

    /**
     * 是否为数据类型边
     */
    public boolean isData() {
        return this == DATA || this == DEFAULT;
    }
}
