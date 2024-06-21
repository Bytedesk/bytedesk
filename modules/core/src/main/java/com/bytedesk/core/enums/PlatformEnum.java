package com.bytedesk.core.enums;

public enum PlatformEnum {
    BYTEDESK("bytedesk"),
    LIANGSHIBAO("liangshibao"),
    ZHAOBIAO("zhaobiao"),
    MEIYU("meiyu"),
    TIKU("tiku");
    
    private final String value;

    PlatformEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 根据字符串查找对应的枚举常量
    public static PlatformEnum fromValue(String value) {
        for (PlatformEnum type : PlatformEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No QuickReplyPlatformEnum constant with value: " + value);
    }

}
