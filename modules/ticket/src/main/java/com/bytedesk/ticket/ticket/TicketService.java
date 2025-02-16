/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-29 12:24:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 22:34:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bytedesk.ticket.consts.TicketConsts;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    // private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final TicketRepository ticketRepository;

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
    // public Page<TicketResponse> queryClaimed(TicketRequest request) {
    //     Pageable pageable = request.getPageable();
    //     List<Task> tasks = taskService.createTaskQuery()
    //         .processDefinitionKey("groupTicketSimpleProcess")
    //         .taskAssignee(request.getUserUid())
    //         .taskDefinitionKey("assignToGroup")
    //         .processVariableValueEquals("orgUid", request.getOrgUid())
    //         .listPage(pageable.getPageNumber(), pageable.getPageSize());

    //     long total = taskService.createTaskQuery()
    //         .processDefinitionKey("groupTicketSimpleProcess")
    //         .taskAssignee(request.getUserUid())
    //         .taskDefinitionKey("assignToGroup")
    //         .processVariableValueEquals("orgUid", request.getOrgUid())
    //         .count();

    //     List<TicketResponse> responses = tasks.stream()
    //         .map(task -> {
    //             String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(), "ticketUid");
    //             TicketEntity ticket = ticketRepository.findByUid(ticketUid);
    //             return TicketResponse.fromEntity(ticket);
    //         })
    //         .toList();

    //     return new PageImpl<>(responses, pageable, total);
    // }

    /**
     * 查询待分配的工单
     */
    // public Page<TicketResponse> queryUnassigned(TicketRequest request) {
    //     Pageable pageable = request.getPageable();
    //     List<Task> tasks = taskService.createTaskQuery()
    //         .processDefinitionKey("groupTicketSimpleProcess")
    //         .taskCandidateGroup(request.getWorkgroupUid())
    //         .processVariableValueEquals("orgUid", request.getOrgUid())
    //         .listPage(pageable.getPageNumber(), pageable.getPageSize());

    //     long total = taskService.createTaskQuery()
    //         .processDefinitionKey("groupTicketSimpleProcess")
    //         .taskCandidateGroup(request.getWorkgroupUid())
    //         .processVariableValueEquals("orgUid", request.getOrgUid())
    //         .count();

    //     List<TicketResponse> responses = tasks.stream()
    //         .map(task -> {
    //             String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(), "ticketUid");
    //             TicketEntity ticket = ticketRepository.findByUid(ticketUid);
    //             return TicketResponse.fromEntity(ticket);
    //         })
    //         .toList();

    //     return new PageImpl<>(responses, pageable, total);
    // }

    /**
     * 认领工单
     */
    // @Transactional
    // public void claimTicket(TicketRequest request) {
    //     Task task = taskService.createTaskQuery()
    //         .processDefinitionKey("groupTicketSimpleProcess")
    //         .taskDefinitionKey("assignToGroup")
    //         .processVariableValueEquals("ticketUid", request.getTicketUid())
    //         .processVariableValueEquals("orgUid", request.getOrgUid())
    //         .singleResult();

    //     if (task != null) {
    //         taskService.claim(task.getId(), request.getUserUid());
            
    //         // 更新工单状态
    //         TicketEntity ticket = ticketRepository.findByUid(request.getTicketUid());
    //         ticket.setAssigneeUid(request.getUserUid());
    //         ticket.setStatus(TicketStatusEnum.PENDING);
    //         ticketRepository.save(ticket);
    //     }
    // }

    /**
     * 退回工单
     */
    // @Transactional
    // public void unclaimTicket(TicketRequest request) {
    //     Task task = taskService.createTaskQuery()
    //         .processDefinitionKey("groupTicketSimpleProcess")
    //         .taskDefinitionKey("assignToGroup")
    //         .taskAssignee(request.getUserUid())
    //         .processVariableValueEquals("ticketUid", request.getTicketUid())
    //         .processVariableValueEquals("orgUid", request.getOrgUid())
    //         .singleResult();

    //     if (task != null) {
    //         taskService.unclaim(task.getId());
            
    //         // 更新工单状态
    //         TicketEntity ticket = ticketRepository.findByUid(request.getTicketUid());
    //         ticket.setAssigneeUid(null);
    //         ticket.setStatus(TicketStatusEnum.OPEN);
    //         ticketRepository.save(ticket);
    //     }
    // }

    /**
     * 完成工单
     */
    // @Transactional
    // public void completeTicket(TicketRequest request) {
    //     Task task = taskService.createTaskQuery()
    //         .processDefinitionKey("groupTicketSimpleProcess")
    //         .processVariableValueEquals("ticketUid", request.getTicketUid())
    //         .processVariableValueEquals("orgUid", request.getOrgUid())
    //         .singleResult();

    //     if (task != null) {
    //         Map<String, Object> variables = new HashMap<>();
    //         variables.put("status", request.getStatus());
    //         variables.put("solution", request.getSolution());
    //         taskService.complete(task.getId(), variables);
            
    //         // 更新工单状态
    //         TicketEntity ticket = ticketRepository.findByUid(request.getTicketUid());
    //         ticket.setStatus(TicketStatusEnum.valueOf(request.getStatus()));
    //         ticket.setSolution(request.getSolution());
    //         ticketRepository.save(ticket);
    //     }
    // }
} 