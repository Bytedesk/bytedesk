/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-04 17:52:52
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

public enum ThreadStatusEnum {
    QUEUING, // 排队中
    NORMAL, // 正常
    CONTINUE, // 会话进行中，访客关闭会话页面之后，重新进入
    REOPEN, // 会话关闭之后，重新进入
    OFFLINE, // 客服不在线
    RATED, // rated, prevent repeated rate
    AUTO_CLOSED, // 自动关闭
    AGENT_CLOSED, // 客服关闭
    DISMISSED, // 会话解散
    MUTED, // 会话静音
    FORBIDDEN, // 会话禁言
    MONITORED, // 会话监控
    TRANSFERED, // 会话转接
    INVITED, // 会话邀请
    ;

    // 根据字符串查找对应的枚举常量
    public static ThreadStatusEnum fromValue(String value) {
        for (ThreadStatusEnum type : ThreadStatusEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ThreadStatusEnum constant with value: " + value);
    }
}
