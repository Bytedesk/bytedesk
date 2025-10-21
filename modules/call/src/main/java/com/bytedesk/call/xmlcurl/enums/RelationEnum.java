package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 筛选关系枚举
 *
 * @author danmo
 * @date 2025/06/30
 */
@Getter
public enum RelationEnum {

    EQUAL(1, "等于", "{0} = {1}", "JSON_EXTRACT({0} ,'$.{1}') = {2}"),
    NOT_EQUAL(2, "不等于", "{0} != {1}", "JSON_EXTRACT({0} ,'$.{1}') != {2}"),
    MORE_THAN(3, "大于", "{0} > {1}", "JSON_EXTRACT({0} ,'$.{1}') > {2}"),
    GREATER_EQUAL(4, "大于等于", "{0} >= {1}", "JSON_EXTRACT({0} ,'$.{1}') >= {2}"),
    LESS_THAN(5, "小于", "{0} < {1}", "JSON_EXTRACT({0} ,'$.{1}') < {2}"),
    LESS_EQUAL(6, "小于等于", "{0} <= {1}", "JSON_EXTRACT({0} ,'$.{1}') <= {2}"),
    INTERVAL(7, "区间", "{0} >= {1} and {0} <= {2}", "JSON_EXTRACT({0} ,'$.{1}') >= {2} and JSON_EXTRACT({0} ,'$.{1}') <= {3}"),
    NULL(8, "为空", "{0} is null", "JSON_EXTRACT({0} ,'$.{1}') is null"),
    NOT_NULL(9, "不为空", "{0} is not null", "JSON_EXTRACT({0} ,'$.{1}') is not null"),
    INCLUDE(10, "包含", "{0} like '%{1}%'", "JSON_EXTRACT({0} ,'$.{1}') like '%{2}%'"),
    NOT_INCLUDE(11, "不包含", "{0} not like '%{1}%'", "JSON_EXTRACT({0} ,'$.{1}') not like '%{2}%'"),

    ;

    private final Integer code;

    private final String value;

    private final String format;

    //sql查询json中信息
    private final String jsonFormat;


    RelationEnum(Integer code, String value, String format, String jsonFormat) {
        this.code = code;
        this.value = value;
        this.format = format;
        this.jsonFormat = jsonFormat;
    }

    private static final Map<Integer, RelationEnum> ENUM_MAP = new HashMap<>();
    private static final Map<Integer, String> VALUE_MAP = new HashMap<>();
    private static final Map<Integer, String> JSON_ENUM_MAP = new HashMap<>();
    private static final Map<Integer, String> SQL_ENUM_MAP = new HashMap<>();

    static {
        for (RelationEnum item : RelationEnum.values()) {
            ENUM_MAP.put(item.getCode(), item);
            VALUE_MAP.put(item.getCode(), item.getValue());
            JSON_ENUM_MAP.put(item.getCode(), item.getJsonFormat());
            SQL_ENUM_MAP.put(item.getCode(), item.getFormat());
        }
    }

    public static RelationEnum getEnum(Integer code) {
        return ENUM_MAP.getOrDefault(code, null);
    }

    public static String getValue(Integer code) {
        return VALUE_MAP.getOrDefault(code, "");
    }

    public static String getJsonValue(Integer code) {
        return JSON_ENUM_MAP.getOrDefault(code, "");
    }

    public static String getSqlValue(Integer code) {
        return SQL_ENUM_MAP.getOrDefault(code, "");
    }

}
