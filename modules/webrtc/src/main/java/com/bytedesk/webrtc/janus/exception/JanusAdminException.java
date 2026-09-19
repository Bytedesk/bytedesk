/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-19 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-19 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is prohibited – see the LICENSE for details.
 *   仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   技术/商务联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.webrtc.janus.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Janus Admin API 调用异常
 * 
 * message 已经过脱敏/截断，可直接返回给前端；不含 admin_secret 等敏感信息
 */
@Getter
public class JanusAdminException extends RuntimeException {

    private final HttpStatus status;

    public JanusAdminException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static JanusAdminException disabled() {
        return new JanusAdminException(HttpStatus.SERVICE_UNAVAILABLE,
                "Janus admin api is not enabled or configured");
    }

    public static JanusAdminException unreachable(String reason) {
        return new JanusAdminException(HttpStatus.BAD_GATEWAY,
                "Janus admin api unreachable: " + truncate(reason));
    }

    public static JanusAdminException secretMismatch() {
        return new JanusAdminException(HttpStatus.FORBIDDEN,
                "Janus admin_secret mismatch, please check bytedesk.webrtc.janus.admin.secret in webrtc.properties");
    }

    /**
     * Janus 错误 490：Stored-Token 认证未启用（janus.jcfg 中 token_auth=false），
     * 属于正常配置状态而非故障，调用方应优雅降级而不是报 502
     */
    public static JanusAdminException tokenAuthDisabled() {
        return new JanusAdminException(HttpStatus.SERVICE_UNAVAILABLE,
                "Stored-Token based authentication is disabled on this Janus server");
    }

    public static boolean isTokenAuthDisabled(JanusAdminException e) {
        return e != null && e.getMessage() != null
                && e.getMessage().contains("Stored-Token based authentication is disabled");
    }

    public static JanusAdminException ofCode(int code, String reason) {
        if (code == 490) {
            // Stored-Token based authentication disabled
            return tokenAuthDisabled();
        }
        return new JanusAdminException(HttpStatus.BAD_GATEWAY,
                "Janus error " + code + ": " + truncate(reason));
    }

    private static String truncate(String reason) {
        if (reason == null) {
            return "unknown";
        }
        String trimmed = reason.strip();
        return trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
    }
}
