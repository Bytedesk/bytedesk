package com.bytedesk.call.xml_curl.enums;

import lombok.Getter;

@Getter
public enum CallTaskStatusEnum {

    //任务状态(0-未开始 1-进行中 2-暂停 3-结束)
    NOT_START(0, "未开始"),

    PROCESSING(1, "进行中"),

    PAUSE(2, "暂停"),

    END(3, "结束");

    private final Integer code;

    private final String message;

    CallTaskStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(Integer code) {
        for (CallTaskStatusEnum value : CallTaskStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getMessage();
            }
        }
        return "";
    }
}
