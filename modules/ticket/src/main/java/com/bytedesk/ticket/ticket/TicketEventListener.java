/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:52:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 12:15:49
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.ticket.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateDepartmentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventListener {

    private final RuntimeService runtimeService;

    private final TicketRestService ticketRestService;

    private final TaskService taskService;

    private final ThreadRestService threadRestService;

    @Order(3)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("ticket - organization created: {}", organization.getName());
        ticketRestService.initTicketCategory(orgUid);
    }

    @EventListener
    public void handleTicketCreateEvent(TicketCreateEvent event) {
        TicketEntity ticket = event.getTicket();
        log.info("开始创建工单流程实例: ticketUid={}, orgUid={}", ticket.getUid(), ticket.getOrgUid());
        // 1. 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        // 基本变量
        variables.put(TicketConsts.TICKET_VARIABLE_TICKET_UID, ticket.getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_DEPARTMENT_UID, ticket.getDepartmentUid());
        variables.put(TicketConsts.TICKET_VARIABLE_REPORTER_UID, ticket.getReporter().getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_ORGUID, ticket.getOrgUid());
        //
        variables.put(TicketConsts.TICKET_VARIABLE_DESCRIPTION, ticket.getDescription());
        variables.put(TicketConsts.TICKET_VARIABLE_START_USER_ID, ticket.getReporter().getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
        variables.put(TicketConsts.TICKET_VARIABLE_PRIORITY, ticket.getPriority());
        variables.put(TicketConsts.TICKET_VARIABLE_CATEGORY_UID, ticket.getCategoryUid());
        
        // 2. 启动流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
                .tenantId(ticket.getOrgUid())
                .name(ticket.getTitle())
                .businessKey(ticket.getUid())
                .variables(variables)
                .start();
        log.info("流程实例创建成功: processInstanceId={}, businessKey={}",
                processInstance.getId(), processInstance.getBusinessKey());

        // 3. 创建任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee(ticket.getReporter().getUid())
                .singleResult();
        if (task != null) {
            // 完成工单创建任务
            taskService.complete(task.getId());
        } else {
            log.error("工单创建任务创建失败: task={}", task);
        }

        // 4. 设置流程实例变量
        // 可以在流程执行的任何时候调用, 每次调用都会产生一次变量更新历史记录
        // 适合设置运行时的动态变量或需要更新的变量
        // 每次调用都会有一次数据库操作
        runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_START_TIME, new Date());

        // 5. 设置 SLA 时间
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
        runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_ASSIGNEE,
                ticket.getReporter());
        // 6. 更新工单的流程实例ID
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(ticket.getUid());
        if (ticketOptional.isPresent()) {
            TicketEntity ticketEntity = ticketOptional.get();
            ticketEntity.setProcessInstanceId(processInstance.getId());
            ticketRestService.save(ticketEntity);
        }
    }

    // 监听工单更新事件
    @EventListener
    public void handleTicketUpdateEvent(TicketUpdateEvent event) {
        log.info("TicketEventListener handleTicketUpdateEvent: {}", event);
        TicketEntity ticket = event.getTicket();
        // 判断是否删除
        if (ticket.getDeleted()) {
            // 同步更新流程实例
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(ticket.getProcessInstanceId())
                    .singleResult();
            if (processInstance != null) {
                runtimeService.deleteProcessInstance(processInstance.getId(), "deleted by user");
            }
            // 删除工单会话
            threadRestService.deleteByUid(ticket.getThreadUid());
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
    public void handleTicketUpdateDepartmentEvent(TicketUpdateDepartmentEvent event) {
        log.info("TicketEventListener handleTicketUpdateDepartmentEvent: {}", event);
        TicketEntity ticket = event.getTicket();
        // 更新当前技能组DepartmentUid
        runtimeService.setVariable(ticket.getProcessInstanceId(), TicketConsts.TICKET_VARIABLE_DEPARTMENT_UID,
                event.getNewDepartmentUid());
    }

    // 监听上传BPMN流程图
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        // 上传BPMN流程图
        if (UploadTypeEnum.BPMN.name().equalsIgnoreCase(upload.getType())) {
            // 启动流程
            // ProcessInstance processInstance =
            // runtimeService.startProcessInstanceByKey(upload.getFileName());
        }
    }

}
