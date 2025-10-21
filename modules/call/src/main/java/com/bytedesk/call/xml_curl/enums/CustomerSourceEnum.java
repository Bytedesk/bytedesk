package com.bytedesk.call.xml_curl.enums;

import lombok.Getter;

/**
 * @author danmo
 * @date 2025/06/30
 */

@Getter
public enum CustomerSourceEnum {

    MANUAL(0, "手动创建"),
    FILE_IMPORT(1, "文件导入"),
    API_IMPORT(2, "API导入");

    private final Integer code;
    private final String msg;

    CustomerSourceEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static CustomerSourceEnum getSourceByCode(Integer code) {
        for (CustomerSourceEnum value : CustomerSourceEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
