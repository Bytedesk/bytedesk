/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 14:30:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-17 17:57:36
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
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

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

    private final FreeSwitchCallRestService callRestService;

    @Override
    public ResponseEntity<?> queryByOrg(FreeSwitchCallRequest request) {
        
        Page<FreeSwitchCallResponse> page = callRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(FreeSwitchCallRequest request) {

        Page<FreeSwitchCallResponse> page = callRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(FreeSwitchCallRequest request) {
        
        Optional<FreeSwitchCallEntity> entity = callRestService.findByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success(entity));
    }

    @Override
    public ResponseEntity<?> create(FreeSwitchCallRequest request) {
        
        FreeSwitchCallResponse entity = callRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(entity));
    }

    @Override
    public ResponseEntity<?> update(FreeSwitchCallRequest request) {
        
        FreeSwitchCallResponse entity = callRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(entity));
    }

    @Override
    public ResponseEntity<?> delete(FreeSwitchCallRequest request) {
        
        callRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(FreeSwitchCallRequest request, HttpServletResponse response) {
        
        // callRestService.export(request, response);

        return JsonResult.success();
    }

    /**
     * 发起呼叫
     */
    @PostMapping("/make")
    public ResponseEntity<?> makeCall(@RequestBody FreeSwitchCallRequest request) {
        log.info("API 发起呼叫: {} -> {}", request.getCallerNumber(), request.getCalleeNumber());
        
        String callId = callService.makeCall(request);
        if (callId != null) {
            return ResponseEntity.ok(JsonResult.success(Map.of("callId", callId)));
        } else {
            return ResponseEntity.badRequest().body(JsonResult.error("发起呼叫失败"));
        }
    }

    /**
     * 应答呼叫
     */
    @PostMapping("/answer")
    public ResponseEntity<?> answerCall(@RequestBody FreeSwitchCallRequest request) {
        log.info("API 应答呼叫: {}", request.getCallId());
        
        boolean success = callService.answerCall(request);
        
        return ResponseEntity.ok(JsonResult.success(success));
    }

    /**
     * 拒绝呼叫
     */
    @PostMapping("/reject")
    public ResponseEntity<?> rejectCall(@RequestBody FreeSwitchCallRequest request) {
        log.info("API 拒绝呼叫: {}", request.getCallId());
        
        boolean success = callService.rejectCall(request);

        return ResponseEntity.ok(JsonResult.success(success));
    }

    /**
     * 结束呼叫
     */
    @PostMapping("/end")
    public ResponseEntity<?> endCall(@RequestBody FreeSwitchCallRequest request) {
        log.info("API 结束呼叫: {}", request.getCallId());
        
        boolean success = callService.endCall(request);

        return ResponseEntity.ok(JsonResult.success(success));
    }

    /**
     * 发送DTMF
     */
    @PostMapping("/dtmf")
    public ResponseEntity<?> sendDtmf(@RequestBody FreeSwitchCallRequest request) {
        log.info("API 发送DTMF: {} 按键 {}", request.getCallId(), request.getDigit());
        
        boolean success = callService.sendDtmf(request);

        return ResponseEntity.ok(JsonResult.success(success));
    }

    /**
     * 开关静音
     */
    @PostMapping("/mute")
    public ResponseEntity<?> toggleMute(@RequestBody FreeSwitchCallRequest request) {
        log.info("API {}静音: {}", request.getMute() ? "开启" : "关闭", request.getCallId());
        
        boolean success = callService.toggleMute(request);

        return ResponseEntity.ok(JsonResult.success(success));
    }
    

}
