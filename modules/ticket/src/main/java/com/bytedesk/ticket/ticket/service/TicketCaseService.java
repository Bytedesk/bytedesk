/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 12:45:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 13:48:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.task.api.Task;

@Service
public class TicketCaseService {
    
    @Autowired
    private CmmnRuntimeService cmmnRuntimeService;
    
    @Autowired
    private CmmnTaskService cmmnTaskService;
    
    public CaseInstance startComplexTicket(Map<String, Object> variables) {
        return cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("complexTicket")
            .variables(variables)
            .start();
    }
    
    public List<Task> getActiveTasks(String caseInstanceId) {
        return cmmnTaskService.createTaskQuery()
            .caseInstanceId(caseInstanceId)
            .active()
            .list();
    }
} 