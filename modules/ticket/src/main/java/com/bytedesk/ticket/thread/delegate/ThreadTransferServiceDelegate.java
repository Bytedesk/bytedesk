/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 08:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 08:40:00
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
 * 客服会话转接服务
 * 
 * 处理客服会话中的转接功能
 * - 记录转接信息
 * - 查找合适的转接目标
 * - 处理转接过程
 */
@Slf4j
@Component("threadTransferServiceDelegate")
public class ThreadTransferServiceDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Transfer service for thread process: {}", processInstanceId);
        
        // 获取流程变量
        String threadUid = (String) execution.getVariable("threadUid");
        String userUid = (String) execution.getVariable("userUid");
        String currentAgentId = (String) execution.getVariable("agentUid");
        String transferReason = (String) execution.getVariable("transferReason");
        
        // 记录转接开始时间
        long startTime = System.currentTimeMillis();
        execution.setVariable("transferStartTime", startTime);
        
        try {
            // TODO: 实际项目中，这里需要实现转接的业务逻辑
            // 1. 根据转接原因确定转接目标
            // 2. 检查目标坐席可用性
            // 3. 执行转接操作
            
            log.info("Processing transfer for thread: {}, visitor: {}, from agent: {}", 
                threadUid, userUid, currentAgentId);
            
            // 记录转接原因(如果未提供)
            if (transferReason == null || transferReason.isEmpty()) {
                execution.setVariable("transferReason", "需要专业支持");
            }
            
            // 查找合适的转接目标
            String targetAgentId = findTransferTarget(execution);
            execution.setVariable("transferTargetAgent", targetAgentId);
            
            // 执行转接
            performTransfer(execution, currentAgentId, targetAgentId);
            
            log.info("Transfer completed for thread: {}, from agent: {} to agent: {}", 
                threadUid, currentAgentId, targetAgentId);
            
            // 更新当前坐席ID
            execution.setVariable("agentUid", targetAgentId);
            execution.setVariable("transferStatus", "COMPLETED");
        } catch (Exception e) {
            log.error("Error in transfer service", e);
            execution.setVariable("transferError", e.getMessage());
            execution.setVariable("transferStatus", "FAILED");
        } finally {
            // 记录转接结束时间和总时长
            long endTime = System.currentTimeMillis();
            execution.setVariable("transferEndTime", endTime);
            execution.setVariable("transferDuration", endTime - startTime);
            execution.setVariable("transferTime", new Date());
        }
    }
    
    /**
     * 查找合适的转接目标
     */
    private String findTransferTarget(DelegateExecution execution) {
        // TODO: 实际项目中，这里应该根据业务规则查找合适的坐席
        
        // 获取相关变量
        String workgroupUid = (String) execution.getVariable("workgroupUid");
        String currentAgentId = (String) execution.getVariable("agentUid");
        String transferReason = (String) execution.getVariable("transferReason");
        
        log.info("Finding transfer target in group: {}, current agent: {}, reason: {}", 
            workgroupUid, currentAgentId, transferReason);
        
        // 模拟找到一个目标坐席
        // 实际项目中，这里应该根据技能匹配、忙闲状态等进行筛选
        String targetAgentId = "agent" + (int)(Math.random() * 5 + 1);
        
        // 确保目标坐席不是当前坐席
        if (targetAgentId.equals(currentAgentId)) {
            targetAgentId = "agent" + (int)(Math.random() * 5 + 6);
        }
        
        log.info("Selected transfer target: {}", targetAgentId);
        return targetAgentId;
    }
    
    /**
     * 执行转接操作
     */
    private void performTransfer(DelegateExecution execution, String fromAgentId, String toAgentId) {
        // TODO: 实际项目中，这里应该执行实际的转接操作
        
        String threadUid = (String) execution.getVariable("threadUid");
        String userUid = (String) execution.getVariable("userUid");
        
        log.info("Transferring thread {} with visitor {} from agent {} to agent {}", 
            threadUid, userUid, fromAgentId, toAgentId);
        
        // 模拟转接过程
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 记录转接历史
        String transferHistory = (String) execution.getVariable("transferHistory");
        if (transferHistory == null) {
            transferHistory = "";
        }
        
        String newTransfer = fromAgentId + "->" + toAgentId + " at " + new Date();
        if (transferHistory.isEmpty()) {
            transferHistory = newTransfer;
        } else {
            transferHistory = transferHistory + "; " + newTransfer;
        }
        
        execution.setVariable("transferHistory", transferHistory);
    }
} 