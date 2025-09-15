/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:49:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

public enum FeedbackTypeEnum {
    WORKGROUP,      // Feedback from workgroup
    AGENT,          // Feedback from agent
    ROBOT,          // Feedback from robot interaction
    SYSTEM,         // System generated feedback
    GENERAL;        // General feedback

    // 根据字符串查找对应的枚举常量
    public static FeedbackTypeEnum fromValue(String value) {
        for (FeedbackTypeEnum type : FeedbackTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return GENERAL; // 默认返回通用类型
    }
}
