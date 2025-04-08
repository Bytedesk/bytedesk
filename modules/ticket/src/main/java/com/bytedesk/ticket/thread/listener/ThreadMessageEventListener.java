/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-06 10:15:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-07 17:11:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.listener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.flowable.engine.RuntimeService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.ticket.thread.ThreadConsts;
import com.bytedesk.ticket.thread.ThreadTransferToAgentService;

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

    private final ThreadTransferToAgentService threadTransferToAgentService;
    private final ThreadRestService threadRestService;
    private final RuntimeService runtimeService;
    private final QueueMemberRestService queueMemberRestService;
    
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
                message.getUid(), message.getThreadUid(), message.getContent()); 
        
        // 获取消息对应的会话线程
        ThreadEntity thread = null;
        try {
            thread = threadRestService.findByUid(message.getThreadUid()).orElse(null);
            if (thread == null) {
                log.warn("消息对应的会话不存在: messageUid={}, threadUid={}", 
                        message.getUid(), message.getThreadUid());
                return;
            }
            
            // 处理访客消息，检查转人工请求
            threadTransferToAgentService.processVisitorMessage(message, thread);
            
            // 如果是访客发送的消息，重置空闲超时计时器
            if (message.isFromVisitor()) {
                resetIdleTimers(thread);
                // 更新访客消息统计
                updateVisitorMessageStats(message, thread);
            } else if (message.isFromAgent() || message.isFromRobot()) {
                // 更新客服消息统计
                updateAgentMessageStats(message, thread);
            }
            
        } catch (Exception e) {
            log.error("处理消息事件时出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 更新访客消息统计
     * 
     * @param message 消息对象
     * @param thread 会话对象
     */
    private void updateVisitorMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        
        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }
            
            QueueMemberEntity queueMember = queueMemberOpt.get();
            LocalDateTime now = LocalDateTime.now();
            
            // 更新首次消息时间（如果尚未设置）
            if (queueMember.getFirstMessageTime() == null) {
                queueMember.setFirstMessageTime(now);
            }
            
            // 更新最后一次访客消息时间
            queueMember.setLastMessageTime(now);
            
            // 更新访客消息计数
            queueMember.setVisitorMessageCount(queueMember.getVisitorMessageCount() + 1);
            
            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员访客消息统计: threadUid={}, visitorMsgCount={}", 
                    thread.getUid(), queueMember.getVisitorMessageCount());
        } catch (Exception e) {
            log.error("更新访客消息统计时出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 更新客服消息统计
     * 
     * @param message 消息对象
     * @param thread 会话对象
     */
    private void updateAgentMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        
        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }
            
            QueueMemberEntity queueMember = queueMemberOpt.get();
            LocalDateTime now = LocalDateTime.now();
            
            // 更新客服消息计数
            queueMember.setAgentMessageCount(queueMember.getAgentMessageCount() + 1);
            
            // 如果是首次响应，记录首次响应时间
            if (!queueMember.isFirstResponse() && queueMember.getLastMessageTime() != null) {
                queueMember.setFirstResponse(true);
                queueMember.setFirstResponseTime(now);
                
                // 计算首次响应时间（秒）
                long responseTimeInSeconds = Duration.between(queueMember.getLastMessageTime(), now).getSeconds();
                queueMember.setMaxResponseTime((int) responseTimeInSeconds);
                queueMember.setAvgResponseTime((int) responseTimeInSeconds);
            } else if (queueMember.getLastMessageTime() != null) {
                // 非首次响应，更新平均和最大响应时间
                long responseTimeInSeconds = Duration.between(queueMember.getLastMessageTime(), now).getSeconds();
                
                // 更新最大响应时间
                if (responseTimeInSeconds > queueMember.getMaxResponseTime()) {
                    queueMember.setMaxResponseTime((int) responseTimeInSeconds);
                }
                
                // 更新平均响应时间 - 使用累计平均计算方法
                // (currentAvg * (messageCount-1) + newValue) / messageCount
                int messageCount = queueMember.getAgentMessageCount();
                if (messageCount > 1) { // 避免除以零
                    int currentTotal = queueMember.getAvgResponseTime() * (messageCount - 1);
                    queueMember.setAvgResponseTime((currentTotal + (int) responseTimeInSeconds) / messageCount);
                }
            }
            
            // 更新最后响应时间
            queueMember.setLastResponseTime(now);
            
            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员客服消息统计: threadUid={}, agentMsgCount={}, avgResponseTime={}s, maxResponseTime={}s", 
                    thread.getUid(), queueMember.getAgentMessageCount(), 
                    queueMember.getAvgResponseTime(), queueMember.getMaxResponseTime());
        } catch (Exception e) {
            log.error("更新客服消息统计时出错: {}", e.getMessage(), e);
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
            if (thread.isRobotType() || thread.isAgentRobot()) {
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
