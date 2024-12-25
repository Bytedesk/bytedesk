/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 12:13:23
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

// 默认机器人接待，除非主动关闭机器人的情况下，才转人工接待。界面支持转人工
// 如果客服不在线，则设置为OFFLINE
// 如果客服在线，且有空闲，则设置为STARTED
// 如果客服在线，且没有空闲，则设置为QUEUING
// 如果客服主动关闭会话或超时，则设置为CLOSED
public enum ThreadStateEnum {
    ROBOT, // 机器人
    QUEUING, // 排队中
    STARTED, // 开始会话
    OFFLINE, // 客服不在线
    CLOSED, // 会话已结束
    ;

    // 根据字符串查找对应的枚举常量
    public static ThreadStateEnum fromValue(String value) {
        for (ThreadStateEnum type : ThreadStateEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ThreadStateEnum constant with value: " + value);
    }
}
