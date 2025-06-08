/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-07 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 12:12:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/test/api/freeswitch")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchController {

    private final FreeSwitchService freeSwitchService;

    /**
     * http://127.0.0.1:9003/test/api/freeswitch/status
     * 获取FreeSwitch状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean connected = freeSwitchService.isConnected();
            String status = freeSwitchService.getStatus();
            
            result.put("connected", connected);
            result.put("status", status);
            result.put("properties", freeSwitchService.getProperties());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取状态失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * http://127.0.0.1:9003/test/api/freeswitch/call/originate?caller=sip:1001@14.103.165.199&destination=sip:1002@14.103.165.199
     * 发起呼叫
     */
    @PostMapping("/call/originate")
    public ResponseEntity<Map<String, Object>> originate(
            @RequestParam String caller,
            @RequestParam String destination,
            @RequestParam(defaultValue = "default") String context) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.originate(caller, destination, context);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("caller", caller);
            result.put("destination", destination);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("发起呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 挂断呼叫
     */
    @PostMapping("/call/hangup")
    public ResponseEntity<Map<String, Object>> hangup(
            @RequestParam String uuid,
            @RequestParam(required = false) String cause) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.hangup(uuid, cause);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("挂断呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 应答呼叫
     */
    @PostMapping("/call/answer")
    public ResponseEntity<Map<String, Object>> answer(@RequestParam String uuid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.answer(uuid);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("应答呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 转接呼叫
     */
    @PostMapping("/call/transfer")
    public ResponseEntity<Map<String, Object>> transfer(
            @RequestParam String uuid,
            @RequestParam String destination,
            @RequestParam(defaultValue = "default") String context) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.transfer(uuid, destination, context);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("destination", destination);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("转接呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 播放语音
     */
    @PostMapping("/call/playback")
    public ResponseEntity<Map<String, Object>> playback(
            @RequestParam String uuid,
            @RequestParam String filePath) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.playback(uuid, filePath);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("filePath", filePath);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("播放语音失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 开始录音
     */
    @PostMapping("/call/record/start")
    public ResponseEntity<Map<String, Object>> startRecord(
            @RequestParam String uuid,
            @RequestParam String filePath) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.record(uuid, filePath);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("filePath", filePath);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("开始录音失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 停止录音
     */
    @PostMapping("/call/record/stop")
    public ResponseEntity<Map<String, Object>> stopRecord(
            @RequestParam String uuid,
            @RequestParam String filePath) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.stopRecord(uuid, filePath);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("filePath", filePath);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("停止录音失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 发送DTMF
     */
    @PostMapping("/call/dtmf")
    public ResponseEntity<Map<String, Object>> sendDtmf(
            @RequestParam String uuid,
            @RequestParam String digits) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.sendDtmf(uuid, digits);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("digits", digits);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("发送DTMF失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取活动通道
     */
    @GetMapping("/channels")
    public ResponseEntity<Map<String, Object>> getChannels() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String channels = freeSwitchService.showChannels();
            
            result.put("success", true);
            result.put("channels", channels);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取通道失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 获取活动呼叫数量
     */
    @GetMapping("/calls/count")
    public ResponseEntity<Map<String, Object>> getCallsCount() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String calls = freeSwitchService.showCalls();
            
            result.put("success", true);
            result.put("calls", calls);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取呼叫数量失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 执行自定义API命令
     */
    @PostMapping("/api/execute")
    public ResponseEntity<Map<String, Object>> executeCommand(
            @RequestParam String command,
            @RequestParam(required = false) String args) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = freeSwitchService.executeApiCommand(command, args);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("command", command);
            result.put("args", args);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("执行命令失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
