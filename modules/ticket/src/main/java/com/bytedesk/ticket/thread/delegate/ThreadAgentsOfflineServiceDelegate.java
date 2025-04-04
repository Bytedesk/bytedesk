/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 15:24:15
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

// import java.util.Date;

/**
 * 坐席离线处理服务
 * 
 * 处理所有坐席离线的情况
 * - 发送离线通知给访客
 * - 提供替代联系方式
 * - 结束会话
 */
@Slf4j
@Component("threadAgentsOfflineServiceDelegate")
public class ThreadAgentsOfflineServiceDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Agents offline service for thread process: {}", processInstanceId);
        
        // 获取流程变量
        // String threadUid = (String) execution.getVariable("threadUid");
        // String userUid = (String) execution.getVariable("userUid");
        // String workgroupUid = (String) execution.getVariable("workgroupUid");
        
        // // 记录处理开始时间
        // long startTime = System.currentTimeMillis();
        // execution.setVariable("agentsOfflineStartTime", startTime);
        
        // try {
        //     // 记录坐席离线事件
        //     execution.setVariable("agentsOfflineTime", new Date());
        //     execution.setVariable("agentsOfflineStatus", "NO_AGENTS_AVAILABLE");
        //     execution.setVariable("threadEndReason", "AGENTS_OFFLINE");
            
        //     log.info("All agents offline for thread: {}, visitor: {}, workgroup: {}", 
        //         threadUid, userUid, workgroupUid);
            
        //     // 发送离线通知给访客
        //     sendOfflineNotification(execution);
            
        //     // 提供替代联系方式
        //     provideAlternativeContactMethods(execution);
            
        //     // 记录会话结束
        //     recordThreadEnd(execution);
            
        //     execution.setVariable("threadStatus", "CLOSED_NO_AGENTS");
        //     log.info("Agents offline processing completed for thread: {}", threadUid);
        // } catch (Exception e) {
        //     log.error("Error in agents offline service", e);
        //     execution.setVariable("agentsOfflineError", e.getMessage());
        // } finally {
        //     // 记录处理结束时间和总时长
        //     long endTime = System.currentTimeMillis();
        //     execution.setVariable("agentsOfflineEndTime", endTime);
        //     execution.setVariable("agentsOfflineDuration", endTime - startTime);
        //     execution.setVariable("threadEndTime", new Date());
        // }
    }
    
    /**
     * 发送离线通知给访客
     */
    // private void sendOfflineNotification(DelegateExecution execution) {
    //     // TODO: 实际项目中，这里应该向访客发送离线通知消息
        
    //     String threadUid = (String) execution.getVariable("threadUid");
    //     String userUid = (String) execution.getVariable("userUid");
        
    //     log.info("Sending offline notification to visitor: {} in thread: {}", userUid, threadUid);
        
    //     // 构建离线通知消息
    //     String offlineMessage = "抱歉，当前所有客服人员都不在线，无法为您提供即时服务。";
        
    //     // 记录发送的消息
    //     execution.setVariable("agentsOfflineMessage", offlineMessage);
    //     execution.setVariable("agentsOfflineMessageSent", true);
    //     execution.setVariable("agentsOfflineMessageSentTime", new Date());
    // }
    
    /**
     * 提供替代联系方式
     */
    // private void provideAlternativeContactMethods(DelegateExecution execution) {
    //     // TODO: 实际项目中，这里应该提供替代联系方式
        
    //     String threadUid = (String) execution.getVariable("threadUid");
    //     log.info("Providing alternative contact methods for thread: {}", threadUid);
        
    //     // 构建替代联系方式消息
    //     StringBuilder contactMethodsBuilder = new StringBuilder();
    //     contactMethodsBuilder.append("您可以通过以下方式联系我们：\n");
    //     contactMethodsBuilder.append("1. 电话：400-800-1234\n");
    //     contactMethodsBuilder.append("2. 邮箱：support@bytedesk.com\n");
    //     contactMethodsBuilder.append("3. 工作时间：周一至周五 9:00-18:00\n");
    //     contactMethodsBuilder.append("4. 留言：您可以留下消息，我们会在工作时间尽快回复\n");
        
    //     String contactMethods = contactMethodsBuilder.toString();
        
    //     // 记录替代联系方式
    //     execution.setVariable("alternativeContactMethods", contactMethods);
    //     execution.setVariable("alternativeContactMethodsProvided", true);
    //     execution.setVariable("alternativeContactMethodsProvidedTime", new Date());
        
    //     // 发送替代联系方式消息
    //     log.info("Sending alternative contact methods to visitor in thread: {}", threadUid);
    // }
    
    /**
     * 记录会话结束
     */
    // private void recordThreadEnd(DelegateExecution execution) {
    //     // TODO: 实际项目中，这里应该记录会话结束
        
    //     String threadUid = (String) execution.getVariable("threadUid");
    //     log.info("Recording thread end for thread: {}", threadUid);
        
    //     // 标记会话已关闭
    //     safeSetVariable(execution, "threadActive", false);
    //     safeSetVariable(execution, "threadClosed", true);
    //     safeSetVariable(execution, "threadClosedTime", new Date());
    //     safeSetVariable(execution, "threadClosedReason", "AGENTS_OFFLINE");
        
    //     // 记录简单的会话统计
    //     Object startTimeObj = execution.getVariable("threadStartTime");
    //     Date startTime = null;
        
    //     // 正确处理类型转换
    //     if (startTimeObj instanceof Date) {
    //         startTime = (Date) startTimeObj;
    //     } else if (startTimeObj instanceof Long) {
    //         startTime = new Date((Long) startTimeObj);
    //     } else if (startTimeObj == null) {
    //         startTime = new Date(System.currentTimeMillis() - 60000); // 假设会话至少持续了1分钟
    //         safeSetVariable(execution, "threadStartTime", startTime);
    //     } else {
    //         log.warn("Unexpected threadStartTime type: {}", startTimeObj.getClass().getName());
    //         startTime = new Date(System.currentTimeMillis() - 60000);
    //         safeSetVariable(execution, "threadStartTime", startTime);
    //     }
        
    //     Date endTime = new Date();
    //     long durationMillis = endTime.getTime() - startTime.getTime();
    //     long durationSeconds = durationMillis / 1000;
        
    //     safeSetVariable(execution, "threadDurationSeconds", durationSeconds);
    //     safeSetVariable(execution, "messageCount", 1); // 至少有一条初始消息
    // }
    
    /**
     * 安全地设置流程变量，避免流程已结束时引发外键约束异常
     */
    // private void safeSetVariable(DelegateExecution execution, String variableName, Object variableValue) {
    //     try {
    //         // 检查流程实例是否仍然活跃
    //         String executionId = execution.getId();
    //         if (executionId != null) {
    //             // 获取当前执行实例的状态，如果出错表示执行实例可能已不存在
    //             execution.getCurrentActivityId();
    //             execution.setVariable(variableName, variableValue);
    //             log.debug("Successfully set variable {} for execution {}", variableName, executionId);
    //         } else {
    //             log.debug("Cannot set variable {}: execution ID is null", variableName);
    //         }
    //     } catch (Exception e) {
    //         // 捕获设置变量时可能出现的异常，包括外键约束异常
    //         log.debug("Failed to set variable {}: {}", variableName, e.getMessage());
    //     }
    // }
}
