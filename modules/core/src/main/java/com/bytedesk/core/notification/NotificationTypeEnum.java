/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 14:26:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 18:13:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

import org.springframework.util.StringUtils;

public enum NotificationTypeEnum {
    GENERAL,
    ANNOUNCEMENT,
    MAINTENANCE,
    SECURITY;

    public static NotificationTypeEnum fromValue(String value) {
        if (!StringUtils.hasText(value)) {
            return GENERAL;
        }
        for (NotificationTypeEnum type : values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        return GENERAL;
    }
}
