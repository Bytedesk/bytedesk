/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 13:33:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-06 11:01:16
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
        
        // 根据规则评估优先级
        String priority = ticket.getPriority();
        String slaTime = (String) execution.getVariable("slaTime");
        
        // 设置流程变量
        execution.setVariable("priority", priority);
        execution.setVariable("slaTime", slaTime);
        
        log.info("Priority evaluation completed: priority={}, slaTime={}", priority, slaTime);
    }
} 