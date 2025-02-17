package com.bytedesk.ticket.delegate;


import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ticketMessageDelegate")
public class TicketMessageDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String messageType = (String) execution.getVariable("messageType");
        String processInstanceId = execution.getProcessInstanceId();
        log.info("ticket message delegate - processInstanceId: {}", processInstanceId);
        
        switch (messageType) {
            case "ASSIGNED":
                sendAssignedNotification(execution);
                break;
            case "ESCALATED":
                sendEscalatedNotification(execution);
                break;
            case "RESOLVED":
                sendResolvedNotification(execution);
                break;
            default:
                log.warn("Unknown message type: {}", messageType);
        }
    }
    
    private void sendAssignedNotification(DelegateExecution execution) {
        // 发送工单分配通知
        String assignee = (String) execution.getVariable("assignee");
        log.info("Sending assigned notification to: {}", assignee);
    }
    
    private void sendEscalatedNotification(DelegateExecution execution) {
        // 发送工单升级通知
        String escalatedReason = (String) execution.getVariable("escalatedReason");
        log.info("Sending escalation notification: {}", escalatedReason);
    }
    
    private void sendResolvedNotification(DelegateExecution execution) {
        // 发送工单解决通知
        String solution = (String) execution.getVariable("solution");
        log.info("Sending resolution notification: {}", solution);
    }
} 