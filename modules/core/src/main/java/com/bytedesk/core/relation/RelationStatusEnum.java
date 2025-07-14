/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-14 09:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-14 09:45:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

/**
 * 关系状态枚举
 * 定义社交关系的各种状态
 */
public enum RelationStatusEnum {
    ACTIVE,     // 活跃 - 关系正常，可以正常互动
    INACTIVE,   // 非活跃 - 关系存在但暂时不活跃
    BLOCKED,    // 屏蔽 - 关系被屏蔽，无法互动
    DELETED,    // 删除 - 关系已删除
    PENDING,    // 待处理 - 关系请求待处理
    REJECTED,   // 拒绝 - 关系请求被拒绝
    EXPIRED,    // 过期 - 关系已过期
    SUSPENDED;  // 暂停 - 关系被暂停

    /**
     * 根据字符串查找对应的枚举常量
     */
    public static RelationStatusEnum fromValue(String value) {
        for (RelationStatusEnum status : RelationStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No RelationStatusEnum constant with value: " + value);
    }
    
    /**
     * 获取状态的中文名称
     */
    public String getChineseName() {
        switch (this) {
            case ACTIVE:
                return "活跃";
            case INACTIVE:
                return "非活跃";
            case BLOCKED:
                return "屏蔽";
            case DELETED:
                return "删除";
            case PENDING:
                return "待处理";
            case REJECTED:
                return "拒绝";
            case EXPIRED:
                return "过期";
            case SUSPENDED:
                return "暂停";
            default:
                return this.name();
        }
    }
    
    /**
     * 判断是否为有效状态（可以正常互动）
     */
    public boolean isValid() {
        return this == ACTIVE || this == INACTIVE;
    }
    
    /**
     * 判断是否为无效状态（无法互动）
     */
    public boolean isInvalid() {
        return this == BLOCKED || this == DELETED || this == REJECTED || 
               this == EXPIRED || this == SUSPENDED;
    }
    
    /**
     * 判断是否为待处理状态
     */
    public boolean isPending() {
        return this == PENDING;
    }
} 