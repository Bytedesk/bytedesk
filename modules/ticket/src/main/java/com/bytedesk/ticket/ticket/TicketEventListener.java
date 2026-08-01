/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:52:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 15:55:36
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.ticket.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateDepartmentEvent;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentService;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.utils.FlowableIdUtils;

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

    private final TicketNotificationService ticketNotificationService;

    private final TicketAssignmentService ticketAssignmentService;

    private final TicketRepository ticketRepository;

    private final TicketSLAService ticketSLAService;

    // 用于防止同一消息重复处理
    private final Set<String> processedMessageUids = ConcurrentHashMap.newKeySet();

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
        variables.putAll(ticketSLAService.buildProcessVariables(ticket));
        //
        // processEntityUid 同时作为 Flowable 的 processDefinitionKey
        String processKey = ticket.getProcessEntityUid();
        if (!StringUtils.hasText(processKey)) {
            // 回退到默认的 processEntityUid（基于 orgUid 和 ticket type 计算）
            processKey = ticket.getType().equals(TicketTypeEnum.EXTERNAL.name())
                    ? Utils.formatUid(ticket.getOrgUid(),
                            TicketConsts.TICKET_PROCESS_KEY + TicketConsts.TICKET_EXTERNAL_PROCESS_UID_SUFFIX)
                    : Utils.formatUid(ticket.getOrgUid(), TicketConsts.TICKET_PROCESS_KEY);
        }
        // Flowable 要求 processDefinitionKey 为 NCName（不能以数字开头）
        processKey = FlowableIdUtils.toProcessDefinitionKey(processKey);
        // 2. 启动流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(processKey)
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

        runtimeService.setVariables(processInstance.getId(), ticketSLAService.buildProcessVariables(ticket));
        // 第一步的assignee设置为reporter
        runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_ASSIGNEE,
                ticket.getReporter());
        // 6. 更新工单的流程实例ID
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(ticket.getUid());
        if (ticketOptional.isPresent()) {
            TicketEntity ticketEntity = ticketOptional.get();
            ticketEntity.setProcessInstanceId(processInstance.getId());
            ticketRestService.save(ticketEntity);
            ticketSLAService.initializeSlaRecords(ticketEntity);

            // 7. 自动分配处理人
            ticketAssignmentService.autoAssign(ticketEntity, processInstance.getId());
        }

        ticketNotificationService.notifyNewTicket(ticket);
    }

    // 监听工单更新事件
    @EventListener
    public void handleTicketUpdateEvent(TicketUpdateEvent event) {
        log.info("TicketEventListener handleTicketUpdateEvent: {}", event.getTicket().getUid());
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
    public void handleTicketUpdateDepartmentEvent(TicketUpdateDepartmentEvent event) {
        log.info("TicketEventListener handleTicketUpdateDepartmentEvent: {}", event);
        TicketEntity ticket = event.getTicket();
        // 更新当前工作组DepartmentUid
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

    /**
     * 监听工单会话中的消息创建事件，当客服在 TICKET_EXTERNAL 会话中发送消息时，
     * 通过 TicketNotificationService 向访客推送邮件/短信通知。
     * 参考 QueueMemberEventListener.onMessageCreateEvent 实现。
     */
    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (message == null) {
            return;
        }

        // 防止重复处理同一消息
        String messageUid = message.getUid();
        if (!StringUtils.hasText(messageUid) || !processedMessageUids.add(messageUid)) {
            log.debug("TicketEventListener onMessageCreateEvent: skip duplicate, messageUid={}", messageUid);
            return;
        }

        // 仅处理客服（AGENT）发送的消息
        if (!message.isFromAgent()) {
            log.debug("TicketEventListener onMessageCreateEvent: skip non-agent, messageUid={}, userType={}",
                    messageUid, message.getUserProtobuf() != null ? message.getUserProtobuf().getType() : "null");
            return;
        }

        // 获取消息对应的会话
        ThreadEntity thread = threadRestService.findByUid(message.getThread().getUid()).orElse(null);
        if (thread == null) {
            log.warn("TicketEventListener onMessageCreateEvent: thread not found, messageUid={}, threadUid={}",
                    messageUid, message.getThread().getUid());
            return;
        }

        // 仅处理 TICKET_EXTERNAL 类型会话（客服↔访客）
        if (!ThreadTypeEnum.TICKET_EXTERNAL.name().equals(thread.getType())) {
            log.debug("TicketEventListener onMessageCreateEvent: skip non-ticket thread, messageUid={}, threadUid={}, threadType={}",
                    messageUid, thread.getUid(), thread.getType());
            return;
        }

        log.info("TicketEventListener onMessageCreateEvent: agent message in ticket thread, messageUid={}, threadUid={}, agentUid={}",
                messageUid, thread.getUid(), message.getUserProtobuf().getUid());

        // 根据 threadUid 查询对应工单
        Optional<TicketEntity> ticketOpt = ticketRepository
                .findFirstByOrgUidAndThreadUidOrderByCreatedAtDesc(thread.getOrgUid(), thread.getUid());
        if (ticketOpt.isEmpty()) {
            log.warn("TicketEventListener onMessageCreateEvent: no ticket found, messageUid={}, threadUid={}, orgUid={}",
                    messageUid, thread.getUid(), thread.getOrgUid());
            return;
        }
        TicketEntity ticket = ticketOpt.get();
        log.info("TicketEventListener onMessageCreateEvent: notify visitor, ticketUid={}, ticketNumber={}, agent={}",
                ticket.getUid(), ticket.getTicketNumber(), message.getUserProtobuf().getNickname());
        ticketSLAService.completeFirstResponse(ticket, message.getUserProtobuf().getUid());

        // 通知访客（邮件 + 短信），仅告知有新回复
        ticketNotificationService.notifyVisitorOfAgentMessage(ticket, message.getUserProtobuf().getNickname());
    }

}
