/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:40:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-25 17:40:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

public enum PushStatusEnum {
    PENDING,
    SCANNED,
    CONFIRMED,
    EXPIRED,
    SUCCESS,
    ERROR;

    // 根据字符串查找对应的枚举常量
    public static PushStatusEnum fromValue(String value) {
        for (PushStatusEnum type : PushStatusEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No PushStatusEnum constant with value: " + value);
    }
}
