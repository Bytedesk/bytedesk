/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 18:12:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 15:47:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

public enum QueueMemberAcceptTypeEnum {
    MANUAL,
    AUTO;

    // 根据字符串查找对应的枚举常量
    public static QueueMemberAcceptTypeEnum fromValue(String value) {
        // 如果value为空，直接返回null
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        for (QueueMemberAcceptTypeEnum type : QueueMemberAcceptTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
    
    /**
     * 将接受类型转换为中文显示
     * @param acceptType 接受类型字符串
     * @return 对应的中文名称
     */
    public static String toChineseDisplay(String acceptType) {
        try {
            QueueMemberAcceptTypeEnum typeEnum = fromValue(acceptType);
            return typeEnum.toChineseDisplay();
        } catch (Exception e) {
            return acceptType;
        }
    }
    
    /**
     * 获取当前枚举值的中文显示
     * @return 对应的中文名称
     */
    public String toChineseDisplay() {
        switch (this) {
            case MANUAL:
                return "手动接受";
            case AUTO:
                return "自动接受";
            default:
                return this.name();
        }
    }
}
