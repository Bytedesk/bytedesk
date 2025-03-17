/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-17 17:00:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

public enum ThreadStateEnum {
    QUEUING, // 排队中
    STARTED, // 开始会话
    TRANSFER_PENDING, // 转接待处理
    TRANSFER_ACCEPTED, // 接受转接
    TRANSFER_REJECTED, // 拒绝转接
    TRANSFER_TIMEOUT, // 转接超时
    TRANSFER_CANCELED, // 取消转接
    INVITE_PENDING, // 邀请处理
    INVITE_ACCEPTED, // 接受邀请
    INVITE_REJECTED, // 拒绝邀请
    INVITE_TIMEOUT, // 邀请超时
    INVITE_CANCELED, // 取消邀请
    // INVITE_VISITOR_PENDING, // 邀请访客处理
    // INVITE_VISITOR_ACCEPTED, // 接受访客邀请
    // INVITE_VISITOR_REJECTED, // 拒绝访客邀请
    // INVITE_VISITOR_TIMEOUT, // 访客邀请超时
    // INVITE_VISITOR_CANCELED, // 取消访客邀请
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
