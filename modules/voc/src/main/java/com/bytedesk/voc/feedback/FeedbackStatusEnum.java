/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-15 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 14:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

/**
 * Feedback processing status enumeration
 * Defines the lifecycle states of feedback from submission to resolution
 */
public enum FeedbackStatusEnum {
    PENDING("待处理"),
    PROCESSING("处理中"),
    READ("已读"),
    REPLIED("已回复"),
    TRANSFERRED("已转接"),
    ESCALATED("已升级"),
    CLOSED("已关闭"),
    RESOLVED("已解决"),
    REJECTED("已拒绝"),
    SPAM("垃圾反馈"),
    INVALID("无效反馈"),
    CONFIRMED("已确认"), // 用户确认反馈有效
    CANCELLED("已取消"), // 用户取消反馈
    ;

    private final String description;

    FeedbackStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static FeedbackStatusEnum fromString(String status) {
        try {
            return FeedbackStatusEnum.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return PENDING;
        }
    }
}