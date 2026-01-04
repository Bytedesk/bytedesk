/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-04
 * @Description: Message visibility scope
 */
package com.bytedesk.core.message;

import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 消息可见范围。
 *
 * public: 外部公开可见（默认）
 * internal: 企业内部可见
 * private: 仅自己可见
 */
public enum MessageVisibilityEnum {

    @JSONField(name = "public")
    PUBLIC("public"),

    @JSONField(name = "internal")
    INTERNAL("internal"),

    @JSONField(name = "private")
    PRIVATE("private");

    private final String value;

    MessageVisibilityEnum(String value) {
        this.value = value;
    }

    @JSONField(value = true)
    public String getValue() {
        return value;
    }

    @JSONCreator
    public static MessageVisibilityEnum from(Object raw) {
        if (raw == null) {
            return PUBLIC;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return PUBLIC;
        }
        String normalized = text.toLowerCase();
        return switch (normalized) {
            case "public" -> PUBLIC;
            case "internal" -> INTERNAL;
            case "private" -> PRIVATE;
            default -> PUBLIC;
        };
    }
}
