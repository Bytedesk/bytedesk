/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-15 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 16:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.complaint;

public enum ComplaintStatusEnum {
    PENDING,        // Waiting for processing
    PROCESSING,     // Currently being processed
    INVESTIGATING,  // Under investigation
    READ,           // Complaint has been read
    REPLIED,        // Reply has been sent
    TRANSFERRED,    // Transferred to another agent
    ESCALATED,      // Escalated to higher level
    CLOSED,         // Complaint is closed
    RESOLVED,       // Complaint has been resolved
    REJECTED,       // Complaint is rejected
    SPAM,           // Marked as spam
    INVALID,        // Invalid complaint
    CONFIRMED,      // User confirmed complaint is valid
    CANCELLED,      // User cancelled complaint
    WITHDRAWN;      // User withdrew complaint

    // 根据字符串查找对应的枚举常量
    public static ComplaintStatusEnum fromValue(String value) {
        for (ComplaintStatusEnum status : ComplaintStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING; // 默认返回待处理状态
    }
}