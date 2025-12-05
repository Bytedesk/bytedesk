/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-27 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-27 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

/**
 * 工作流状态枚举
 */
public enum WorkflowStatusEnum {
    
    /**
     * 草稿状态
     * 工作流已创建但未发布
     */
    DRAFT,
    
    /**
     * 已发布状态
     * 工作流已发布，可以使用
     */
    PUBLISHED,
    
    /**
     * 已禁用状态
     * 工作流被禁用，不可使用
     */
    DISABLED;
    
    /**
     * 根据字符串查找对应的枚举常量
     */
    public static WorkflowStatusEnum fromValue(String value) {
        for (WorkflowStatusEnum status : WorkflowStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No WorkflowStatusEnum constant with value: " + value);
    }
    
    /**
     * 获取枚举类型对应的中文名称
     * @return 对应的中文名称
     */
    public String getChineseName() {
        switch (this) {
            case DRAFT:
                return "草稿";
            case PUBLISHED:
                return "已发布";
            case DISABLED:
                return "已禁用";
            default:
                return this.name();
        }
    }
} 