/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 12:45:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 12:29:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.call;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.freeswitch.call.event.FreeSwitchCallAnsweredEvent;
import com.bytedesk.freeswitch.call.event.FreeSwitchCallHangupEvent;
import com.bytedesk.freeswitch.call.event.FreeSwitchCallStartEvent;
import com.bytedesk.freeswitch.call.event.FreeSwitchDtmfEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 通话事件监听器
 * 负责监听和处理各种通话相关的事件
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchCallEventListener {

    private final FreeSwitchCallService callService;
    // private final FreeSwitchCdrService cdrService; // 暂时未使用，保留以备后续扩展
    // private final FreeSwitchNumberService userService;

    /**
     * 监听通话开始事件
     */
    @EventListener
    public void handleCallStartEvent(FreeSwitchCallStartEvent event) {
        log.info("接收到通话开始事件: UUID {} 主叫 {} 被叫 {}", 
                event.getUuid(), event.getCallerId(), event.getDestination());
        
        try {
            // 更新用户活动时间
            updateUserActivity(event.getCallerId());
            updateUserActivity(event.getDestination());
            
            callService.handleCallStart(event.getCallerId(), event.getDestination(), event.getUuid());
            log.debug("通话开始事件处理完成: UUID {}", event.getUuid());
        } catch (Exception e) {
            log.error("处理通话开始事件失败: UUID {} - {}", event.getUuid(), e.getMessage(), e);
        }
    }

    /**
     * 监听通话应答事件
     */
    @EventListener
    public void handleCallAnsweredEvent(FreeSwitchCallAnsweredEvent event) {
        log.info("接收到通话应答事件: UUID {}", event.getUuid());
        
        try {
            callService.handleCallAnswered(event.getUuid());
            
            // 额外的数据库操作已在FreeSwitchEventListener中处理
            log.debug("通话应答事件处理完成: UUID {}", event.getUuid());
        } catch (Exception e) {
            log.error("处理通话应答事件失败: UUID {} - {}", event.getUuid(), e.getMessage(), e);
        }
    }

    /**
     * 监听通话挂断事件
     */
    @EventListener
    public void handleCallHangupEvent(FreeSwitchCallHangupEvent event) {
        log.info("接收到通话挂断事件: UUID {} 原因 {}", 
                event.getUuid(), event.getHangupCause());
        
        try {
            callService.handleCallEnd(event.getUuid(), event.getHangupCause());
            
            // 额外的CDR更新已在FreeSwitchEventListener中处理
            log.debug("通话挂断事件处理完成: UUID {}", event.getUuid());
        } catch (Exception e) {
            log.error("处理通话挂断事件失败: UUID {} - {}", event.getUuid(), e.getMessage(), e);
        }
    }

    /**
     * 监听DTMF事件
     */
    @EventListener
    public void handleDtmfEvent(FreeSwitchDtmfEvent event) {
        log.info("接收到DTMF事件: UUID {} 按键 {}", 
                event.getUuid(), event.getDtmfDigit());
        
        try {
            callService.handleDtmf(event.getUuid(), event.getDtmfDigit());
            log.debug("DTMF事件处理完成: UUID {} 按键 {}", event.getUuid(), event.getDtmfDigit());
        } catch (Exception e) {
            log.error("处理DTMF事件失败: UUID {} 按键 {} - {}", 
                    event.getUuid(), event.getDtmfDigit(), e.getMessage(), e);
        }
    }
    
    /**
     * 更新用户活动时间
     */
    private void updateUserActivity(String username) {
        try {
            // if (userService.findByUsername(username).isPresent()) {
            //     userService.updateLastRegistration(username, BdDateUtils.now());
            //     log.debug("已更新用户活动时间: {}", username);
            // }
        } catch (Exception e) {
            log.error("更新用户活动时间失败: {} - {}", username, e.getMessage(), e);
        }
    }
}
