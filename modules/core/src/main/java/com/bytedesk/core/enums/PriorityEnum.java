/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-15 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 16:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.enums;

public enum PriorityEnum {
    LOW,
    MEDIUM,
    HIGH,
    URGENT,
    CRITICAL;

    // 根据字符串查找对应的枚举常量
    public static PriorityEnum fromValue(String value) {
        for (PriorityEnum priority : PriorityEnum.values()) {
            if (priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        return MEDIUM; // 默认返回中等优先级
    }

    public boolean isHighPriority() {
        return this == HIGH || this == URGENT || this == CRITICAL;
    }

    public boolean isLowPriority() {
        return this == LOW;
    }

    public boolean isUrgent() {
        return this == URGENT || this == CRITICAL;
    }
}