/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 13:06:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 21:49:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import org.flowable.cmmn.api.listener.CaseInstanceLifecycleListener;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.TicketRestService;
import com.bytedesk.ticket.ticket.TicketSLAService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketCaseListener implements CaseInstanceLifecycleListener {
    
    @Autowired
    private TicketRestService ticketService;
    
    @Autowired
    private TicketSLAService slaService;
    
    @Autowired
    private TicketNotificationService notificationService;
    
    @Override
    public void stateChanged(CaseInstance caseInstance, String oldState, String newState) {
        String caseInstanceId = caseInstance.getId();
        log.info("工单状态变更 - ID: {}, 旧状态: {}, 新状态: {}", caseInstanceId, oldState, newState);
        
        // 根据不同状态处理
        switch (newState) {
            case "active":
                handleCaseActivated(caseInstance);
                break;
            case "completed":
                handleCaseCompleted(caseInstance);
                break;
            case "terminated":
                handleCaseTerminated(caseInstance);
                break;
            default:
                break;
        }
    }
    
    private void handleCaseActivated(CaseInstance caseInstance) {
        // 检查是否是技术分析阶段
        if ("technicalAnalysis".equals(caseInstance.getCaseDefinitionName())) {
            notificationService.notifyTechnicalTeam(
                caseInstance.getId(),
                "新的技术分析任务已分配"
            );
        }
    }
    
    private void handleCaseCompleted(CaseInstance caseInstance) {
        // 记录完成时间并检查 SLA
        ticketService.findByUid(caseInstance.getId()).ifPresent(ticket -> {
            if (slaService.isSLABreached(ticket)) {
                notificationService.sendSLABreachNotification(
                    ticket.getId().toString(),
                    "COMPLETION",
                    "工单完成时已超出 SLA 时限"
                );
            }
        });
    }
    
    private void handleCaseTerminated(CaseInstance caseInstance) {
        // 通知相关人员工单被终止
        ticketService.findByUid(caseInstance.getId()).ifPresent(ticket -> {
            String assigneeUid = ticket.getAssignee().getUid();
            // 
            notificationService.notifyManager(
                assigneeUid,
                String.format("工单 #%d 已被终止", ticket.getId())
            );
        });
    }

    @Override
    public String getSourceState() {
        return null; // 返回 null 表示监听所有状态变更
    }

    @Override
    public String getTargetState() {
        return null; // 返回 null 表示监听所有状态变更
    }
} 