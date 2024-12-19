/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 15:55:33
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

public enum ThreadStateEnum {
    INITIAL, // 初始状态
    QUEUING, // 排队中
    STARTED, // 开始会话
    OFFLINE, // 客服不在线
    CLOSED, // 会话已结束
    // TRANSFERRED, // 转接
    // REJECTED, // 拒绝会话
    // CANCELLED, // 取消会话
    // RESOLVED, // 解决问题
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
