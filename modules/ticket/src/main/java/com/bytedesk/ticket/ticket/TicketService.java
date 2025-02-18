/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-29 12:24:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-19 06:58:15
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
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.ticket.consts.TicketConsts;
// import com.bytedesk.ticket.message.event.TicketMessageEvent;
// import com.bytedesk.ticket.message.event.TicketMessageType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final TicketRepository ticketRepository;
    private final HistoryService historyService;
    private final AgentRestService agentRestService;
    // private final ApplicationEventPublisher eventPublisher;

    /**
     * 查询工单，并过滤掉没有任务的工单
     * 支持specification查询
     */
    public Page<TicketResponse> queryTicketFilter(TicketRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TicketEntity> spec = TicketSpecification.search(request);
        // 2. 获取符合条件的工单
        Page<TicketEntity> ticketPage = ticketRepository.findAll(spec, pageable);

        // 3. 获取这些工单的当前任务
        List<TicketResponse> responses = ticketPage.getContent().stream()
                .map(ticket -> {
                    // 查询工单对应的当前任务
                    Task task = taskService.createTaskQuery()
                            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                            .processInstanceId(ticket.getProcessInstanceId())
                            .singleResult();
                    // 如果任务为空，则返回null
                    if (task == null) {
                        return null;
                    }
                    // 如果任务不为空，则返回工单响应
                    return TicketConvertUtils.convertToResponse(ticket);
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(responses, pageable, ticketPage.getTotalElements());
    }

    /**
     * 查询企业orgUid所有工单
     */
    public Page<TicketResponse> queryTicket(TicketRequest request) {
        Pageable pageable = request.getPageable();
        //
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(),
                            TicketConsts.TICKET_VARIABLE_TICKET_UID);
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
                .taskAssignee(request.getReporter().getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskAssignee(request.getReporter().getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(),
                            TicketConsts.TICKET_VARIABLE_TICKET_UID);
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
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskAssignee(request.getAssigneeUid())
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .orderByTaskCreateTime().desc()
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(),
                            TicketConsts.TICKET_VARIABLE_TICKET_UID);
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
        // 如果workgroupUid为空，则查询所有待分配的工单
        if (!StringUtils.hasText(request.getWorkgroupUid())) {
            return queryAllUnassigned(request);
        }

        Pageable pageable = request.getPageable();
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskCandidateGroup(request.getWorkgroupUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskCandidateGroup(request.getWorkgroupUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(),
                            TicketConsts.TICKET_VARIABLE_TICKET_UID);
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
     * 查询所有待分配的工单
     */
    public Page<TicketResponse> queryAllUnassigned(TicketRequest request) {
        // 查询所有待分配的工单
        Pageable pageable = request.getPageable();
        //
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .listPage(pageable.getPageNumber(), pageable.getPageSize());

        long total = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .count();

        List<TicketResponse> responses = tasks.stream()
                .map(task -> {
                    String ticketUid = (String) runtimeService.getVariable(task.getExecutionId(),
                            TicketConsts.TICKET_VARIABLE_TICKET_UID);
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
    public TicketResponse claimTicket(TicketRequest request) {
        log.info("开始认领工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssigneeUid(), request.getOrgUid());

        // 1. 先查询工单
        Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId()) // 使用processInstanceId查询
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .active() // 只查询活动的任务
                .singleResult();

        log.info("查询到的任务: task={}, processInstanceId={}", task, ticket.getProcessInstanceId());

        if (task == null) {
            // 如果没有找到任务，打印更多信息以便调试
            List<Task> allTasks = taskService.createTaskQuery()
                    .processInstanceId(ticket.getProcessInstanceId())
                    .list();
            log.error("未找到可认领的任务, 当前流程实例所有任务: {}", allTasks);
            throw new RuntimeException("工单任务不存在或已被认领: " + request.getUid());
        }

        // 3. 认领任务
        String assigneeUid = request.getAssigneeUid();
        if (!StringUtils.hasText(assigneeUid)) {
            throw new RuntimeException("处理人uid不能为空");
        }

        try {
            taskService.claim(task.getId(), assigneeUid);
            log.info("工单认领成功: taskId={}, assigneeUid={}", task.getId(), assigneeUid);
        } catch (Exception e) {
            log.error("工单认领失败: ", e);
            throw new RuntimeException("工单认领失败: " + e.getMessage());
        }

        // 4. 更新工单状态
        Optional<AgentEntity> assigneeOptional = agentRestService.findByUid(assigneeUid);
        if (assigneeOptional.isPresent()) {
            UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                    .nickname(assigneeOptional.get().getNickname())
                    .avatar(assigneeOptional.get().getAvatar())
                    .build();
            assigneeProtobuf.setUid(assigneeOptional.get().getUid());
            assigneeProtobuf.setType(UserTypeEnum.AGENT.name());
            ticket.setAssignee(JSON.toJSONString(assigneeProtobuf));
            ticket.setStatus(TicketStatusEnum.ASSIGNED.name());

            // 4. 更新流程变量
            Map<String, Object> variables = new HashMap<>();
            variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssignee());
            variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
            variables.put(TicketConsts.TICKET_VARIABLE_CLAIM_TIME, new Date());
            runtimeService.setVariables(ticket.getProcessInstanceId(), variables);

            // 5. 发布工单分配消息事件
            // 此处没有使用ticket自带消息机制，便于扩展
            // eventPublisher.publishEvent(TicketMessageEvent.builder()
            // .ticketUid(ticket.getUid())
            // .processInstanceId(ticket.getProcessInstanceId())
            // .type(TicketMessageType.ASSIGNED.name())
            // .assignee(assigneeProtobuf)
            // .description("工单已分配给 " + assigneeProtobuf.getNickname())
            // .createTime(new Date())
            // .build());
        }

        // 6. 保存工单
        ticketRepository.save(ticket);

        // 7. 返回工单响应
        return TicketConvertUtils.convertToResponse(ticket);

    }

    /**
     * 退回工单
     */
    @Transactional
    public TicketResponse unclaimTicket(TicketRequest request) {
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .taskAssignee(request.getAssigneeUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
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
                return TicketConvertUtils.convertToResponse(ticket);
            }
        }
        return null;
    }

    /**
     * 完成工单
     */
    @Transactional
    public TicketResponse completeTicket(TicketRequest request) {
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY_GROUP_SIMPLE)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put(TicketConsts.TICKET_VARIABLE_STATUS, request.getStatus());
            // variables.put("solution", request.getSolution());
            taskService.complete(task.getId(), variables);

            // 更新工单状态
            Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
            if (ticketOptional.isPresent()) {
                TicketEntity ticket = ticketOptional.get();
                // ticket.setStatus(TicketStatusEnum.valueOf(request.getStatus()));
                // ticket.setSolution(request.getSolution());
                ticketRepository.save(ticket);
                return TicketConvertUtils.convertToResponse(ticket);
            }
        }
        return null;
    }

    /**
     * 查询某个工单的处理历史
     */
    public List<TicketHistoryProcessResponse> queryTicketProcessInstanceHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                // 根据uid查询processInstanceId
                Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
                if (ticketOptional.isPresent()) {
                    request.setProcessInstanceId(ticketOptional.get().getProcessInstanceId());
                }
            } else {
                throw new RuntimeException("processInstanceId不能为空");
            }
        }

        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .includeProcessVariables() // 重要：包含流程变量
                .orderByProcessInstanceEndTime().asc()
                .list();

        List<TicketHistoryProcessResponse> responses = historicProcessInstances.stream()
                .map(historicProcessInstance -> {
                    Map<String, Object> variables = historicProcessInstance.getProcessVariables();

                    // 从流程变量中获取 assignee
                    UserProtobuf assignee = null;
                    Object assigneeObj = variables.get(TicketConsts.TICKET_VARIABLE_ASSIGNEE);
                    if (assigneeObj != null) {
                        if (assigneeObj instanceof UserProtobuf) {
                            // 如果已经是 UserProtobuf 对象，直接使用
                            assignee = (UserProtobuf) assigneeObj;
                        } else if (assigneeObj instanceof String) {
                            // 如果是 JSON 字符串，需要解析
                            try {
                                assignee = JSON.parseObject((String) assigneeObj, UserProtobuf.class);
                            } catch (Exception e) {
                                log.warn("Failed to parse assignee JSON: {}", assigneeObj, e);
                            }
                        } else {
                            log.warn("Unexpected assignee type: {}", assigneeObj.getClass());
                        }
                    }

                    return TicketHistoryProcessResponse.builder()
                            .processInstanceId(historicProcessInstance.getId())
                            .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                            .processDefinitionName(historicProcessInstance.getProcessDefinitionName())
                            .processDefinitionKey(historicProcessInstance.getProcessDefinitionKey())
                            .processDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion())
                            .businessKey(historicProcessInstance.getBusinessKey())
                            .startTime(historicProcessInstance.getStartTime())
                            .endTime(historicProcessInstance.getEndTime())
                            .durationInMillis(historicProcessInstance.getDurationInMillis())
                            .deleteReason(historicProcessInstance.getDeleteReason())
                            .tenantId(historicProcessInstance.getTenantId())
                            .name(historicProcessInstance.getName())
                            // 从流程变量中获取状态
                            .assignee(assignee)
                            .description((String) variables.get(TicketConsts.TICKET_VARIABLE_DESCRIPTION))
                            .startUserId((String) variables.get(TicketConsts.TICKET_VARIABLE_START_USER_ID))
                            .status((String) variables.get(TicketConsts.TICKET_VARIABLE_STATUS))
                            .priority((String) variables.get(TicketConsts.TICKET_VARIABLE_PRIORITY))
                            .categoryUid((String) variables.get(TicketConsts.TICKET_VARIABLE_CATEGORY_UID))
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        return responses;
    }


    /**
     * 查询某个工单的流程实例历史
     */
    public List<TicketHistoryProcessResponse> queryTicketTaskHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                // 根据uid查询processInstanceId
                Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
                if (ticketOptional.isPresent()) {
                    request.setProcessInstanceId(ticketOptional.get().getProcessInstanceId());
                }
            } else {
                throw new RuntimeException("processInstanceId不能为空");
            }
        }

        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .orderByHistoricTaskInstanceStartTime().asc()
                .list();

        List<TicketHistoryProcessResponse> responses = historicTasks.stream()
                .map(historicTask -> {
                    return TicketHistoryProcessResponse.builder()
                            .taskId(historicTask.getId())
                            .taskName(historicTask.getName())
                            .build();
                })
                .toList();
        return responses;
    }
}