/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Quick button type definitions
 */
package com.bytedesk.kbase.quick_button;

/**
 * Supported quick button action types for toolbar quick buttons.
 */
public enum QuickButtonTypeEnum {

    FAQ,
    URL,
    FORM,
    IMAGE,
    PHONE,
    EMAIL,
    ORDER,
    GOODS;

    public static QuickButtonTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return FAQ;
        }
        for (QuickButtonTypeEnum type : QuickButtonTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return FAQ;
    }
}
