package com.bytedesk.freeswitch.callcenter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.freeswitch.esl.client.inbound.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// import com.bytedesk.freeswitch.freeswitch.FreeSwitchProperties;
import com.bytedesk.freeswitch.freeswitch.FreeSwitchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 呼叫服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class CallService {

    private final Client eslClient;
    // private final FreeSwitchProperties freeSwitchProperties;
    private final FreeSwitchService freeSwitchService;
    private final SimpMessagingTemplate messagingTemplate;
    
    // 存储活动呼叫信息
    private final Map<String, CallInfo> activeCallMap = new ConcurrentHashMap<>();
    // 用户与UUID的映射
    private final Map<String, String> userCallMap = new ConcurrentHashMap<>();
    // UUID与CallInfo的映射
    private final Map<String, CallInfo> uuidCallMap = new ConcurrentHashMap<>();

    /**
     * 发起呼叫
     *
     * @param fromUser 主叫用户ID
     * @param toUser   被叫用户ID
     * @return 呼叫ID
     */
    public String makeCall(String fromUser, String toUser) {
        log.info("发起呼叫: 从 {} 到 {}", fromUser, toUser);
        
        try {
            // 使用FreeSwitchService发起呼叫
            String result = freeSwitchService.originate(fromUser, toUser, "default");
            
            if (result != null && !result.contains("ERR")) {
                // 生成呼叫ID
                String callId = String.format("%s-%s-%d", fromUser, toUser, System.currentTimeMillis());
                
                // 创建呼叫信息对象
                CallInfo callInfo = new CallInfo();
                callInfo.setCallId(callId);
                callInfo.setFromUser(fromUser);
                callInfo.setToUser(toUser);
                callInfo.setStartTime(System.currentTimeMillis());
                callInfo.setStatus("CALLING");
                
                // 存储呼叫信息
                activeCallMap.put(callId, callInfo);
                userCallMap.put(fromUser, callId);
                userCallMap.put(toUser, callId);
                
                // 通知用户有新的呼叫
                notifyCallEvent(toUser, "incoming_call", callInfo);
                notifyCallEvent(fromUser, "outgoing_call", callInfo);
                
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
     * @param callId 呼叫ID
     * @return 是否成功
     */
    public boolean answerCall(String callId) {
        log.info("应答呼叫: {}", callId);
        
        try {
            CallInfo callInfo = activeCallMap.get(callId);
            if (callInfo == null) {
                log.warn("找不到呼叫信息: {}", callId);
                return false;
            }
            
            // 更新呼叫状态
            callInfo.setStatus("ANSWERED");
            callInfo.setAnswerTime(System.currentTimeMillis());
            
            // 存储更新后的呼叫信息
            activeCallMap.put(callId, callInfo);
            
            // 如果呼叫尚未应答，则发送应答命令
            if (callInfo.getUuid() != null) {
                freeSwitchService.answer(callInfo.getUuid());
            }
            
            // 通知用户呼叫已应答
            notifyCallEvent(callInfo.getFromUser(), "call_answered", callInfo);
            notifyCallEvent(callInfo.getToUser(), "call_answered", callInfo);
            
            return true;
        } catch (Exception e) {
            log.error("应答呼叫失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 拒绝呼叫
     *
     * @param callId 呼叫ID
     * @return 是否成功
     */
    public boolean rejectCall(String callId) {
        log.info("拒绝呼叫: {}", callId);
        
        try {
            CallInfo callInfo = activeCallMap.get(callId);
            if (callInfo == null) {
                log.warn("找不到呼叫信息: {}", callId);
                return false;
            }
            
            // 更新呼叫状态
            callInfo.setStatus("REJECTED");
            callInfo.setEndTime(System.currentTimeMillis());
            
            // 如果呼叫UUID存在，则挂断
            if (callInfo.getUuid() != null) {
                eslClient.sendSyncApiCommand("uuid_kill", callInfo.getUuid());
            }
            
            // 通知用户呼叫已拒绝
            notifyCallEvent(callInfo.getFromUser(), "call_rejected", callInfo);
            
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
     * @param callId 呼叫ID
     * @return 是否成功
     */
    public boolean endCall(String callId) {
        log.info("结束呼叫: {}", callId);
        
        try {
            CallInfo callInfo = activeCallMap.get(callId);
            if (callInfo == null) {
                log.warn("找不到呼叫信息: {}", callId);
                return false;
            }
            
            // 更新呼叫状态
            callInfo.setStatus("ENDED");
            callInfo.setEndTime(System.currentTimeMillis());
            
            // 如果呼叫UUID存在，则挂断
            if (callInfo.getUuid() != null) {
                eslClient.sendSyncApiCommand("uuid_kill", callInfo.getUuid());
            }
            
            // 通知用户呼叫已结束
            notifyCallEvent(callInfo.getFromUser(), "call_ended", callInfo);
            notifyCallEvent(callInfo.getToUser(), "call_ended", callInfo);
            
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
     * @param callId 呼叫ID
     * @param digit  按键值
     * @return 是否成功
     */
    public boolean sendDtmf(String callId, String digit) {
        log.info("发送DTMF: {} 按键 {}", callId, digit);
        
        try {
            CallInfo callInfo = activeCallMap.get(callId);
            if (callInfo == null || callInfo.getUuid() == null) {
                log.warn("找不到呼叫信息或UUID: {}", callId);
                return false;
            }
            
            // 发送DTMF命令
            eslClient.sendSyncApiCommand("uuid_send_dtmf", 
                String.format("%s %s", callInfo.getUuid(), digit));
            
            return true;
        } catch (Exception e) {
            log.error("发送DTMF失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 静音/取消静音
     *
     * @param callId 呼叫ID
     * @param mute   是否静音
     * @return 是否成功
     */
    public boolean toggleMute(String callId, boolean mute) {
        log.info("{}静音: {}", mute ? "开启" : "关闭", callId);
        
        try {
            CallInfo callInfo = activeCallMap.get(callId);
            if (callInfo == null || callInfo.getUuid() == null) {
                log.warn("找不到呼叫信息或UUID: {}", callId);
                return false;
            }
            
            // 发送静音/取消静音命令
            String command = mute ? "uuid_audio mute" : "uuid_audio unmute";
            eslClient.sendSyncApiCommand(command, 
                String.format("%s read", callInfo.getUuid()));
            
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
        
        // 查找匹配的呼叫
        for (Map.Entry<String, CallInfo> entry : activeCallMap.entrySet()) {
            CallInfo callInfo = entry.getValue();
            
            // 检查是否匹配
            if ((callInfo.getFromUser().equals(callerId) && callInfo.getToUser().equals(destination)) ||
                (callInfo.getToUser().equals(callerId) && callInfo.getFromUser().equals(destination))) {
                
                // 更新UUID
                callInfo.setUuid(uuid);
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
        for (Map.Entry<String, CallInfo> entry : activeCallMap.entrySet()) {
            CallInfo callInfo = entry.getValue();
            
            // 检查UUID是否匹配
            if (uuid.equals(callInfo.getUuid())) {
                // 更新状态
                callInfo.setStatus("ANSWERED");
                callInfo.setAnswerTime(System.currentTimeMillis());
                activeCallMap.put(entry.getKey(), callInfo);
                
                // 通知用户
                notifyCallEvent(callInfo.getFromUser(), "call_answered", callInfo);
                notifyCallEvent(callInfo.getToUser(), "call_answered", callInfo);
                
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
        for (Map.Entry<String, CallInfo> entry : activeCallMap.entrySet()) {
            CallInfo callInfo = entry.getValue();
            
            // 检查UUID是否匹配
            if (uuid.equals(callInfo.getUuid())) {
                // 更新状态
                callInfo.setStatus("ENDED");
                callInfo.setEndTime(System.currentTimeMillis());
                callInfo.setHangupCause(hangupCause);
                activeCallMap.put(entry.getKey(), callInfo);
                
                // 通知用户
                notifyCallEvent(callInfo.getFromUser(), "call_ended", callInfo);
                notifyCallEvent(callInfo.getToUser(), "call_ended", callInfo);
                
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
        for (Map.Entry<String, CallInfo> entry : activeCallMap.entrySet()) {
            CallInfo callInfo = entry.getValue();
            
            // 检查UUID是否匹配
            if (uuid.equals(callInfo.getUuid())) {
                // 通知用户
                notifyCallEvent(callInfo.getFromUser(), "dtmf_received", 
                    Map.of("callId", entry.getKey(), "digit", digit));
                notifyCallEvent(callInfo.getToUser(), "dtmf_received", 
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
        CallInfo callInfo = activeCallMap.remove(callId);
        if (callInfo != null) {
            userCallMap.remove(callInfo.getFromUser());
            userCallMap.remove(callInfo.getToUser());
            if (callInfo.getUuid() != null) {
                uuidCallMap.remove(callInfo.getUuid());
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
    public CallInfo getUserActiveCall(String userId) {
        String callId = userCallMap.get(userId);
        if (callId != null) {
            return activeCallMap.get(callId);
        }
        return null;
    }

    /**
     * 获取所有活动呼叫
     */
    public Map<String, CallInfo> getAllActiveCalls() {
        return new java.util.HashMap<>(activeCallMap);
    }
}
