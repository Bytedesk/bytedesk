/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 18:14:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

// process status
// 默认New，判断是否Offline, 如果是，直接Closed
// 如果不是，判断是否需要排队，如果是，则直接Queuing
// 如果不需要排队，则直接Chatting
// Chatting结束后，直接Closed
public enum ThreadProcessStatusEnum {
    NEW, // 新会话
    OFFLINE, // 客服不在线
    QUEUING, // 排队中
    CHATTING, // 对话中
    CLOSED, // 会话已结束
    ;

    // 根据字符串查找对应的枚举常量
    public static ThreadProcessStatusEnum fromValue(String value) {
        for (ThreadProcessStatusEnum type : ThreadProcessStatusEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ThreadStateEnum constant with value: " + value);
    }
}
