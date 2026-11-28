package com.bytedesk.remote.controller;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.remote.config.RemoteActionRequest;
import com.bytedesk.remote.config.RemoteHeartbeatRequest;
import com.bytedesk.remote.config.RemoteSignalRequest;
import com.bytedesk.remote.device.RemoteDeviceRegisterRequest;
import com.bytedesk.remote.session.RemoteSessionService;

import org.jspecify.annotations.NullMarked;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@NullMarked
@RequestMapping("/visitor/api/v1/remote/sessions/{uid}")
@RequiredArgsConstructor
public class RemoteHostController {
    
    private final RemoteSessionService service;

    @Operation(summary = "Join", description = "j remote host")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_JOIN, description = "join remote")
    @PostMapping("/join")
    public JsonResult<?> join(@PathVariable String uid, @Valid @RequestBody RemoteDeviceRegisterRequest request) {
        return JsonResult.success(service.join(uid, request));
    }

    @Operation(summary = "Remote Heartbeat", description = "Report remote session heartbeat")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_UPDATE, description = "update remote")
    @PostMapping("/heartbeat")
    public JsonResult<?> heartbeat(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody RemoteHeartbeatRequest request) {
        return JsonResult.success(service.heartbeat(uid, token, request.inputCount()));
    }

    @Operation(summary = "Execute Remote Action", description = "Execute a remote control action")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute remote")
    @PostMapping("/action")
    public JsonResult<?> action(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody RemoteActionRequest request) {
        return JsonResult.success(service.action(uid, token, request.action()));
    }

    @Operation(summary = "Send Remote Signal", description = "Send a WebRTC signal for the remote session")
    @ActionAnnotation(title = I18Consts.I18N_REMOTE, action = I18Consts.I18N_ACTION_SEND, description = "send remote")
    @PostMapping("/signal")
    public JsonResult<?> signal(@PathVariable String uid, @RequestHeader("X-Remote-Token") String token,
            @Valid @RequestBody RemoteSignalRequest request) {
        service.signal(uid, token, request);
        return JsonResult.success();
    }
}
