/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Session Status Enumeration
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desktop Session Connection Status
 */
@Getter
@AllArgsConstructor
public enum DesktopSessionStatusEnum {

    /**
     * Session initialized but not connected
     */
    INITIALIZED("initialized", "已初始化"),

    /**
     * Connection in progress
     */
    CONNECTING("connecting", "连接中"),

    /**
     * Session active and connected
     */
    CONNECTED("connected", "已连接"),

    /**
     * Session paused (temporary)
     */
    PAUSED("paused", "已暂停"),

    /**
     * Session disconnected
     */
    DISCONNECTED("disconnected", "已断开"),

    /**
     * Session terminated
     */
    TERMINATED("terminated", "已终止"),

    /**
     * Session failed to connect
     */
    FAILED("failed", "连接失败");

    private final String code;
    private final String description;

    /**
     * Get enum by code
     */
    public static DesktopSessionStatusEnum fromCode(String code) {
        for (DesktopSessionStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown session status: " + code);
    }
}
