/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 08:34:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 22:58:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.thread.ThreadConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 机器人接待服务
 * 
 * 处理客服会话中的机器人自动应答环节
 * - 接收访客消息
 * - 调用AI模块进行回复
 * - 判断是否需要转人工服务
 * - 仅当访客手动触发时才转人工
 */
@Slf4j
@Component("threadRobotServiceDelegate")
public class ThreadRobotServiceDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Robot service for thread process: {}", processInstanceId);
        
        // 获取流程变量
        String threadUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID);
        String userUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_USER_UID);
        String workgroupUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID);
        log.info("Processing robot service for thread: {}, visitor: {}, workgroup: {}",
            threadUid, userUid, workgroupUid);
        
        // 跟踪执行次数，防止无限循环
        Integer executionCount = (Integer) execution.getVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_EXECUTION_COUNT);
        if (executionCount == null) {
            executionCount = 1;
        } else {
            executionCount++;
        }
        execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_EXECUTION_COUNT, executionCount);
        
        // 如果执行次数过多，强制设置为需要人工服务，打破循环
        if (executionCount > ThreadConsts.THREAD_MAX_ROBOT_EXECUTION_COUNT) {
            log.warn("检测到机器人服务多次执行，可能存在循环问题，强制设置为需要人工服务。threadUid: {}", threadUid);
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, true);
            return;
        }
        
        // 记录机器人接待开始时间
        long startTime = System.currentTimeMillis();
        execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_START_TIME, startTime);
        
        try {
            // TODO: 实际项目中，这里需要调用AI服务进行机器人回复
            // 1. 获取访客最近的消息
            // 2. 调用AI服务生成回复
            // 3. 发送机器人回复给访客
            
            log.info("Robot processing thread: {}, visitor: {}", threadUid, userUid);
            
            // ===== visitorRequestedTransfer 使用实例 BEGIN =====
            
            // 检查是否有访客手动请求转人工的标记
            Boolean visitorRequestedTransfer = (Boolean) execution.getVariable(
                ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER);
            
            // 仅当访客明确请求转人工时才设置为需要人工服务
            boolean needHumanService = (visitorRequestedTransfer != null && visitorRequestedTransfer);
            
            // 如果检测到访客请求转人工，则记录日志
            if (needHumanService) {
                log.info("检测到访客 {} 请求转人工，正在处理转接流程...", userUid);
                
                // 在这里可以添加转人工前的处理逻辑
                // 例如：发送提示消息给访客，告知正在转接人工客服
                // sendSystemMessage(threadUid, "正在为您转接人工客服，请稍候...");
                
                // 可以添加一些额外信息，帮助客服了解访客之前的问题
                execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_SUMMARY, "访客主动请求转人工服务");
                execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_REASON, "访客请求");
                
                // 设置转人工的优先级，访客主动请求可能需要更高优先级
                execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_PRIORITY, 2); // 高优先级
            }
            
            // 设置是否需要人工服务的流程变量
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, needHumanService);
            
            // 重置访客请求转人工的标记，避免下次循环时仍然转人工
            if (needHumanService) {
                execution.setVariable(ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, false);
            }
            // ===== visitorRequestedTransfer 使用实例 END =====
            
            log.info("Robot service completed for thread: {}, visitor requested transfer: {}, need human service: {}", 
                     threadUid, visitorRequestedTransfer, needHumanService);
            
        } catch (Exception e) {
            log.error("Error in robot service for thread: {}", threadUid, e);
            // 异常情况下转人工处理
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, true);
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_ERROR, e.getMessage());
        } finally {
            // 记录机器人接待结束时间和总时长
            long endTime = System.currentTimeMillis();
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_END_TIME, endTime);
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_DURATION, endTime - startTime);
        }
    }
}