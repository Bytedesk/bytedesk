package com.bytedesk.call.xmlcurl.enums;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * 字段类型枚举
 *
 * @author danmo
 * @date 2025/7/2 9:39
 */

@Getter
public enum FieldTypeEnum {

    //字段类型 0-电话 1-文本 2-数字 3-单选 4-多选 5-电子邮箱 6-日期 7-日期时间 8-时间
    PHONE(0, "电话", "^1[3-9]\\d{8}$","电话格式错误"),
    TEXT(1, "文本", "", ""),
    NUMBER(2, "数字", "^[0-9]+$","数字格式错误" ),
    SINGLE_CHOICE(3, "单选", "^[0-9]+$", "单选格式错误"),
    MULTI_CHOICE(4, "多选", "^[^,]+(?:,[^,]+)+$", "多选格式错误"),
    EMAIL(5, "电子邮箱", "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", "邮箱格式错误"),
    DATE(6, "日期", "^\\d{4}-\\d{2}-\\d{2}$", "日期格式错误"),
    DATE_TIME(7, "日期时间", "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", "日期时间格式错误"),
    TIME(8, "时间", "^\\d{2}:\\d{2}(?::\\d{2})?$", "时间格式错误"),
    UNKNOWN(-1, "未知类型", "", "");

    private final Integer code;
    private final String message;
    //校验格式
    private final String format;
    private final Pattern pattern;
    private final String warning;


    private static final Map<Integer, FieldTypeEnum> ENUM_MAP = new HashMap<>();

    static {
        for (FieldTypeEnum item : FieldTypeEnum.values()) {
            ENUM_MAP.put(item.getCode(), item);
        }
    }

    FieldTypeEnum(Integer code, String message, String format, String warning) {
        this.code = code;
        this.message = message;
        this.format = format;
        this.pattern = !StringUtils.hasText(format) ? null : Pattern.compile(format);
        this.warning = warning;
    }

    public static String getMessage(Integer code) {
        FieldTypeEnum item = ENUM_MAP.get(code);
        return item != null ? item.getMessage() : UNKNOWN.getMessage();
    }

    public static String checkFormat(Integer code, String value) {
        FieldTypeEnum item = ENUM_MAP.get(code);
        if (item == null) {
            return UNKNOWN.getMessage();
        }
        Pattern pattern = item.getPattern();
        if (pattern == null) {
            return "";
        }
        boolean matches = pattern.matcher(value).matches();
        if( matches){
            return item.getWarning();
        }
        return "";
    }
}
