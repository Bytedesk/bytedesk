/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 12:45:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 11:21:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.service;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.flowable.dmn.api.DmnRuleService;

import java.util.HashMap;
import java.util.Map;

@Service
public class TicketDmnService {
    
    // @Autowired
    // private DmnRuleService dmnRuleService;
    
    public Map<String, Object> determineAssignment(String category, String priority) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("category", category);
        variables.put("priority", priority);
        
        // return dmnRuleService.createExecuteDecisionBuilder()
        //     .decisionKey("assignTicket")
        //     .variables(variables)
        //     .execute()
        //     .getFirstResult();

        return null;
    }
} 