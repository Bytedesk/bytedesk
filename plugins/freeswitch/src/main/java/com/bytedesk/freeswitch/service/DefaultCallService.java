/*
 * @Author: GitHub Copilot
 * @Date: 2025-06-03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认呼叫服务实现，当FreeSwitch未启用时使用
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "false", matchIfMissing = true)
public class DefaultCallService implements ICallService {

    public DefaultCallService() {
        log.info("创建默认呼叫服务（FreeSwitch未启用）");
    }

    @Override
    public String makeCall(String fromUser, String toUser) {
        log.warn("FreeSwitch未启用，无法发起呼叫");
        return null;
    }

    @Override
    public boolean answerCall(String callId) {
        log.warn("FreeSwitch未启用，无法应答呼叫");
        return false;
    }

    @Override
    public boolean rejectCall(String callId) {
        log.warn("FreeSwitch未启用，无法拒绝呼叫");
        return false;
    }

    @Override
    public boolean endCall(String callId) {
        log.warn("FreeSwitch未启用，无法结束呼叫");
        return false;
    }

    @Override
    public boolean sendDtmf(String callId, String digit) {
        log.warn("FreeSwitch未启用，无法发送DTMF");
        return false;
    }

    @Override
    public boolean toggleMute(String callId, boolean mute) {
        log.warn("FreeSwitch未启用，无法{}静音", mute ? "开启" : "关闭");
        return false;
    }

    @Override
    public void handleCallStart(String callerId, String destination, String uuid) {
        // 无操作
    }

    @Override
    public void handleCallAnswered(String uuid) {
        // 无操作
    }

    @Override
    public void handleCallEnd(String uuid, String hangupCause) {
        // 无操作
    }

    @Override
    public void handleDtmf(String uuid, String digit) {
        // 无操作
    }
}
