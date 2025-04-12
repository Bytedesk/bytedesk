/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-06 10:15:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 10:33:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.listener;

import java.util.Date;
import org.flowable.engine.RuntimeService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.ticket.thread.ThreadConsts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息事件监听器
 * 
 * 当新消息创建时，检查是否需要触发转人工请求
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThreadMessageEventListener {

    // private final ThreadTransferToAgentService threadTransferToAgentService;
    private final ThreadRestService threadRestService;
    private final RuntimeService runtimeService;
    
    /**
     * 监听消息创建事件
     * 
     * @param event 消息创建事件
     */
    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (message == null) {
            return;
        }
        if (message.isFromSystem()) {
            return;
        }
        
        log.debug("接收到新消息事件: messageUid={}, threadUid={}, content={}",
                message.getUid(), message.getThread().getUid(), message.getContent()); 
        
        // 获取消息对应的会话线程
        ThreadEntity thread = null;
        try {
            thread = threadRestService.findByUid(message.getThread().getUid()).orElse(null);
            if (thread == null) {
                log.warn("消息对应的会话不存在: messageUid={}, threadUid={}", 
                        message.getUid(), message.getThread().getUid());
                return;
            }
            
            // 暂时先在前端拦截，转人工
            // 处理访客消息，检查转人工请求
            // threadTransferToAgentService.processVisitorMessage(message, thread);

            // 如果是访客发送的消息，重置空闲超时计时器
            if (message.isFromVisitor()) {
                resetIdleTimers(thread);
            }
            
        } catch (Exception e) {
            log.error("处理消息事件时出错: {}", e.getMessage(), e);
        }
    }
    
    
    /**
     * 重置会话的空闲超时计时器
     * 
     * 当访客发送消息时，需要重置空闲计时器，确保不会误判为空闲超时
     * 
     * @param thread 会话对象
     */
    private void resetIdleTimers(ThreadEntity thread) {
        if (thread == null || thread.getProcessInstanceId() == null) {
            log.warn("无法重置空闲计时器，会话或流程实例ID为空");
            return;
        }
        
        try {
            // 检查流程实例是否仍然活跃
            boolean exists = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(thread.getProcessInstanceId())
                    .count() > 0;
                    
            if (!exists) {
                log.warn("流程实例已结束，无需重置计时器: threadUid={}, processInstanceId={}", 
                        thread.getUid(), thread.getProcessInstanceId());
                return;
            }
            
            // 获取当前时间作为新的计时起点
            Date now = new Date();
            
            // 更新流程变量，重置空闲计时器
            if (thread.isRobotType() || thread.isRoboting()) {
                // 重置机器人空闲计时器
                runtimeService.setVariable(thread.getProcessInstanceId(), 
                        ThreadConsts.THREAD_VARIABLE_LAST_VISITOR_MESSAGE_TIME, now);
                        
                log.debug("重置机器人空闲计时器: threadUid={}, processInstanceId={}, time={}", 
                        thread.getUid(), thread.getProcessInstanceId(), now);
            } else {
                // 重置人工客服空闲计时器
                runtimeService.setVariable(thread.getProcessInstanceId(), 
                        ThreadConsts.THREAD_VARIABLE_LAST_VISITOR_MESSAGE_TIME, now);
                        
                log.debug("重置人工客服空闲计时器: threadUid={}, processInstanceId={}, time={}", 
                        thread.getUid(), thread.getProcessInstanceId(), now);
            }
            
            // 同时重置一个通用的访客最后活动时间变量，可用于各种计时目的
            runtimeService.setVariable(thread.getProcessInstanceId(), 
                    ThreadConsts.THREAD_VARIABLE_LAST_VISITOR_ACTIVITY_TIME, now);
            
        } catch (Exception e) {
            log.error("重置空闲计时器时出错: {}", e.getMessage(), e);
        }
    }


}
