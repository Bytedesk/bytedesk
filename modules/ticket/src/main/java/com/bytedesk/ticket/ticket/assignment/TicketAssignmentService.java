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
import java.util.LinkedHashMap;
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
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.workgroup_routing.WorkgroupRoutingService;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsEntity;
import com.bytedesk.core.redis.RedisRoundRobinSequenceService;

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
    private final TicketSettingsRestService ticketSettingsRestService;
    private final TaskService taskService;
    private final TicketAssignmentLogRepository assignmentLogRepository;
    private final TicketUserOrgRoleRepository userOrgRoleRepository;
    private final TicketNotificationService ticketNotificationService;
    private final WorkgroupRestService workgroupRestService;
    private final WorkgroupRoutingService workgroupRoutingService;
    private final UidUtils uidUtils;
    private final TicketRoundRobinScopeService roundRobinScopeService;

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
        ensureWaitClaimCandidateUsers(ticket, activeTask);

        if (StringUtils.hasText(ticket.getAssigneeString()) && ticket.getAssignee() != null
                && StringUtils.hasText(ticket.getAssignee().getUid())) {
            String explicitAssigneeUid = ticket.getAssignee().getUid();
            AssignmentResolutionResult result = AssignmentResolutionResult.resolved(
                    explicitAssigneeUid,
                    AssignmentSource.AUTOMATIC,
                    "EXPLICIT_ASSIGNEE",
                    "ticket already created with explicit assignee",
                    "single explicit assignee from ticket request");
            applyAssignment(ticket, activeTask, explicitAssigneeUid);
            writeAssignmentLog(ticket, processInstanceId, activeTask.getTaskDefinitionKey(),
                    null, result);
            return result;
        }

        // 2. Try explicit assignment from workflow node configuration
        AssignmentResolutionResult result = resolveFromWorkflowNode(ticket, activeTask.getTaskDefinitionKey());

        // 3. Fallback to assignment strategy
        if (!result.isResolved()) {
            result = resolveByStrategy(ticket);
        }

        // 4. Apply assignment
        if (result.isResolved()) {
            // 防御：报告人节点不做成员级分配，保持 Flowable 静态归属
            if (result.source() == AssignmentSource.REPORTER) {
                log.debug("autoAssign: reporter-owned task {} keeps flowable assignee, ticket={}",
                        activeTask.getTaskDefinitionKey(), ticket.getUid());
                return result;
            }
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
            ensureWaitClaimCandidateUsers(ticket, activeTask);
            AssignmentResolutionResult result = resolveFromWorkflowNode(ticket, activeTask.getTaskDefinitionKey());

            if (!result.isResolved()) {
                result = resolveByStrategy(ticket);
            }

            if (result.isResolved()) {
                // 报告人节点（如 customerVerify）：任务归属由 Flowable 静态表达式（${reporterUid}）决定，
                // 不做成员级自动分配——报告人可能为访客（无 MemberEntity），成员级改派会剥夺访客端的
                // 「确认解决/未解决」验证入口（外部工单转派后验证按钮错落到客服端的根因）
                if (result.source() == AssignmentSource.REPORTER) {
                    log.debug("autoAssignForNextNode: reporter-owned task {} keeps flowable assignee, ticket={}",
                            activeTask.getTaskDefinitionKey(), ticket.getUid());
                    return result;
                }
                String previousAssignee = ticket.getAssigneeString();
                UserProtobuf currentAssignee = ticket.getAssignee();
                boolean ticketAssigneeMatches = currentAssignee != null
                        && result.assigneeUid().equals(currentAssignee.getUid());
                // 以 Flowable 任务实际归属兜底校验：转派路径曾遗漏同步流程变量 assigneeUid，
                // 回退/循环重建的处理人节点会携带陈旧归属，仅比对 ticket.assignee 会误判
                // 「未变」而放弃纠正，导致工单重新打开后当前处理人无法操作（actionable=false）。
                // 任务未分配时保持原跳过语义，不干预 waitClaim 等待认领场景。
                String taskAssigneeUid = activeTask.getAssignee();
                boolean taskAssigneeStale = StringUtils.hasText(taskAssigneeUid)
                        && !result.assigneeUid().equals(taskAssigneeUid);
                if (ticketAssigneeMatches && !taskAssigneeStale) {
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
            case "user" -> resolveSpecificUser(assigneeUids, ticket);
            case "department" -> resolveNodeDepartment(assigneeUids, ticket, nodeAssignmentMode, taskDefinitionKey);
            case "role" -> resolveRoleMember(ticket, data.getString("roleUid"), nodeAssignmentMode, taskDefinitionKey);
            case "reporter" -> resolveReporter(ticket);
            case "leader" -> resolveLeader(ticket);
            default -> AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "unsupported assigneeType: " + assigneeType);
        };
    }

    private AssignmentResolutionResult resolveNodeDepartment(JSONArray assigneeUids,
                                                             TicketEntity ticket,
                                                             String nodeAssignmentMode,
                                                             String taskDefinitionKey) {
        if (assigneeUids == null || assigneeUids.isEmpty()) {
            return resolveDepartmentMemberWithStrategy(ticket, nodeAssignmentMode);
        }

        Map<String, MemberEntity> candidateMap = new LinkedHashMap<>();
        List<String> configuredDepartmentUids = new ArrayList<>();
        List<String> emptyDepartmentUids = new ArrayList<>();

        for (int i = 0; i < assigneeUids.size(); i++) {
            String departmentUid = assigneeUids.getString(i);
            if (!StringUtils.hasText(departmentUid)) {
                continue;
            }
            configuredDepartmentUids.add(departmentUid);
            List<MemberEntity> members = memberRepository.findByDeptUidAndDeletedFalse(departmentUid);
            if (members == null || members.isEmpty()) {
                emptyDepartmentUids.add(departmentUid);
                continue;
            }
            for (MemberEntity member : members) {
                if (member != null && StringUtils.hasText(member.getUid())) {
                    candidateMap.putIfAbsent(member.getUid(), member);
                }
            }
        }

        if (candidateMap.isEmpty()) {
            return AssignmentResolutionResult.unresolved(
                    AssignmentSource.NODE_CONFIG,
                    "no members found in configured departments: " + configuredDepartmentUids
                            + (emptyDepartmentUids.isEmpty() ? "" : ", empty=" + emptyDepartmentUids));
        }

        String strategy = StringUtils.hasText(nodeAssignmentMode)
            ? TicketAssignmentModeEnum.normalizeRuntime(nodeAssignmentMode)
            : TicketAssignmentModeEnum.normalizeRuntime(getAssignmentMode(ticket));
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.resolveRuntimeMode(strategy);
        if (mode == TicketAssignmentModeEnum.MANUAL) {
            // 手动分配：节点未显式配置分配方式时沿用全局设置，不做自动指派，
            // 工单停留在待认领节点等待成员认领或负责人指派
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "manual mode — no auto-assignment for configured departments");
        }
        List<MemberEntity> candidates = new ArrayList<>(candidateMap.values());
        // 节点明确指定多个部门：使用 NODE_DEPARTMENTS 作用域（流程+节点+部门集合），
        // 与表单部门(DEPARTMENT)、组织兜底(ORGANIZATION)序列相互隔离
        List<String> sortedDepartmentUids = configuredDepartmentUids.stream()
                .distinct()
                .sorted()
                .toList();
        MemberEntity selected = roundRobinScopeService.roundRobinNodeDepartments(
                ticket, ticket.getProcessEntityUid(), taskDefinitionKey, sortedDepartmentUids, candidates);
        String chosenUid = selected != null ? selected.getUid() : null;
        if (!StringUtils.hasText(chosenUid)) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "strategy returned no result for configured department members");
        }
        return AssignmentResolutionResult.resolved(chosenUid, AssignmentSource.NODE_CONFIG,
                strategy,
                "节点部门 " + configuredDepartmentUids + " 按 " + strategy + " 分配",
                "候选部门 " + configuredDepartmentUids.size() + " 个，成员 " + candidates.size() + " 人");
    }

    private AssignmentResolutionResult resolveSpecificUser(JSONArray assigneeUids, TicketEntity ticket) {
        if (assigneeUids == null || assigneeUids.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "assigneeUids is empty for type=user");
        }
        String reporterUid = ticket.getReporter() != null ? ticket.getReporter().getUid() : null;
        List<String> validUids = new ArrayList<>();
        for (int i = 0; i < assigneeUids.size(); i++) {
            String uid = resolvePlaceholderUid(assigneeUids.getString(i), ticket);
            if (!StringUtils.hasText(uid)) {
                continue;
            }
            // 报告人节点（${reporterUid} 占位符或显式报告人 uid）：归属交给 Flowable 静态表达式/报告人语义，
            // 不做成员级分配——报告人可能为访客（无 MemberEntity），
            // 成员级改派会剥夺访客端的「确认解决/未解决」验证入口
            if (uid.equals(reporterUid)) {
                return resolveReporter(ticket);
            }
            Optional<MemberEntity> memberOpt = memberRepository.findByUid(uid);
            if (memberOpt.isPresent()) {
                validUids.add(uid);
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

    /**
     * 解析节点配置 assigneeUids 中的占位符（如 ${reporterUid}/${assigneeUid}）为工单上下文中的实际 uid。
     * 历史缺陷：占位符被当作字面量 member uid 查询导致解析失败，进而回退全局策略把报告人节点
     * （如 customerVerify）错派给无关客服。
     */
    private String resolvePlaceholderUid(String raw, TicketEntity ticket) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        return switch (raw.trim()) {
            case "${reporterUid}" -> ticket.getReporter() != null ? ticket.getReporter().getUid() : null;
            case "${assigneeUid}" -> ticket.getAssignee() != null ? ticket.getAssignee().getUid() : null;
            default -> raw.trim();
        };
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
            if (isExternalWorkgroupTicket(ticket)) {
                return resolveWorkgroupMemberWithStrategy(ticket, nodeAssignmentMode, AssignmentSource.NODE_CONFIG);
            }
            // 内部工单未选部门：回退到组织全部有效成员，避免设置自动分配时无部门工单无法分配
            if (StringUtils.hasText(ticket.getOrgUid())) {
                List<MemberEntity> orgMembers = memberRepository.findByOrgUidAndDeletedFalse(ticket.getOrgUid());
                if (!orgMembers.isEmpty()) {
                    String orgStrategy = StringUtils.hasText(nodeAssignmentMode)
                        ? TicketAssignmentModeEnum.normalizeRuntime(nodeAssignmentMode)
                        : TicketAssignmentModeEnum.normalizeRuntime(getAssignmentMode(ticket));
                    TicketAssignmentModeEnum orgMode = TicketAssignmentModeEnum.resolveRuntimeMode(orgStrategy);
                    if (orgMode == TicketAssignmentModeEnum.MANUAL) {
                        return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                                "manual mode — no auto-assignment for org fallback");
                    }
                    String orgChosenUid = applyStrategy(orgMode, ticket, orgMembers);
                    if (StringUtils.hasText(orgChosenUid)) {
                        return AssignmentResolutionResult.resolved(orgChosenUid, AssignmentSource.NODE_CONFIG,
                                orgStrategy, "组织成员按 " + orgStrategy + " 分配（未选部门兜底）",
                                "组织成员 " + orgMembers.size() + " 人");
                    }
                }
            }
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no department uid for type=department");
        }
        List<MemberEntity> members = memberRepository.findByDeptUidAndDeletedFalse(departmentUid);
        if (members.isEmpty()) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "no members in department: " + departmentUid);
        }
        String strategy = StringUtils.hasText(nodeAssignmentMode)
            ? TicketAssignmentModeEnum.normalizeRuntime(nodeAssignmentMode)
            : TicketAssignmentModeEnum.normalizeRuntime(getAssignmentMode(ticket));
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.resolveRuntimeMode(strategy);
        if (mode == TicketAssignmentModeEnum.MANUAL) {
            // 手动分配：不自动指派，工单停留在待认领节点等待认领/指派（修复内部工单设置手动分配仍被自动分配的问题）
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "manual mode — no auto-assignment for department " + departmentUid);
        }
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
                                                          String nodeAssignmentMode, String taskDefinitionKey) {
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
            ? TicketAssignmentModeEnum.normalizeRuntime(nodeAssignmentMode)
            : TicketAssignmentModeEnum.normalizeRuntime(getAssignmentMode(ticket));
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.resolveRuntimeMode(strategy);
        if (mode == TicketAssignmentModeEnum.MANUAL) {
            // 手动分配：不自动指派，工单停留在待认领节点等待认领/指派
            return AssignmentResolutionResult.unresolved(AssignmentSource.NODE_CONFIG,
                    "manual mode — no auto-assignment for role " + roleUid);
        }
        // 节点角色：使用 NODE_ROLE 作用域（流程+节点+roleUid），与其他候选来源序列隔离
        MemberEntity selected = roundRobinScopeService.roundRobinNodeRole(
                ticket, ticket.getProcessEntityUid(), taskDefinitionKey, roleUid, candidates);
        String chosenUid = selected != null ? selected.getUid() : null;
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
        String assignmentMode = TicketAssignmentModeEnum.normalizeRuntime(getAssignmentMode(ticket));
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.resolveRuntimeMode(assignmentMode);

        if (mode == TicketAssignmentModeEnum.MANUAL) {
            return AssignmentResolutionResult.unresolved(AssignmentSource.GLOBAL_STRATEGY,
                    "manual mode — no auto-assignment");
        }

        if (isExternalWorkgroupTicket(ticket)) {
            return resolveWorkgroupMemberWithStrategy(ticket, assignmentMode, AssignmentSource.GLOBAL_STRATEGY);
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
            // 手动分配：不自动指派任何候选成员，返回 null 由调用方按未分配处理（防御分支：
            // 正常流程在上游 resolve* 方法已拦截 MANUAL，这里避免新增调用路径遗漏时误取第一个候选人）
            case MANUAL -> null;
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

        if (isExternalWorkgroupTicket(ticket)) {
            try {
                TicketSettingsEntity settings = ticketSettingsRestService.resolveEntityByWorkgroup(
                        ticket.getOrgUid(),
                        ticket.getWorkgroupUid(),
                        ticket.getType());
                TicketBasicSettingsEntity basic = settings != null ? settings.getBasicSettings() : null;
                if (basic != null && StringUtils.hasText(basic.getAssignmentMode())) {
                    return TicketAssignmentModeEnum.normalize(basic.getAssignmentMode());
                }
            } catch (Exception ex) {
                log.debug("getAssignmentMode: failed to resolve workgroup settings for ticket={}", ticket.getUid(), ex);
            }
        }

        return DEFAULT_ASSIGNMENT_MODE;
    }

    /**
     * Get candidate members for assignment based on ticket's department.
     * <p>
     * 候选人解析优先级：
     * <ol>
     * <li>工单已选部门 → 部门成员（部门无人则不分配，尊重显式部门选择）</li>
     * <li>外部工单且绑定工作组 → 工作组可用客服</li>
     * <li>内部工单未选部门 → 组织全部有效成员兜底（修复：设置自动分配时无部门工单不分配的问题）</li>
     * </ol>
     */
    List<MemberEntity> getCandidates(TicketEntity ticket) {
        if (StringUtils.hasText(ticket.getDepartmentUid())) {
            return memberRepository.findByDeptUidAndDeletedFalse(ticket.getDepartmentUid());
        }
        if (isExternalWorkgroupTicket(ticket)) {
            return getWorkgroupMembers(ticket);
        }
        // 兜底：内部工单未选部门时，使用组织全部有效成员，
        // 保证 TicketBasicSettings 配置自动分配时无论是否选择部门都能分配处理人
        if (StringUtils.hasText(ticket.getOrgUid())) {
            return memberRepository.findByOrgUidAndDeletedFalse(ticket.getOrgUid());
        }
        return List.of();
    }

    private AssignmentResolutionResult resolveWorkgroupMemberWithStrategy(TicketEntity ticket,
            String assignmentMode,
            AssignmentSource source) {
        WorkgroupEntity workgroup = getWorkgroup(ticket);
        if (workgroup == null) {
            return AssignmentResolutionResult.unresolved(source,
                    "workgroup not found: " + ticket.getWorkgroupUid());
        }

        String strategy = StringUtils.hasText(assignmentMode)
            ? TicketAssignmentModeEnum.normalizeRuntime(assignmentMode)
            : TicketAssignmentModeEnum.normalizeRuntime(getAssignmentMode(ticket));
        TicketAssignmentModeEnum mode = TicketAssignmentModeEnum.resolveRuntimeMode(strategy);
        if (mode == TicketAssignmentModeEnum.MANUAL) {
            return AssignmentResolutionResult.unresolved(source, "manual mode — no auto-assignment");
        }

        // 工单独立选人：不复用客服会话预计算的 nextAgentUid，也不发布异步推进事件；
        // 通过原子取号在工作组客服快照（不要求在线）中严格轮询，避免连续分配集中到同一人，
        // 且不受工作组自身 routingMode（如 MANUAL 不推进游标）干扰
        AgentEntity selectedAgent;
        try {
            selectedAgent = workgroupRoutingService.selectAgentForTicket(workgroup, ticket.getType(), mode.name());
        } catch (RedisRoundRobinSequenceService.RoundRobinSequenceException ex) {
            log.error("resolveWorkgroupMemberWithStrategy: round robin sequence failed for ticket={}, workgroup={}",
                    ticket.getUid(), workgroup.getUid(), ex);
            return AssignmentResolutionResult.unresolved(source,
                    "round robin sequence unavailable: " + ex.getMessage());
        }
        if (selectedAgent == null || selectedAgent.getMember() == null
                || !StringUtils.hasText(selectedAgent.getMember().getUid())) {
            return AssignmentResolutionResult.unresolved(source,
                    "no valid member-backed workgroup agents: " + workgroup.getUid());
        }

        return AssignmentResolutionResult.resolved(
                selectedAgent.getMember().getUid(),
                source,
                mode.name(),
                "工作组 " + workgroup.getUid() + " 按 " + mode.name() + " 分配",
                "工单按工作组客服（去重后）选中 " + selectedAgent.getMember().getUid());
    }

    private boolean isExternalWorkgroupTicket(TicketEntity ticket) {
        return ticket != null
                && TicketTypeEnum.EXTERNAL.name().equalsIgnoreCase(ticket.getType())
                && StringUtils.hasText(ticket.getWorkgroupUid());
    }

    private WorkgroupEntity getWorkgroup(TicketEntity ticket) {
        if (!isExternalWorkgroupTicket(ticket)) {
            return null;
        }
        return workgroupRestService.findByUid(ticket.getWorkgroupUid()).orElse(null);
    }

    private List<MemberEntity> getWorkgroupMembers(TicketEntity ticket) {
        WorkgroupEntity workgroup = getWorkgroup(ticket);
        if (workgroup == null || workgroup.getAgents() == null || workgroup.getAgents().isEmpty()) {
            return List.of();
        }

        Map<String, MemberEntity> candidateMap = new LinkedHashMap<>();
        for (AgentEntity agent : workgroup.getAgents()) {
            if (agent == null || agent.getMember() == null || !StringUtils.hasText(agent.getMember().getUid())) {
                continue;
            }
            candidateMap.putIfAbsent(agent.getMember().getUid(), agent.getMember());
        }
        return new ArrayList<>(candidateMap.values());
    }

    private void ensureWaitClaimCandidateUsers(TicketEntity ticket, Task activeTask) {
        if (activeTask == null || !isExternalWorkgroupTicket(ticket) || !isWaitClaimTask(ticket, activeTask)) {
            return;
        }

        for (MemberEntity candidate : getWorkgroupMembers(ticket)) {
            if (candidate == null || !StringUtils.hasText(candidate.getUid())) {
                continue;
            }
            try {
                taskService.addCandidateUser(activeTask.getId(), candidate.getUid());
            } catch (Exception ex) {
                log.debug("ensureWaitClaimCandidateUsers: candidate already linked or invalid, taskId={}, uid={}",
                        activeTask.getId(), candidate.getUid());
            }
        }
    }

    // ==================== Strategy Implementations ====================

    private String roundRobinAssign(TicketEntity ticket, List<MemberEntity> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        // 原子取号轮询：不再从历史工单推导游标（历史查询存在无部门 NULL/空串不一致、
        // createdAt 排序偏差、并发窗口与作用域混用问题）。按实际候选来源隔离序列：
        // 有部门 → DEPARTMENT/departmentUid；未选部门（NULL、空串、纯空白统一归一化）→ ORGANIZATION/orgUid。
        // Redis 取号失败时抛出 RoundRobinSequenceException，向上传播为分配失败，不回退首人。
        MemberEntity selected;
        if (StringUtils.hasText(ticket.getDepartmentUid())) {
            selected = roundRobinScopeService.roundRobinDepartment(ticket, ticket.getDepartmentUid(), candidates);
        } else {
            selected = roundRobinScopeService.roundRobinOrganization(ticket, candidates);
        }
        return selected != null ? selected.getUid() : null;
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
        // 部门维度查询最近已分配工单；内部工单未选部门时回退到组织维度（department_uid 为空）
        Optional<TicketEntity> lastTicketBase;
        if (StringUtils.hasText(ticket.getDepartmentUid())) {
            lastTicketBase = ticketRepository
                    .findFirstByDepartmentUidAndAssigneeNotAndAssigneeNotOrderByCreatedAtDesc(
                            ticket.getDepartmentUid(),
                            BytedeskConsts.EMPTY_JSON_STRING,
                            BytedeskConsts.EMPTY_JSON_STRING);
        } else {
            lastTicketBase = ticketRepository
                    .findFirstByOrgUidAndDepartmentUidIsNullAndAssigneeNotAndAssigneeNotOrderByCreatedAtDesc(
                            ticket.getOrgUid(),
                            BytedeskConsts.EMPTY_JSON_STRING,
                            BytedeskConsts.EMPTY_JSON_STRING);
        }
        // Find the most recently assigned ticket among all candidates
        for (MemberEntity candidate : candidates) {
            Optional<TicketEntity> lastTicket = lastTicketBase
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

            // 重新加载最新工单实体：调用方传入的可能是游离态旧版本引用，
            // 直接 merge 会因版本过期触发 ObjectOptimisticLockingFailureException。
            // 使用悲观行锁读取：与并发分配事务（会话补分配/重复事件重试）互斥，
            // 保证“重读 → 幂等判断 → 写入”是跨实例原子临界区
            TicketEntity current;
            try {
                current = ticketRepository.findByUidForAssignment(ticket.getUid()).orElse(ticket);
            } catch (Exception lockEx) {
                // 行锁不可用（如非事务上下文/测试桩）时退回普通读取，保持可测试性
                log.debug("applyAssignment: pessimistic read unavailable, fallback to findByUid, ticket={}",
                        ticket.getUid(), lockEx);
                current = ticketRepository.findByUid(ticket.getUid()).orElse(ticket);
            }
            // 分配幂等保护：重读后若工单已有有效处理人，复用既有分配，不二次写入/消费轮询名额
            // （重复创建事件、会话补分配竞态等场景；显式指派路径在上游已拦截，不会走到这里）
            if (hasValidAssignee(current) && !assigneeUid.equals(current.getAssignee().getUid())) {
                log.info("applyAssignment: ticket={} already assigned to {}, skip reassignment to {}",
                        ticket.getUid(), current.getAssignee().getUid(), assigneeUid);
                ticket.setAssignee(current.getAssigneeString());
                ticket.setStatus(current.getStatus());
                return;
            }
            UserProtobuf assigneeProtobuf = buildAssigneeProtobuf(member);
            current.setAssignee(assigneeProtobuf.toJson());

            if (isWaitClaimTask(current, activeTask)) {
                if (!StringUtils.hasText(activeTask.getAssignee())) {
                    taskService.claim(activeTask.getId(), assigneeUid);
                } else if (!assigneeUid.equals(activeTask.getAssignee())) {
                    taskService.setAssignee(activeTask.getId(), assigneeUid);
                }
                Map<String, Object> variables = new HashMap<>();
                variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, current.getAssigneeString());
                variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, assigneeUid);
                variables.put(TicketConsts.TICKET_VARIABLE_STATUS, TicketStatusEnum.PROCESSING.name());
                variables.put(TicketConsts.TICKET_VARIABLE_CLAIM_TIME, new Date());
                taskService.complete(activeTask.getId(), variables);
                Task processTask = findNextTaskByStage(current, activeTask.getProcessInstanceId(), "PROCESSING");
                if (processTask != null && !assigneeUid.equals(processTask.getAssignee())) {
                    taskService.setAssignee(processTask.getId(), assigneeUid);
                }
                current.setStatus(TicketStatusEnum.PROCESSING.name());
            } else {
                if (StringUtils.hasText(activeTask.getAssignee())) {
                    taskService.setAssignee(activeTask.getId(), assigneeUid);
                } else {
                    taskService.claim(activeTask.getId(), assigneeUid);
                }
                current.setStatus(TicketStatusEnum.ASSIGNED.name());
            }
            TicketEntity savedTicket = ticketRepository.save(current);

            // 回写调用方引用，保证后续分配日志/通知读取到最新处理人与状态
            ticket.setAssignee(savedTicket.getAssigneeString());
            ticket.setStatus(savedTicket.getStatus());

            log.info("applyAssignment: assigned ticket={} to member={} (nickname={})",
                    ticket.getUid(), assigneeUid, member.getNickname());
            // Send notification to the new assignee
            try {
                ticketNotificationService.notifyTicketAssigned(savedTicket);
            } catch (Exception ex) {
                log.warn("applyAssignment: notification failed for ticket={}", ticket.getUid(), ex);
            }
        } catch (Exception e) {
            log.error("applyAssignment: failed for ticket={}, assigneeUid={}", ticket.getUid(), assigneeUid, e);
            // 分配成功以落地为准：写入/流程操作失败必须向上传播，阻止调用方写“成功分配”日志或发出成功通知
            throw new AssignmentApplyException("apply assignment failed for ticket " + ticket.getUid(), e);
        }
    }

    /** 分配写入失败（工单/Flowable 任务/流程变量落地失败）时抛出，阻止调用方按成功处理 */
    public static class AssignmentApplyException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public AssignmentApplyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private boolean hasValidAssignee(TicketEntity ticket) {
        try {
            return ticket.getAssignee() != null && StringUtils.hasText(ticket.getAssignee().getUid());
        } catch (Exception ex) {
            return false;
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

            // 与 applyAssignment 相同：重新加载最新实体 + 悲观行锁，规避游离态旧版本 merge 失败，
            // 并与并发分配事务（创建监听/会话补分配/重复事件重试）互斥
            TicketEntity current;
            try {
                current = ticketRepository.findByUidForAssignment(ticket.getUid()).orElse(ticket);
            } catch (Exception lockEx) {
                // 行锁不可用（如非事务上下文/测试桩）时退回普通读取，保持可测试性
                log.debug("applyAssignmentForNextNode: pessimistic read unavailable, fallback to findByUid, ticket={}",
                        ticket.getUid(), lockEx);
                current = ticketRepository.findByUid(ticket.getUid()).orElse(ticket);
            }
            UserProtobuf assigneeProtobuf = buildAssigneeProtobuf(member);
            current.setAssignee(assigneeProtobuf.toJson());

            if (!StringUtils.hasText(activeTask.getAssignee())) {
                taskService.claim(activeTask.getId(), assigneeUid);
            } else if (!assigneeUid.equals(activeTask.getAssignee())) {
                taskService.setAssignee(activeTask.getId(), assigneeUid);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, current.getAssigneeString());
            variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, assigneeUid);
            variables.put(TicketConsts.TICKET_VARIABLE_STATUS, current.getStatus());
            taskService.setVariables(activeTask.getId(), variables);

            TicketEntity savedTicket = ticketRepository.save(current);
            ticket.setAssignee(savedTicket.getAssigneeString());

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
            // 与 applyAssignment 一致：失败必须传播，不写成功分配日志
            throw new AssignmentApplyException(
                    "apply assignment for next node failed for ticket " + ticket.getUid(), e);
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
                    .uid(uidUtils.getUid())
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
                    .uid(uidUtils.getUid())
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
