package com.bytedesk.ticket.ticket.enums;

/**
 * 工单生成方式
 */
public enum TicketCreateTypeEnum {
    MANUAL, // 手动生成：用户/访客通过页面或接口主动创建
    AUTO;   // 自动生成：会话关闭后按自动建单规则生成

    public static TicketCreateTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return TicketCreateTypeEnum.MANUAL;
        }
        for (TicketCreateTypeEnum item : TicketCreateTypeEnum.values()) {
            if (item.name().equalsIgnoreCase(value)) {
                return item;
            }
        }
        return TicketCreateTypeEnum.MANUAL;
    }

    public static String getNameFromValue(String value) {
        return fromValue(value).name();
    }
}
