/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-29 12:24:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 07:11:53
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
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.dto.TicketHistoryActivityResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryProcessResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryTaskResponse;
import com.bytedesk.ticket.ticket.dto.TicketWorkflowActionFieldResponse;
import com.bytedesk.ticket.ticket.dto.TicketWorkflowActionResponse;
import com.bytedesk.ticket.ticket.dto.TicketWorkflowTaskResponse;
import com.bytedesk.ticket.utils.TicketConvertUtils;
import com.bytedesk.core.utils.BdDateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Description("Ticket Service - Workflow-based ticket management and processing service")
public class TicketService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final MemberRestService memberRestService;
    private final ThreadRestService threadRestService;
    private final TopicSubscriptionRestService topicSubscriptionRestService;
    private final TicketRestService ticketRestService;
    private final TicketNotificationService ticketNotificationService;
    private final ProcessRepository processRepository;

    private TicketEntity getTicketOrThrow(String ticketUid) {
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(ticketUid);
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + ticketUid);
        }
        return ticketOptional.get();
    }

    private Task getActiveTaskOrThrow(TicketEntity ticket, TicketRequest request) {
        if (StringUtils.hasText(request.getTaskId())) {
            Task task = taskService.createTaskQuery()
                    .taskId(request.getTaskId())
                    .active()
                    .singleResult();
            if (task == null) {
                throw new RuntimeException("任务不存在或已结束: " + request.getTaskId());
            }
            if (!Objects.equals(task.getProcessInstanceId(), ticket.getProcessInstanceId())) {
                throw new RuntimeException("任务不属于该工单流程实例: " + request.getTaskId());
            }
            return task;
        }

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .active()
                .list();

        if (tasks == null || tasks.isEmpty()) {
            throw new RuntimeException("工单任务不存在: " + ticket.getUid());
        }
        if (tasks.size() > 1) {
            throw new RuntimeException("当前存在多个并行任务，请指定 taskId");
        }
        return tasks.get(0);
    }

    private void addTaskComment(Task task, TicketEntity ticket, String userId, String type, String message) {
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(), type, message);
        if (StringUtils.hasText(userId)) {
            comment.setUserId(userId);
        }
        taskService.saveComment(comment);
    }

    public List<TicketWorkflowTaskResponse> queryWorkflowActions(TicketRequest request) {
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getUid(), "ticket uid required");
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : request.getAssigneeUid();
        Assert.hasText(operatorUid, "operator uid required");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        if (!StringUtils.hasText(ticket.getProcessInstanceId())) {
            return List.of();
        }

        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .active()
                .list();
        if (activeTasks == null || activeTasks.isEmpty()) {
            return List.of();
        }

        JSONObject flowgramSchema = loadFlowgramSchema(ticket);
        return activeTasks.stream()
                .map(task -> buildWorkflowTaskResponse(ticket, task, operatorUid, flowgramSchema))
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse executeWorkflowAction(TicketRequest request) {
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getUid(), "ticket uid required");
        String actionKey = StringUtils.hasText(request.getActionKey()) ? request.getActionKey() : request.getStatus();
        Assert.hasText(actionKey, "workflow action key required");
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : request.getAssigneeUid();
        Assert.hasText(operatorUid, "operator uid required");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        String previousStatus = ticket.getStatus();
        Task task = getActiveTaskOrThrow(ticket, request);
        TicketWorkflowRuntimeContext runtimeContext = buildRuntimeContext(ticket, task, actionKey);

        switch (runtimeContext.actionType()) {
            case "claim" -> claimWorkflowTask(ticket, task, request, operatorUid, runtimeContext);
            case "assign" -> assignWorkflowTask(ticket, task, request, operatorUid, false, runtimeContext);
            case "transfer" -> assignWorkflowTask(ticket, task, request, operatorUid, true, runtimeContext);
            case "transferDepartment" -> transferWorkflowTaskToDepartment(ticket, task, request, operatorUid,
                    runtimeContext);
            case "complete" -> completeWorkflowTask(ticket, task, request, operatorUid, runtimeContext);
            case "hold" -> holdWorkflowTask(ticket, task, request, operatorUid, runtimeContext);
            case "close" -> closeWorkflowTask(ticket, task, request, operatorUid, runtimeContext);
            case "delegate" -> delegateTicket(request);
            case "delegateResolve" -> resolveDelegatedTicket(request);
            case "cc" -> ccTicket(request);
            case "addSign" -> addSignTicket(request);
            case "rollback" -> rollbackTicket(request);
            case "revoke" -> {
                request.setReason(
                        StringUtils.hasText(request.getReason()) ? request.getReason() : "workflow action revoke");
                return revokeTicket(request);
            }
            default -> throw new RuntimeException("unsupported workflow action type: " + runtimeContext.actionType());
        }

        persistAndNotifyStatusChange(ticket, previousStatus);
        return TicketConvertUtils.convertToResponse(ticket);
    }

    private TicketWorkflowTaskResponse buildWorkflowTaskResponse(TicketEntity ticket, Task task, String operatorUid,
            JSONObject flowgramSchema) {
        JSONObject node = findFlowgramNode(flowgramSchema, task.getTaskDefinitionKey());
        String nodeType = node != null ? node.getString("type") : null;
        JSONObject data = node != null ? node.getJSONObject("data") : null;
        String nodeTitle = data != null && StringUtils.hasText(data.getString("title"))
                ? data.getString("title")
                : task.getName();
        boolean assignedToOperator = StringUtils.hasText(task.getAssignee())
                && Objects.equals(task.getAssignee(), operatorUid);
        boolean unassigned = !StringUtils.hasText(task.getAssignee());
        boolean actionable = assignedToOperator || unassigned;

        return TicketWorkflowTaskResponse.builder()
                .ticketUid(ticket.getUid())
                .processEntityUid(ticket.getProcessEntityUid())
                .processInstanceId(ticket.getProcessInstanceId())
                .taskId(task.getId())
                .taskName(task.getName())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .assignee(task.getAssignee())
                .nodeType(nodeType)
                .nodeTitle(nodeTitle)
                .actionable(actionable)
                .actions(actionable ? buildWorkflowActions(task, node, nodeTitle, unassigned,
                        hasVerifiedGatewayAfterNode(flowgramSchema, task.getTaskDefinitionKey())) : List.of())
                .build();
    }

    private List<TicketWorkflowActionResponse> buildWorkflowActions(Task task, JSONObject node, String nodeTitle,
            boolean unassigned, boolean followedByVerifiedGateway) {
        List<TicketWorkflowActionResponse> configuredActions = buildConfiguredWorkflowActions(task, node);
        if (!configuredActions.isEmpty()) {
            if (unassigned) {
                return configuredActions.stream()
                        .filter(action -> "CLAIM".equals(action.getKey()) || "ASSIGN".equals(action.getKey()))
                        .collect(Collectors.toList());
            }
            if (task.getDelegationState() == DelegationState.PENDING) {
                return configuredActions.stream()
                        .filter(action -> "DELEGATE_RESOLVE".equals(action.getKey()))
                        .collect(Collectors.toList());
            }
            return configuredActions;
        }

        List<TicketWorkflowActionResponse> actions = new ArrayList<>();
        if (unassigned) {
            actions.add(TicketWorkflowActionResponse.builder()
                    .key("CLAIM")
                    .label("认领")
                    .type("claim")
                    .taskId(task.getId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .build());
            actions.add(TicketWorkflowActionResponse.builder()
                    .key("ASSIGN")
                    .label("指派")
                    .type("assign")
                    .taskId(task.getId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .build());
            return actions;
        }

        if (task.getDelegationState() == DelegationState.PENDING) {
            actions.add(TicketWorkflowActionResponse.builder()
                    .key("DELEGATE_RESOLVE")
                    .label("解决委托")
                    .type("delegateResolve")
                    .taskId(task.getId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .build());
            return actions;
        }

        JSONArray conditions = node != null && node.getJSONObject("data") != null
                ? node.getJSONObject("data").getJSONArray("conditions")
                : null;
        if ((conditions != null && !conditions.isEmpty() && hasVerifiedBranches(conditions))
                || followedByVerifiedGateway) {
            actions.add(TicketWorkflowActionResponse.builder()
                    .key("COMPLETE_VERIFIED")
                    .label("确认解决")
                    .type("complete")
                    .taskId(task.getId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .build());
            actions.add(TicketWorkflowActionResponse.builder()
                    .key("COMPLETE_REJECTED")
                    .label("未解决")
                    .type("complete")
                    .taskId(task.getId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .danger(true)
                    .build());
            return actions;
        }

        String completeLabel = TicketConsts.TICKET_USER_TASK_PROCESS_TICKET.equals(task.getTaskDefinitionKey())
            ? "提交处理结果"
            : (StringUtils.hasText(nodeTitle) ? "完成" + nodeTitle : "完成任务");
        actions.add(TicketWorkflowActionResponse.builder()
                .key("COMPLETE")
            .label(completeLabel)
                .type("complete")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("HOLD")
                .label("暂存")
                .type("hold")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("CLOSE")
                .label("关单")
                .type("close")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .danger(true)
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("TRANSFER")
                .label("转派")
                .type("transfer")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("TRANSFER_DEPARTMENT")
                .label("转派部门")
                .type("transferDepartment")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("DELEGATE")
                .label("委托")
                .type("delegate")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("CC")
                .label("抄送")
                .type("cc")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("ADDSIGN")
                .label("加签")
                .type("addSign")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("ROLLBACK")
                .label("退回")
                .type("rollback")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .danger(true)
                .build());
        actions.add(TicketWorkflowActionResponse.builder()
                .key("REVOKE")
                .label("撤销")
                .type("revoke")
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .danger(true)
                .build());
        return actions;
    }

    private List<TicketWorkflowActionResponse> buildConfiguredWorkflowActions(Task task, JSONObject node) {
        JSONObject data = node != null ? node.getJSONObject("data") : null;
        JSONArray availableActions = data != null ? data.getJSONArray("availableActions") : null;
        if (availableActions == null || availableActions.isEmpty()) {
            return List.of();
        }

        List<TicketWorkflowActionResponse> actions = new ArrayList<>();
        for (int i = 0; i < availableActions.size(); i++) {
            Object item = availableActions.get(i);
            JSONObject actionConfig = item instanceof JSONObject ? (JSONObject) item : null;
            String key = actionConfig != null ? actionConfig.getString("key") : String.valueOf(item);
            if (!StringUtils.hasText(key)) {
                continue;
            }
            actions.add(TicketWorkflowActionResponse.builder()
                    .key(key)
                    .label(resolveActionLabel(key, actionConfig, data))
                    .type(actionConfig != null ? actionConfig.getString("type") : resolveActionType(key))
                    .taskId(task.getId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .danger(actionConfig != null ? actionConfig.getBoolean("danger") : null)
                    .fields(buildActionFields(actionConfig))
                    .build());
        }
        return actions;
    }

    private String resolveActionLabel(String key, JSONObject actionConfig, JSONObject nodeData) {
        if (actionConfig != null && StringUtils.hasText(actionConfig.getString("label"))) {
            return actionConfig.getString("label");
        }
        if ("COMPLETE".equals(key) && nodeData != null && StringUtils.hasText(nodeData.getString("completeActionLabel"))) {
            return nodeData.getString("completeActionLabel");
        }
        return switch (key) {
            case "CLAIM" -> "认领";
            case "ASSIGN" -> "指派";
            case "TRANSFER" -> "转派";
            case "TRANSFER_DEPARTMENT" -> "转派部门";
            case "COMPLETE" -> "完成任务";
            case "COMPLETE_VERIFIED" -> "确认解决";
            case "COMPLETE_REJECTED" -> "未解决";
            case "HOLD" -> "暂存";
            case "CLOSE" -> "关单";
            default -> key;
        };
    }

    private String resolveActionType(String key) {
        return switch (key) {
            case "CLAIM" -> "claim";
            case "ASSIGN" -> "assign";
            case "TRANSFER" -> "transfer";
            case "TRANSFER_DEPARTMENT" -> "transferDepartment";
            case "HOLD" -> "hold";
            case "CLOSE" -> "close";
            default -> "complete";
        };
    }

    private List<TicketWorkflowActionFieldResponse> buildActionFields(JSONObject actionConfig) {
        JSONArray fields = actionConfig != null ? actionConfig.getJSONArray("fields") : null;
        if (fields == null || fields.isEmpty()) {
            return List.of();
        }
        List<TicketWorkflowActionFieldResponse> responses = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            JSONObject field = fields.getJSONObject(i);
            if (field == null || !StringUtils.hasText(field.getString("name"))) {
                continue;
            }
            responses.add(TicketWorkflowActionFieldResponse.builder()
                    .name(field.getString("name"))
                    .label(field.getString("label"))
                    .component(field.getString("component"))
                    .required(field.getBoolean("required"))
                    .placeholder(field.getString("placeholder"))
                    .build());
        }
        return responses;
    }

    private TicketWorkflowRuntimeContext buildRuntimeContext(TicketEntity ticket, Task task, String actionKey) {
        JSONObject flowgramSchema = loadFlowgramSchema(ticket);
        JSONObject node = findFlowgramNode(flowgramSchema, task.getTaskDefinitionKey());
        JSONObject nodeData = node != null ? node.getJSONObject("data") : null;
        JSONObject actionConfig = findActionConfig(nodeData, actionKey);
        JSONArray availableActions = nodeData != null ? nodeData.getJSONArray("availableActions") : null;
        if (availableActions != null && !availableActions.isEmpty() && actionConfig == null) {
            throw new RuntimeException("workflow action is not configured on current task: " + actionKey);
        }
        String actionType = actionConfig != null && StringUtils.hasText(actionConfig.getString("type"))
                ? actionConfig.getString("type")
                : resolveActionType(actionKey);
        return new TicketWorkflowRuntimeContext(flowgramSchema, node, nodeData, actionConfig, actionKey, actionType);
    }

    private JSONObject findActionConfig(JSONObject nodeData, String actionKey) {
        JSONArray availableActions = nodeData != null ? nodeData.getJSONArray("availableActions") : null;
        if (availableActions == null || availableActions.isEmpty()) {
            return null;
        }
        for (int i = 0; i < availableActions.size(); i++) {
            Object item = availableActions.get(i);
            if (item instanceof JSONObject actionConfig && actionKey.equals(actionConfig.getString("key"))) {
                return actionConfig;
            }
        }
        return null;
    }

    private String configuredString(JSONObject primary, JSONObject secondary, String... names) {
        for (String name : names) {
            if (primary != null && StringUtils.hasText(primary.getString(name))) {
                return primary.getString(name);
            }
            if (secondary != null && StringUtils.hasText(secondary.getString(name))) {
                return secondary.getString(name);
            }
        }
        return null;
    }

    private String normalizeTicketStatusValue(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        try {
            return TicketStatusEnum.valueOf(status.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("unsupported configured ticket status: " + status);
        }
    }

    private String resolveConfiguredStatus(JSONObject actionConfig, JSONObject nodeData, String fallback,
            String... names) {
        String configured = configuredString(actionConfig, nodeData, names);
        return StringUtils.hasText(configured) ? normalizeTicketStatusValue(configured) : fallback;
    }

    private static record TicketWorkflowRuntimeContext(JSONObject flowgramSchema, JSONObject node, JSONObject nodeData,
            JSONObject actionConfig, String actionKey, String actionType) {
    }

    private void claimWorkflowTask(TicketEntity ticket, Task task, TicketRequest request, String operatorUid,
            TicketWorkflowRuntimeContext runtimeContext) {
        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("当前流程任务已被其他人领取");
        }
        if (isWaitClaimTask(ticket, task)) {
            completeWaitClaimTask(ticket, task, operatorUid, request, false, runtimeContext);
            return;
        }
        if (!StringUtils.hasText(task.getAssignee())) {
            taskService.claim(task.getId(), operatorUid);
        }
        Optional<MemberEntity> memberOptional = memberRestService.findByUid(operatorUid);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                    .uid(member.getUid())
                    .nickname(member.getNickname())
                    .avatar(member.getAvatar())
                    .type(UserTypeEnum.MEMBER.name())
                    .build();
            ticket.setAssignee(assigneeProtobuf.toJson());
                ticket.setStatus(resolveConfiguredStatus(runtimeContext.actionConfig(), runtimeContext.nodeData(),
                    TicketStatusEnum.CLAIMED.name(), "ticketStatus", "ticketStatusOnComplete"));

            if (StringUtils.hasText(ticket.getThreadUid())) {
                threadRestService.findByUid(ticket.getThreadUid()).ifPresent(thread -> {
                    String assigneeJson = assigneeProtobuf.toJson();
                    if (!thread.getTicketors().contains(assigneeJson)) {
                        thread.getTicketors().add(assigneeJson);
                    }
                    threadRestService.save(thread);
                    if (member.getUser() != null && StringUtils.hasText(member.getUser().getUid())) {
                        topicSubscriptionRestService.create(thread.getTopic(), member.getUser().getUid());
                    }
                });
            }
        }
        addTaskComment(task, ticket, operatorUid, "CLAIMED",
                StringUtils.hasText(request.getReason()) ? request.getReason() : "流程任务已认领");
    }

        private void assignWorkflowTask(TicketEntity ticket, Task task, TicketRequest request, String operatorUid,
            boolean transfer, TicketWorkflowRuntimeContext runtimeContext) {
        Assert.hasText(request.getTargetAssigneeUid(), transfer ? "转派目标处理人不能为空" : "指派目标处理人不能为空");
        if (transfer && StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务处理人，不能转派");
        }

        if (isWaitClaimTask(ticket, task)) {
            completeWaitClaimTask(ticket, task, request.getTargetAssigneeUid(), request, true, runtimeContext);
            return;
        }

        MemberEntity targetMember = memberRestService.findByUid(request.getTargetAssigneeUid())
                .orElseThrow(() -> new RuntimeException("目标处理人不存在: " + request.getTargetAssigneeUid()));
        UserProtobuf targetAssignee = UserProtobuf.builder()
                .uid(targetMember.getUid())
                .nickname(targetMember.getNickname())
                .avatar(targetMember.getAvatar())
                .type(UserTypeEnum.MEMBER.name())
                .build();

        taskService.setAssignee(task.getId(), targetMember.getUid());
        ticket.setAssignee(targetAssignee.toJson());
        ticket.setStatus(resolveConfiguredStatus(runtimeContext.actionConfig(), runtimeContext.nodeData(),
            transfer ? TicketStatusEnum.TRANSFERRED.name() : TicketStatusEnum.ASSIGNED.name(),
            "ticketStatus", "ticketStatusOnComplete"));

        Map<String, Object> variables = new HashMap<>();
        variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssigneeString());
        variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
        runtimeService.setVariables(ticket.getProcessInstanceId(), variables);

        syncTicketThreadAssignee(ticket, targetAssignee, targetMember);
        addTaskComment(task, ticket, operatorUid, transfer ? "TRANSFERRED" : "ASSIGNED",
                (transfer ? "工单被转派给 " : "工单被指派给 ")
                        + (StringUtils.hasText(targetMember.getNickname()) ? targetMember.getNickname() : targetMember.getUid())
                        + buildCommentSuffix(request));
    }

        private void completeWaitClaimTask(TicketEntity ticket, Task waitClaimTask, String targetAssigneeUid,
            TicketRequest request, boolean assignedByOther, TicketWorkflowRuntimeContext runtimeContext) {
        MemberEntity targetMember = memberRestService.findByUid(targetAssigneeUid)
                .orElseThrow(() -> new RuntimeException("目标处理人不存在: " + targetAssigneeUid));
        UserProtobuf targetAssignee = UserProtobuf.builder()
                .uid(targetMember.getUid())
                .nickname(targetMember.getNickname())
                .avatar(targetMember.getAvatar())
                .type(UserTypeEnum.MEMBER.name())
                .build();

        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : request.getAssigneeUid();
        if (!StringUtils.hasText(waitClaimTask.getAssignee())) {
            taskService.claim(waitClaimTask.getId(), targetMember.getUid());
        } else if (!Objects.equals(waitClaimTask.getAssignee(), targetMember.getUid())) {
            taskService.setAssignee(waitClaimTask.getId(), targetMember.getUid());
        }

        ticket.setAssignee(targetAssignee.toJson());
        ticket.setStatus(resolveConfiguredStatus(runtimeContext.actionConfig(), runtimeContext.nodeData(),
            TicketStatusEnum.PROCESSING.name(), "ticketStatus", "ticketStatusOnComplete"));
        syncTicketThreadAssignee(ticket, targetAssignee, targetMember);

        Map<String, Object> variables = new HashMap<>();
        variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssigneeString());
        variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, targetMember.getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
        variables.put(TicketConsts.TICKET_VARIABLE_CLAIM_TIME, new Date());
        addTaskComment(waitClaimTask, ticket, StringUtils.hasText(operatorUid) ? operatorUid : targetMember.getUid(),
                assignedByOther ? "ASSIGNED" : "CLAIMED",
                (assignedByOther ? "工单被指派给 " : "工单被认领，进入处理：")
                        + (StringUtils.hasText(targetMember.getNickname()) ? targetMember.getNickname() : targetMember.getUid())
                        + buildCommentSuffix(request));
        taskService.complete(waitClaimTask.getId(), variables);

        String nextTaskStage = configuredString(runtimeContext.actionConfig(), runtimeContext.nodeData(), "nextTaskStage");
        Task processTask = findNextTaskByStage(ticket, waitClaimTask,
            StringUtils.hasText(nextTaskStage) ? nextTaskStage : "PROCESSING");
        if (processTask != null && !Objects.equals(processTask.getAssignee(), targetMember.getUid())) {
            taskService.setAssignee(processTask.getId(), targetMember.getUid());
        }
    }

    private boolean isWaitClaimTask(TicketEntity ticket, Task task) {
        JSONObject node = findFlowgramNode(loadFlowgramSchema(ticket), task.getTaskDefinitionKey());
        JSONObject data = node != null ? node.getJSONObject("data") : null;
        String ticketStage = data != null ? data.getString("ticketStage") : null;
        return "WAIT_CLAIM".equals(ticketStage)
                || TicketConsts.TICKET_USER_TASK_WAIT_CLAIM.equals(task.getTaskDefinitionKey());
    }

    private Task findNextTaskByStage(TicketEntity ticket, Task completedTask, String ticketStage) {
        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .active()
                .list();
        if (activeTasks == null || activeTasks.isEmpty()) {
            return null;
        }

        JSONObject flowgramSchema = loadFlowgramSchema(ticket);
        JSONObject completedNode = findFlowgramNode(flowgramSchema, completedTask.getTaskDefinitionKey());
        JSONObject completedData = completedNode != null ? completedNode.getJSONObject("data") : null;
        String configuredNextStage = completedData != null ? completedData.getString("nextTaskStage") : null;
        String expectedStage = StringUtils.hasText(configuredNextStage) ? configuredNextStage : ticketStage;

        for (Task activeTask : activeTasks) {
            JSONObject node = findFlowgramNode(flowgramSchema, activeTask.getTaskDefinitionKey());
            JSONObject data = node != null ? node.getJSONObject("data") : null;
            if (data != null && expectedStage.equals(data.getString("ticketStage"))) {
                return activeTask;
            }
        }

        if (activeTasks.size() == 1) {
            return activeTasks.get(0);
        }
        return activeTasks.stream()
                .filter(activeTask -> TicketConsts.TICKET_USER_TASK_PROCESS_TICKET.equals(activeTask.getTaskDefinitionKey()))
                .findFirst()
                .orElse(null);
    }

        private void transferWorkflowTaskToDepartment(TicketEntity ticket, Task task, TicketRequest request, String operatorUid,
            TicketWorkflowRuntimeContext runtimeContext) {
        Assert.hasText(request.getTargetDepartmentUid(), "目标部门不能为空");
        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务处理人，不能转派部门");
        }

        ticket.setDepartmentUid(request.getTargetDepartmentUid());
        ticket.setStatus(resolveConfiguredStatus(runtimeContext.actionConfig(), runtimeContext.nodeData(),
                TicketStatusEnum.TRANSFERRED.name(), "ticketStatus", "ticketStatusOnComplete"));
        if (StringUtils.hasText(request.getTargetAssigneeUid())) {
            assignWorkflowTask(ticket, task, request, operatorUid, true, runtimeContext);
        } else {
            taskService.unclaim(task.getId());
            ticket.setAssignee(null);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put(TicketConsts.TICKET_VARIABLE_DEPARTMENT_UID, ticket.getDepartmentUid());
        variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
        runtimeService.setVariables(ticket.getProcessInstanceId(), variables);

        addTaskComment(task, ticket, operatorUid, "TRANSFERRED_DEPARTMENT",
                "工单被转派到部门 " + request.getTargetDepartmentUid() + buildCommentSuffix(request));
    }

        private void holdWorkflowTask(TicketEntity ticket, Task task, TicketRequest request, String operatorUid,
            TicketWorkflowRuntimeContext runtimeContext) {
        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务处理人，不能暂存");
        }
        ticket.setStatus(resolveConfiguredStatus(runtimeContext.actionConfig(), runtimeContext.nodeData(),
            TicketStatusEnum.HOLDING.name(), "ticketStatus", "ticketStatusOnComplete"));
        Map<String, Object> variables = new HashMap<>();
        variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
        variables.put("holdTime", new Date());
        variables.put("holdBy", operatorUid);
        if (StringUtils.hasText(request.getProcessComment())) {
            variables.put("processComment", request.getProcessComment());
        }
        runtimeService.setVariables(ticket.getProcessInstanceId(), variables);
        addTaskComment(task, ticket, operatorUid, "HOLDING", "工单已暂存" + buildCommentSuffix(request));
    }

        private void closeWorkflowTask(TicketEntity ticket, Task task, TicketRequest request, String operatorUid,
            TicketWorkflowRuntimeContext runtimeContext) {
        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务处理人，不能关单");
        }
        Map<String, Object> variables = new HashMap<>();
        String targetStatus = resolveConfiguredStatus(runtimeContext.actionConfig(), runtimeContext.nodeData(),
            TicketStatusEnum.CLOSED.name(), "ticketStatus", "ticketStatusOnComplete");
        variables.put(TicketConsts.TICKET_VARIABLE_STATUS, targetStatus);
        variables.put("closedBy", operatorUid);
        variables.put("closedTime", new Date());
        if (StringUtils.hasText(request.getProcessComment())) {
            variables.put("processComment", request.getProcessComment());
        }
        addTaskComment(task, ticket, operatorUid, "CLOSED", "工单已关单" + buildCommentSuffix(request));
        taskService.complete(task.getId(), variables);

        ticket.setStatus(targetStatus);
        ticket.setClosedTime(BdDateUtils.now());
    }

    private void syncTicketThreadAssignee(TicketEntity ticket, UserProtobuf assignee, MemberEntity member) {
        if (!StringUtils.hasText(ticket.getThreadUid())) {
            return;
        }
        threadRestService.findByUid(ticket.getThreadUid()).ifPresent(thread -> {
            String assigneeJson = assignee.toJson();
            if (!thread.getTicketors().contains(assigneeJson)) {
                thread.getTicketors().add(assigneeJson);
            }
            threadRestService.save(thread);
            if (member.getUser() != null && StringUtils.hasText(member.getUser().getUid())) {
                topicSubscriptionRestService.create(thread.getTopic(), member.getUser().getUid());
            }
        });
    }

    private String buildCommentSuffix(TicketRequest request) {
        if (StringUtils.hasText(request.getProcessComment())) {
            return "，处理意见：" + request.getProcessComment();
        }
        if (StringUtils.hasText(request.getReason())) {
            return "，原因：" + request.getReason();
        }
        return "";
    }

        private void completeWorkflowTask(TicketEntity ticket, Task task, TicketRequest request, String operatorUid,
            TicketWorkflowRuntimeContext runtimeContext) {
        if (!StringUtils.hasText(task.getAssignee())) {
            taskService.claim(task.getId(), operatorUid);
        } else if (!Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前流程任务办理人，不能完成任务");
        }

        Map<String, Object> variables = new HashMap<>();
        if (request.getVariables() != null) {
            variables.putAll(request.getVariables());
        }
        if (StringUtils.hasText(request.getProcessComment())) {
            variables.put("processComment", request.getProcessComment());
        }
        JSONObject actionConfig = runtimeContext.actionConfig();
        JSONObject nodeData = runtimeContext.nodeData();
        String decisionVariable = actionConfig != null && StringUtils.hasText(actionConfig.getString("decisionVariable"))
                ? actionConfig.getString("decisionVariable")
                : (nodeData != null ? nodeData.getString("decisionVariable") : null);
        if (StringUtils.hasText(decisionVariable) && actionConfig != null && actionConfig.containsKey("decisionValue")) {
            Object decisionValue = actionConfig.get("decisionValue");
            variables.put(decisionVariable, decisionValue);
            if (TicketConsts.TICKET_VARIABLE_VERIFIED.equals(decisionVariable) && decisionValue instanceof Boolean verifiedValue) {
                ticket.setVerified(verifiedValue);
            }
        } else if ("COMPLETE_VERIFIED".equals(runtimeContext.actionKey())) {
            variables.put("verified", true);
            ticket.setVerified(true);
        } else if ("COMPLETE_REJECTED".equals(runtimeContext.actionKey())) {
            variables.put("verified", false);
            ticket.setVerified(false);
        }
        addTaskComment(task, ticket, operatorUid, runtimeContext.actionKey(),
            StringUtils.hasText(request.getProcessComment()) ? request.getProcessComment()
                : (StringUtils.hasText(request.getReason()) ? request.getReason() : "流程任务已完成"));
        taskService.complete(task.getId(), variables);
        syncTicketStatusAfterWorkflowComplete(ticket, runtimeContext);
    }

        private void syncTicketStatusAfterWorkflowComplete(TicketEntity ticket, TicketWorkflowRuntimeContext runtimeContext) {
            String configuredCompleteStatus = resolveConfiguredStatus(runtimeContext.actionConfig(), runtimeContext.nodeData(),
                    null, "ticketStatus", "ticketStatusOnComplete");
            if (StringUtils.hasText(configuredCompleteStatus)) {
                ticket.setStatus(configuredCompleteStatus);
                if (TicketStatusEnum.RESOLVED.name().equals(configuredCompleteStatus)) {
                    ticket.setResolvedTime(BdDateUtils.now());
                }
                if (TicketStatusEnum.CLOSED.name().equals(configuredCompleteStatus)
                        || TicketStatusEnum.VERIFIED_OK.name().equals(configuredCompleteStatus)) {
                    ticket.setClosedTime(BdDateUtils.now());
                }
                if (TicketStatusEnum.REOPENED.name().equals(configuredCompleteStatus)) {
                    ticket.setResolvedTime(null);
                }
                return;
            }

            if ("COMPLETE_REJECTED".equals(runtimeContext.actionKey())) {
                ticket.setStatus(TicketStatusEnum.REOPENED.name());
                ticket.setResolvedTime(null);
                return;
            }

            List<Task> nextTasks = taskService.createTaskQuery()
                    .processInstanceId(ticket.getProcessInstanceId())
                    .active()
                    .list();
            if (nextTasks == null || nextTasks.isEmpty()) {
                ticket.setStatus("COMPLETE_VERIFIED".equals(runtimeContext.actionKey()) ? TicketStatusEnum.VERIFIED_OK.name()
                        : TicketStatusEnum.CLOSED.name());
                ticket.setClosedTime(BdDateUtils.now());
                return;
            }

            JSONObject flowgramSchema = runtimeContext.flowgramSchema();
            for (Task nextTask : nextTasks) {
                JSONObject nextNode = findFlowgramNode(flowgramSchema, nextTask.getTaskDefinitionKey());
                JSONObject nextData = nextNode != null ? nextNode.getJSONObject("data") : null;
                String nextStatus = resolveConfiguredStatus(null, nextData, null, "ticketStatusOnEnter", "ticketStatus");
                if (StringUtils.hasText(nextStatus)) {
                    ticket.setStatus(nextStatus);
                    if (TicketStatusEnum.RESOLVED.name().equals(nextStatus)) {
                        ticket.setResolvedTime(BdDateUtils.now());
                    }
                    return;
                }
            }

            String reporterUid = ticket.getReporter() != null ? ticket.getReporter().getUid() : null;
            boolean waitingReporter = nextTasks.stream()
                    .anyMatch(task -> StringUtils.hasText(reporterUid) && Objects.equals(task.getAssignee(), reporterUid));
            if (waitingReporter) {
                ticket.setStatus(TicketStatusEnum.RESOLVED.name());
                ticket.setResolvedTime(BdDateUtils.now());
            } else {
                ticket.setStatus(TicketStatusEnum.PROCESSING.name());
            }
        }

    private JSONObject loadFlowgramSchema(TicketEntity ticket) {
        if (ticket == null || !StringUtils.hasText(ticket.getProcessEntityUid())) {
            return null;
        }
        Optional<ProcessEntity> processOptional = processRepository.findByUid(ticket.getProcessEntityUid());
        if (processOptional.isEmpty() || !StringUtils.hasText(processOptional.get().getFlowgramSchema())) {
            return null;
        }
        try {
            return JSON.parseObject(processOptional.get().getFlowgramSchema());
        } catch (Exception e) {
            log.warn("parse ticket flowgramSchema failed: processEntityUid={}", ticket.getProcessEntityUid(), e);
            return null;
        }
    }

    private JSONObject findFlowgramNode(JSONObject schema, String nodeId) {
        if (schema == null || !StringUtils.hasText(nodeId)) {
            return null;
        }
        JSONArray nodes = schema.getJSONArray("nodes");
        if (nodes == null) {
            return null;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (nodeId.equals(node.getString("id"))) {
                return node;
            }
        }
        return null;
    }

    private boolean hasVerifiedBranches(JSONArray conditions) {
        boolean hasTrue = false;
        boolean hasFalse = false;
        for (int i = 0; i < conditions.size(); i++) {
            JSONObject condition = conditions.getJSONObject(i);
            String expression = condition.getString("expression");
            hasTrue = hasTrue || (StringUtils.hasText(expression) && expression.contains("verified")
                    && expression.contains("true"));
            hasFalse = hasFalse || (StringUtils.hasText(expression) && expression.contains("verified")
                    && expression.contains("false"));
        }
        return hasTrue && hasFalse;
    }

    private boolean hasVerifiedGatewayAfterNode(JSONObject schema, String nodeId) {
        if (schema == null || !StringUtils.hasText(nodeId)) {
            return false;
        }
        JSONArray edges = schema.getJSONArray("edges");
        if (edges == null) {
            return false;
        }
        for (int i = 0; i < edges.size(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            if (!nodeId.equals(edge.getString("sourceNodeId"))) {
                continue;
            }
            JSONObject target = findFlowgramNode(schema, edge.getString("targetNodeId"));
            if (target == null || target.getJSONObject("data") == null) {
                continue;
            }
            JSONArray conditions = target.getJSONObject("data").getJSONArray("conditions");
            if (conditions != null && hasVerifiedBranches(conditions)) {
                return true;
            }
        }
        return false;
    }

    private void persistAndNotifyStatusChange(TicketEntity ticket, String previousStatus) {
        ticketRestService.save(ticket);
        if (!Objects.equals(previousStatus, ticket.getStatus())) {
            ticketNotificationService.notifyTicketStatusChanged(ticket, previousStatus, ticket.getStatus());
        }
    }

    /**
     * 认领工单
     * NEW -&gt; CLAIMED (认领)
     */
    @Transactional
    public TicketResponse claimTicket(TicketRequest request) {
        Assert.notNull(request.getAssignee(), "处理人不能为空");
        log.info("开始认领工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");
        String assigneeName = request.getAssignee().getNickname();
        // 1. 先查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        final String status = ticket.getStatus();
        final String statusNew = TicketStatusEnum.NEW.name();
        final String statusAssigned = TicketStatusEnum.ASSIGNED.name();
        final String statusUnclaimed = TicketStatusEnum.UNCLAIMED.name();

        // 判断状态是否为NEW或ASSIGNED或退回状态，如果不是，则不能认领
        if (!statusNew.equals(status) &&
                !statusAssigned.equals(status) &&
                !statusUnclaimed.equals(status)) {
            throw new RuntimeException("已经被认领，工单状态为" + status + "，不能重复认领: " + request.getUid());
        }

        // 如果是ASSIGNED状态，判断是否为本人
        if (statusAssigned.equals(status)) {
            if (!ticket.getAssignee().getUid().equals(assigneeUid)) {
                throw new RuntimeException("工单已经被分配，非本人不能认领: " + request.getUid());
            }
        }

        // 2. 查询任务：同一流程实例可能存在多个活动任务，不能使用 singleResult()
        List<Task> candidateTasks = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId()) // 使用processInstanceId查询
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .active() // 只查询活动的任务
                .list();

        Task task = candidateTasks.stream()
                // 优先选择未认领任务；若已被本人认领，也允许继续处理
                .filter(t -> !StringUtils.hasText(t.getAssignee()) || assigneeUid.equals(t.getAssignee()))
                .findFirst()
                .orElse(null);

        if (candidateTasks.size() > 1) {
            log.warn("认领工单存在多个活动任务，已选择首个可认领任务: processInstanceId={}, taskCount={}, assigneeUid={}",
                    ticket.getProcessInstanceId(), candidateTasks.size(), assigneeUid);
        }

        log.info("查询到的任务: task={}, processInstanceId={}", task, ticket.getProcessInstanceId());

        if (task == null) {
            // 如果没有找到任务，打印更多信息以便调试
            List<Task> allTasks = taskService.createTaskQuery()
                    .processInstanceId(ticket.getProcessInstanceId())
                    .list();
            log.error("未找到可认领的任务, 当前流程实例所有任务: {}", allTasks);
            throw new RuntimeException("工单任务不存在或已被认领: " + request.getUid());
        }

        try {
            // 3. 认领任务
            taskService.claim(task.getId(), assigneeUid);
            log.info("工单认领成功: taskId={}, assigneeUid={}", task.getId(), assigneeUid);

            // 只添加任务评论，指定userId参数为assigneeUid
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    TicketStatusEnum.CLAIMED.name(),
                    "工单被 " + assigneeName + " 认领");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

        } catch (Exception e) {
            log.error("工单认领失败: ", e);
            throw new RuntimeException("工单认领失败: " + e.getMessage());
        }

        // 4. 更新工单状态
        Optional<MemberEntity> assigneeOptional = memberRestService.findByUid(assigneeUid);
        if (assigneeOptional.isPresent()) {
            MemberEntity member = assigneeOptional.get();
            // 更新assignee
            UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                    .uid(member.getUid())
                    .nickname(member.getNickname())
                    .avatar(member.getAvatar())
                    .type(UserTypeEnum.MEMBER.name())
                    .build();
            ticket.setAssignee(assigneeProtobuf.toJson());
            ticket.setStatus(TicketStatusEnum.CLAIMED.name());

            // 4. 更新流程变量
            Map<String, Object> variables = new HashMap<>();
            variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssignee());
            variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
            variables.put(TicketConsts.TICKET_VARIABLE_CLAIM_TIME, new Date());
            runtimeService.setVariables(ticket.getProcessInstanceId(), variables);

            // 5. 创建工单会话，迁移到工单创建时创建
            // serviceThreadTopic跟threadUid合并
            // if (!StringUtils.hasText(ticket.getThreadUid())) {
            // // 如果创建工单的时候没有绑定会话，则创建会话
            // ThreadEntity thread = ticketRestService.createTicketThread(ticket);
            // if (thread != null) {
            // ticket.setThreadTopic(thread.getTopic());
            // ticket.setThreadUid(thread.getUid());
            // }
            // }

            // 将claimer添加到会话中
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(ticket.getThreadUid());
            if (threadOptional.isPresent()) {
                ThreadEntity thread = threadOptional.get();
                // 添加claimer到会话中
                thread.getTicketors().add(assigneeProtobuf.toJson());
                // thread.setAgent(assigneeProtobuf.toJson());
                // 保存
                threadRestService.save(thread);
                // 添加订阅
                String userUid = member.getUser().getUid();
                topicSubscriptionRestService.create(thread.getTopic(), userUid);
            }

            // 6. 发布工单分配消息事件
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
        persistAndNotifyStatusChange(ticket, previousStatus);

        // 7. 返回工单响应
        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 开始处理工单
     * CLAIMED/REOPENED -&gt; PROCESSING (开始处理)
     */
    @Transactional
    public TicketResponse startTicket(TicketRequest request) {
        log.info("开始处理工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");
        String assigneeName = request.getAssignee().getNickname();

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态 - 修改此处以支持REOPENED状态
        if (!ticket.getStatus().equals(TicketStatusEnum.CLAIMED.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.REOPENED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能开始处理: " + request.getUid());
        }

        // 3. 判断处理人是否为本人 - 对于REOPENED状态，可能需要判断是否为原处理人
        if (!StringUtils.hasText(ticket.getAssigneeString()) &&
                !ticket.getStatus().equals(TicketStatusEnum.REOPENED.name())) {
            throw new RuntimeException("工单未被认领，不能开始处理: " + request.getUid());
        }

        if (StringUtils.hasText(ticket.getAssigneeString()) &&
                !ticket.getAssignee().getUid().equals(request.getAssignee().getUid())) {
            throw new RuntimeException("非工单处理人，不能开始处理: " + request.getUid());
        }

        // 4. 判断工单是否已开始处理
        if (ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单已开始处理，不能重复开始处理: " + request.getUid());
        }

        // 5. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 5. 添加任务评论，记录开始处理
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    TicketStatusEnum.PROCESSING.name(),
                    "工单由" + assigneeName + "开始处理");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 6. 设置任务变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("startProcessingTime", new Date());
            variables.put("processingUser", ticket.getAssigneeString());
            taskService.setVariables(task.getId(), variables);

            // 7. 更新工单状态
            ticket.setStatus(TicketStatusEnum.PROCESSING.name());
            persistAndNotifyStatusChange(ticket, previousStatus);

            log.info("工单开始处理成功: taskId={}, assigneeUid={}", task.getId(), ticket.getAssigneeString());

            return TicketConvertUtils.convertToResponse(ticket);
        } catch (Exception e) {
            log.error("工单开始处理失败: ", e);
            throw new RuntimeException("工单开始处理失败: " + e.getMessage());
        }
    }

    /**
     * 退回工单
     * CLAIMED -&gt; UNCLAIMED (退回)
     */
    @Transactional
    public TicketResponse unclaimTicket(TicketRequest request) {
        log.info("开始退回工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");
        String assigneeName = request.getAssignee().getNickname();

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 判断状态是否为已认领，如果不是，则不能退回
        if (!ticket.getStatus().equals(TicketStatusEnum.CLAIMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能退回: " + request.getUid());
        }
        if (!StringUtils.hasText(ticket.getAssigneeString())) {
            throw new RuntimeException("非已认领工单，不能退回: " + request.getUid());
        }
        // 判断认领人是否为本人，如果不是，则不能退回
        if (!ticket.getAssignee().getUid().equals(assigneeUid)) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能退回: " + request.getUid());
        }

        // 2. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .taskAssignee(assigneeUid)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        // 如果任务不存在，则返回null
        if (task == null) {
            // return null;
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        log.info("退回工单: task={}", task);

        // 退回任务
        taskService.unclaim(task.getId());

        // 只添加任务评论
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.UNCLAIMED.name(), "工单被 " + assigneeName + "退回到工作组");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 更新工单状态
        ticket.setAssignee(null);
        ticket.setStatus(TicketStatusEnum.UNCLAIMED.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        // 发布工单退回消息事件
        // eventPublisher.publishEvent(TicketMessageEvent.builder()
        // .ticketUid(ticket.getUid())
        // .processInstanceId(ticket.getProcessInstanceId())
        // .type(TicketMessageType.UNCLAIMED.name())
        // .build());

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 转派工单: TRANSFERRED
     * CLAIMED -&gt; CLAIMED (转派)
     */
    @Transactional
    public TicketResponse transferTicket(TicketRequest request) {
        log.info("开始转派工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");
        String assigneeName = request.getAssignee().getNickname();

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.CLAIMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能转派: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 转派任务
        taskService.setAssignee(task.getId(), assigneeUid);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.TRANSFERRED.name(),
                "工单被转派给 " + (StringUtils.hasText(assigneeName) ? assigneeName : assigneeUid));
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.CLAIMED.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 挂起工单
     * PROCESSING -&gt; HOLDING (挂起)
     */
    @Transactional
    public TicketResponse holdTicket(TicketRequest request) {
        log.info("开始挂起工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.RESUMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能挂起: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.HOLDING.name(), "工单被挂起");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 4. 挂起任务
        taskService.setAssignee(task.getId(), null);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.HOLDING.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 恢复工单
     * HOLDING -&gt; RESUMED (恢复)
     */
    @Transactional
    public TicketResponse resumeTicket(TicketRequest request) {
        log.info("开始恢复工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.HOLDING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能恢复: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 恢复任务
        taskService.setAssignee(task.getId(), assigneeUid);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.RESUMED.name(), "工单被恢复");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.RESUMED.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 待回应工单
     * PROCESSING -&gt; PENDING (待回应)
     */
    @Transactional
    public TicketResponse pendTicket(TicketRequest request) {
        log.info("开始待回应工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能待处理: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 待处理任务
        taskService.setAssignee(task.getId(), null);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.PENDING.name(), "工单被待回应");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.PENDING.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 重新打开工单
     * CLOSED/CANCELLED -&gt; REOPENED -&gt; PROCESSING (重新打开)
     */
    @Transactional
    public TicketResponse reopenTicket(TicketRequest request) {
        log.info("开始重新打开工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.CLOSED.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.CANCELLED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能重新打开: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 重新打开任务
        taskService.setAssignee(task.getId(), assigneeUid);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.REOPENED.name(), "工单被重新打开");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.PROCESSING.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 升级工单
     * PROCESSING -&gt; ESCALATED (升级)
     */
    @Transactional
    public TicketResponse escalateTicket(TicketRequest request) {
        log.info("开始升级工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能升级: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 4. 升级任务
            taskService.setAssignee(task.getId(), assigneeUid);

            // comment
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    TicketStatusEnum.ESCALATED.name(), "工单被升级");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 5. 更新工单状态
            ticket.setStatus(TicketStatusEnum.ESCALATED.name());
            persistAndNotifyStatusChange(ticket, previousStatus);

            return TicketConvertUtils.convertToResponse(ticket);

        } catch (Exception e) {
            log.error("工单升级失败: ", e);
            throw new RuntimeException("工单升级失败: " + e.getMessage());
        }
    }

    /**
     * 解决工单
     */
    @Transactional
    public TicketResponse resolveTicket(TicketRequest request) {
        log.info("开始解决工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.RESUMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能解决: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 4. 添加评论
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    TicketStatusEnum.RESOLVED.name(), "工单已解决");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 5. 设置流程变量，添加这段代码
            Map<String, Object> variables = new HashMap<>();
            variables.put("verified", false); // 默认设置为false，等待客户验证

            // 6. 完成任务，传入变量
            taskService.complete(task.getId(), variables);

            // 7. 更新工单状态
            ticket.setStatus(TicketStatusEnum.RESOLVED.name());
            ticket.setResolvedTime(BdDateUtils.now());
            persistAndNotifyStatusChange(ticket, previousStatus);

            return TicketConvertUtils.convertToResponse(ticket);

        } catch (Exception e) {
            log.error("工单解决失败: ", e);
            throw new RuntimeException("工单解决失败: " + e.getMessage());
        }
    }

    /**
     * 客户验证工单
     * RESOLVED -&gt; CLOSED/REOPENED (验证通过/不通过)
     */
    @Transactional
    public TicketResponse verifyTicket(TicketRequest request) {
        log.info("开始验证工单: uid={}, verified={}, orgUid={}",
                request.getUid(), request.getVerified(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.RESOLVED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能验证: " + request.getUid());
        }

        // 3. 判断验证人是否为提交人
        if (!ticket.getReporter().getUid().equals(assigneeUid)) {
            throw new RuntimeException("非工单提交人，不能验证: " + request.getUid());
        }

        // 4. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 5. 设置验证结果变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("verified", request.getVerified());

            // 6. 添加评论
            String commentType = request.getVerified() ? TicketStatusEnum.VERIFIED_OK.name()
                    : TicketStatusEnum.VERIFIED_FAIL.name();
            String commentMessage = request.getVerified() ? "客户确认工单已解决" : "客户反馈工单未解决";
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    commentType, commentMessage);
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 7. 完成任务
            taskService.complete(task.getId(), variables);

            // 8. 更新工单状态
            if (request.getVerified()) {
                ticket.setStatus(TicketStatusEnum.VERIFIED_OK.name());
                ticket.setVerified(true);
                ticket.setClosedTime(BdDateUtils.now());
            } else {
                ticket.setStatus(TicketStatusEnum.REOPENED.name());
                ticket.setVerified(false);
                // 重置解决时间
                ticket.setResolvedTime(null);
            }
            persistAndNotifyStatusChange(ticket, previousStatus);

            return TicketConvertUtils.convertToResponse(ticket);

        } catch (Exception e) {
            log.error("工单验证失败: ", e);
            throw new RuntimeException("工单验证失败: " + e.getMessage());
        }
    }

    /**
     * 关闭工单
     * PROCESSING/RESUMED -&gt; CLOSED (关闭)
     */
    @Transactional
    public TicketResponse closeTicket(TicketRequest request) {
        log.info("开始关闭工单: uid={}, status={}, orgUid={}",
                request.getUid(), request.getStatus(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态 - 修改此处，允许RESUMED状态也可以关闭
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.RESUMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能关闭: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 添加评论
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.CLOSED.name(), "工单已关闭");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前处理人
        taskService.saveComment(comment);

        // 4. 关闭任务
        taskService.complete(task.getId());

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.CLOSED.name());
        ticket.setClosedTime(BdDateUtils.now()); // 添加关闭时间记录
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 取消工单
     * PROCESSING -&gt; CANCELLED (取消)
     */
    @Transactional
    public TicketResponse cancelTicket(TicketRequest request) {
        log.info("开始取消工单: uid={}, status={}, orgUid={}",
                request.getUid(), request.getStatus(), request.getOrgUid());

        //
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();
        String previousStatus = ticket.getStatus();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能取消: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // comment
        addTaskComment(task, ticket, assigneeUid, TicketStatusEnum.CANCELLED.name(), "工单已取消");

        // 4. 终止流程实例（比直接 deleteTask 更一致，避免流程实例悬挂）
        try {
            runtimeService.deleteProcessInstance(ticket.getProcessInstanceId(),
                    StringUtils.hasText(request.getReason()) ? request.getReason() : "cancel ticket");
        } catch (Exception e) {
            log.warn("终止流程实例失败，继续更新工单状态: processInstanceId={}, err={}",
                    ticket.getProcessInstanceId(), e.getMessage());
        }

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.CANCELLED.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 委托工单任务
     */
    @Transactional
    public TicketResponse delegateTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");
        Assert.hasText(request.getDelegateUid(), "被委托人uid不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        // 基础校验：只有当前任务办理人才能委托（如果任务未分配，则允许委托但建议先转办/认领）
        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务办理人，不能委托");
        }

        // Flowable 委托语义：owner=委托人, assignee=被委托人, delegationState=PENDING
        if (!StringUtils.hasText(task.getOwner())) {
            taskService.setOwner(task.getId(), operatorUid);
        }
        taskService.delegateTask(task.getId(), request.getDelegateUid());

        addTaskComment(task, ticket, operatorUid, "DELEGATED",
                "任务被委托给 " + request.getDelegateUid()
                        + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 被委托人处理完成后“解决委托”，任务回到委托人
     */
    @Transactional
    public TicketResponse resolveDelegatedTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");
        Assert.hasText(request.getTaskId(), "taskId不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务办理人，不能解决委托");
        }

        taskService.resolveTask(task.getId());

        addTaskComment(task, ticket, operatorUid, "DELEGATION_RESOLVED",
                "委托任务已由 " + operatorUid + " 处理并归还"
                        + (StringUtils.hasText(request.getReason()) ? ("，说明：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 抄送工单：知会相关人员（不参与流转）
     */
    @Transactional
    public TicketResponse ccTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");

        Set<String> ccUids = request.getCcUids();
        if (ccUids == null || ccUids.isEmpty()) {
            throw new RuntimeException("ccUids不能为空");
        }

        TicketEntity ticket = getTicketOrThrow(request.getUid());

        // 1) 尽量将抄送人加入工单会话订阅，便于接收通知/查看会话
        if (StringUtils.hasText(ticket.getThreadUid())) {
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(ticket.getThreadUid());
            if (threadOptional.isPresent()) {
                ThreadEntity thread = threadOptional.get();
                for (String ccUid : ccUids) {
                    if (!StringUtils.hasText(ccUid)) {
                        continue;
                    }
                    Optional<MemberEntity> memberOptional = memberRestService.findByUid(ccUid);
                    if (!memberOptional.isPresent()) {
                        continue;
                    }
                    MemberEntity member = memberOptional.get();
                    UserProtobuf ccProtobuf = UserProtobuf.builder()
                            .uid(member.getUid())
                            .nickname(member.getNickname())
                            .avatar(member.getAvatar())
                            .type(UserTypeEnum.MEMBER.name())
                            .build();
                    String ccJson = ccProtobuf.toJson();
                    if (!thread.getTicketors().contains(ccJson)) {
                        thread.getTicketors().add(ccJson);
                    }
                    // 订阅工单会话 topic，便于收到站内信/IM
                    String userUid = member.getUser() != null ? member.getUser().getUid() : null;
                    if (StringUtils.hasText(userUid)) {
                        topicSubscriptionRestService.create(thread.getTopic(), userUid);
                    }
                }
                threadRestService.save(thread);
            }
        }

        // 2) 记录到流程评论
        try {
            Task task = getActiveTaskOrThrow(ticket, request);
            addTaskComment(task, ticket, operatorUid, "CC",
                    "工单抄送给：" + String.join(",", ccUids)
                            + (StringUtils.hasText(request.getReason()) ? ("，说明：" + request.getReason()) : ""));
        } catch (Exception e) {
            log.debug("抄送记录评论失败（可能无活动任务）: {}", e.getMessage());
        }

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 加签：最小实现为给当前任务追加候选人
     */
    @Transactional
    public TicketResponse addSignTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");

        Set<String> addSignUids = request.getAddSignUids();
        if (addSignUids == null || addSignUids.isEmpty()) {
            throw new RuntimeException("addSignUids不能为空");
        }

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        for (String uid : addSignUids) {
            if (!StringUtils.hasText(uid)) {
                continue;
            }
            taskService.addCandidateUser(task.getId(), uid);
        }

        addTaskComment(task, ticket, operatorUid, "ADDSIGN",
                "任务加签候选人：" + String.join(",", addSignUids)
                        + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 退回：跳转回指定节点（需要 BPMN 节点 activityId）
     */
    @Transactional
    public TicketResponse rollbackTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");
        Assert.hasText(request.getRollbackToActivityId(), "rollbackToActivityId不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        String fromActivityId = StringUtils.hasText(request.getRollbackFromActivityId())
                ? request.getRollbackFromActivityId()
                : task.getTaskDefinitionKey();

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(ticket.getProcessInstanceId())
                .moveActivityIdTo(fromActivityId, request.getRollbackToActivityId())
                .changeState();

        addTaskComment(task, ticket, operatorUid, "ROLLBACK",
                "流程退回：" + fromActivityId + " -> " + request.getRollbackToActivityId()
                        + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 撤销：终止流程实例（通常用于发起人撤回/管理员撤销）
     */
    @Transactional
    public TicketResponse revokeTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        String previousStatus = ticket.getStatus();

        // 尽量先给当前活动任务写评论（删除实例后就无法再写 task comment）
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .active()
                .list();
        if (tasks != null) {
            for (Task t : tasks) {
                try {
                    addTaskComment(t, ticket, operatorUid, "REVOKED",
                            "流程已撤销" + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));
                } catch (Exception ex) {
                    log.debug("写撤销评论失败: {}", ex.getMessage());
                }
            }
        }

        runtimeService.deleteProcessInstance(ticket.getProcessInstanceId(),
                StringUtils.hasText(request.getReason()) ? request.getReason() : "revoke ticket");

        // 工单侧统一落到 CANCELLED（前端已有该状态）
        ticket.setStatus(TicketStatusEnum.CANCELLED.name());
        persistAndNotifyStatusChange(ticket, previousStatus);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 查询某个工单实例的处理历史
     */
    public List<TicketHistoryProcessResponse> queryTicketProcessHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                // 根据uid查询processInstanceId
                Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
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
    public List<TicketHistoryTaskResponse> queryTicketTaskHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                // 根据uid查询processInstanceId
                Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
                if (ticketOptional.isPresent()) {
                    request.setProcessInstanceId(ticketOptional.get().getProcessInstanceId());
                }
            } else {
                throw new RuntimeException("processInstanceId不能为空");
            }
        }

        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .includeTaskLocalVariables() // 包含任务局部变量
                .includeProcessVariables() // 包含流程变量
                .orderByHistoricTaskInstanceStartTime().asc()
                .list();

        List<TicketHistoryTaskResponse> responses = historicTasks.stream()
                .map(historicTask -> {
                    return TicketHistoryTaskResponse.builder()
                            .taskId(historicTask.getId())
                            .taskName(historicTask.getName())
                            .taskDefinitionKey(historicTask.getTaskDefinitionKey())
                            .taskDefinitionId(historicTask.getTaskDefinitionId())
                            .description(historicTask.getDescription())
                            .category(historicTask.getCategory())
                            .formKey(historicTask.getFormKey())
                            .processInstanceId(historicTask.getProcessInstanceId())
                            .processDefinitionId(historicTask.getProcessDefinitionId())
                            .executionId(historicTask.getExecutionId())

                            .assignee(historicTask.getAssignee())
                            .owner(historicTask.getOwner())
                            // 注意：历史任务可能无法直接获取候选人/组信息

                            .priority(historicTask.getPriority())
                            .createTime(historicTask.getCreateTime())
                            .dueDate(historicTask.getDueDate())
                            .claimTime(historicTask.getClaimTime())
                            .endTime(historicTask.getEndTime())
                            .durationInMillis(historicTask.getDurationInMillis())
                            .deleteReason(historicTask.getDeleteReason())
                            .tenantId(historicTask.getTenantId())

                            .taskLocalVariables(historicTask.getTaskLocalVariables())
                            .processVariables(historicTask.getProcessVariables())
                            .build();
                })
                .toList();
        return responses;
    }

    /**
     * 查询工单的完整活动历史
     */
    public List<TicketHistoryActivityResponse> queryTicketActivityHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
                if (ticketOptional.isPresent()) {
                    request.setProcessInstanceId(ticketOptional.get().getProcessInstanceId());
                }
            } else {
                throw new RuntimeException("processInstanceId不能为空");
            }
        }

        // 获取活动历史，过滤掉 sequenceFlow
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().asc()
                .list()
                .stream()
                .filter(activity -> !"sequenceFlow".equals(activity.getActivityType()))
                .collect(Collectors.toList());

        // 获取任务评论
        List<Comment> comments = taskService.getProcessInstanceComments(request.getProcessInstanceId());

        // 合并活动和评论信息
        List<TicketHistoryActivityResponse> responses = new ArrayList<>();

        // 添加活动历史，只保留关键信息
        responses.addAll(activities.stream()
                .map(activity -> TicketHistoryActivityResponse.builder()
                        .id(activity.getId())
                        .activityName(activity.getActivityName())
                        .activityType(activity.getActivityType())
                        .assignee(activity.getAssignee())
                        .startTime(activity.getStartTime())
                        .endTime(activity.getEndTime())
                        .durationInMillis(activity.getDurationInMillis())
                        .build())
                .collect(Collectors.toList()));

        // 添加评论历史
        responses.addAll(comments.stream()
                .map(comment -> TicketHistoryActivityResponse.builder()
                        .id(comment.getId())
                        .activityType("comment")
                        .activityName(comment.getType())
                        .description(comment.getFullMessage())
                        .startTime(comment.getTime())
                        .assignee(comment.getUserId())
                        .build())
                .collect(Collectors.toList()));

        // 按时间排序
        return responses.stream()
                .sorted(Comparator.comparing(TicketHistoryActivityResponse::getStartTime))
                .collect(Collectors.toList());
    }

}
