/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 15:49:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 12:06:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
    
    ACTIVE("active"),        // 队列可排队
    PAUSED("paused"),      // 队列暂停
    CLOSED("closed"),      // 队列关闭
    FULL("full"),        // 队列已满
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
    
    /**
     * 获取枚举类型对应的中文名称
     * @return 对应的中文名称
     */
    public String getChineseName() {
        switch (this) {
            case ACTIVE:
                return "正常";
            case PAUSED:
                return "暂停";
            case CLOSED:
                return "关闭";
            case FULL:
                return "已满";
            case MAINTENANCE:
                return "维护中";
            default:
                return this.name();
        }
    }
    
    /**
     * 根据枚举名称获取对应的中文名称
     * @param name 枚举名称
     * @return 对应的中文名称，如果找不到匹配的枚举则返回原始名称
     */
    public static String getChineseNameByString(String name) {
        try {
            QueueStatusEnum status = QueueStatusEnum.valueOf(name);
            return status.getChineseName();
        } catch (IllegalArgumentException e) {
            return name;
        }
    }
}