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

public enum FeedbackStatusEnum {
    PENDING,        // Waiting for processing
    PROCESSING,     // Currently being processed
    READ,           // Feedback has been read
    REPLIED,        // Reply has been sent
    TRANSFERRED,    // Transferred to another agent
    ESCALATED,      // Escalated to higher level
    CLOSED,         // Feedback is closed
    RESOLVED,       // Feedback has been resolved
    REJECTED,       // Feedback is rejected
    SPAM,           // Marked as spam
    INVALID,        // Invalid feedback
    CONFIRMED,      // User confirmed feedback is valid
    CANCELLED;      // User cancelled feedback

    // 根据字符串查找对应的枚举常量
    public static FeedbackStatusEnum fromValue(String value) {
        for (FeedbackStatusEnum status : FeedbackStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING; // 默认返回待处理状态
    }
}