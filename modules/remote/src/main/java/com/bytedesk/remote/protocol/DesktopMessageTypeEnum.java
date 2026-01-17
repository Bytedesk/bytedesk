/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Message Type Enumeration
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desktop Remote Control Message Types
 */
@Getter
@AllArgsConstructor
public enum DesktopMessageTypeEnum {

    /**
     * Session initialization (viewer -> host)
     * Content: JSON with device info and permissions
     */
    SESSION_INIT("session_init", "会话初始化"),

    /**
     * Session init acknowledgment (host -> viewer)
     * Content: JSON with session info and capabilities
     */
    SESSION_INIT_ACK("session_init_ack", "会话初始化确认"),

    /**
     * Screen frame data (host -> viewer)
     * Content: Base64 encoded image (JPEG/PNG/WebP)
     */
    SCREEN_FRAME("screen_frame", "屏幕帧数据"),

    /**
     * Mouse movement/click event (viewer -> host)
     * Content: JSON with x, y, button, action
     */
    MOUSE_EVENT("mouse_event", "鼠标事件"),

    /**
     * Keyboard event (viewer -> host)
     * Content: JSON with key, modifiers, action
     */
    KEY_EVENT("key_event", "键盘事件"),

    /**
     * Control permission request (viewer -> host)
     * Content: JSON with request reason
     */
    CONTROL_REQUEST("control_request", "控制权限请求"),

    /**
     * Control permission response (host -> viewer)
     * Content: JSON with granted/boolean
     */
    CONTROL_RESPONSE("control_response", "控制权限响应"),

    /**
     * Quality adjustment (either direction)
     * Content: JSON with quality level
     */
    QUALITY_ADJUST("quality_adjust", "质量调整"),

    /**
     * Clipboard sync (either direction)
     * Content: Base64 encoded clipboard data
     */
    CLIPBOARD_SYNC("clipboard_sync", "剪贴板同步"),

    /**
     * Session heartbeat (either direction)
     * Content: Timestamp
     */
    HEARTBEAT("heartbeat", "心跳消息"),

    /**
     * Session termination (either direction)
     * Content: Reason string
     */
    SESSION_TERMINATE("session_terminate", "会话终止"),

    /**
     * Error message (either direction)
     * Content: Error details JSON
     */
    ERROR("error", "错误消息");

    private final String code;
    private final String description;

    /**
     * Get enum by code
     */
    public static DesktopMessageTypeEnum fromCode(String code) {
        for (DesktopMessageTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + code);
    }
}
