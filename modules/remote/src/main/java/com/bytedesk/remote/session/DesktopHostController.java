package com.bytedesk.remote.session;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.remote.device.DesktopDeviceRegisterRequest;
import org.jspecify.annotations.NullMarked;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@NullMarked
@RequestMapping("/visitor/api/v1/remote/sessions")
@RequiredArgsConstructor
public class DesktopHostController {
    private final DesktopSessionService service;
    private final DesktopIceService ice;
    public record Heartbeat(@Min(0) @Max(1000000000) long inputCount) {}

    @PostMapping("/{uid}/join")
    public JsonResult<?> join(@PathVariable String uid, @Valid @RequestBody DesktopDeviceRegisterRequest request) {
        return JsonResult.success(service.join(uid, request));
    }

    @PostMapping("/{uid}/heartbeat")
    public JsonResult<?> heartbeat(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody Heartbeat request) {
        return JsonResult.success(service.heartbeat(uid, token, request.inputCount()));
    }

    @PostMapping("/{uid}/action")
    public JsonResult<?> action(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody DesktopActionRequest request) {
        return JsonResult.success(service.action(uid, token, request.action()));
    }

    @PostMapping("/{uid}/signal")
    public JsonResult<?> signal(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody DesktopSignalRequest request) {
        service.signal(uid, token, request);
        return JsonResult.success();
    }

    @PostMapping("/{uid}/ice")
    public JsonResult<?> ice(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody Heartbeat request) {
        return JsonResult.success(ice.servers(service.heartbeat(uid, token, request.inputCount())));
    }
}
