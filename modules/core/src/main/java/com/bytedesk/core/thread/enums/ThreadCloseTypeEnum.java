package com.bytedesk.core.thread.enums;

/**
 * 会话关闭来源类型
 */
public enum ThreadCloseTypeEnum {
    NONE,      // 未关闭/无来源
    AUTO,      // 系统自动(超时/策略)关闭
    AGENT,     // 客服手动关闭
    VISITOR,   // 访客主动结束
    SYSTEM;    // 系统指令/管理员操作关闭

    public static ThreadCloseTypeEnum fromValue(String value) {
        for (ThreadCloseTypeEnum t : values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("No ThreadCloseTypeEnum constant with value: " + value);
    }
}
