/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 09:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 09:20:00
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

import java.util.Date;

/**
 * 人工客服服务
 * 
 * 处理客服会话中的人工服务阶段
 * - 记录服务开始和结束时间
 * - 监控服务质量和SLA
 * - 处理人工服务过程中的事件
 */
@Slf4j
@Component("threadHumanServiceDelegate")
public class ThreadHumanServiceDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Human service for thread process: {}", processInstanceId);
        
        // 获取流程变量
        String threadUid = (String) execution.getVariable("threadUid");
        String visitorId = (String) execution.getVariable("visitorId");
        String agentId = (String) execution.getVariable("agentId");
        
        // 记录人工服务开始时间
        long startTime = System.currentTimeMillis();
        execution.setVariable("humanServiceStartTime", startTime);
        execution.setVariable("humanServiceStartDate", new Date());
        
        try {
            log.info("Starting human service for thread: {}, visitor: {}, agent: {}", 
                threadUid, visitorId, agentId);
            
            // 检查坐席是否有效
            if (agentId == null || agentId.isEmpty()) {
                throw new RuntimeException("No agent assigned for human service");
            }
            
            // 记录服务开始
            recordServiceStart(execution);
            
            // 实际项目中，这里不会阻塞执行，而是由事件驱动
            // 这里只是模拟一个人工服务过程
            simulateHumanServiceProcess(execution);
            
            // 检查服务质量
            checkServiceQuality(execution);
            
            execution.setVariable("humanServiceStatus", "COMPLETED");
            log.info("Human service completed for thread: {}", threadUid);
        } catch (Exception e) {
            log.error("Error in human service", e);
            execution.setVariable("humanServiceError", e.getMessage());
            execution.setVariable("humanServiceStatus", "FAILED");
        } finally {
            // 记录人工服务结束时间和总时长
            long endTime = System.currentTimeMillis();
            execution.setVariable("humanServiceEndTime", endTime);
            execution.setVariable("humanServiceDuration", endTime - startTime);
            execution.setVariable("humanServiceEndDate", new Date());
            
            // 记录服务结束
            recordServiceEnd(execution);
        }
    }
    
    /**
     * 记录服务开始
     */
    private void recordServiceStart(DelegateExecution execution) {
        // TODO: 实际项目中，这里应该实现记录服务开始的逻辑
        
        String threadUid = (String) execution.getVariable("threadUid");
        String agentId = (String) execution.getVariable("agentId");
        Date startDate = (Date) execution.getVariable("humanServiceStartDate");
        
        log.info("Recording service start for thread: {}, agent: {}, time: {}", 
            threadUid, agentId, startDate);
        
        // 初始化SLA计时
        initializeSLATimer(execution);
        
        // 设置首次响应时间限制（秒）
        execution.setVariable("firstResponseTimeLimit", 60);
        
        // 设置平均响应时间标准（秒）
        execution.setVariable("avgResponseTimeStandard", 180);
    }
    
    /**
     * 记录服务结束
     */
    private void recordServiceEnd(DelegateExecution execution) {
        // TODO: 实际项目中，这里应该实现记录服务结束的逻辑
        
        String threadUid = (String) execution.getVariable("threadUid");
        String agentId = (String) execution.getVariable("agentId");
        Date endDate = (Date) execution.getVariable("humanServiceEndDate");
        
        log.info("Recording service end for thread: {}, agent: {}, time: {}", 
            threadUid, agentId, endDate);
        
        // 计算服务时长
        Date startDate = (Date) execution.getVariable("humanServiceStartDate");
        long serviceDurationSeconds = (endDate.getTime() - startDate.getTime()) / 1000;
        execution.setVariable("humanServiceDurationSeconds", serviceDurationSeconds);
        
        // 记录会话数据
        int messageCount = (int)(Math.random() * 20) + 5; // 模拟消息数量
        execution.setVariable("messageCount", messageCount);
        
        // 检查是否违反了SLA
        Boolean slaViolated = (Boolean) execution.getVariable("slaViolated");
        if (slaViolated != null && slaViolated) {
            log.warn("SLA was violated during service for thread: {}", threadUid);
        }
    }
    
    /**
     * 初始化SLA计时器
     */
    private void initializeSLATimer(DelegateExecution execution) {
        // TODO: 实际项目中，这里应该实现初始化SLA计时器的逻辑
        
        // 设置SLA时间（秒）
        int slaTime = 1800; // 30分钟
        execution.setVariable("slaTime", slaTime);
        
        // 初始化SLA状态
        execution.setVariable("slaViolated", false);
        
        log.info("Initialized SLA timer for thread: {}, SLA time: {} seconds", 
            execution.getVariable("threadUid"), slaTime);
    }
    
    /**
     * 模拟人工服务过程
     */
    private void simulateHumanServiceProcess(DelegateExecution execution) {
        // 模拟服务过程
        try {
            // 随机延迟，模拟服务时间
            Thread.sleep(1000 + (int)(Math.random() * 1000));
            
            // 记录首次响应时间（秒）
            int firstResponseTime = 10 + (int)(Math.random() * 60);
            execution.setVariable("firstResponseTime", firstResponseTime);
            
            // 记录平均响应时间（秒）
            int avgResponseTime = 60 + (int)(Math.random() * 180);
            execution.setVariable("avgResponseTime", avgResponseTime);
            
            // 随机决定是否有SLA违规
            boolean slaViolated = Math.random() < 0.2; // 20%的概率违规
            execution.setVariable("slaViolated", slaViolated);
            
            if (slaViolated) {
                String slaViolationReason = "响应时间超过SLA限制";
                execution.setVariable("slaViolationReason", slaViolationReason);
                execution.setVariable("slaViolationTime", new Date());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 检查服务质量
     */
    private void checkServiceQuality(DelegateExecution execution) {
        // TODO: 实际项目中，这里应该实现检查服务质量的逻辑
        
        // 获取相关数据
        int firstResponseTime = (int) execution.getVariable("firstResponseTime");
        int firstResponseTimeLimit = (int) execution.getVariable("firstResponseTimeLimit");
        
        int avgResponseTime = (int) execution.getVariable("avgResponseTime");
        int avgResponseTimeStandard = (int) execution.getVariable("avgResponseTimeStandard");
        
        // 计算服务质量分数
        int serviceQualityScore = 0;
        
        // 首次响应时间评分
        if (firstResponseTime <= firstResponseTimeLimit / 2) {
            serviceQualityScore += 40; // 极快响应
        } else if (firstResponseTime <= firstResponseTimeLimit) {
            serviceQualityScore += 30; // 达标响应
        } else if (firstResponseTime <= firstResponseTimeLimit * 1.5) {
            serviceQualityScore += 20; // 稍慢响应
        } else {
            serviceQualityScore += 10; // 慢响应
        }
        
        // 平均响应时间评分
        if (avgResponseTime <= avgResponseTimeStandard / 2) {
            serviceQualityScore += 40; // 极快响应
        } else if (avgResponseTime <= avgResponseTimeStandard) {
            serviceQualityScore += 30; // 达标响应
        } else if (avgResponseTime <= avgResponseTimeStandard * 1.5) {
            serviceQualityScore += 20; // 稍慢响应
        } else {
            serviceQualityScore += 10; // 慢响应
        }
        
        // SLA违规扣分
        Boolean slaViolated = (Boolean) execution.getVariable("slaViolated");
        if (slaViolated != null && slaViolated) {
            serviceQualityScore -= 30; // SLA违规严重扣分
        }
        
        // 确保分数在0-100之间
        serviceQualityScore = Math.max(0, Math.min(100, serviceQualityScore));
        
        // 记录服务质量分数
        execution.setVariable("serviceQualityScore", serviceQualityScore);
        
        // 设置服务质量级别
        String serviceQualityLevel;
        if (serviceQualityScore >= 90) {
            serviceQualityLevel = "EXCELLENT";
        } else if (serviceQualityScore >= 70) {
            serviceQualityLevel = "GOOD";
        } else if (serviceQualityScore >= 50) {
            serviceQualityLevel = "AVERAGE";
        } else if (serviceQualityScore >= 30) {
            serviceQualityLevel = "POOR";
        } else {
            serviceQualityLevel = "VERY_POOR";
        }
        
        execution.setVariable("serviceQualityLevel", serviceQualityLevel);
        
        log.info("Service quality check for thread: {}, score: {}, level: {}", 
            execution.getVariable("threadUid"), serviceQualityScore, serviceQualityLevel);
    }
} 