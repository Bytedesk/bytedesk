/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 13:33:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-05 12:19:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.delegate;

import java.util.Optional;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ticketEvaluatePriorityDelegate")
public class TicketEvaluatePriorityDelegate implements JavaDelegate {

    @Autowired
    private TicketRestService ticketRestService;

    public TicketEvaluatePriorityDelegate() {
        // 默认构造函数
    }

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Evaluating ticket priority");
        
        // 获取工单相关变量
        String ticketUid = (String) execution.getVariable("ticketUid");

        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(ticketUid);
        if (!ticketOptional.isPresent()) {
            log.error("Ticket not found: {}", ticketUid);
            return;
        }
        TicketEntity ticket = ticketOptional.get(); 
        
        // String customerLevel = (String) execution.getVariable("customerLevel");
        // String issueType = (String) execution.getVariable("issueType");
        // String impactLevel = (String) execution.getVariable("impactLevel");
        
        // 根据规则评估优先级
        String priority = ticket.getPriority();
        // String slaTime = ticket.getSlaTime();
        
        // if ("VIP".equals(customerLevel) && "SYSTEM_ERROR".equals(issueType) && "HIGH".equals(impactLevel)) {
        //     priority = TicketPriorityEnum.URGENT.name();
        //     slaTime = "PT1H";
        // } else if ("VIP".equals(customerLevel) && "COMPLAINT".equals(issueType)) {
        //     priority = TicketPriorityEnum.HIGH.name();
        //     slaTime = "PT2H";
        // } else if ("REGULAR".equals(customerLevel) && "SYSTEM_ERROR".equals(issueType) && "HIGH".equals(impactLevel)) {
        //     priority = TicketPriorityEnum.HIGH.name();
        //     slaTime = "PT4H";
        // } else if ("FUNCTION".equals(issueType)) {
        //     priority = TicketPriorityEnum.MEDIUM.name();
        //     slaTime = "PT8H";
        // } else {
        //     priority = TicketPriorityEnum.LOW.name();
        //     slaTime = "P1D";
        // }

        // 根据优先级设置SLA时间
        // 根据不同优先级设置不同的SLA时间
        String slaTime = "P1D";
        switch (ticket.getPriority()) {
            case "CRITICAL":
                slaTime = "PT30M";     // 30分钟
                break;
            case "URGENT":
                slaTime = "PT1H";     // 1小时
                break;
            case "HIGH":
                slaTime = "PT2H";     // 2小时
                break;
            case "MEDIUM":
                slaTime = "PT4H";     // 4小时
                break;
            case "LOW":
                slaTime = "PT8H";     // 8小时
                break;
            case "LOWEST":
                slaTime = "P1D";      // 1天
                break;
            default:
                slaTime = "P1D";      // 1天
        }
        
        // 设置流程变量
        execution.setVariable("priority", priority);
        execution.setVariable("slaTime", slaTime);
        
        log.info("Priority evaluation completed: priority={}, slaTime={}", priority, slaTime);
    }
} 