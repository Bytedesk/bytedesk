/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 07:51:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:29:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class QueueMemberEventListener {

    private final QueueMemberRestService queueMemberRestService;

    private final ThreadRestService threadRestService;

    @EventListener
    public void onThreadAcceptEvent(ThreadAcceptEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("queue member onThreadAcceptEvent: {}", thread.getUid());
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
        if (memberOptional.isPresent()) {
            QueueMemberEntity member = memberOptional.get();
            member.manualAcceptThread();
            queueMemberRestService.save(member);
            // TODO: 找出队列中所有等待中的访客，发送更新排队数量消息，通知访客前端
            // MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(thread.getAgent(), thread);
            // messageSendService.sendProtobufMessage(messageProtobuf);
        } else {
            log.error("queue member onThreadAcceptEvent: member not found: {}", thread.getUid());
        }
    }

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (message == null) {
            return;
        }
        // log.debug("QueueMemberEventListener 接收到新消息事件: messageUid={}, threadUid={}, content={}",
        //         message.getUid(), message.getThread().getUid(), message.getContent()); 
        
        // 获取消息对应的会话线程
        ThreadEntity thread = null;
        try {
            thread = threadRestService.findByUid(message.getThread().getUid()).orElse(null);
            if (thread == null) {
                log.warn("消息对应的会话不存在: messageUid={}, threadUid={}", 
                        message.getUid(), message.getThread().getUid());
                return;
            }
            
            if (message.isFromVisitor()) {
                // 更新访客消息统计
                updateVisitorMessageStats(message, thread);
            } else if (message.isFromAgent()) {
                // 更新客服消息统计
                updateAgentMessageStats(message, thread);
            } else if (message.isFromRobot()) {
                // 处理机器人消息
                updateRobotMessageStats(message, thread);
            } else if (message.isFromSystem()) {
                // 处理系统消息
                updateSystemMessageStats(message, thread);
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
        if (!message.isFromVisitor()) {
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
            ZonedDateTime now = BdDateUtils.now();
            
            // 更新首次消息时间（如果尚未设置）
            if (queueMember.getVisitorFirstMessageAt() == null) {
                queueMember.setVisitorFirstMessageAt(now);
            }
            
            // 更新最后一次访客消息时间
            queueMember.setVisitorLastMessageAt(now);
            
            // 更新访客消息计数
            queueMember.setVisitorMessageCount(queueMember.getVisitorMessageCount() + 1);
            
            // 保存更新 - 支持重试机制
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
        if (!message.isFromAgent()) {
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
            ZonedDateTime now = BdDateUtils.now();
            
            // 更新客服消息计数
            queueMember.setAgentMessageCount(queueMember.getAgentMessageCount() + 1);
            
            // 如果是首次响应，记录首次响应时间
            if (!queueMember.getAgentFirstResponse() && queueMember.getVisitorLastMessageAt() != null) {
                queueMember.setAgentFirstResponse(true);
                queueMember.setAgentFirstResponseAt(now);
                
                // 计算首次响应时间（秒）
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();
                queueMember.setAgentMaxResponseLength((int) responseTimeInSeconds);
                queueMember.setAgentAvgResponseLength((int) responseTimeInSeconds);
            } else if (queueMember.getVisitorLastMessageAt() != null) {
                // 非首次响应，更新平均和最大响应时间
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();
                
                // 更新最大响应时间
                if (responseTimeInSeconds > queueMember.getAgentMaxResponseLength()) {
                    queueMember.setAgentMaxResponseLength((int) responseTimeInSeconds);
                }
                
                // 更新平均响应时间 - 使用累计平均计算方法
                // (currentAvg * (messageCount-1) + newValue) / messageCount
                int messageCount = queueMember.getAgentMessageCount();
                if (messageCount > 1) { // 避免除以零
                    int currentTotal = queueMember.getAgentAvgResponseLength() * (messageCount - 1);
                    queueMember.setAgentAvgResponseLength((currentTotal + (int) responseTimeInSeconds) / messageCount);
                }
            }
            
            // 更新最后响应时间
            queueMember.setAgentLastResponseAt(now);
            
            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员客服消息统计: threadUid={}, agentMsgCount={}, avgResponseTime={}s, maxResponseTime={}s", 
                    thread.getUid(), queueMember.getAgentMessageCount(), 
                    queueMember.getAgentAvgResponseLength(), queueMember.getAgentMaxResponseLength());
        } catch (Exception e) {
            log.error("更新客服消息统计时出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 更新机器人消息统计
     *
     * @param message 消息对象
     * @param thread 会话对象
     */
    private void updateRobotMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromRobot()) {
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
            ZonedDateTime now = BdDateUtils.now();
            
            // 更新首次机器人消息时间（如果尚未设置）
            if (queueMember.getRobotFirstResponseAt() == null) {
                queueMember.setRobotFirstResponseAt(now);
            }
            
            // 更新最后一次机器人消息时间
            queueMember.setRobotLastResponseAt(now);
            
            // 更新机器人消息计数
            queueMember.setRobotMessageCount(queueMember.getRobotMessageCount() + 1);
            
            // 如果是访客提问后的机器人回复，计算响应时间
            if (queueMember.getVisitorLastMessageAt() != null) {
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();
                
                // 更新最大响应时间
                if (queueMember.getRobotMaxResponseLength() == 0 || 
                    responseTimeInSeconds > queueMember.getRobotMaxResponseLength()) {
                    queueMember.setRobotMaxResponseLength((int) responseTimeInSeconds);
                }
                
                // 更新平均响应时间
                int messageCount = queueMember.getRobotMessageCount();
                if (messageCount > 1) {
                    int currentTotal = queueMember.getRobotAvgResponseLength() * (messageCount - 1);
                    queueMember.setRobotAvgResponseLength((currentTotal + (int) responseTimeInSeconds) / messageCount);
                } else {
                    queueMember.setRobotAvgResponseLength((int) responseTimeInSeconds);
                }
            }
            
            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员机器人消息统计: threadUid={}, robotMsgCount={}, avgResponseTime={}s, maxResponseTime={}s", 
                    thread.getUid(), queueMember.getRobotMessageCount(), 
                    queueMember.getRobotAvgResponseLength(), queueMember.getRobotMaxResponseLength());
        } catch (Exception e) {
            log.error("更新机器人消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新系统消息统计
     *
     * @param message 消息对象
     * @param thread 会话对象
     */
    private void updateSystemMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromSystem()) {
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
            ZonedDateTime now = BdDateUtils.now();
            
            // 更新系统消息计数
            queueMember.setSystemMessageCount(queueMember.getSystemMessageCount() + 1);
            
            // 记录首次系统消息时间（如果尚未设置）
            if (queueMember.getSystemFirstResponseAt() == null) {
                queueMember.setSystemFirstResponseAt(now);
            }
            
            // 更新最后一次系统消息时间
            queueMember.setSystemLastResponseAt(now);
            
            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员系统消息统计: threadUid={}, systemMsgCount={}", 
                    thread.getUid(), queueMember.getSystemMessageCount());
        } catch (Exception e) {
            log.error("更新系统消息统计时出错: {}", e.getMessage(), e);
        }
    }



    
}
