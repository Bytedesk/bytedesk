/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:52:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 10:24:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.FlowableEventListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.consts.TicketConsts;
import com.bytedesk.ticket.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventListener implements FlowableEventListener {

    private final RuntimeService runtimeService;

    @Override
    public void onEvent(FlowableEvent event) {
        String eventType = event.getType().name();
        log.info("Flowable event received: {}", eventType);

        if (event.getType() == FlowableEngineEventType.PROCESS_STARTED) {
            handleProcessStarted(event);
        } else if (event.getType() == FlowableEngineEventType.PROCESS_COMPLETED) {
            handleProcessCompleted(event);
        } else if (event.getType() == FlowableEngineEventType.TASK_CREATED) {
            handleTaskCreated(event);
        }
        // 可以添加更多事件类型的处理
    }

    private void handleProcessStarted(FlowableEvent event) {
        log.info("Process started event: {}", event);
        // TODO: 处理流程启动事件
    }

    private void handleProcessCompleted(FlowableEvent event) {
        log.info("Process completed event: {}", event);
        // TODO: 处理流程完成事件
    }

    private void handleTaskCreated(FlowableEvent event) {
        log.info("Task created event: {}", event);
        // TODO: 处理任务创建事件
    }

    @Override
    public boolean isFailOnException() {
        // 发生异常时是否终止流程
        return false;
    }

    @Override
    public boolean isFireOnTransactionEnabled() {
        // 是否在事务中触发
        return false;
    }

    @EventListener
    public void handleTicketCreateEvent(TicketCreateEvent event) {
        log.info("TicketEventListener handleTicketCreateEvent: {}", event);
        TicketEntity ticket = event.getTicket();

        // 启动工单流程
        Map<String, Object> variables = new HashMap<>();
        variables.put("ticket", ticket);
        variables.put("reporter", ticket.getReporter());
        
        // 
        runtimeService.startProcessInstanceByKey(TicketConsts.TICKET_PROCESS_KEY, 
            ticket.getId().toString(), 
            variables);
    }

    @EventListener
    public void handleTicketUpdateEvent(TicketUpdateEvent event) {
        log.info("TicketEventListener handleTicketUpdateEvent: {}", event);
    }
}

