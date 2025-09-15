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

/**
 * Priority enumeration for tasks, feedback, complaints, and other entities
 * Defines different priority levels from low to urgent
 */
public enum PriorityEnum {
    LOW("低优先级", 1),
    MEDIUM("中等优先级", 2),
    HIGH("高优先级", 3),
    URGENT("紧急", 4),
    CRITICAL("严重", 5);

    private final String description;
    private final int level;

    PriorityEnum(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return this.description;
    }

    public int getLevel() {
        return this.level;
    }

    public static PriorityEnum fromString(String priority) {
        try {
            return PriorityEnum.valueOf(priority.toUpperCase());
        } catch (Exception e) {
            return MEDIUM;
        }
    }

    public static PriorityEnum fromLevel(int level) {
        for (PriorityEnum priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        return MEDIUM;
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