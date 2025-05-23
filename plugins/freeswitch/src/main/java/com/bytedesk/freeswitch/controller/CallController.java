package com.bytedesk.freeswitch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.freeswitch.service.CallService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 呼叫控制器
 */
@RestController
@RequestMapping("/api/v1/call")
@RequiredArgsConstructor
public class CallController {
    
    private static final Logger log = LoggerFactory.getLogger(CallController.class);

    private final CallService callService;

    /**
     * 发起呼叫
     */
    @PostMapping("/make")
    public ResponseEntity<?> makeCall(@RequestBody Map<String, String> request) {
        String fromUser = request.get("fromUser");
        String toUser = request.get("toUser");
        
        log.info("API 发起呼叫: {} -> {}", fromUser, toUser);
        
        String callId = callService.makeCall(fromUser, toUser);
        if (callId != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "callId", callId
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "发起呼叫失败"
            ));
        }
    }

    /**
     * 应答呼叫
     */
    @PostMapping("/{callId}/answer")
    public ResponseEntity<?> answerCall(@PathVariable String callId) {
        log.info("API 应答呼叫: {}", callId);
        
        boolean success = callService.answerCall(callId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 拒绝呼叫
     */
    @PostMapping("/{callId}/reject")
    public ResponseEntity<?> rejectCall(@PathVariable String callId) {
        log.info("API 拒绝呼叫: {}", callId);
        
        boolean success = callService.rejectCall(callId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 结束呼叫
     */
    @PostMapping("/{callId}/end")
    public ResponseEntity<?> endCall(@PathVariable String callId) {
        log.info("API 结束呼叫: {}", callId);
        
        boolean success = callService.endCall(callId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 发送DTMF
     */
    @PostMapping("/{callId}/dtmf")
    public ResponseEntity<?> sendDtmf(
            @PathVariable String callId,
            @RequestBody Map<String, String> request) {
        String digit = request.get("digit");
        
        log.info("API 发送DTMF: {} 按键 {}", callId, digit);
        
        boolean success = callService.sendDtmf(callId, digit);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 开关静音
     */
    @PostMapping("/{callId}/mute")
    public ResponseEntity<?> toggleMute(
            @PathVariable String callId,
            @RequestBody Map<String, Boolean> request) {
        boolean mute = request.get("mute");
        
        log.info("API {}静音: {}", mute ? "开启" : "关闭", callId);
        
        boolean success = callService.toggleMute(callId, mute);
        return ResponseEntity.ok(Map.of("success", success));
    }
}
