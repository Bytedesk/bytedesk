package com.bytedesk.remote.invitation;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.bytedesk.remote.config.RemoteProperties;
import com.bytedesk.remote.RemotePermissions;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/remote/invitations")
@RequiredArgsConstructor
public class RemoteInvitationController {
    
    private final RemoteInvitationService invitations;
    private final RemoteProperties remoteProperties;

    public record Connect(@NotBlank @Pattern(regexp = "[0-9]{6}") String code) {
        @Override public String toString() { return "Connect[code=REDACTED]"; }
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_CREATE)
    @Operation(summary = "Issue Remote Invitation", description = "Issue a new remote support invitation code")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_CREATE, description = "create remote")
    @PostMapping
    public JsonResult<?> issue() {
        requireEnabled();
        return JsonResult.success(invitations.issue());
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_UPDATE)
    @Operation(summary = "Connect Remote Invitation", description = "Connect to a remote support session with an invitation code")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query remote")
    @PostMapping("/connect")
    public JsonResult<?> connect(@Valid @RequestBody Connect request) {
        requireEnabled();
        return JsonResult.success(invitations.connect(request.code()));
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_READ)
    @Operation(summary = "Get Pending Invitation", description = "Retrieve the pending state of a remote invitation")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query remote")
    @GetMapping("/{uid}")
    public JsonResult<?> pending(@PathVariable String uid) {
        requireEnabled();
        return JsonResult.success(invitations.pending(uid));
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_UPDATE)
    @Operation(summary = "Close Remote Session", description = "Close a remote support session")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_CLOSE, description = "close remote")
    @PostMapping("/{uid}/close")
    public JsonResult<?> close(@PathVariable String uid) {
        requireEnabled();
        invitations.close(uid);
        return JsonResult.success();
    }

    @PreAuthorize(RemotePermissions.HAS_REMOTE_UPDATE)
    @Operation(summary = "Reject Remote Invitation", description = "Reject a remote support invitation")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_REJECT, description = "reject remote")
    @PostMapping("/{uid}/reject")
    public JsonResult<?> reject(@PathVariable String uid) {
        requireEnabled();
        invitations.reject(uid);
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
