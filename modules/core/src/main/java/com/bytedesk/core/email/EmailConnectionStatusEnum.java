/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-01 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-01 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

public enum EmailConnectionStatusEnum {
    CONNECTED("已连接"),
    DISCONNECTED("未连接"),
    CONNECTING("连接中"),
    CONNECTION_FAILED("连接失败"),
    AUTHENTICATION_FAILED("认证失败"),
    SYNCING("同步中"),
    SYNC_FAILED("同步失败");

    private final String description;

    EmailConnectionStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
