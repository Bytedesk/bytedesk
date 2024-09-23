/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:26:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-24 14:14:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

public enum AgentStatusEnum {
    AVAILABLE, // 接待状态
    REST, // 休息状态
    BUSY, // 忙碌状态
    OFFLINE, // 离线状态
    DISABLED; // 禁用状态

    // 根据字符串查找对应的枚举常量
    public static AgentStatusEnum fromValue(String value) {
        for (AgentStatusEnum type : AgentStatusEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No AgentStatus constant with value: " + value);
    }
}
