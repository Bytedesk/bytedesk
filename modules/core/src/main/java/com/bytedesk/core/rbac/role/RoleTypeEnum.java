package com.bytedesk.core.rbac.role;


public enum RoleTypeEnum {
    SYSTEM("system"),
    USER("user");

    private final String value;

    RoleTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 根据字符串查找对应的枚举常量
    public static RoleTypeEnum fromValue(String value) {
        for (RoleTypeEnum type : RoleTypeEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No MessageTypeEnum constant with value: " + value);
    }
}
