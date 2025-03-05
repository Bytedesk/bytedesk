/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 07:32:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 10:01:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

public enum AutoReplyKeywordMatchEnum {
    EXACT, // 精确匹配
    FUZZY, // 模糊匹配
    REGULAR; // 正则匹配

    // 根据字符串查找对应的枚举常量
    public static AutoReplyKeywordMatchEnum fromValue(String value) {
        for (AutoReplyKeywordMatchEnum type : AutoReplyKeywordMatchEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No AutoReplyKeywordMatchEnum constant with value: " + value);
    }
}
