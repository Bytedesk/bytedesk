/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-20 15:55:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-24 14:12:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.enums;

public enum LevelEnum {
    PLATFORM,
    ORGNIZATION,
    DEPARTMENT,
    WORKGROUP,
    GROUP,
    USER;

    // 根据字符串查找对应的枚举常量
    public static LevelEnum fromValue(String value) {
        for (LevelEnum type : LevelEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No QuickReplyLevelEnum constant with value: " + value);
    }
}
