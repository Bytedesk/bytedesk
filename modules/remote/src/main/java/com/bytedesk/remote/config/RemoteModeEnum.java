/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Remote Control Mode Enumeration
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Remote Control Session Mode
 */
@Getter
@AllArgsConstructor
public enum RemoteModeEnum {

    /**
     * Customer Support Mode
     * Agent views and optionally controls customer screen
     * Typically view-only by default, control on request
     */
    SUPPORT("support", "客服支持"),

    /**
     * Remote Access Mode
     * User accesses their own remote device
     * Full control by default
     */
    REMOTE("remote", "远程访问"),

    /**
     * Presentation Mode
     * One host broadcasts screen to multiple viewers
     * View-only for all viewers
     */
    PRESENTATION("presentation", "屏幕共享");

    private final String code;
    private final String description;

    /**
     * Get enum by code
     */
    public static RemoteModeEnum fromCode(String code) {
        for (RemoteModeEnum mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown desktop mode: " + code);
    }
}
