/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-29 12:24:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 10:36:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.ticket.consts.TicketConsts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final TicketRepository ticketRepository;
    private final HistoryService historyService;
    private final AgentRestService agentRestService;

    /**
     * 查询企业orgUid所有工单
     */
    public Page<TicketResponse> queryTicket(TicketRequest request) {
        Pageable pageable = request.getPageable();
        //
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(), "ticketUid");
                    Optional<TicketEntity> ticket = ticketRepository.findByUid(ticketUid);
                    if (ticket.isPresent()) {
                        return TicketConvertUtils.convertToResponse(ticket.get());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    /**
     * 查询我创建的工单
     */
    public Page<TicketResponse> queryCreated(TicketRequest request) {
        Pageable pageable = request.getPageable();
        //
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskAssignee(request.getReporterUid())
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskAssignee(request.getReporterUid())
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(), "ticketUid");
                    Optional<TicketEntity> ticket = ticketRepository.findByUid(ticketUid);
                    if (ticket.isPresent()) {
                        return TicketConvertUtils.convertToResponse(ticket.get());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    /**
     * 查询待我处理的工单
     */
    public Page<TicketResponse> queryClaimed(TicketRequest request) {
        Pageable pageable = request.getPageable();
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskAssignee(request.getAssigneeUid())
                .taskDefinitionKey("assignToGroup")
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskAssignee(request.getAssigneeUid())
                .taskDefinitionKey("assignToGroup")
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(), "ticketUid");
                    Optional<TicketEntity> ticket = ticketRepository.findByUid(ticketUid);
                    if (ticket.isPresent()) {
                        return TicketConvertUtils.convertToResponse(ticket.get());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    /**
     * 查询待分配的工单
     */
    public Page<TicketResponse> queryUnassigned(TicketRequest request) {
        Pageable pageable = request.getPageable();
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskCandidateGroup(request.getWorkgroupUid())
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskCandidateGroup(request.getWorkgroupUid())
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(), "ticketUid");
                    Optional<TicketEntity> ticket = ticketRepository.findByUid(ticketUid);
                    if (ticket.isPresent()) {
                        return TicketConvertUtils.convertToResponse(ticket.get());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    /**
     * 认领工单
     */
    @Transactional
    public void claimTicket(TicketRequest request) {
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskDefinitionKey("assignToGroup")
                .processVariableValueEquals("ticketUid", request.getUid())
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .singleResult();

        if (task != null) {
            taskService.claim(task.getId(), request.getAssigneeUid());

            // 更新工单状态
            Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
            if (ticketOptional.isPresent()) {
                TicketEntity ticket = ticketOptional.get();
                Optional<AgentEntity> assigneeOptional = agentRestService.findByUid(request.getAssigneeUid());
                if (assigneeOptional.isPresent()) {
                    UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                            .nickname(assigneeOptional.get().getNickname())
                            .avatar(assigneeOptional.get().getAvatar())
                            .build();
                    assigneeProtobuf.setUid(assigneeOptional.get().getUid());
                    assigneeProtobuf.setType(UserTypeEnum.AGENT.name());
                    ticket.setAssignee(JSON.toJSONString(assigneeProtobuf));
                    ticket.setStatus(TicketStatusEnum.ASSIGNED.name());
                }
                ticketRepository.save(ticket);
            }
        }
    }

    /**
     * 退回工单
     */
    @Transactional
    public void unclaimTicket(TicketRequest request) {
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskDefinitionKey("assignToGroup")
                .taskAssignee(request.getAssigneeUid())
                .processVariableValueEquals("ticketUid", request.getUid())
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .singleResult();

        if (task != null) {
            taskService.unclaim(task.getId());

            // 更新工单状态
            Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
            if (ticketOptional.isPresent()) {
                TicketEntity ticket = ticketOptional.get();
                // ticket.setAssigneeUid(null);
                // ticket.setStatus(TicketStatusEnum.OPEN);
                ticketRepository.save(ticket);
            }
        }
    }

    /**
     * 完成工单
     */
    @Transactional
    public void completeTicket(TicketRequest request) {
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals("ticketUid", request.getUid())
                .processVariableValueEquals("orgUid", request.getOrgUid())
                .singleResult();

        if (task != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("status", request.getStatus());
            // variables.put("solution", request.getSolution());
            taskService.complete(task.getId(), variables);

            // 更新工单状态
            Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
            if (ticketOptional.isPresent()) {
                TicketEntity ticket = ticketOptional.get();
                // ticket.setStatus(TicketStatusEnum.valueOf(request.getStatus()));
                // ticket.setSolution(request.getSolution());
                ticketRepository.save(ticket);
            }
        }
    }

    /**
     * 查询某个工单的处理历史
     */
    public List<TicketHistory> queryTicketHistory(TicketRequest request) {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .orderByProcessInstanceEndTime().asc()
                .list();

        List<TicketHistory> responses = historicProcessInstances.stream()
                .map(historicProcessInstance -> {
                    return TicketHistory.builder()
                            .processInstanceId(historicProcessInstance.getId())
                            .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                            .processDefinitionName(historicProcessInstance.getProcessDefinitionName())
                            .processDefinitionKey(historicProcessInstance.getProcessDefinitionKey())
                            .processDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion())
                            .businessKey(historicProcessInstance.getBusinessKey())
                            .startUserId(historicProcessInstance.getStartUserId())
                            .startTime(historicProcessInstance.getStartTime())
                            .endTime(historicProcessInstance.getEndTime())
                            .durationInMillis(historicProcessInstance.getDurationInMillis())
                            .deleteReason(historicProcessInstance.getDeleteReason())
                            .tenantId(historicProcessInstance.getTenantId())
                            .name(historicProcessInstance.getName())
                            .description(historicProcessInstance.getDescription())
                            // 从流程变量中获取状态
                            .status((String) historicProcessInstance.getProcessVariables().get("status"))
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        return responses;
    }

}