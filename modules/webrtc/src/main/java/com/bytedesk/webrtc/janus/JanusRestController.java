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
package com.bytedesk.webrtc.janus;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.webrtc.janus.exception.JanusAdminException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Janus Admin/Monitor REST 控制器（P0 只读）
 * 
 * 面向 meetAdmin 管理后台的 Janus 服务器监控页面，后端代理调用 Janus Admin API，
 * admin_secret 等敏感信息不透传前端。仅超级管理员可访问（与前端 access: canSuper 对齐）。
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/webrtc/janus/admin")
@Tag(name = "Janus Admin Monitor", description = "Read-only Janus WebRTC gateway admin/monitor APIs for meetAdmin")
@Description("Janus Admin Monitor Controller")
@PreAuthorize(RolePermissions.ROLE_SUPER)
public class JanusRestController {

    private final JanusAdminService janusAdminService;

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC, action = "janus_info", description = "query janus server info")
    @Operation(summary = "Query Janus server info", description = "Retrieve Janus server info via admin api")
    @GetMapping("/info")
    public ResponseEntity<?> info() {
        try {
            return ResponseEntity.ok(JsonResult.success(janusAdminService.serverInfo()));
        } catch (JanusAdminException e) {
            return error(e);
        }
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC, action = "janus_status", description = "query janus runtime status")
    @Operation(summary = "Query Janus status", description = "Retrieve Janus runtime settings snapshot via admin api")
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        try {
            return ResponseEntity.ok(JsonResult.success(janusAdminService.getStatus()));
        } catch (JanusAdminException e) {
            return error(e);
        }
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC, action = "janus_sessions", description = "query janus sessions")
    @Operation(summary = "Query Janus sessions", description = "List current Janus session ids via admin api")
    @GetMapping("/sessions")
    public ResponseEntity<?> sessions() {
        try {
            return ResponseEntity.ok(JsonResult.success(janusAdminService.listSessions()));
        } catch (JanusAdminException e) {
            return error(e);
        }
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC, action = "janus_handles", description = "query janus handles by session")
    @Operation(summary = "Query Janus handles", description = "List handles of a Janus session via admin api")
    @GetMapping("/sessions/{sessionId}/handles")
    public ResponseEntity<?> handles(@PathVariable("sessionId") Long sessionId) {
        try {
            return ResponseEntity.ok(JsonResult.success(janusAdminService.listHandles(sessionId)));
        } catch (JanusAdminException e) {
            return error(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC, action = "janus_handle_info", description = "query janus handle info")
    @Operation(summary = "Query Janus handle info", description = "Retrieve Janus handle detailed info via admin api")
    @GetMapping("/sessions/{sessionId}/handles/{handleId}")
    public ResponseEntity<?> handleInfo(@PathVariable("sessionId") Long sessionId,
            @PathVariable("handleId") Long handleId) {
        try {
            JSONObject info = janusAdminService.handleInfo(sessionId, handleId);
            return ResponseEntity.ok(JsonResult.success(info));
        } catch (JanusAdminException e) {
            return error(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage(), 400));
        }
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC, action = "janus_tokens", description = "query janus stored tokens")
    @Operation(summary = "Query Janus tokens", description = "List stored Janus auth tokens (masked) via admin api")
    @GetMapping("/tokens")
    public ResponseEntity<?> tokens() {
        try {
            return ResponseEntity.ok(JsonResult.success(janusAdminService.listTokens()));
        } catch (JanusAdminException e) {
            return error(e);
        }
    }

    private ResponseEntity<?> error(JanusAdminException e) {
        log.warn("janus admin api error: status={}, message={}", e.getStatus().value(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(JsonResult.error(e.getMessage(), e.getStatus().value()));
    }
}
