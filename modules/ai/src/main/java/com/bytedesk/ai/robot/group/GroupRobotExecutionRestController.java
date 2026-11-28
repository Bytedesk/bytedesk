package com.bytedesk.ai.robot.group;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.group.GroupEntity;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.group.GroupRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 群机器人执行任务查询/重试（客服端）。
 * 
 * - 查询：群成员可读（校验当前用户在群内）
 * - 重试：仅提问者或群管理员，FAILED 任务重置为 PENDING（重新校验资格后由 worker 领取）
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/group/robot/execution")
@RequiredArgsConstructor
@Tag(name = "群机器人执行任务", description = "群内 @机器人 AI 执行任务的状态查询与重试")
public class GroupRobotExecutionRestController {

    private final GroupRobotExecutionRepository executionRepository;

    private final GroupRestService groupRestService;

    private final AuthService authService;

    @PreAuthorize(GroupRobotExecutionPermissions.HAS_GROUP_ROBOT_EXECUTION_READ)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query robot")
    @Operation(summary = "查询执行任务", description = "按源消息查询群机器人执行状态（群成员可读）")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/query")
    public ResponseEntity<?> query(@RequestParam String groupUid, @RequestParam String sourceMessageUid) {
        UserEntity user = requireLogin();
        GroupEntity group = requireGroupMember(groupUid, user);
        //
        List<GroupRobotExecutionEntity> executions = executionRepository
                .findByOrgUidAndGroupUidAndSourceMessageUid(group.getOrgUid(), group.getUid(), sourceMessageUid);
        List<Map<String, Object>> result = executions.stream().map(this::toSummary).collect(Collectors.toList());
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @PreAuthorize(GroupRobotExecutionPermissions.HAS_GROUP_ROBOT_EXECUTION_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute robot")
    @Operation(summary = "重试失败任务", description = "仅提问者或群管理员可重试；任务重置为 PENDING")
    @ApiResponse(responseCode = "200", description = "重试成功")
    @PostMapping("/retry")
    public ResponseEntity<?> retry(@RequestBody Map<String, String> body) {
        UserEntity user = requireLogin();
        String executionUid = body == null ? null : body.get("executionUid");
        if (!StringUtils.hasText(executionUid)) {
            return ResponseEntity.ok(JsonResult.error("executionUid required"));
        }
        //
        GroupRobotExecutionEntity execution = executionRepository.findByUid(executionUid)
                .orElse(null);
        if (execution == null) {
            return ResponseEntity.ok(JsonResult.error("execution not found"));
        }
        // 权限：提问者或群管理员
        GroupEntity group = groupRestService.findByUid(execution.getGroupUid()).orElse(null);
        if (group == null) {
            return ResponseEntity.ok(JsonResult.error("group not found"));
        }
        boolean isSender = execution.getSenderUserUid().equals(user.getUid());
        boolean isGroupAdmin = (group.getCreator() != null && user.getUid().equals(group.getCreator().getUid()))
                || group.getAdmins().stream().anyMatch(a -> a != null && user.getUid().equals(a.getUid()));
        if (!isSender && !isGroupAdmin) {
            return ResponseEntity.ok(JsonResult.error("no permission to retry"));
        }
        // 仅 FAILED 可重试
        if (!GroupRobotExecutionEntity.STATUS_FAILED.equals(execution.getStatus())) {
            return ResponseEntity.ok(JsonResult.error("only failed execution can be retried"));
        }
        //
        execution.setStatus(GroupRobotExecutionEntity.STATUS_PENDING);
        execution.setErrorCode(null);
        execution.setStatusUpdatedAt(java.time.ZonedDateTime.now());
        executionRepository.save(execution);
        return ResponseEntity.ok(JsonResult.success(toSummary(execution)));
    }

    private Map<String, Object> toSummary(GroupRobotExecutionEntity execution) {
        return Map.of(
                "uid", execution.getUid(),
                "robotUid", execution.getRobotUid(),
                "status", execution.getStatus(),
                "replyMessageUid", execution.getReplyMessageUid() == null ? "" : execution.getReplyMessageUid(),
                "errorCode", execution.getErrorCode() == null ? "" : execution.getErrorCode(),
                "attempt", execution.getAttempt());
    }

    private UserEntity requireLogin() {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException("login required");
        }
        return user;
    }

    /**
     * 群成员校验：当前用户必须是群成员或群管理员（不泄露群外信息，统一 not found）。
     */
    private GroupEntity requireGroupMember(String groupUid, UserEntity user) {
        GroupEntity group = groupRestService.findByUid(groupUid).orElse(null);
        if (group == null) {
            throw new RuntimeException("group not found");
        }
        boolean isMember = group.getMembers().stream()
                .anyMatch(member -> member != null && member.getUser() != null
                        && user.getUid().equals(member.getUser().getUid()));
        boolean isAdmin = (group.getCreator() != null && user.getUid().equals(group.getCreator().getUid()))
                || group.getAdmins().stream().anyMatch(a -> a != null && user.getUid().equals(a.getUid()));
        if (!isMember && !isAdmin) {
            throw new RuntimeException("group not found");
        }
        return group;
    }
}
