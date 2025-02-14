/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:52:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 11:33:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadTypeEnum;
import com.bytedesk.kbase.upload.event.UploadCreateEvent;
import com.bytedesk.ticket.consts.TicketConsts;
import com.bytedesk.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.event.TicketUpdateEvent;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRestService;
import com.bytedesk.ticket.ticket.TicketTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventListener {
    
    private final RuntimeService runtimeService;

    private final TicketRestService ticketRestService;

    @EventListener
    public void handleTicketCreateEvent(TicketCreateEvent event) {
        log.info("TicketEventListener handleTicketCreateEvent: {}", event);
        TicketEntity ticket = event.getTicket();

        // 启动工单流程
        Map<String, Object> variables = new HashMap<>();
        variables.put("ticketUid", ticket.getUid());
        variables.put("orgUid", ticket.getOrgUid());
        variables.put("userUid", JSON.parseObject(ticket.getReporter(), UserProtobuf.class).getUid());
        String processKey = null;
        if (ticket.getType().equals(TicketTypeEnum.AGENT.name())) {
            processKey = TicketConsts.TICKET_PROCESS_KEY_AGENT;
            variables.put("agentUid", JSON.parseObject(ticket.getAssignee(), UserProtobuf.class).getUid());
        } else {
            processKey = TicketConsts.TICKET_PROCESS_KEY_GROUP;
            variables.put("workgroupUid", JSON.parseObject(ticket.getWorkgroup(), UserProtobuf.class).getUid());
        }
        // 根据不同优先级设置不同的SLA时间
        switch (ticket.getPriority()) {
            case "CRITICAL":
                variables.put("slaTime", "PT30M");     // 30分钟
                break;
            case "URGENT":
                variables.put("slaTime", "PT1H");     // 1小时
                break;
            case "HIGH":
                variables.put("slaTime", "PT2H");     // 2小时
                break;
            case "MEDIUM":
                variables.put("slaTime", "PT4H");     // 4小时
                break;
            case "LOW":
                variables.put("slaTime", "PT8H");     // 8小时
                break;
            case "LOWEST":
                variables.put("slaTime", "P1D");      // 1天
                break;
            default:
                variables.put("slaTime", "P1D");      // 1天
        }

        // 启动流程时指定租户   
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
            .processDefinitionKey(processKey)
            // .tenantId(ticket.getOrgUid())
            .variables(variables)
            .start();
        log.info("TicketEventListener processInstance: {}", processInstance.getId());

        // 设置工单的流程实例ID
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(ticket.getUid());
        if (ticketOptional.isPresent()) {
            TicketEntity ticketEntity = ticketOptional.get();
            ticketEntity.setProcessInstanceId(processInstance.getId());
            ticketRestService.save(ticketEntity);
        }
    }

    @EventListener
    public void handleTicketUpdateEvent(TicketUpdateEvent event) {
        log.info("TicketEventListener handleTicketUpdateEvent: {}", event);
    }

    // 监听上传BPMN流程图
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        log.info("TicketEventListener upload bpmn create: {}", upload.toString());
        // 上传BPMN流程图
        if (upload.getType().equals(UploadTypeEnum.BPMN.name())) {
            // 启动流程
            // ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(upload.getFileName());
        }
    }
    
}

