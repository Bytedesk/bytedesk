/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 12:45:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-03 08:29:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.service;

import org.flowable.dmn.api.DmnDecisionService;
import org.flowable.dmn.engine.DmnEngine;
import org.springframework.stereotype.Service;

import com.bytedesk.ticket.ticket.TicketEntity;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class TicketDmnService {
    
    private final DmnEngine dmnEngine;
    // private final IdentityService identityService;
    
    public void evaluateTicketPriority(TicketEntity ticket) {
        // 设置当前租户
        // identityService.setTenantId(ticket.getTenantId());
        
        try {
            Map<String, Object> variables = new HashMap<>();
            // variables.put("tenantId", ticket.getTenantId());
            // variables.put("customerLevel", ticket.getCustomerLevel());
            // variables.put("issueType", ticket.getIssueType());
            // variables.put("impactLevel", ticket.getImpactLevel());
            
            DmnDecisionService decisionService = dmnEngine.getDmnDecisionService();
            Map<String, Object> result = decisionService.createExecuteDecisionBuilder()
                .decisionKey("ticketPriorityRules")
                // .tenantId(ticket.getTenantId())  // 指定租户
                .variables(variables)
                .executeDecision()
                .get(0);
            
            String priority = (String) result.get("priority");
            ticket.setPriority(priority);
            
        } finally {
            // 清除租户上下文
            // identityService.clearAuthentication();
        }
    }
    
    // public Map<String, Object> determineAssignment(String category, String priority) {
    //     Map<String, Object> variables = new HashMap<>();
    //     variables.put("category", category);
    //     variables.put("priority", priority);
        
    //     return dmnEngine.getDmnDecisionService()
    //         .createExecuteDecisionBuilder()
    //         .decisionKey("assignTicket")
    //         .variables(variables)
    //         .execute()
    //         .getFirstResult();
    // }
} 