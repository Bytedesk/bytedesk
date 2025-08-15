/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 17:00:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread.enums;

// 默认New，判断是否Offline, 如果是，直接Closed
// 如果不是，判断是否需要排队，如果是，则直接Queuing
// 如果不需要排队，则直接Chatting
// Chatting结束后，直接Closed
public enum ThreadProcessStatusEnum {
    NEW, // 新会话，待处理
    ROBOTING, // 机器人接待中，外部访客跟大模型机器人会话
    // LLMING, // 大模型对话中，内部成员跟大模型对话
    OFFLINE, // 客服离线
    QUEUING, // 排队中
    CHATTING, // 对话中
    TIMEOUT, // 超时未处理
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
    
    /**
     * 将状态字符串转换为中文显示
     * @param status 状态字符串
     * @return 对应的中文名称
     */
    public static String toChineseDisplay(String status) {
        try {
            ThreadProcessStatusEnum statusEnum = fromValue(status);
            return statusEnum.toChineseDisplay();
        } catch (Exception e) {
            return status;
        }
    }
    
    /**
     * 获取当前状态的中文显示
     * @return 对应的中文名称
     */
    public String toChineseDisplay() {
        switch (this) {
            case NEW:
                return "新会话";
            // case ROBOTING:
            //     return "机器人接待中";
            // case LLMING:
            //     return "大模型对话中";
            case OFFLINE:
                return "客服离线";
            case QUEUING:
                return "排队中";
            case CHATTING:
                return "对话中";
            case CLOSED:
                return "已结束";
            default:
                return this.name();
        }
    }
}
