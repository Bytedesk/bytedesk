/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-27 14:19:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 14:19:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

public enum MessageLeavePriorityEnum {
    LOW("low", "低"),
    MEDIUM("medium", "中"),
    HIGH("high", "高"),
    URGENT("urgent", "紧急");

    private String code;
    private String name;

    MessageLeavePriorityEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static MessageLeavePriorityEnum fromCode(String code) {
        for (MessageLeavePriorityEnum priority : MessageLeavePriorityEnum.values()) {
            if (priority.getCode().equals(code)) {
                return priority;
            }
        }
        return null;
    }
}
