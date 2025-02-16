/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 10:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 08:47:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 使用 ExecutionListener 处理流程执行和变量相关事件
 * 
 * ExecutionListener (执行监听器):
 * 监听流程执行事件
 * 事件类型：start、end、take
 * 可以绑定到流程、活动、连线上
 * 
 * 关注流程执行的生命周期
 * 可以访问和修改流程变量
 * 适合处理流程级别的业务逻辑
 */
@Slf4j
@Component("ticketExecutionListener")
public class TicketExecutionListener implements ExecutionListener {
    
    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        String activityName = execution.getCurrentActivityName();
        
        log.info("Execution event: {}, processInstanceId: {}, activityId: {}, activityName: {}", 
            eventName, processInstanceId, activityId, activityName);

        // 获取流程变量
        Map<String, Object> variables = execution.getVariables();
        log.info("Process variables: {}", variables);

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
        // 可以在这里添加流程开始时的业务逻辑
        String creatorUser = (String) execution.getVariable("creatorUser");
        log.info("Process started by user: {}", creatorUser);
    }

    private void handleProcessEnd(DelegateExecution execution) {
        // 可以在这里添加流程结束时的业务逻辑
        log.info("Process ended: {}", execution.getProcessInstanceId());
    }

    private void handleProcessTake(DelegateExecution execution) {
        // 可以在这里添加流程流转时的业务逻辑
        log.info("Process transition from {} to {}", 
            execution.getCurrentActivityId(), 
            execution.getCurrentActivityName());
    }
} 