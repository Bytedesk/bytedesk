/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 18:40:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 12:22:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.chunk;

public enum ChunkStatusEnum {
    NEW, // 新建
    PROCESSING, // 处理中
    WAITING, // 等待中
    SUCCESS,  // 成功
    ERROR;  // 失败

    // 根据字符串查找对应的枚举常量
    public static ChunkStatusEnum fromValue(String value) {
        for (ChunkStatusEnum status : ChunkStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
    
    /**
     * 将状态类型转换为中文显示
     * @param status 状态类型字符串
     * @return 对应的中文名称
     */
    public static String toChineseDisplay(String status) {
        try {
            ChunkStatusEnum statusEnum = fromValue(status);
            return statusEnum.toChineseDisplay();
        } catch (Exception e) {
            return status;
        }
    }
    
    /**
     * 获取当前枚举值的中文显示
     * @return 对应的中文名称
     */
    public String toChineseDisplay() {
        switch (this) {
            case NEW:
                return "新建";
            case PROCESSING:
                return "处理中";
            case WAITING:
                return "等待中";
            case SUCCESS:
                return "成功";
            case ERROR:
                return "失败";
            default:
                return this.name();
        }
    }
}
