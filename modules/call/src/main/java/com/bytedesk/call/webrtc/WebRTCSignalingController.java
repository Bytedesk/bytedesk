/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-07 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:56:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.webrtc;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.bytedesk.call.config.CallService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebRTC信令控制器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class WebRTCSignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CallService freeSwitchService;

    /**
     * 处理WebRTC Offer
     */
    @MessageMapping("/webrtc/offer")
    public void handleOffer(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String fromUser = (String) message.get("fromUser");
        String toUser = (String) message.get("toUser");
        Object sdp = message.get("sdp");

        log.info("收到WebRTC Offer: {} -> {}, Session: {}", fromUser, toUser, sessionId);

        try {
            // 创建Call通道
            String result = freeSwitchService.originate(fromUser, toUser, "default");
            
            if (result != null) {
                // 转发Offer到目标用户
                messagingTemplate.convertAndSendToUser(toUser, "/queue/webrtc/offer", Map.of(
                    "fromUser", fromUser,
                    "toUser", toUser,
                    "sdp", sdp,
                    "sessionId", sessionId,
                    "freeSwitchResult", result
                ));
            } else {
                // 发送错误消息
                messagingTemplate.convertAndSendToUser(fromUser, "/queue/webrtc/error", Map.of(
                    "error", "Failed to create Call channel",
                    "sessionId", sessionId
                ));
            }
        } catch (Exception e) {
            log.error("处理WebRTC Offer失败: {}", e.getMessage(), e);
            messagingTemplate.convertAndSendToUser(fromUser, "/queue/webrtc/error", Map.of(
                "error", e.getMessage(),
                "sessionId", sessionId
            ));
        }
    }

    /**
     * 处理WebRTC Answer
     */
    @MessageMapping("/webrtc/answer")
    public void handleAnswer(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String fromUser = (String) message.get("fromUser");
        String toUser = (String) message.get("toUser");
        Object sdp = message.get("sdp");

        log.info("收到WebRTC Answer: {} -> {}, Session: {}", fromUser, toUser, sessionId);

        // 转发Answer到发起方
        messagingTemplate.convertAndSendToUser(toUser, "/queue/webrtc/answer", Map.of(
            "fromUser", fromUser,
            "toUser", toUser,
            "sdp", sdp,
            "sessionId", sessionId
        ));
    }

    /**
     * 处理ICE候选
     */
    @MessageMapping("/webrtc/ice-candidate")
    public void handleIceCandidate(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String fromUser = (String) message.get("fromUser");
        String toUser = (String) message.get("toUser");
        Object candidate = message.get("candidate");

        log.debug("收到ICE候选: {} -> {}, Session: {}", fromUser, toUser, sessionId);

        // 转发ICE候选到对方
        messagingTemplate.convertAndSendToUser(toUser, "/queue/webrtc/ice-candidate", Map.of(
            "fromUser", fromUser,
            "toUser", toUser,
            "candidate", candidate,
            "sessionId", sessionId
        ));
    }

    /**
     * 处理通话挂断
     */
    @MessageMapping("/webrtc/hangup")
    public void handleHangup(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String fromUser = (String) message.get("fromUser");
        String toUser = (String) message.get("toUser");
        String uuid = (String) message.get("uuid");

        log.info("收到挂断请求: {} -> {}, UUID: {}, Session: {}", fromUser, toUser, uuid, sessionId);

        try {
            // 通过Call挂断通话
            if (uuid != null) {
                freeSwitchService.hangup(uuid, "NORMAL_CLEARING");
            }

            // 通知对方挂断
            messagingTemplate.convertAndSendToUser(toUser, "/queue/webrtc/hangup", Map.of(
                "fromUser", fromUser,
                "toUser", toUser,
                "uuid", uuid,
                "sessionId", sessionId
            ));
        } catch (Exception e) {
            log.error("处理挂断请求失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理通话拒绝
     */
    @MessageMapping("/webrtc/reject")
    public void handleReject(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String fromUser = (String) message.get("fromUser");
        String toUser = (String) message.get("toUser");
        String reason = (String) message.get("reason");

        log.info("收到拒绝请求: {} -> {}, 原因: {}, Session: {}", fromUser, toUser, reason, sessionId);

        // 通知发起方被拒绝
        messagingTemplate.convertAndSendToUser(toUser, "/queue/webrtc/reject", Map.of(
            "fromUser", fromUser,
            "toUser", toUser,
            "reason", reason != null ? reason : "Call rejected",
            "sessionId", sessionId
        ));
    }

    /**
     * 处理DTMF
     */
    @MessageMapping("/webrtc/dtmf")
    public void handleDtmf(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String uuid = (String) message.get("uuid");
        String digits = (String) message.get("digits");

        log.info("收到DTMF: UUID: {}, 数字: {}, Session: {}", uuid, digits, sessionId);

        try {
            if (uuid != null && digits != null) {
                freeSwitchService.sendDtmf(uuid, digits);
            }
        } catch (Exception e) {
            log.error("发送DTMF失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理静音/取消静音
     */
    @MessageMapping("/webrtc/mute")
    public void handleMute(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String uuid = (String) message.get("uuid");
        Boolean muted = (Boolean) message.get("muted");
        String fromUser = (String) message.get("fromUser");
        String toUser = (String) message.get("toUser");

        log.info("收到静音请求: UUID: {}, 静音: {}, Session: {}", uuid, muted, sessionId);

        try {
            if (uuid != null) {
                // 这里可以通过Call API控制音频
                // freeSwitchService.executeApiCommand("uuid_audio", uuid + " " + (muted ? "pause" : "resume"));
            }

            // 通知对方静音状态
            messagingTemplate.convertAndSendToUser(toUser, "/queue/webrtc/mute", Map.of(
                "fromUser", fromUser,
                "uuid", uuid,
                "muted", muted,
                "sessionId", sessionId
            ));
        } catch (Exception e) {
            log.error("处理静音请求失败: {}", e.getMessage(), e);
        }
    }
}
