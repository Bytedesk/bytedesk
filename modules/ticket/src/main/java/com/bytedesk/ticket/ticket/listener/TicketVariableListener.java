/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 10:20:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 10:55:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketVariableListener implements ExecutionListener {
    
    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();
        
        log.info("Variable event: {} in process: {}", eventName, processInstanceId);
    }
} 