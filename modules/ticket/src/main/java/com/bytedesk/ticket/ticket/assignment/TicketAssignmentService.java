/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-22
 * @Description: Auto-assignment service for tickets based on workflow configuration.
 *   Resolves the first task node's assignee from flowgramSchema JSON,
 *   falling back to TicketBasicSettings.assignmentMode strategy.
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket.assignment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_basic.TicketAssignmentModeEnum;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketAssignmentService {

    private final ProcessRepository processRepository;
    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final TicketSettingsRepository ticketSettingsRepository;
    private final TaskService taskService;

    private static final Random RANDOM = new Random();

    /**
     * Auto-assign ticket assignee based on workflow configuration.
     * Called after Flowable process instance is started and "createTicket" task is completed.
     *
     * @param ticket           the ticket entity (already saved with processInstanceId)
     * @param processInstanceId the Flowable process instance ID
     */
    public void autoAssign(TicketEntity ticket, String processInstanceId) {
        // Only auto-assign if ticket doesn't already have an assignee
        if (StringUtils.hasText(ticket.getAssigneeString()) && ticket.getAssignee() != null
                && StringUtils.hasText(ticket.getAssignee().getUid())) {
            log.debug("autoAssign: ticket {} already has assignee, skipping", ticket.getUid());
            return;
        }

        // 1. Query the active wait-claim/process task
        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();

        if (activeTasks == null || activeTasks.isEmpty()) {
            log.debug("autoAssign: no active tasks for processInstanceId={}, ticketUid={}",
                    processInstanceId, ticket.getUid());
            return;
        }

        Task activeTask = activeTasks.get(0);

        // 2. Try explicit assignment from workflow node configuration
        String assigneeUid = resolveFromWorkflowNode(ticket, activeTask.getTaskDefinitionKey());

        // 3. Fallback to assignment strategy
        if (!StringUtils.hasText(assigneeUid)) {
            assigneeUid = resolveByStrategy(ticket);
        }

        // 4. Apply assignment
        if (StringUtils.hasText(assigneeUid)) {
            applyAssignment(ticket, activeTask, assigneeUid);
        } else {
            log.info("autoAssign: could not determine assignee for ticket={}, leaving as NEW", ticket.getUid());
        }
    }

    /**
     * Try to resolve assignee from the first task node in the workflow's flowgramSchema.
     */
    private String resolveFromWorkflowNode(TicketEntity ticket, String taskDefinitionKey) {
        String processEntityUid = ticket.getProcessEntityUid();
        if (!StringUtils.hasText(processEntityUid)) {
            return null;
        }

        Optional<ProcessEntity> processOpt = processRepository.findByUid(processEntityUid);
        if (processOpt.isEmpty()) {
            return null;
        }

        ProcessEntity process = processOpt.get();
        String flowgramSchema = process.getFlowgramSchema();
        if (!StringUtils.hasText(flowgramSchema)) {
            return null;
        }

        JSONObject firstTask = parseTaskNode(flowgramSchema, taskDefinitionKey);
        if (firstTask == null) {
            return null;
        }

        JSONObject data = firstTask.getJSONObject("data");
        if (data == null) {
            return null;
        }

        String assigneeType = data.getString("assigneeType");
        JSONArray assigneeUids = data.getJSONArray("assigneeUids");

        if (!StringUtils.hasText(assigneeType)) {
            return null;
        }

        return switch (assigneeType) {
            case "user" -> resolveSpecificUser(assigneeUids);
            case "department" -> resolveDepartmentMember(ticket.getDepartmentUid());
            case "role" -> null; // TODO: role-based assignment
            case "leader" -> null; // TODO: reporter's leader
            case "reporter" -> ticket.getReporter() != null ? ticket.getReporter().getUid() : null;
            default -> null;
        };
    }

    /**
     * Parse the flowgramSchema JSON to find the first non-start, non-end task node.
     * Traverses from "start" node following edges to find the first actionable node.
     */
    private JSONObject parseTaskNode(String flowgramSchema, String taskDefinitionKey) {
        try {
            JSONObject schema = JSON.parseObject(flowgramSchema);
            JSONArray nodes = schema.getJSONArray("nodes");
            JSONArray edges = schema.getJSONArray("edges");

            if (nodes == null || nodes.isEmpty()) {
                return null;
            }

            if (StringUtils.hasText(taskDefinitionKey)) {
                for (int i = 0; i < nodes.size(); i++) {
                    JSONObject node = nodes.getJSONObject(i);
                    if (taskDefinitionKey.equals(node.getString("id"))) {
                        return node;
                    }
                }
            }

            // Find start node
            JSONObject startNode = null;
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                if ("start".equals(node.getString("type"))) {
                    startNode = node;
                    break;
                }
            }

            if (startNode == null) {
                // No start node — pick the first non-start, non-end task node
                for (int i = 0; i < nodes.size(); i++) {
                    JSONObject node = nodes.getJSONObject(i);
                    String type = node.getString("type");
                    if (!"start".equals(type) && !"end".equals(type) && isTaskNode(type)) {
                        return node;
                    }
                }
                return null;
            }

            // Follow edges from start to find first task node
            String startId = startNode.getString("id");
            return findFirstTaskFromNode(startId, nodes, edges);
        } catch (Exception e) {
            log.warn("parseTaskNode: failed to parse flowgramSchema", e);
            return null;
        }
    }

    private JSONObject findFirstTaskFromNode(String sourceId, JSONArray nodes, JSONArray edges) {
        if (edges == null) {
            return null;
        }

        String targetId = null;
        for (int i = 0; i < edges.size(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            if (sourceId.equals(edge.getString("sourceNodeId"))) {
                targetId = edge.getString("targetNodeId");
                break;
            }
        }

        if (targetId == null) {
            return null;
        }

        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (targetId.equals(node.getString("id"))) {
                String type = node.getString("type");
                if (isTaskNode(type)) {
                    return node;
                }
                // Continue traversing (e.g., skip "condition" or "parallel" gateways)
                if ("condition".equals(type) || "parallel".equals(type)) {
                    return findFirstTaskFromNode(targetId, nodes, edges);
                }
                return null;
            }
        }

        return null;
    }

    private boolean isTaskNode(String type) {
        return "approval".equals(type) || "countersign".equals(type) || "orSign".equals(type)
                || "notification".equals(type);
    }

    /**
     * Pick the first valid user from the assignee UIDs.
     */
    private String resolveSpecificUser(JSONArray assigneeUids) {
        if (assigneeUids == null || assigneeUids.isEmpty()) {
            return null;
        }
        for (int i = 0; i < assigneeUids.size(); i++) {
            String uid = assigneeUids.getString(i);
            if (StringUtils.hasText(uid)) {
                Optional<MemberEntity> memberOpt = memberRepository.findByUid(uid);
                if (memberOpt.isPresent()) {
                    return uid;
                }
            }
        }
        return null;
    }

    /**
     * Pick a member from the given department.
     */
    private String resolveDepartmentMember(String departmentUid) {
        if (!StringUtils.hasText(departmentUid)) {
            return null;
        }
        List<MemberEntity> members = memberRepository.findByDeptUidAndDeletedFalse(departmentUid);
        if (members.isEmpty()) {
            return null;
        }
        return members.get(0).getUid();
    }

    /**
     * Fallback: use TicketBasicSettings.assignmentMode strategy.
     */
    private String resolveByStrategy(TicketEntity ticket) {
        String assignmentMode = getAssignmentMode(ticket);
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.fromValue(assignmentMode);

        if (mode == TicketAssignmentModeEnum.MANUAL) {
            return null; // Manual mode — no auto-assignment
        }

        List<MemberEntity> candidates = getCandidates(ticket);
        if (candidates.isEmpty()) {
            log.warn("resolveByStrategy: no candidates for ticket={}, mode={}", ticket.getUid(), mode);
            return null;
        }

        return switch (mode) {
            case ROUND_ROBIN -> roundRobinAssign(ticket, candidates);
            case LEAST_ACTIVE -> leastActiveAssign(candidates);
            case RANDOM -> randomAssign(candidates);
            default -> null; // Other modes not implemented yet
        };
    }

    private String getAssignmentMode(TicketEntity ticket) {
        String settingsUid = ticket.getTicketSettingsUid();
        if (StringUtils.hasText(settingsUid)) {
            Optional<TicketSettingsEntity> settingsOpt = ticketSettingsRepository.findByUid(settingsUid);
            if (settingsOpt.isPresent()) {
                TicketBasicSettingsEntity basic = settingsOpt.get().getBasicSettings();
                if (basic != null && StringUtils.hasText(basic.getAssignmentMode())) {
                    return basic.getAssignmentMode();
                }
            }
        }
        return TicketAssignmentModeEnum.MANUAL.name();
    }

    /**
     * Get candidate members for assignment based on ticket's department/workgroup.
     */
    private List<MemberEntity> getCandidates(TicketEntity ticket) {
        if (StringUtils.hasText(ticket.getDepartmentUid())) {
            return memberRepository.findByDeptUidAndDeletedFalse(ticket.getDepartmentUid());
        }
        // Fallback: all org members (limited)
        // Could also use workgroupUid
        return List.of();
    }

    /**
     * Round-robin: pick the next member based on last assigned ticket in the department.
     */
    private String roundRobinAssign(TicketEntity ticket, List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        // Find the most recently assigned ticket in the same department
        TicketEntity lastAssigned = ticketRepository
                .findFirstByDepartmentUidAndAssigneeNotAndAssigneeNotOrderByCreatedAtDesc(
                        ticket.getDepartmentUid(),
                        BytedeskConsts.EMPTY_JSON_STRING,
                        BytedeskConsts.EMPTY_JSON_STRING)
                .orElse(null);

        if (lastAssigned == null || lastAssigned.getAssignee() == null
                || !StringUtils.hasText(lastAssigned.getAssignee().getUid())) {
            return candidates.get(0).getUid();
        }

        String lastAssigneeUid = lastAssigned.getAssignee().getUid();
        for (int i = 0; i < candidates.size(); i++) {
            if (lastAssigneeUid.equals(candidates.get(i).getUid())) {
                int nextIndex = (i + 1) % candidates.size();
                return candidates.get(nextIndex).getUid();
            }
        }
        return candidates.get(0).getUid();
    }

    /**
     * Least-active: pick the member with the fewest active (non-closed) tickets.
     */
    private String leastActiveAssign(List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        String bestUid = null;
        long minCount = Long.MAX_VALUE;
        String closedStatus = TicketStatusEnum.CLOSED.name();
        String cancelledStatus = TicketStatusEnum.CANCELLED.name();
        for (MemberEntity member : candidates) {
            long count = ticketRepository.countByAssigneeContainingAndStatusNotAndStatusNot(
                    member.getUid(), closedStatus, cancelledStatus);
            if (count < minCount) {
                minCount = count;
                bestUid = member.getUid();
            }
        }
        return bestUid;
    }

    /**
     * Random: pick a random member from candidates.
     */
    private String randomAssign(List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(RANDOM.nextInt(candidates.size())).getUid();
    }

    /**
     * Apply the assignment: claim/setAssignee on the Flowable task, update ticket entity.
     */
    private void applyAssignment(TicketEntity ticket, Task activeTask, String assigneeUid) {
        try {
            // Find member info for the assignee
            Optional<MemberEntity> memberOpt = memberRepository.findByUid(assigneeUid);
            if (memberOpt.isEmpty()) {
                log.warn("applyAssignment: member not found for uid={}", assigneeUid);
                return;
            }
            MemberEntity member = memberOpt.get();

            // Update ticket entity
            UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                    .uid(member.getUid())
                    .nickname(member.getNickname())
                    .avatar(member.getAvatar())
                    .type(UserTypeEnum.MEMBER.name())
                    .build();
            ticket.setAssignee(assigneeProtobuf.toJson());

            if (isWaitClaimTask(ticket, activeTask)) {
                if (!StringUtils.hasText(activeTask.getAssignee())) {
                    taskService.claim(activeTask.getId(), assigneeUid);
                } else if (!assigneeUid.equals(activeTask.getAssignee())) {
                    taskService.setAssignee(activeTask.getId(), assigneeUid);
                }
                Map<String, Object> variables = new java.util.HashMap<>();
                variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssigneeString());
                variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, assigneeUid);
                variables.put(TicketConsts.TICKET_VARIABLE_STATUS, TicketStatusEnum.PROCESSING.name());
                variables.put(TicketConsts.TICKET_VARIABLE_CLAIM_TIME, new java.util.Date());
                taskService.complete(activeTask.getId(), variables);
                Task processTask = findNextTaskByStage(ticket, activeTask.getProcessInstanceId(), "PROCESSING");
                if (processTask != null && !assigneeUid.equals(processTask.getAssignee())) {
                    taskService.setAssignee(processTask.getId(), assigneeUid);
                }
                ticket.setStatus(TicketStatusEnum.PROCESSING.name());
            } else {
                if (StringUtils.hasText(activeTask.getAssignee())) {
                    taskService.setAssignee(activeTask.getId(), assigneeUid);
                } else {
                    taskService.claim(activeTask.getId(), assigneeUid);
                }
                ticket.setStatus(TicketStatusEnum.ASSIGNED.name());
            }
            ticketRepository.save(ticket);

            log.info("autoAssign: assigned ticket={} to member={} (nickname={})",
                    ticket.getUid(), assigneeUid, member.getNickname());
        } catch (Exception e) {
            log.error("applyAssignment: failed for ticket={}, assigneeUid={}", ticket.getUid(), assigneeUid, e);
        }
    }

    private boolean isWaitClaimTask(TicketEntity ticket, Task task) {
        JSONObject node = findFlowgramNode(ticket, task.getTaskDefinitionKey());
        JSONObject data = node != null ? node.getJSONObject("data") : null;
        String ticketStage = data != null ? data.getString("ticketStage") : null;
        return "WAIT_CLAIM".equals(ticketStage)
                || TicketConsts.TICKET_USER_TASK_WAIT_CLAIM.equals(task.getTaskDefinitionKey());
    }

    private Task findNextTaskByStage(TicketEntity ticket, String processInstanceId, String ticketStage) {
        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();
        if (activeTasks == null || activeTasks.isEmpty()) {
            return null;
        }
        for (Task task : activeTasks) {
            JSONObject node = findFlowgramNode(ticket, task.getTaskDefinitionKey());
            JSONObject data = node != null ? node.getJSONObject("data") : null;
            if (data != null && ticketStage.equals(data.getString("ticketStage"))) {
                return task;
            }
        }
        if (activeTasks.size() == 1) {
            return activeTasks.get(0);
        }
        return activeTasks.stream()
                .filter(task -> TicketConsts.TICKET_USER_TASK_PROCESS_TICKET.equals(task.getTaskDefinitionKey()))
                .findFirst()
                .orElse(null);
    }

    private JSONObject findFlowgramNode(TicketEntity ticket, String taskDefinitionKey) {
        if (!StringUtils.hasText(taskDefinitionKey) || !StringUtils.hasText(ticket.getProcessEntityUid())) {
            return null;
        }
        Optional<ProcessEntity> processOpt = processRepository.findByUid(ticket.getProcessEntityUid());
        if (processOpt.isEmpty() || !StringUtils.hasText(processOpt.get().getFlowgramSchema())) {
            return null;
        }
        try {
            JSONObject schema = JSON.parseObject(processOpt.get().getFlowgramSchema());
            JSONArray nodes = schema.getJSONArray("nodes");
            if (nodes == null) {
                return null;
            }
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                if (taskDefinitionKey.equals(node.getString("id"))) {
                    return node;
                }
            }
        } catch (Exception e) {
            log.warn("findFlowgramNode: failed to parse flowgramSchema, ticketUid={}", ticket.getUid(), e);
        }
        return null;
    }

}
