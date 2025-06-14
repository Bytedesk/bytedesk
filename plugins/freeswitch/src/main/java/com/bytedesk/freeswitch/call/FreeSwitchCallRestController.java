/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 14:30:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 12:40:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.call;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 呼叫控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/freeswitch/call")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchCallRestController extends BaseRestController<FreeSwitchCallRequest> {

    private final FreeSwitchCallService callService;

    @Override
    public ResponseEntity<?> queryByOrg(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public ResponseEntity<?> queryByUser(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public ResponseEntity<?> queryByUid(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Object export(FreeSwitchCallRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

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
