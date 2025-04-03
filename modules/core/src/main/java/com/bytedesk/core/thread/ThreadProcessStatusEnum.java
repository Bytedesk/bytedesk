/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 16:03:45
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
public enum ThreadProcessStatusEnum {
    NEW, // 新会话
    ROBOTING, // 访客机器人接待中
    LLMING, // 用户LLM对话中
    QUEUING, // 排队中
    STARTED, // 开始会话
    OFFLINE, // 客服不在线
    CLOSED, // 会话已结束
    ;

    // TRANSFER_PENDING, // 转接待处理
    // TRANSFER_ACCEPTED, // 接受转接
    // TRANSFER_REJECTED, // 拒绝转接
    // TRANSFER_TIMEOUT, // 转接超时
    // TRANSFER_CANCELED, // 取消转接
    
    // INVITE_PENDING, // 邀请处理
    // INVITE_ACCEPTED, // 接受邀请
    // INVITE_REJECTED, // 拒绝邀请
    // INVITE_TIMEOUT, // 邀请超时
    // INVITE_CANCELED, // 取消邀请

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
