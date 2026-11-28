package com.bytedesk.remote.session;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.remote.config.RemoteActionRequest;
import com.bytedesk.remote.config.RemoteProperties;
import com.bytedesk.remote.config.RemoteSignalRequest;
import com.bytedesk.remote.RemotePermissions;

import org.jspecify.annotations.NullMarked;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@NullMarked
@RequestMapping("/api/v1/remote/sessions")
@RequiredArgsConstructor
public class RemoteSessionController {
    
    private final RemoteSessionService sessionService;
    private final RemoteProperties remoteProperties;

    @PreAuthorize(RemotePermissions.HAS_REMOTE_CREATE)
    @Operation(summary = "Invite to Remote Session", description = "Invite a user to a remote support session")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_CREATE, description = "create remote")
    @PostMapping
    public JsonResult<?> invite(@Valid @RequestBody RemoteSessionCreateRequest request) {
        requireEnabled();
        return JsonResult.success(sessionService.invite(request));
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_UPDATE)
    @Operation(summary = "Remote Heartbeat", description = "Report remote session heartbeat")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_UPDATE, description = "update remote")
    @PostMapping("/{uid}/heartbeat")
    public JsonResult<?> heartbeat(@PathVariable String uid) {
        requireEnabled();
        return JsonResult.success(sessionService.heartbeat(uid, null, 0));
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_UPDATE)
    @Operation(summary = "Execute Remote Action", description = "Execute a remote control action")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute remote")
    @PostMapping("/{uid}/action")
    public JsonResult<?> action(@PathVariable String uid, @Valid @RequestBody RemoteActionRequest request) {
        requireEnabled();
        return JsonResult.success(sessionService.action(uid, null, request.action()));
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_UPDATE)
    @Operation(summary = "Send Remote Signal", description = "Send a WebRTC signal for the remote session")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_SEND, description = "send remote")
    @PostMapping("/{uid}/signal")
    public JsonResult<?> signal(@PathVariable String uid, @Valid @RequestBody RemoteSignalRequest request) {
        requireEnabled();
        sessionService.signal(uid, null, request);
        return JsonResult.success();
    }

    /** 服务端开关（bytedesk.remote.enabled=false 时拒绝，防止绕过前端直接调用） */
    private void requireEnabled() {
        if (!remoteProperties.isEnabled()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "Remote assistance is disabled");
        }
    }
}
