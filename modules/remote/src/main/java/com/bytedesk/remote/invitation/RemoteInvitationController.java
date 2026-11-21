package com.bytedesk.remote.invitation;

import com.bytedesk.core.utils.JsonResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.bytedesk.remote.config.RemoteProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/remote/invitations")
@RequiredArgsConstructor
public class RemoteInvitationController {
    
    private final RemoteInvitationService invitations;
    private final RemoteProperties remoteProperties;

    public record Connect(@NotBlank @Pattern(regexp = "[0-9]{6}") String code) {
        @Override public String toString() { return "Connect[code=REDACTED]"; }
    }

    @PostMapping
    public JsonResult<?> issue() {
        requireEnabled();
        return JsonResult.success(invitations.issue());
    }

    @PostMapping("/connect")
    public JsonResult<?> connect(@Valid @RequestBody Connect request) {
        requireEnabled();
        return JsonResult.success(invitations.connect(request.code()));
    }

    @GetMapping("/{uid}")
    public JsonResult<?> pending(@PathVariable String uid) {
        requireEnabled();
        return JsonResult.success(invitations.pending(uid));
    }

    @PostMapping("/{uid}/close")
    public JsonResult<?> close(@PathVariable String uid) {
        requireEnabled();
        invitations.close(uid);
        return JsonResult.success();
    }

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
