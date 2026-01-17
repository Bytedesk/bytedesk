/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop Remote Control Mode Enumeration
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desktop Remote Control Session Mode
 */
@Getter
@AllArgsConstructor
public enum DesktopModeEnum {

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
    public static DesktopModeEnum fromCode(String code) {
        for (DesktopModeEnum mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown desktop mode: " + code);
    }
}
