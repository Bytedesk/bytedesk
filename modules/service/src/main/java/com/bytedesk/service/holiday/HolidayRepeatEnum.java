/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-02 16:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 17:12:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 */
package com.bytedesk.service.holiday;

/**
 * 节假日重复类型枚举
 */
public enum HolidayRepeatEnum {
    
    /**
     * 不重复
     */
    NONE,
    
    /**
     * 每天重复
     */
    DAILY,
    
    /**
     * 每周重复
     */
    WEEKLY,
    
    /**
     * 每月重复
     */
    MONTHLY,
    
    /**
     * 每年重复
     */
    YEARLY
}
