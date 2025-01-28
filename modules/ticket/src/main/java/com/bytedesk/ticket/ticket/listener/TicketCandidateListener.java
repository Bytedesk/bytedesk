/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 10:21:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 10:56:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import org.flowable.task.service.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketCandidateListener implements TaskListener {
    
    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String taskId = delegateTask.getId();
        
        if (EVENTNAME_ASSIGNMENT.equals(eventName)) {
            String assignee = delegateTask.getAssignee();
            log.info("Task assigned to: {} for task {}", assignee, taskId);
        }
    }
} 