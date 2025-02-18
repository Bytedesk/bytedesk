/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:52:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 15:22:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadTypeEnum;
import com.bytedesk.kbase.upload.event.UploadCreateEvent;
import com.bytedesk.ticket.consts.TicketConsts;
import com.bytedesk.ticket.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateWorkgroupEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventListener {
    
    private final RuntimeService runtimeService;

    private final TicketRestService ticketRestService;

    private final TaskService taskService;

    @EventListener
    public void handleTicketCreateEvent(TicketCreateEvent event) {
        log.info("TicketEventListener handleTicketCreateEvent: {}", event);
        TicketEntity ticket = event.getTicket();

        // 1. 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        // 基本变量
        variables.put(TicketConsts.TICKET_VARIABLE_TICKET_UID, ticket.getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_WORKGROUP_UID, JSON.parseObject(ticket.getWorkgroup(), UserProtobuf.class).getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_REPORTER_UID, JSON.parseObject(ticket.getReporter(), UserProtobuf.class).getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_ORGUID, ticket.getOrgUid());
        // 
        variables.put(TicketConsts.TICKET_VARIABLE_DESCRIPTION, ticket.getDescription());
        variables.put(TicketConsts.TICKET_VARIABLE_START_USER_ID, JSON.parseObject(ticket.getReporter(), UserProtobuf.class).getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
        variables.put(TicketConsts.TICKET_VARIABLE_PRIORITY, ticket.getPriority());
        variables.put(TicketConsts.TICKET_VARIABLE_CATEGORY_UID, ticket.getCategoryUid());

        // 2. 启动流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
            .tenantId(ticket.getOrgUid())
            .name(ticket.getTitle())                // 流程实例名称
            .businessKey(ticket.getUid())          // 业务键
            .variables(variables)
            .start();

        // 3. 设置流程实例变量
        // 可以在流程执行的任何时候调用, 每次调用都会产生一次变量更新历史记录
        // 适合设置运行时的动态变量或需要更新的变量
        // 每次调用都会有一次数据库操作
        runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_START_TIME, new Date());

        // 4. 设置 SLA 时间
        String slaTime = switch (ticket.getPriority()) {
            case "CRITICAL" -> "PT30M";
            case "URGENT" -> "PT1H";
            case "HIGH" -> "PT2H";
            case "MEDIUM" -> "PT4H";
            case "LOW" -> "PT8H";
            default -> "P1D";
        };
        runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_SLA_TIME, slaTime);
        // 第一步的assignee设置为reporter
        runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getReporter());
        // 5. 更新工单的流程实例ID
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(ticket.getUid());
        if (ticketOptional.isPresent()) {
            TicketEntity ticketEntity = ticketOptional.get();
            ticketEntity.setProcessInstanceId(processInstance.getId());
            // 
            if (StringUtils.hasText(ticketEntity.getAssignee())) {
                Task task = taskService.createTaskQuery()
                    .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                    .taskDefinitionKey(TicketConsts.TICKET_TASK_DEFINITION_ASSIGN_TO_GROUP)
                    .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, ticketEntity.getUid())
                    .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, ticketEntity.getOrgUid())
                    .singleResult();
                if (task != null) {
                    taskService.claim(task.getId(), JSON.parseObject(ticketEntity.getAssignee(), UserProtobuf.class).getUid());
                }
            }
            ticketRestService.save(ticketEntity);
        }
    }

    // 监听工单更新事件
    @EventListener
    public void handleTicketUpdateEvent(TicketUpdateEvent event) {
        log.info("TicketEventListener handleTicketUpdateEvent: {}", event);
        TicketEntity ticket = event.getTicket();
        // 判断是否删除
        if (ticket.isDeleted()) {
            // 同步更新流程实例
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .singleResult();
            if (processInstance != null) {
                runtimeService.deleteProcessInstance(processInstance.getId(), "deleted by user");
            }
        }
    }

    @EventListener
    public void handleTicketUpdateAssigneeEvent(TicketUpdateAssigneeEvent event) {
        log.info("TicketEventListener handleTicketUpdateAssigneeEvent: {}", event);
        TicketEntity ticket = event.getTicket();
        // 撤回原assignee的claim
        taskService.unclaim(ticket.getProcessInstanceId());
        // 重新claim新的assignee
        taskService.claim(ticket.getProcessInstanceId(), event.getNewAssigneeUid());
    }

    @EventListener
    public void handleTicketUpdateWorkgroupEvent(TicketUpdateWorkgroupEvent event) {
        log.info("TicketEventListener handleTicketUpdateWorkgroupEvent: {}", event);
        TicketEntity ticket = event.getTicket();
        // 更新当前技能组workgroupUid
        runtimeService.setVariable(ticket.getProcessInstanceId(), TicketConsts.TICKET_VARIABLE_WORKGROUP_UID, event.getNewWorkgroupUid());
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

