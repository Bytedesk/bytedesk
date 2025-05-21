/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 14:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 11:40:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.delegate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Date;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.ticket.thread.ThreadConsts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 人工接待访客超时服务
 * 
 * 处理访客在人工接待过程中长时间未发送消息的情况
 * - 记录超时信息
 * - 发送超时提醒
 * - 结束会话
 */
@Slf4j
@Component("threadHumanIdleTimeoutServiceDelegate")
@RequiredArgsConstructor
public class ThreadHumanIdleTimeoutServiceDelegate implements JavaDelegate {
    
    private final QueueMemberRestService queueMemberRestService;

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Human idle timeout service for thread process: {}", processInstanceId);
        
        // 获取流程变量，使用常量
        String threadUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID);
        String userUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_USER_UID);
        String agentUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_AGENT_UID);
        String workgroupUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID);
        log.info("Processing human idle timeout for thread: {}, visitor: {}, agent: {}, workgroup: {}",
            threadUid, userUid, agentUid, workgroupUid); 
        
        // 检查是否真的超时 - 基于最后访客消息时间
        if (!isReallyTimedOut(execution)) {
            log.info("访客最近有活动，不执行超时处理: threadUid={}", threadUid);
            return;
        }
        
        // 标记会话为超时状态
        markThreadAsTimeout(threadUid);
        
        // 其他超时处理逻辑
    }
    
    /**
     * 检查是否真的超时
     * 
     * 考虑访客最后活动时间，如果访客在超时时间内有新消息，则不应判定为超时
     * 
     * @param execution 流程执行上下文
     * @return 如果真的超时，返回true；否则返回false
     */
    private Boolean isReallyTimedOut(DelegateExecution execution) {
        try {
            // 获取设置的超时时间
            Integer idleTimeout = (Integer) execution.getVariable(ThreadConsts.THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT);
            if (idleTimeout == null) {
                idleTimeout = ThreadConsts.DEFAULT_HUMAN_IDLE_TIMEOUT;
            }
            
            // 获取访客最后活动时间
            Date lastActivityTime = (Date) execution.getVariable(ThreadConsts.THREAD_VARIABLE_LAST_VISITOR_MESSAGE_TIME);
            if (lastActivityTime == null) {
                // 如果没有记录访客活动时间，默认为超时
                return true;
            }
            
            // 计算从最后活动到现在的时间
            long elapsedTime = System.currentTimeMillis() - lastActivityTime.getTime();
            
            // 判断是否超时
            boolean timedOut = elapsedTime >= idleTimeout;
            log.debug("人工客服接待超时检查: threadUid={}, 已经过时间={}ms, 超时阈值={}ms, 是否超时={}", 
                    execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID), 
                    elapsedTime, idleTimeout, timedOut);
            
            return timedOut;
        } catch (Exception e) {
            log.error("检查超时状态时出错: {}", e.getMessage(), e);
            // 默认返回true，确保流程能继续执行
            return true;
        }
    }
    
    /**
     * 标记会话为超时状态
     */
    private void markThreadAsTimeout(String threadUid) {
        try {
            // 查找会话相关的队列成员记录
            Optional<QueueMemberEntity> optionalQueueMember = queueMemberRestService.findByThreadUid(threadUid);
            
            if (optionalQueueMember.isPresent()) {
                QueueMemberEntity queueMember = optionalQueueMember.get();
                
                // 设置人工客服超时时间和超时标志
                queueMember.setAgentTimeoutAt(LocalDateTime.now());
                queueMember.setAgentTimeout(true);
                
                // 保存更新
                queueMemberRestService.save(queueMember);
                
                log.info("Marked human timeout for thread: {}, queue member: {}", 
                    threadUid, queueMember.getUid());
            } else {
                log.warn("Could not find queue member for thread: {}", threadUid);
            }
        } catch (Exception e) {
            log.error("Error marking thread as timeout", e);
        }
    }
    
    /**
     * 发送超时提醒消息
     */
    // private void sendTimeoutNotification(DelegateExecution execution) {
    //     // TODO: 实际项目中，这里应该向访客发送超时提醒消息
        
    //     String threadUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID);
    //     String userUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_USER_UID);
    //     String agentUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_AGENT_UID);
        
    //     log.info("Sending idle timeout notification to visitor: {} in thread: {} from agent: {}", 
    //         userUid, threadUid, agentUid);
        
    //     // 构建超时提醒消息
    //     String timeoutMessage = "由于您长时间未发送消息，本次会话已自动结束。如有需要，请重新发起会话。";
        
    //     // 记录发送的消息
    //     execution.setVariable("humanIdleTimeoutMessage", timeoutMessage);
    //     execution.setVariable("humanIdleTimeoutMessageSent", true);
    //     execution.setVariable("humanIdleTimeoutMessageSentTime", new Date());
    // }
    
    /**
     * 记录会话统计数据
     */
    // private void recordThreadStatistics(DelegateExecution execution) {
    //     // TODO: 实际项目中，这里应该记录会话统计数据
        
    //     String threadUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID);
    //     Date startTime = (Date) execution.getVariable("threadStartTime");
    //     Date endTime = new Date();
        
    //     log.info("Recording statistics for idle timeout thread: {}", threadUid);
        
    //     // 计算会话总时长
    //     long durationMillis = endTime.getTime() - startTime.getTime();
    //     long durationSeconds = durationMillis / 1000;
    //     execution.setVariable("threadDurationSeconds", durationSeconds);
        
    //     // 获取或模拟消息数量
    //     Integer messageCount = (Integer) execution.getVariable("messageCount");
    //     if (messageCount == null) {
    //         // 超时的会话通常消息较少
    //         messageCount = (int)(Math.random() * 10) + 2;
    //         execution.setVariable("messageCount", messageCount);
    //     }
        
    //     // 获取或模拟访客发送消息数量
    //     Integer visitorMessageCount = (int)(messageCount * 0.3); // 假设访客发送30%的消息
    //     if (visitorMessageCount < 1) visitorMessageCount = 1;
    //     execution.setVariable("visitorMessageCount", visitorMessageCount);
        
    //     // 获取或模拟坐席发送消息数量
    //     Integer agentMessageCount = messageCount - visitorMessageCount;
    //     execution.setVariable("agentMessageCount", agentMessageCount);
        
    //     // 是否使用了机器人
    //     Boolean robotUsed = (Boolean) execution.getVariable("robotUsed");
    //     execution.setVariable("robotUsed", robotUsed != null && robotUsed);
        
    //     // 记录超时终止
    //     execution.setVariable("terminatedByTimeout", true);
        
    //     log.info("Thread statistics recorded for idle timeout thread: {}, duration: {}s, messages: {}", 
    //         threadUid, durationSeconds, messageCount);
    // }
    
    /**
     * 清理资源
     */
    // private void cleanupResources(DelegateExecution execution) {
    //     // TODO: 实际项目中，这里应该清理会话相关资源
        
    //     String threadUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID);
    //     log.info("Cleaning up resources for idle timeout thread: {}", threadUid);
        
    //     // 标记会话已关闭
    //     execution.setVariable("threadActive", false);
    //     execution.setVariable("threadClosed", true);
    //     execution.setVariable("threadClosedTime", new Date());
    //     execution.setVariable("threadClosedReason", "VISITOR_IDLE_TIMEOUT");
        
    //     // 释放坐席资源
    //     String agentUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_AGENT_UID);
    //     if (agentUid != null && !agentUid.isEmpty()) {
    //         log.info("Releasing agent: {} from thread: {}", agentUid, threadUid);
    //         // 实际的坐席资源释放操作
    //     }
    // }

}
