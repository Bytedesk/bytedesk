package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

/**
 * 任务类型枚举
 * @author danmo
 * @date 2025/06/25
 */

@Getter
public enum TaskTypeEnum {

    PREVIEW(0, "预测", "predictiveDialerHandler"),
    PREDICTIVE(1, "预览","previewOutboundHandler");

    private final Integer code;
    private final String message;
    private final String handler;
    TaskTypeEnum(Integer code, String message, String handler) {
        this.code = code;
        this.message = message;
        this.handler = handler;
    }

    public static String getMessage(Integer code) {
        for (TaskTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value.message;
            }
        }
        return null;
    }

    public static String getHandler(Integer code) {
        for (TaskTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value.handler;
            }
        }
        return null;
    }
}
