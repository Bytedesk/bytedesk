/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 15:49:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 14:26:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

/**
 * 队列成员状态枚举
 */
public enum QueueMemberStatusEnum {
    
    WAITING("waiting"),         // 等待中
    SERVING("serving"),         // 正在服务
    COMPLETED("completed"),     // 已完成
    CANCELLED("cancelled"),     // 已取消(访客取消)
    TIMEOUT("timeout"),         // 已超时
    REJECTED("rejected");       // 已拒绝(无可用客服)
    
    private final String value;
    
    QueueMemberStatusEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 检查是否是结束状态
     */
    public boolean isEndStatus() {
        return this == COMPLETED || this == CANCELLED || 
               this == TIMEOUT || this == REJECTED;
    }
    
    public static QueueMemberStatusEnum fromValue(String value) {
        for (QueueMemberStatusEnum status : QueueMemberStatusEnum.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid member status: " + value);
    }
} 