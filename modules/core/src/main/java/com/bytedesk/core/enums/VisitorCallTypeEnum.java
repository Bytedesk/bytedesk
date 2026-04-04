package com.bytedesk.core.enums;

import org.springframework.util.StringUtils;

/**
 * 访客会话接入意图（不替代 ThreadType）
 */
public enum VisitorCallTypeEnum {
    TEXT,
    WEBRTC,
    PHONE;

    public static VisitorCallTypeEnum fromValue(String value) {
        if (!StringUtils.hasText(value)) {
            return TEXT;
        }
        try {
            return VisitorCallTypeEnum.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            return TEXT;
        }
    }
}