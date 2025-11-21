/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-30 16:52:00
 * @Description: MessageLeaveSettings 辅助工具类，用于处理 Worktime 关联实体的解析
 */
package com.bytedesk.service.message_leave_settings;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * MessageLeaveSettings 辅助工具类
 * 提供统一的 Worktime 关联解析方法，避免在多个 Service 中重复代码
 */
@Component
public class MessageLeaveSettingsHelper {

    /**
     * 复制 MessageLeaveSettings 属性,排除 id/uid/version 与时间字段。
     * 
     * @param source 源 MessageLeaveSettings 实体
     * @param target 目标 MessageLeaveSettings 实体
     */
    public void copyMessageLeaveSettingsProperties(MessageLeaveSettingsEntity source, MessageLeaveSettingsEntity target) {
        BeanUtils.copyProperties(source, target, "id", "uid", "version", "createdAt", "updatedAt");
    }

    /**
     * 通用属性复制方法,仅复制业务字段,忽略 id/uid/version 与时间字段。
     * 适用于不包含懒加载集合的简单实体。
     * 
     * @param source 源对象
     * @param target 目标对象
     */
    public void copyPropertiesExcludingIds(Object source, Object target) {
        BeanUtils.copyProperties(source, target, "id", "uid", "version", "createdAt", "updatedAt");
    }
}
