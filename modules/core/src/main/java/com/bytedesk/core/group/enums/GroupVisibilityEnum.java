/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group.enums;

/**
 * 群组可见性：独立于 GroupTypeEnum（群组织形态）的正交属性
 * PUBLIC: 组织内可搜索、可申请加入
 * PRIVATE: 仅成员可见（默认值，与存量行为一致）
 */
public enum GroupVisibilityEnum {
    PUBLIC,
    PRIVATE;

    public static GroupVisibilityEnum fromValue(String value) {
        for (GroupVisibilityEnum item : GroupVisibilityEnum.values()) {
            if (item.name().equalsIgnoreCase(value)) {
                return item;
            }
        }
        // 非法值回退 PRIVATE（fail-closed：不能因输入错误意外公开）
        return PRIVATE;
    }
}
