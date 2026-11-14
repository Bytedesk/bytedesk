package com.bytedesk.remote.session;

import com.bytedesk.core.utils.JsonResult;
import org.jspecify.annotations.NullMarked;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@NullMarked
@RequestMapping("/api/v1/remote/sessions")
@RequiredArgsConstructor
public class DesktopSessionController {
    private final DesktopSessionService service;
    private final DesktopIceService ice;

    @PostMapping
    public JsonResult<?> invite(@Valid @RequestBody DesktopSessionCreateRequest request) {
        return JsonResult.success(service.invite(request));
    }

    @PostMapping("/{uid}/heartbeat")
    public JsonResult<?> heartbeat(@PathVariable String uid) {
        return JsonResult.success(service.heartbeat(uid, null, 0));
    }

    @PostMapping("/{uid}/action")
    public JsonResult<?> action(@PathVariable String uid, @Valid @RequestBody DesktopActionRequest request) {
        return JsonResult.success(service.action(uid, null, request.action()));
    }

    @PostMapping("/{uid}/signal")
    public JsonResult<?> signal(@PathVariable String uid, @Valid @RequestBody DesktopSignalRequest request) {
        service.signal(uid, null, request);
        return JsonResult.success();
    }

    @GetMapping("/{uid}/audit")
    public JsonResult<?> audit(@PathVariable String uid) {
        return JsonResult.success(service.auditHistory(uid));
    }

    @PostMapping("/{uid}/ice")
    public JsonResult<?> ice(@PathVariable String uid) {
        return JsonResult.success(ice.servers(service.heartbeat(uid, null, 0)));
    }
}
