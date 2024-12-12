/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 15:49:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 16:02:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

/**
 * 队列状态枚举
 */
public enum QueueStatusEnum {
    
    ACTIVE("active"),       // 队列开放中
    PAUSED("paused"),      // 队列暂停
    CLOSED("closed"),      // 队列关闭
    MAINTENANCE("maintenance"); // 系统维护
    
    private final String value;
    
    QueueStatusEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static QueueStatusEnum fromValue(String value) {
        for (QueueStatusEnum status : QueueStatusEnum.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid queue status: " + value);
    }
} 