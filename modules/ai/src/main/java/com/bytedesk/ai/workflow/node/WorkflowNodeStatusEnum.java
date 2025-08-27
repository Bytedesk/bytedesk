/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-27 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-23 21:42:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.node;

/**
 * 工作流节点状态枚举
 */
public enum WorkflowNodeStatusEnum {
    
    /**
     * 空闲状态
     * 节点已创建但未开始执行
     */
    IDLE,
    
    /**
     * 处理中
     * 节点正在执行中
     */
    PROCESSING,
    
    /**
     * 成功
     * 节点执行成功完成
     */
    SUCCESS,
    
    /**
     * 失败
     * 节点执行失败
     */
    FAIL,
    
    /**
     * 已取消
     * 节点被手动取消
     */
    CANCELED;
    
    /**
     * 根据字符串查找对应的枚举常量
     */
    public static WorkflowNodeStatusEnum fromValue(String value) {
        for (WorkflowNodeStatusEnum status : WorkflowNodeStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No WorkflowNodeStatusEnum constant with value: " + value);
    }
    
    /**
     * 获取枚举类型对应的中文名称
     * @return 对应的中文名称
     */
    public String getChineseName() {
        switch (this) {
            case IDLE:
                return "空闲";
            case PROCESSING:
                return "处理中";
            case SUCCESS:
                return "成功";
            case FAIL:
                return "失败";
            case CANCELED:
                return "已取消";
            default:
                return this.name();
        }
    }
} 