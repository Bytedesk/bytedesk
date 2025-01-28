/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 10:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 11:18:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// 使用 ExecutionListener 处理流程执行和变量相关事件
@Slf4j
@Component
public class TicketExecutionListener implements ExecutionListener {
    
    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();
        
        log.info("Execution event: {}, processInstanceId: {}", eventName, processInstanceId);

        switch (eventName) {
            case EVENTNAME_START:
                handleProcessStart(execution);
                break;
            case EVENTNAME_END:
                handleProcessEnd(execution);
                break;
            case EVENTNAME_TAKE:
                handleProcessTake(execution);
                break;
            default:
                log.warn("Unhandled execution event: {}", eventName);
                break;
        }
    }

    private void handleProcessStart(DelegateExecution execution) {
        log.info("Process started: {}", execution.getProcessInstanceId());
        // TODO: 流程开始时的处理逻辑
    }

    private void handleProcessEnd(DelegateExecution execution) {
        log.info("Process ended: {}", execution.getProcessInstanceId());
        // TODO: 流程结束时的处理逻辑
    }

    private void handleProcessTake(DelegateExecution execution) {
        log.info("Process transition taken: {} -> {}", 
            execution.getCurrentActivityId(), 
            execution.getCurrentActivityName());
        // TODO: 流程流转时的处理逻辑
    }
} 