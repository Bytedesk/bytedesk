/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 09:37:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 12:34:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

// 群组类型
// 普通群、付费群、公开群、私有群、临时群、机器人群
// 普通群：普通群组，没有特殊功能
// 付费群：用户到期时间
// 公开群：所有人可以加入
// 私有群：需要邀请才能加入
// 临时群：临时群组，没有特殊功能
// 机器人群：机器人群组，没有特殊功能
// 项目群：项目群组，项目管理，任务分配管理、日报、周报等
public enum GroupTypeEnum {
    NORMAL, // 普通群组
    TOPIC, // 话题群组
    CHANNEL, // 频道群组
    PROJECT; // 项目群组

    // 根据字符串查找对应的枚举常量
    public static GroupTypeEnum fromValue(String value) {
        for (GroupTypeEnum type : GroupTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No GroupTypeEnum constant with value: " + value);
    }
}
