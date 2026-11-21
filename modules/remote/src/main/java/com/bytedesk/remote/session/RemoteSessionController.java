package com.bytedesk.remote.session;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.remote.config.RemoteActionRequest;
import com.bytedesk.remote.config.RemoteProperties;
import com.bytedesk.remote.config.RemoteSignalRequest;

import org.jspecify.annotations.NullMarked;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@NullMarked
@RequestMapping("/api/v1/remote/sessions")
@RequiredArgsConstructor
public class RemoteSessionController {
    
    private final RemoteSessionService sessionService;
    private final RemoteProperties remoteProperties;

    @PostMapping
    public JsonResult<?> invite(@Valid @RequestBody RemoteSessionCreateRequest request) {
        requireEnabled();
        return JsonResult.success(sessionService.invite(request));
    }

    @PostMapping("/{uid}/heartbeat")
    public JsonResult<?> heartbeat(@PathVariable String uid) {
        requireEnabled();
        return JsonResult.success(sessionService.heartbeat(uid, null, 0));
    }

    @PostMapping("/{uid}/action")
    public JsonResult<?> action(@PathVariable String uid, @Valid @RequestBody RemoteActionRequest request) {
        requireEnabled();
        return JsonResult.success(sessionService.action(uid, null, request.action()));
    }

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
