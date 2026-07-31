/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-22
 * @LastEditors: Copilot
 * @LastEditTime: 2026-07-28
 * @Description: Auto-assignment service for tickets based on workflow configuration.
 *   Resolves the first task node's assignee from flowgramSchema JSON,
 *   falling back to TicketBasicSettings.assignmentMode strategy.
 *   Also handles auto-assignment on task completion for next-node resolution.
 * 
 * Copyright (c) 2025-2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket.assignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.bytedesk.ticket.service.TicketNotificationService;
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
    private final TicketAssignmentLogRepository assignmentLogRepository;
    private final TicketUserOrgRoleRepository userOrgRoleRepository;
    private final TicketNotificationService ticketNotificationService;

    private static final Random RANDOM = new Random();
    private static final String DEFAULT_ASSIGNMENT_MODE = TicketAssignmentModeEnum.DEFAULT.name();

    // ==================== Public API ====================

    /**
     * Auto-assign ticket assignee based on workflow configuration.
     * Called after Flowable process instance is started and "createTicket" task is completed.
     *
     * @param ticket            the ticket entity (already saved with processInstanceId)
     * @param processInstanceId the Flowable process instance ID
     * @return resolution result for audit/logging
     */
    public AssignmentResolutionResult autoAssign(TicketEntity ticket, String processInstanceId) {
        // Only auto-assign if ticket doesn't already have an assignee
        if (StringUtils.hasText(ticket.getAssigneeString()) && ticket.getAssignee() != null
                && StringUtils.hasText(ticket.getAssignee().getUid())) {
            log.debug("autoAssign: ticket {} already has assignee, skipping", ticket.getUid());
            return AssignmentResolutionResult.unresolved(AssignmentSource.AUTOMATIC, "already assigned");
        }

        // 1. Query the active task
        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();

        if (activeTasks == null || activeTasks.isEmpty()) {
            log.debug("autoAssign: no active tasks for processInstanceId={}, ticketUid={}",
                    processInstanceId, ticket.getUid());
            return AssignmentResolutionResult.unresolved(AssignmentSource.AUTOMATIC, "no active tasks");
        }

        Task activeTask = activeTasks.get(0);

        // 2. Try explicit assignment from workflow node configuration
        AssignmentResolutionResult result = resolveFromWorkflowNode(ticket, activeTask.getTaskDefinitionKey());

        // 3. Fallback to assignment strategy
        if (!result.isResolved()) {
            result = resolveByStrategy(ticket);
        }

        // 4. Apply assignment
        if (result.isResolved()) {
            applyAssignment(ticket, activeTask, result.assigneeUid());
            writeAssignmentLog(ticket, processInstanceId, activeTask.getTaskDefinitionKey(),
                    null, result);
        } else {
            log.info("autoAssign: could not determine assignee for ticket={}, reason={}",
                    ticket.getUid(), result.reason());
        }

        return result;
    }

    /**
     * Auto-assign for the next active task after current task completion.
     * Called when a workflow task completes and the process advances to the next node.
     *
     * @param ticket            the ticket entity
     * @param processInstanceId the Flowable process instance ID
     * @return resolution result for audit/logging
     */
    public AssignmentResolutionResult autoAssignForNextNode(TicketEntity ticket, String processInstanceId) {
        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();

        if (activeTasks == null || activeTasks.isEmpty()) {
            log.debug("autoAssignForNextNode: no active tasks for processInstanceId={}", processInstanceId);
            return AssignmentResolutionResult.unresolved(AssignmentSource.AUTOMATIC, "no active tasks after completion");
        }

        for (Task activeTask : activeTasks) {
            AssignmentResolutionResult result = resolveFromWorkflowNode(ticket, activeTask.getTaskDefinitionKey());

            if (!result.isResolved()) {
                result = resolveByStrategy(ticket);
            }

            if (result.isResolved()) {
                String previousAssignee = ticket.getAssigneeString();
                UserProtobuf currentAssignee = ticket.getAssignee();
                if (currentAssignee != null && result.assigneeUid().equals(currentAssignee.getUid())) {
                    log.debug("autoAssignForNextNode: ticket={} assignee unchanged, skipping", ticket.getUid());
                    return result;
                }

                applyAssignmentForNextNode(ticket, activeTask, result.assigneeUid());
                writeAssignmentLog(ticket, processInstanceId, activeTask.getTaskDefinitionKey(),
                        previousAssignee, result);
                log.info("autoAssignForNextNode: assigned next task for ticket={} to member={} (strategy={})",
                        ticket.getUid(), result.assigneeUid(), result.strategy());
                return result;
            }
        }

        log.info("autoAssignForNextNode: could not determine assignee for any next task, ticket={}", ticket.getUid());
        return AssignmentResolutionResult.unresolved(AssignmentSource.AUTOMATIC,
                "no resolvable next task assignee");
    }

    // ==================== Resolution Methods ====================

    /**
     * Try to resolve assignee from the workflow node configuration in flowgramSchema.
     */
    AssignmentResolutionResult resolveFromWorkflowNode(TicketEntity ticket, String taskDefinitionKey) {
        String processEntityUid = ticket.getProcessEntityUid();
        if (!StringUtils.hasText(processEntityUid)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG, "no process entity uid");
        }

        Optional<ProcessEntity> processOpt = processRepository.findByUid(processEntityUid);
        if (processOpt.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG, "process not found");
        }

        ProcessEntity process = processOpt.get();
        String flowgramSchema = process.getFlowgramSchema();
        if (!StringUtils.hasText(flowgramSchema)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG, "no flowgram schema");
        }

        JSONObject taskNode = parseTaskNode(flowgramSchema, taskDefinitionKey);
        if (taskNode == null) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "task node not found: " + taskDefinitionKey);
        }

        JSONObject data = taskNode.getJSONObject("data");
        if (data == null) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG, "no node data");
        }

        String assigneeType = data.getString("assigneeType");
        JSONArray assigneeUids = data.getJSONArray("assigneeUids");

        if (!StringUtils.hasText(assigneeType)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG, "no assigneeType configured");
        }

        String nodeAssignmentMode = data.getString("assignmentMode");

        return switch (assigneeType) {
            case "user" -> resolveSpecificUser(assigneeUids);
            case "department" -> resolveDepartmentMemberWithStrategy(ticket, nodeAssignmentMode);
            case "role" -> resolveRoleMember(ticket, data.getString("roleUid"), nodeAssignmentMode);
            case "reporter" -> resolveReporter(ticket);
            case "leader" -> resolveLeader(ticket);
            default -> AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "unsupported assigneeType: " + assigneeType);
        };
    }

    private AssignmentResolutionResult resolveSpecificUser(JSONArray assigneeUids) {
        if (assigneeUids == null || assigneeUids.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "assigneeUids is empty for type=user");
        }
        List<String> validUids = new ArrayList<>();
        for (int i = 0; i < assigneeUids.size(); i++) {
            String uid = assigneeUids.getString(i);
            if (StringUtils.hasText(uid)) {
                Optional<MemberEntity> memberOpt = memberRepository.findByUid(uid);
                if (memberOpt.isPresent()) {
                    validUids.add(uid);
                }
            }
        }
        if (validUids.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no valid members in assigneeUids");
        }
        String chosenUid = validUids.get(0);
        return AssignmentResolutionResult.resolved(chosenUid, AssignmentSource.NODE_CONFIG,
                "MANUAL", "节点指定用户: " + chosenUid,
                "精确指定 " + validUids.size() + " 人");
    }

    private AssignmentResolutionResult resolveReporter(TicketEntity ticket) {
        if (ticket.getReporter() != null && StringUtils.hasText(ticket.getReporter().getUid())) {
            String uid = ticket.getReporter().getUid();
            return AssignmentResolutionResult.resolved(uid, AssignmentSource.REPORTER,
                    "MANUAL", "上报人作为处理人", "reporter");
        }
        return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG, "no reporter");
    }

    private AssignmentResolutionResult resolveLeader(TicketEntity ticket) {
        // 1. Find the reporter's member entity
        if (ticket.getReporter() == null || !StringUtils.hasText(ticket.getReporter().getUid())) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no reporter for leader resolution");
        }
        String reporterUid = ticket.getReporter().getUid();

        Optional<MemberEntity> reporterMemberOpt = memberRepository.findByUid(reporterUid);
        if (reporterMemberOpt.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "reporter member not found: " + reporterUid);
        }

        MemberEntity reporterMember = reporterMemberOpt.get();

        // 2. Check if reporter has a direct manager
        if (StringUtils.hasText(reporterMember.getManagerMemberUid())) {
            String managerUid = reporterMember.getManagerMemberUid();
            Optional<MemberEntity> managerOpt = memberRepository.findByUid(managerUid);
            if (managerOpt.isPresent()) {
                return AssignmentResolutionResult.resolved(managerUid, AssignmentSource.NODE_CONFIG,
                        "MANUAL", "上报人直属领导", "leader");
            }
        }

        // 3. Fallback: try department-level leader (department owner)
        //    This would require DepartmentEntity to have ownerMemberUid,
        //    which is not yet implemented. For now, fall through to global strategy.
        log.debug("resolveLeader: no managerMemberUid for reporter={}, ticket={}",
                reporterUid, ticket.getUid());

        return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                "no leader configured for reporter: " + reporterUid
                        + " — add managerMemberUid to MemberEntity or configure department owner");
    }

    private AssignmentResolutionResult resolveDepartmentMemberWithStrategy(TicketEntity ticket,
                                                                            String nodeAssignmentMode) {
        String departmentUid = ticket.getDepartmentUid();
        if (!StringUtils.hasText(departmentUid)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no department uid for type=department");
        }
        List<MemberEntity> members = memberRepository.findByDeptUidAndDeletedFalse(departmentUid);
        if (members.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no members in department: " + departmentUid);
        }
        String strategy = StringUtils.hasText(nodeAssignmentMode)
            ? TicketAssignmentModeEnum.normalize(nodeAssignmentMode)
            : getAssignmentMode(ticket);
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.fromValue(strategy);
        String chosenUid = applyStrategy(mode, ticket, members);
        if (!StringUtils.hasText(chosenUid)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "strategy returned no result for department members");
        }
        return AssignmentResolutionResult.resolved(chosenUid, AssignmentSource.NODE_CONFIG,
                strategy, "部门 " + departmentUid + " 按 " + strategy + " 分配",
                "部门 " + members.size() + " 人");
    }

    private AssignmentResolutionResult resolveRoleMember(TicketEntity ticket, String roleUid,
                                                          String nodeAssignmentMode) {
        if (!StringUtils.hasText(roleUid)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no roleUid configured for type=role");
        }

        List<String> memberUids = userOrgRoleRepository.findMemberUidsByOrgUidAndRoleUid(
                ticket.getOrgUid(), roleUid);

        if (memberUids.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no members with role=" + roleUid + " in org=" + ticket.getOrgUid());
        }

        List<MemberEntity> candidates = new ArrayList<>();
        for (String uid : memberUids) {
            memberRepository.findByUid(uid).ifPresent(candidates::add);
        }

        if (candidates.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "role members not found as MemberEntity: " + roleUid);
        }

        String strategy = StringUtils.hasText(nodeAssignmentMode)
            ? TicketAssignmentModeEnum.normalize(nodeAssignmentMode)
            : getAssignmentMode(ticket);
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.fromValue(strategy);
        String chosenUid = applyStrategy(mode, ticket, candidates);
        if (!StringUtils.hasText(chosenUid)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "strategy returned no result for role members");
        }
        return AssignmentResolutionResult.resolved(chosenUid, AssignmentSource.NODE_CONFIG,
                strategy, "角色 " + roleUid + " 按 " + strategy + " 分配",
                "角色 " + candidates.size() + " 人");
    }

    // ==================== Strategy Resolution ====================

    /**
     * Fallback: use TicketBasicSettings.assignmentMode strategy.
     */
    AssignmentResolutionResult resolveByStrategy(TicketEntity ticket) {
        String assignmentMode = getAssignmentMode(ticket);
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.fromValue(assignmentMode);

        if (mode == TicketAssignmentModeEnum.MANUAL) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.GLOBAL_STRATEGY,
                    "manual mode — no auto-assignment");
        }

        List<MemberEntity> candidates = getCandidates(ticket);
        if (candidates.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.GLOBAL_STRATEGY,
                    "no candidates (department: " + ticket.getDepartmentUid() + ")");
        }

        String chosenUid = applyStrategy(mode, ticket, candidates);
        if (!StringUtils.hasText(chosenUid)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.GLOBAL_STRATEGY,
                    "strategy " + mode + " returned no result");
        }

        return AssignmentResolutionResult.resolved(chosenUid, AssignmentSource.GLOBAL_STRATEGY,
                mode.name(), "全局策略 " + mode.name() + " 分配",
                "候选 " + candidates.size() + " 人");
    }

    private String applyStrategy(TicketAssignmentModeEnum mode, TicketEntity ticket,
                                  List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        return switch (mode) {
            case ROUND_ROBIN -> roundRobinAssign(ticket, candidates);
            case LEAST_ACTIVE -> leastActiveAssign(candidates);
            case RANDOM -> randomAssign(candidates);
            case CONSISTENT_HASH -> consistentHashAssign(ticket, candidates);
            case RECENT -> recentAssign(ticket, candidates);
            default -> candidates.get(0).getUid();
        };
    }

    // ==================== Candidate Resolution ====================

    private String getAssignmentMode(TicketEntity ticket) {
        String settingsUid = ticket.getTicketSettingsUid();
        if (StringUtils.hasText(settingsUid)) {
            Optional<TicketSettingsEntity> settingsOpt = ticketSettingsRepository.findByUid(settingsUid);
            if (settingsOpt.isPresent()) {
                TicketBasicSettingsEntity basic = settingsOpt.get().getBasicSettings();
                if (basic != null && StringUtils.hasText(basic.getAssignmentMode())) {
                    return TicketAssignmentModeEnum.normalize(basic.getAssignmentMode());
                }
            }
        }
        return DEFAULT_ASSIGNMENT_MODE;
    }

    /**
     * Get candidate members for assignment based on ticket's department.
     */
    List<MemberEntity> getCandidates(TicketEntity ticket) {
        if (StringUtils.hasText(ticket.getDepartmentUid())) {
            return memberRepository.findByDeptUidAndDeletedFalse(ticket.getDepartmentUid());
        }
        return List.of();
    }

    // ==================== Strategy Implementations ====================

    private String roundRobinAssign(TicketEntity ticket, List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
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

    private String randomAssign(List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(RANDOM.nextInt(candidates.size())).getUid();
    }

    /**
     * Consistent-hash: deterministically pick a member based on ticket UID hash.
     * Same ticket always maps to the same member from the candidate pool.
     */
    private String consistentHashAssign(TicketEntity ticket, List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        int hash = Math.abs(ticket.getUid().hashCode());
        int index = hash % candidates.size();
        return candidates.get(index).getUid();
    }

    /**
     * Recent: pick the member who has most recently handled a ticket in the department.
     * Prefers members who have recent activity.
     */
    private String recentAssign(TicketEntity ticket, List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        // Find the most recently assigned ticket among all candidates
        for (MemberEntity candidate : candidates) {
            Optional<TicketEntity> lastTicket = ticketRepository
                    .findFirstByDepartmentUidAndAssigneeNotAndAssigneeNotOrderByCreatedAtDesc(
                            ticket.getDepartmentUid(),
                            BytedeskConsts.EMPTY_JSON_STRING,
                            BytedeskConsts.EMPTY_JSON_STRING)
                    .filter(t -> {
                        var a = t.getAssignee();
                        return a != null && candidate.getUid().equals(a.getUid());
                    });
            if (lastTicket.isPresent()) {
                return candidate.getUid();
            }
        }
        // Fallback: first member in the list
        return candidates.get(0).getUid();
    }

    // ==================== Assignment Application ====================

    private void applyAssignment(TicketEntity ticket, Task activeTask, String assigneeUid) {
        try {
            Optional<MemberEntity> memberOpt = memberRepository.findByUid(assigneeUid);
            if (memberOpt.isEmpty()) {
                log.warn("applyAssignment: member not found for uid={}", assigneeUid);
                return;
            }
            MemberEntity member = memberOpt.get();

            UserProtobuf assigneeProtobuf = buildAssigneeProtobuf(member);
            ticket.setAssignee(assigneeProtobuf.toJson());

            if (isWaitClaimTask(ticket, activeTask)) {
                if (!StringUtils.hasText(activeTask.getAssignee())) {
                    taskService.claim(activeTask.getId(), assigneeUid);
                } else if (!assigneeUid.equals(activeTask.getAssignee())) {
                    taskService.setAssignee(activeTask.getId(), assigneeUid);
                }
                Map<String, Object> variables = new HashMap<>();
                variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssigneeString());
                variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, assigneeUid);
                variables.put(TicketConsts.TICKET_VARIABLE_STATUS, TicketStatusEnum.PROCESSING.name());
                variables.put(TicketConsts.TICKET_VARIABLE_CLAIM_TIME, new Date());
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

            log.info("applyAssignment: assigned ticket={} to member={} (nickname={})",
                    ticket.getUid(), assigneeUid, member.getNickname());
            // Send notification to the new assignee
            try {
                ticketNotificationService.notifyTicketAssigned(ticket);
            } catch (Exception ex) {
                log.warn("applyAssignment: notification failed for ticket={}", ticket.getUid(), ex);
            }
        } catch (Exception e) {
            log.error("applyAssignment: failed for ticket={}, assigneeUid={}", ticket.getUid(), assigneeUid, e);
        }
    }

    private void applyAssignmentForNextNode(TicketEntity ticket, Task activeTask, String assigneeUid) {
        try {
            Optional<MemberEntity> memberOpt = memberRepository.findByUid(assigneeUid);
            if (memberOpt.isEmpty()) {
                log.warn("applyAssignmentForNextNode: member not found for uid={}", assigneeUid);
                return;
            }
            MemberEntity member = memberOpt.get();

            UserProtobuf assigneeProtobuf = buildAssigneeProtobuf(member);
            ticket.setAssignee(assigneeProtobuf.toJson());

            if (!StringUtils.hasText(activeTask.getAssignee())) {
                taskService.claim(activeTask.getId(), assigneeUid);
            } else if (!assigneeUid.equals(activeTask.getAssignee())) {
                taskService.setAssignee(activeTask.getId(), assigneeUid);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssigneeString());
            variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, assigneeUid);
            variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
            taskService.setVariables(activeTask.getId(), variables);

            ticketRepository.save(ticket);

            log.info("applyAssignmentForNextNode: assigned ticket={} next task to member={} (nickname={})",
                    ticket.getUid(), assigneeUid, member.getNickname());
            // Send notification to the new assignee
            try {
                ticketNotificationService.notifyTicketAssigned(ticket);
            } catch (Exception ex) {
                log.warn("applyAssignmentForNextNode: notification failed for ticket={}", ticket.getUid(), ex);
            }
        } catch (Exception e) {
            log.error("applyAssignmentForNextNode: failed for ticket={}, assigneeUid={}",
                    ticket.getUid(), assigneeUid, e);
        }
    }

    private UserProtobuf buildAssigneeProtobuf(MemberEntity member) {
        return UserProtobuf.builder()
                .uid(member.getUid())
                .nickname(member.getNickname())
                .avatar(member.getAvatar())
                .type(UserTypeEnum.MEMBER.name())
                .build();
    }

    // ==================== Assignment Logging ====================

    private void writeAssignmentLog(TicketEntity ticket, String processInstanceId,
                                     String taskDefinitionKey, String previousAssigneeJson,
                                     AssignmentResolutionResult result) {
        try {
            TicketAssignmentLogEntity logEntry = TicketAssignmentLogEntity.builder()
                    .ticketUid(ticket.getUid())
                    .processInstanceId(processInstanceId)
                    .taskDefinitionKey(taskDefinitionKey)
                    .fromAssignee(previousAssigneeJson)
                    .toAssignee(ticket.getAssigneeString())
                    .assignmentType(result.source() == AssignmentSource.NODE_CONFIG
                            ? "AUTO_WORKFLOW" : "AUTO_STRATEGY")
                    .strategy(result.strategy())
                    .reason(result.reason())
                    .candidatePoolDescription(result.candidatePoolDescription() != null
                            ? result.candidatePoolDescription() : "")
                    .build();
            logEntry.setOrgUid(ticket.getOrgUid());
            assignmentLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("writeAssignmentLog: failed for ticket={}", ticket.getUid(), e);
        }
    }

    /**
     * Write a manual assignment log entry (claim, assign, transfer).
     */
    public void writeManualAssignmentLog(TicketEntity ticket, String processInstanceId,
                                          String taskDefinitionKey, String previousAssigneeJson,
                                          String assignmentType, String reason) {
        try {
            TicketAssignmentLogEntity logEntry = TicketAssignmentLogEntity.builder()
                    .ticketUid(ticket.getUid())
                    .processInstanceId(processInstanceId)
                    .taskDefinitionKey(taskDefinitionKey)
                    .fromAssignee(previousAssigneeJson)
                    .toAssignee(ticket.getAssigneeString())
                    .assignmentType(assignmentType)
                    .strategy("MANUAL")
                    .reason(reason)
                    .candidatePoolDescription("")
                    .build();
            logEntry.setOrgUid(ticket.getOrgUid());
            assignmentLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("writeManualAssignmentLog: failed for ticket={}", ticket.getUid(), e);
        }
    }

    // ==================== Flowgram Schema Parsing ====================

    JSONObject parseTaskNode(String flowgramSchema, String taskDefinitionKey) {
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

            JSONObject startNode = findStartNode(nodes);
            if (startNode == null) {
                return findFirstTaskNode(nodes);
            }

            return findFirstTaskFromNode(startNode.getString("id"), nodes, edges);
        } catch (Exception e) {
            log.warn("parseTaskNode: failed to parse flowgramSchema", e);
            return null;
        }
    }

    private JSONObject findStartNode(JSONArray nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if ("start".equals(node.getString("type"))) {
                return node;
            }
        }
        return null;
    }

    private JSONObject findFirstTaskNode(JSONArray nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            String type = node.getString("type");
            if (!"start".equals(type) && !"end".equals(type) && isTaskNode(type)) {
                return node;
            }
        }
        return null;
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
                if ("condition".equals(type) || "parallel".equals(type)) {
                    return findFirstTaskFromNode(targetId, nodes, edges);
                }
                return null;
            }
        }
        return null;
    }

    boolean isTaskNode(String type) {
        return "approval".equals(type) || "countersign".equals(type) || "orSign".equals(type)
                || "notification".equals(type);
    }

    // ==================== Task Stage Helpers ====================

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

    JSONObject findFlowgramNode(TicketEntity ticket, String taskDefinitionKey) {
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
