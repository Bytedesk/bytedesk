/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Remote Message Status Enumeration
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Remote Message Status
 */
@Getter
@AllArgsConstructor
public enum RemoteMessageStatusEnum {

    /**
     * Message created but not sent
     */
    PENDING("pending", "待发送"),

    /**
     * Message sent successfully
     */
    SENT("sent", "已发送"),

    /**
     * Message delivered to destination
     */
    DELIVERED("delivered", "已送达"),

    /**
     * Message processing failed
     */
    FAILED("failed", "发送失败"),

    /**
     * Message acknowledged by receiver
     */
    ACKNOWLEDGED("acknowledged", "已确认");

    private final String code;
    private final String description;

    /**
     * Get enum by code
     */
    public static RemoteMessageStatusEnum fromCode(String code) {
        for (RemoteMessageStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}
