package com.bytedesk.remote.controller;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.remote.config.RemoteActionRequest;
import com.bytedesk.remote.config.RemoteHeartbeatRequest;
import com.bytedesk.remote.config.RemoteSignalRequest;
import com.bytedesk.remote.device.RemoteDeviceRegisterRequest;
import com.bytedesk.remote.session.RemoteSessionService;

import org.jspecify.annotations.NullMarked;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@NullMarked
@RequestMapping("/visitor/api/v1/remote/sessions/{uid}")
@RequiredArgsConstructor
public class RemoteHostController {
    
    private final RemoteSessionService service;

    @PostMapping("/join")
    public JsonResult<?> join(@PathVariable String uid, @Valid @RequestBody RemoteDeviceRegisterRequest request) {
        return JsonResult.success(service.join(uid, request));
    }

    @PostMapping("/heartbeat")
    public JsonResult<?> heartbeat(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody RemoteHeartbeatRequest request) {
        return JsonResult.success(service.heartbeat(uid, token, request.inputCount()));
    }

    @PostMapping("/action")
    public JsonResult<?> action(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody RemoteActionRequest request) {
        return JsonResult.success(service.action(uid, token, request.action()));
    }

    @PostMapping("/signal")
    public JsonResult<?> signal(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody RemoteSignalRequest request) {
        service.signal(uid, token, request);
        return JsonResult.success();
    }
}
