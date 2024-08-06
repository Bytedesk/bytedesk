/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 16:32:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-01 18:33:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

public enum ThreadTypeEnum {
    AGENT(0),
    WORKGROUP(1),
    ROBOT(2),
    MEMBER(3),
    GROUP(4),
    LEAVEMSG(5),
    FEEDBACK(6),
    ASISTANT(7),
    CHANNEL(8),
    LOCAL(9),
    FRIEND(10),
    TICKET(11);

    private final int value;

    // 枚举构造器，每个枚举常量都有一个与之关联的整型值
    ThreadTypeEnum(int value) {
        this.value = value;
    }

    // 获取枚举常量的整型值
    public int getValue() {
        return value;
    }

    // 根据整型值查找对应的枚举常量
    public static ThreadTypeEnum fromValue(int value) {
        for (ThreadTypeEnum type : ThreadTypeEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
