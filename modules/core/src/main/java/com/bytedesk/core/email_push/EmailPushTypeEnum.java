/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:57:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_push;

public enum EmailPushTypeEnum {
    EMAIL_NOTIFICATION,  // 邮件通知
    EMAIL_REGISTER,      // 邮箱注册
    EMAIL_LOGIN,         // 邮箱登录
    EMAIL_RESET,         // 邮箱重置
    EMAIL_VERIFY;        // 邮箱验证

    public static EmailPushTypeEnum fromValue(String value) {
        for (EmailPushTypeEnum type : EmailPushTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return EMAIL_NOTIFICATION; // 默认返回
    }
}
