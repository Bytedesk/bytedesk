/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 08:34:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 22:33:41
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

    // 添加执行计数跟踪
    private static final String EXECUTION_COUNT_KEY = "robotServiceExecutionCount";
    
    // 访客请求转人工的关键词标记
    private static final String MANUAL_TRANSFER_FLAG = "visitorRequestedTransfer";

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Robot service for thread process: {}", processInstanceId);
        
        // 获取流程变量
        String threadUid = (String) execution.getVariable("threadUid");
        String userUid = (String) execution.getVariable("userUid");
        String workgroupUid = (String) execution.getVariable("workgroupUid");
        log.info("Processing robot service for thread: {}, visitor: {}, workgroup: {}",
            threadUid, userUid, workgroupUid);
        
        // 跟踪执行次数，防止无限循环
        Integer executionCount = (Integer) execution.getVariable(EXECUTION_COUNT_KEY);
        if (executionCount == null) {
            executionCount = 1;
        } else {
            executionCount++;
        }
        execution.setVariable(EXECUTION_COUNT_KEY, executionCount);
        
        // 如果执行次数过多，强制设置为需要人工服务，打破循环
        if (executionCount > 3) {
            log.warn("检测到机器人服务多次执行，可能存在循环问题，强制设置为需要人工服务。threadUid: {}", threadUid);
            execution.setVariable("needHumanService", true);
            return;
        }
        
        // 记录机器人接待开始时间
        long startTime = System.currentTimeMillis();
        execution.setVariable("robotServiceStartTime", startTime);
        
        try {
            // TODO: 实际项目中，这里需要调用AI服务进行机器人回复
            // 1. 获取访客最近的消息
            // 2. 调用AI服务生成回复
            // 3. 发送机器人回复给访客
            
            log.info("Robot processing thread: {}, visitor: {}", threadUid, userUid);
            
            // 检查是否有访客手动请求转人工的标记
            // 此标记应当在消息处理层设置，例如当检测到访客发送"转人工"、"人工客服"等关键词时
            Boolean visitorRequestedTransfer = (Boolean) execution.getVariable(MANUAL_TRANSFER_FLAG);
            
            // 仅当访客明确请求转人工时才设置为需要人工服务
            boolean needHumanService = (visitorRequestedTransfer != null && visitorRequestedTransfer);
            execution.setVariable("needHumanService", needHumanService);
            
            log.info("Robot service completed for thread: {}, visitor requested transfer: {}, need human service: {}", 
                     threadUid, visitorRequestedTransfer, needHumanService);
            
            // 重置访客请求转人工的标记，避免下次循环时仍然转人工
            if (needHumanService) {
                execution.setVariable(MANUAL_TRANSFER_FLAG, false);
            }
        } catch (Exception e) {
            log.error("Error in robot service for thread: {}", threadUid, e);
            // 异常情况下转人工处理
            execution.setVariable("needHumanService", true);
            execution.setVariable("robotServiceError", e.getMessage());
        } finally {
            // 记录机器人接待结束时间和总时长
            long endTime = System.currentTimeMillis();
            execution.setVariable("robotServiceEndTime", endTime);
            execution.setVariable("robotServiceDuration", endTime - startTime);
        }
    }
}