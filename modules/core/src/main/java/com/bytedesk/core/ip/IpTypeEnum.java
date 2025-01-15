/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 11:12:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 15:15:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

public enum IpTypeEnum {
    WHITELIST,
    BLACKLIST;

    // 根据字符串查找对应的枚举常量
    public static IpTypeEnum fromValue(String value) {
        for (IpTypeEnum type : IpTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No IpTypeEnum constant with value: " + value);
    }
}
