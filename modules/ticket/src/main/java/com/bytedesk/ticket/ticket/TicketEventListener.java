/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:52:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 12:24:55
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
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
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

    private final ThreadRestService threadRestService;

    private final TicketService ticketService;

    private final TopicCacheService topicCacheService;

    @EventListener
    public void handleTicketCreateEvent(TicketCreateEvent event) {
        TicketEntity ticket = event.getTicket();
        log.info("开始创建工单流程实例: ticketUid={}, orgUid={}", ticket.getUid(), ticket.getOrgUid());
        // 1. 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        // 基本变量
        variables.put(TicketConsts.TICKET_VARIABLE_TICKET_UID, ticket.getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_WORKGROUP_UID, ticket.getWorkgroup().getUid());
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
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
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
            // 认领任务
            if (StringUtils.hasText(ticketEntity.getAssigneeString())) {
                TicketRequest request = new TicketRequest();
                request.setUid(ticketEntity.getUid());
                request.setAssigneeUid(ticketEntity.getAssignee().getUid());
                request.setOrgUid(ticketEntity.getOrgUid());
                // 认领工单
                ticketService.claimTicket(request);
            }
        }
        // 订阅工单会话
        UserEntity owner = ticket.getOwner();
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(TopicUtils.formatOrgWorkgroupTicketThreadTopic(ticket.getWorkgroup().getUid(), ticket.getUid()))
                // .userUid(owner.getUid())
                .build();
            topicRequest.setUserUid(owner.getUid());
        topicCacheService.push(JSON.toJSONString(topicRequest));
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
    public void handleTicketUpdateWorkgroupEvent(TicketUpdateWorkgroupEvent event) {
        log.info("TicketEventListener handleTicketUpdateWorkgroupEvent: {}", event);
        TicketEntity ticket = event.getTicket();
        // 更新当前技能组workgroupUid
        runtimeService.setVariable(ticket.getProcessInstanceId(), TicketConsts.TICKET_VARIABLE_WORKGROUP_UID,
                event.getNewWorkgroupUid());
    }

    // 监听上传BPMN流程图
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        log.info("TicketEventListener upload bpmn create: {}", upload.toString());
        // 上传BPMN流程图
        if (upload.getType().equals(UploadTypeEnum.BPMN.name())) {
            // 启动流程
            // ProcessInstance processInstance =
            // runtimeService.startProcessInstanceByKey(upload.getFileName());
        }
    }

}
