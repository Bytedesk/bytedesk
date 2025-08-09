/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:31:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:31:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.call;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.freeswitch.esl.client.inbound.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.call.cdr.FreeSwitchCdrEntity;
import com.bytedesk.call.cdr.FreeSwitchCdrService;
import com.bytedesk.call.config.FreeSwitchService;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 呼叫服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchCallService {

    private final Client eslClient;
    // private final FreeSwitchProperties freeSwitchProperties;
    private final FreeSwitchService freeSwitchService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FreeSwitchCdrService cdrService;
    // private final FreeSwitchNumberService userService;
    
    // 存储活动呼叫信息
    private final Map<String, FreeSwitchCallRequest> activeCallMap = new ConcurrentHashMap<>();
    // 用户与UUID的映射
    private final Map<String, String> userCallMap = new ConcurrentHashMap<>();
    // UUID与CallInfo的映射
    private final Map<String, FreeSwitchCallRequest> uuidCallMap = new ConcurrentHashMap<>();

    /**
     * 发起呼叫
     *
     * @param request 呼叫请求
     * @return 呼叫ID
     */
    public String makeCall(FreeSwitchCallRequest request) {
        String fromUser = request.getCallerNumber();
        String toUser = request.getCalleeNumber();
        log.info("发起呼叫: 从 {} 到 {}", fromUser, toUser);
        
        try {
            // 使用FreeSwitchService发起呼叫
            String result = freeSwitchService.originate(fromUser, toUser, "default");
            
            if (result != null && !result.contains("ERR")) {
                // 生成呼叫ID
                String callId = String.format("%s-%s-%d", fromUser, toUser, System.currentTimeMillis());
                
                // 创建呼叫信息对象
                request.setCallUuid(String.format("%s-%s-%d", fromUser, toUser, System.currentTimeMillis()));
                request.setStartTime(System.currentTimeMillis());
                request.setStatus("CALLING");
                request.setType("OUTBOUND");
                
                // 存储呼叫信息
                activeCallMap.put(callId, request);
                userCallMap.put(fromUser, callId);
                userCallMap.put(toUser, callId);
                
                // 通知用户有新的呼叫
                notifyCallEvent(toUser, "incoming_call", request);
                notifyCallEvent(fromUser, "outgoing_call", request);
                
                return callId;
            } else {
                log.error("FreeSwitch发起呼叫失败: {}", result);
                return null;
            }
        } catch (Exception e) {
            log.error("发起呼叫失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 应答呼叫
     *
     * @param request 呼叫请求
     * @return 是否成功
     */
    public boolean answerCall(FreeSwitchCallRequest request) {
        String callId = request.getCallId();
        log.info("应答呼叫: {}", callId);
        
        try {
            FreeSwitchCallRequest callInfo = activeCallMap.get(callId);
            if (callInfo == null) {
                log.warn("找不到呼叫信息: {}", callId);
                return false;
            }
            
            // 更新呼叫状态
            callInfo.setStatus("ANSWERED");
            callInfo.setStartTime(System.currentTimeMillis());
            
            // 存储更新后的呼叫信息
            activeCallMap.put(callId, callInfo);
            
            // 如果呼叫尚未应答，则发送应答命令
            if (callInfo.getCallUuid() != null) {
                freeSwitchService.answer(callInfo.getCallUuid());
            }
            
            // 通知用户呼叫已应答
            notifyCallEvent(callInfo.getCallerNumber(), "call_answered", callInfo);
            notifyCallEvent(callInfo.getCalleeNumber(), "call_answered", callInfo);
            
            return true;
        } catch (Exception e) {
            log.error("应答呼叫失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 拒绝呼叫
     *
     * @param request 呼叫请求
     * @return 是否成功
     */
    public boolean rejectCall(FreeSwitchCallRequest request) {
        String callId = request.getCallId();
        log.info("拒绝呼叫: {}", callId);
        
        try {
            FreeSwitchCallRequest callInfo = activeCallMap.get(callId);
            if (callInfo == null) {
                log.warn("找不到呼叫信息: {}", callId);
                return false;
            }
            
            // 更新呼叫状态
            callInfo.setStatus("REJECTED");
            callInfo.setEndTime(System.currentTimeMillis());
            
            // 如果呼叫UUID存在，则挂断
            if (callInfo.getCallUuid() != null) {
                eslClient.sendSyncApiCommand("uuid_kill", callInfo.getCallUuid());
            }
            
            // 通知用户呼叫已拒绝
            notifyCallEvent(callInfo.getCallerNumber(), "call_rejected", callInfo);
            
            // 清理呼叫信息
            cleanupCall(callId);
            
            return true;
        } catch (Exception e) {
            log.error("拒绝呼叫失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 结束呼叫
     *
     * @param request 呼叫请求
     * @return 是否成功
     */
    public boolean endCall(FreeSwitchCallRequest request) {
        String callId = request.getCallId();
        log.info("结束呼叫: {}", callId);
        
        try {
            FreeSwitchCallRequest callInfo = activeCallMap.get(callId);
            if (callInfo == null) {
                log.warn("找不到呼叫信息: {}", callId);
                return false;
            }
            
            // 更新呼叫状态
            callInfo.setStatus("ENDED");
            callInfo.setEndTime(System.currentTimeMillis());
            
            // 如果呼叫UUID存在，则挂断
            if (callInfo.getCallUuid() != null) {
                eslClient.sendSyncApiCommand("uuid_kill", callInfo.getCallUuid());
            }
            
            // 通知用户呼叫已结束
            notifyCallEvent(callInfo.getCallerNumber(), "call_ended", callInfo);
            notifyCallEvent(callInfo.getCalleeNumber(), "call_ended", callInfo);
            
            // 清理呼叫信息
            cleanupCall(callId);
            
            return true;
        } catch (Exception e) {
            log.error("结束呼叫失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 发送DTMF按键
     *
     * @param request 呼叫请求
     * @return 是否成功
     */
    public boolean sendDtmf(FreeSwitchCallRequest request) {
        String callId = request.getCallId();
        String digit = request.getDigit();
        log.info("发送DTMF: {} 按键 {}", callId, digit);
        
        try {
            FreeSwitchCallRequest callInfo = activeCallMap.get(callId);
            if (callInfo == null || callInfo.getCallUuid() == null) {
                log.warn("找不到呼叫信息或UUID: {}", callId);
                return false;
            }
            
            // 发送DTMF命令
            eslClient.sendSyncApiCommand("uuid_send_dtmf", 
                String.format("%s %s", callInfo.getCallUuid(), digit));
            
            return true;
        } catch (Exception e) {
            log.error("发送DTMF失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 静音/取消静音
     *
     * @param request 呼叫请求
     * @return 是否成功
     */
    public boolean toggleMute(FreeSwitchCallRequest request) {
        String callId = request.getCallId();
        boolean mute = request.getMute();
        log.info("{}静音: {}", mute ? "开启" : "关闭", callId);
        
        try {
            FreeSwitchCallRequest callInfo = activeCallMap.get(callId);
            if (callInfo == null || callInfo.getCallUuid() == null) {
                log.warn("找不到呼叫信息或UUID: {}", callId);
                return false;
            }
            
            // 发送静音/取消静音命令
            String command = mute ? "uuid_audio mute" : "uuid_audio unmute";
            eslClient.sendSyncApiCommand(command, 
                String.format("%s read", callInfo.getCallUuid()));
            
            return true;
        } catch (Exception e) {
            log.error("{}静音失败: {}", mute ? "开启" : "关闭", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 处理呼叫开始
     *
     * @param callerId    主叫号码
     * @param destination 被叫号码
     * @param uuid        呼叫UUID
     */
    public void handleCallStart(String callerId, String destination, String uuid) {
        log.info("处理呼叫开始: {} -> {} (UUID: {})", callerId, destination, uuid);
        
        // 更新用户最后注册时间
        updateUserLastRegistration(callerId);
        updateUserLastRegistration(destination);
        
        // 查找匹配的呼叫
        for (Map.Entry<String, FreeSwitchCallRequest> entry : activeCallMap.entrySet()) {
            FreeSwitchCallRequest callInfo = entry.getValue();
            
            // 检查是否匹配
            if ((callInfo.getCallerNumber().equals(callerId) && callInfo.getCalleeNumber().equals(destination)) ||
                (callInfo.getCalleeNumber().equals(callerId) && callInfo.getCallerNumber().equals(destination))) {
                
                // 更新UUID
                callInfo.setCallUuid(uuid);
                activeCallMap.put(entry.getKey(), callInfo);
                
                log.info("已关联UUID到呼叫: {} -> UUID {}", entry.getKey(), uuid);
                return;
            }
        }
        
        log.warn("无法找到匹配的呼叫: {} -> {}", callerId, destination);
    }

    /**
     * 处理呼叫应答
     *
     * @param uuid 呼叫UUID
     */
    public void handleCallAnswered(String uuid) {
        log.info("处理呼叫应答: UUID {}", uuid);
        
        // 查找匹配的呼叫
        for (Map.Entry<String, FreeSwitchCallRequest> entry : activeCallMap.entrySet()) {
            FreeSwitchCallRequest callInfo = entry.getValue();
            
            // 检查UUID是否匹配
            if (uuid.equals(callInfo.getCallUuid())) {
                // 更新状态
                callInfo.setStatus("ANSWERED");
                callInfo.setStartTime(System.currentTimeMillis());
                activeCallMap.put(entry.getKey(), callInfo);
                
                // 通知用户
                notifyCallEvent(callInfo.getCallerNumber(), "call_answered", callInfo);
                notifyCallEvent(callInfo.getCalleeNumber(), "call_answered", callInfo);
                
                log.info("通话已应答: {}", entry.getKey());
                return;
            }
        }
        
        log.warn("无法找到匹配的呼叫: UUID {}", uuid);
    }

    /**
     * 处理呼叫结束
     *
     * @param uuid        呼叫UUID
     * @param hangupCause 挂断原因
     */
    public void handleCallEnd(String uuid, String hangupCause) {
        log.info("处理呼叫结束: UUID {} 原因 {}", uuid, hangupCause);
        
        // 查找匹配的呼叫
        for (Map.Entry<String, FreeSwitchCallRequest> entry : activeCallMap.entrySet()) {
            FreeSwitchCallRequest callInfo = entry.getValue();
            
            // 检查UUID是否匹配
            if (uuid.equals(callInfo.getCallUuid())) {
                // 更新状态
                callInfo.setStatus("ENDED");
                callInfo.setEndTime(System.currentTimeMillis());
                callInfo.setNotes(hangupCause);
                activeCallMap.put(entry.getKey(), callInfo);
                
                // 保存CDR记录到数据库（如果还没有保存的话）
                saveCdrRecord(callInfo, hangupCause);
                
                // 通知用户
                notifyCallEvent(callInfo.getCallerNumber(), "call_ended", callInfo);
                notifyCallEvent(callInfo.getCalleeNumber(), "call_ended", callInfo);
                
                // 清理呼叫
                cleanupCall(entry.getKey());
                
                log.info("通话已结束: {}", entry.getKey());
                return;
            }
        }
        
        log.warn("无法找到匹配的呼叫: UUID {}", uuid);
    }

    /**
     * 处理DTMF按键
     *
     * @param uuid  呼叫UUID
     * @param digit 按键值
     */
    public void handleDtmf(String uuid, String digit) {
        log.info("处理DTMF: UUID {} 按键 {}", uuid, digit);
        
        // 查找匹配的呼叫
        for (Map.Entry<String, FreeSwitchCallRequest> entry : activeCallMap.entrySet()) {
            FreeSwitchCallRequest callInfo = entry.getValue();
            
            // 检查UUID是否匹配
            if (uuid.equals(callInfo.getCallUuid())) {
                // 通知用户
                notifyCallEvent(callInfo.getCallerNumber(), "dtmf_received", 
                    Map.of("callId", entry.getKey(), "digit", digit));
                notifyCallEvent(callInfo.getCalleeNumber(), "dtmf_received", 
                    Map.of("callId", entry.getKey(), "digit", digit));
                
                return;
            }
        }
        
        log.warn("无法找到匹配的呼叫: UUID {}", uuid);
    }
    
    /**
     * 通知用户呼叫事件
     */
    private void notifyCallEvent(String userId, String eventType, Object data) {
        try {
            String destination = String.format("/user/%s/queue/call-events", userId);
            Map<String, Object> payload = Map.of(
                "type", eventType,
                "data", data,
                "timestamp", System.currentTimeMillis()
            );
            
            log.debug("发送呼叫事件: {} -> {} {}", destination, eventType, data);
            messagingTemplate.convertAndSendToUser(userId, "/queue/call-events", payload);
        } catch (Exception e) {
            log.error("发送呼叫事件失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 清理呼叫信息
     */
    private void cleanupCall(String callId) {
        FreeSwitchCallRequest callInfo = activeCallMap.remove(callId);
        if (callInfo != null) {
            userCallMap.remove(callInfo.getCallerNumber());
            userCallMap.remove(callInfo.getCalleeNumber());
            if (callInfo.getCallUuid() != null) {
                uuidCallMap.remove(callInfo.getCallUuid());
            }
        }
    }

    /**
     * 获取活动呼叫数量
     */
    public int getActiveCallCount() {
        return activeCallMap.size();
    }

    /**
     * 获取用户的活动呼叫
     */
    public FreeSwitchCallRequest getUserActiveCall(String userId) {
        String callId = userCallMap.get(userId);
        if (callId != null) {
            return activeCallMap.get(callId);
        }
        return null;
    }

    /**
     * 获取所有活动呼叫
     */
    public Map<String, FreeSwitchCallRequest> getAllActiveCalls() {
        return new java.util.HashMap<>(activeCallMap);
    }
    
    /**
     * 更新用户最后注册时间
     */
    private void updateUserLastRegistration(String username) {
        try {
            // Optional<FreeSwitchNumberEntity> userOptional = userService.findByUsername(username);
            // if (userOptional.isPresent()) {
            //     userService.updateLastRegistration(username, BdDateUtils.now());
            //     log.debug("已更新用户最后注册时间: {}", username);
            // }
        } catch (Exception e) {
            log.error("更新用户最后注册时间失败: {} - {}", username, e.getMessage(), e);
        }
    }
    
    /**
     * 保存CDR记录到数据库
     */
    private void saveCdrRecord(FreeSwitchCallRequest callInfo, String hangupCause) {
        try {
            // 检查是否已经存在CDR记录
            Optional<FreeSwitchCdrEntity> existingCdrOptional = cdrService.findByUid(callInfo.getCallUuid());
            if (!existingCdrOptional.isPresent()) {
                // 创建新的CDR记录
                FreeSwitchCdrEntity cdr = new FreeSwitchCdrEntity();
                cdr.setUid(callInfo.getCallUuid());
                cdr.setCallerIdNumber(callInfo.getCallerNumber());
                cdr.setDestinationNumber(callInfo.getCalleeNumber());
                cdr.setStartStamp(BdDateUtils.now().minusSeconds(
                    (System.currentTimeMillis() - callInfo.getStartTime()) / 1000));
                
                if (callInfo.getStartTime() > 0) {
                    cdr.setAnswerStamp(BdDateUtils.now().minusSeconds(
                        (System.currentTimeMillis() - callInfo.getStartTime()) / 1000));
                }
                
                cdr.setEndStamp(BdDateUtils.now());
                cdr.setDuration((int) ((System.currentTimeMillis() - callInfo.getStartTime()) / 1000));
                cdr.setHangupCause(hangupCause);
                cdr.setDirection(callInfo.getType().toLowerCase());
                
                cdrService.createCdr(cdr);
                log.debug("已保存CDR记录: UUID {}", callInfo.getCallUuid());
            }
        } catch (Exception e) {
            log.error("保存CDR记录失败: UUID {} - {}", callInfo.getCallUuid(), e.getMessage(), e);
        }
    }
}
