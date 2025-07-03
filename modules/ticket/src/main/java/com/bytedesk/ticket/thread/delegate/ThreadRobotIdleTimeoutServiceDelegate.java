/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 10:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-06 21:51:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.delegate;

import java.time.ZonedDateTime;
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
 * 机器人接待访客超时服务
 * 
 * 处理访客在机器人接待过程中长时间未发送消息的情况
 * - 记录超时信息
 * - 发送超时提醒
 * - 结束会话
 */
@Slf4j
@Component("threadRobotIdleTimeoutServiceDelegate")
@RequiredArgsConstructor
public class ThreadRobotIdleTimeoutServiceDelegate implements JavaDelegate {
    
    private final QueueMemberRestService queueMemberRestService;

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Robot idle timeout service for thread process: {}", processInstanceId);
        
        // 获取流程变量
        String threadUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID);
        String userUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_USER_UID);
        log.info("Processing robot idle timeout for thread: {}, visitor: {}", 
            threadUid, userUid);
        
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
            Integer idleTimeout = (Integer) execution.getVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT);
            if (idleTimeout == null) {
                idleTimeout = ThreadConsts.DEFAULT_ROBOT_IDLE_TIMEOUT;
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
            log.debug("机器人接待超时检查: threadUid={}, 已经过时间={}ms, 超时阈值={}ms, 是否超时={}", 
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
                
                // 设置机器人超时时间和超时标志
                queueMember.setRobotTimeoutAt(ZonedDateTime.now());
                queueMember.setRobotTimeout(true);
                
                // 保存更新
                queueMemberRestService.save(queueMember);
                
                log.info("Marked robot timeout for thread: {}, queue member: {}", 
                    threadUid, queueMember.getUid());
            } else {
                log.warn("Could not find queue member for thread: {}", threadUid);
            }
        } catch (Exception e) {
            log.error("Error marking thread as timeout", e);
        }
    }
}
