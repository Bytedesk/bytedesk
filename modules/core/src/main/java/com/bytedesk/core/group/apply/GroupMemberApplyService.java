/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group.apply;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.group.GroupAccessService;
import com.bytedesk.core.group.GroupEntity;
import com.bytedesk.core.group.GroupI18nExceptions;
import com.bytedesk.core.group.GroupNotificationConsts;
import com.bytedesk.core.group.GroupRepository;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.notification.NotificationRequest;
import com.bytedesk.core.notification.NotificationService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.alibaba.fastjson2.JSONObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 公开群入群申请状态机（规划 §4.3/§4.4）
 * 
 * 单条当前申请模型 + attempt 轮次；审批/拒绝/撤回在行锁内串行化；
 * approve 与成员入库、thread 创建、通知落库同事务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMemberApplyService {

    private final GroupMemberApplyRepository applyRepository;

    private final GroupRepository groupRepository;

    private final GroupAccessService groupAccessService;

    private final MemberRestService memberRestService;

    private final ThreadRestService threadRestService;

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    private final UidUtils uidUtils;

    // ==================== 申请创建 ====================

    /**
     * 提交入群申请：仅公开群且 needApproval=true；
     * 已有 PENDING 幂等返回；终态后重新申请递增 attempt
     */
    @Transactional
    public GroupMemberApplyResponse create(GroupMemberApplyRequest request) {
        UserEntity applicant = groupAccessService.requireUser();
        if (!StringUtils.hasText(request.getGroupUid())) {
            throw new IllegalArgumentException("groupUid is required");
        }
        //
        GroupEntity group = groupAccessService.findGroupOrNotFound(request.getGroupUid());
        // 申请人必须与群同组织
        if (!applicant.getOrgUid().equals(group.getOrgUid())) {
            throw new NotFoundException("group not found: " + request.getGroupUid());
        }
        // 仅公开群可申请
        if (groupAccessService.isPrivate(group)) {
            throw GroupI18nExceptions.forbiddenApply();
        }
        // 已是成员则无需申请
        if (groupAccessService.isMemberOrAdmin(group, applicant.getUid())) {
            throw GroupI18nExceptions.conflictApplyState();
        }
        // 满员直接拒绝
        checkCapacity(group, 1);
        //
        Optional<GroupMemberApplyEntity> existing = applyRepository
                .findByOrgUidAndGroupUidAndUserUidAndDeletedFalse(group.getOrgUid(), group.getUid(), applicant.getUid());
        GroupMemberApplyEntity apply;
        if (existing.isPresent()) {
            apply = existing.get();
            if (apply.isPending()) {
                // 幂等：返回当前待审批申请，不重复通知
                return toResponse(apply, false);
            }
            // 终态后重新申请：递增轮次
            int nextAttempt = (apply.getAttempt() == null ? 0 : apply.getAttempt()) + 1;
            apply.setAttempt(nextAttempt);
        } else {
            apply = GroupMemberApplyEntity.builder().build();
            apply.setUid(uidUtils.getUid());
            apply.setGroupUid(group.getUid());
            apply.setUserUid(applicant.getUid());
            apply.setAttempt(1);
        }
        apply.setStatus(GroupMemberApplyStatusEnum.PENDING.name());
        apply.setReason(trimToLength(request.getReason(), 255));
        apply.setRejectReason(null);
        apply.setHandledByUid(null);
        apply.setHandledAt(null);
        apply.setOrgUid(group.getOrgUid());
        //
        GroupMemberApplyEntity saved = applyRepository.save(apply);
        // 通知群主 + 管理员（best-effort，失败不影响申请已提交）
        notifyGroupAdminsNewApply(group, saved, applicant);
        return toResponse(saved, false);
    }

    // ==================== 审批 ====================

    /**
     * 审批通过：行锁内复核群状态/成员资格/容量，加入成员并创建 thread，同事务提交
     */
    @Transactional
    public GroupMemberApplyResponse approve(GroupMemberApplyRequest request) {
        UserEntity operator = groupAccessService.requireUser();
        GroupMemberApplyEntity apply = lockApplyOrNotFound(request.getUid());
        GroupEntity group = groupAccessService.requireAdministerableGroup(apply.getGroupUid());
        // 轮次校验：旧轮次请求拒绝
        checkAttempt(apply, request.getAttempt());
        // 仅 PENDING 可审批
        if (!apply.isPending()) {
            throw GroupI18nExceptions.conflictApplyState();
        }
        //
        UserEntity applicant = userRepository.findByUid(apply.getUserUid())
                .orElseThrow(() -> new NotFoundException("applicant not found"));
        // 已是成员：置 FULFILLED，不重复入群
        if (groupAccessService.isMemberOrAdmin(group, applicant.getUid())) {
            apply.setStatus(GroupMemberApplyStatusEnum.FULFILLED.name());
            apply.setHandledByUid(operator.getUid());
            apply.setHandledAt(ZonedDateTime.now());
            return toResponse(applyRepository.save(apply), false);
        }
        // 复核群仍公开、仍需审批（群转私有则失效）
        if (groupAccessService.isPrivate(group)) {
            apply.setStatus(GroupMemberApplyStatusEnum.INVALIDATED.name());
            apply.setHandledByUid(operator.getUid());
            apply.setHandledAt(ZonedDateTime.now());
            applyRepository.save(apply);
            throw GroupI18nExceptions.conflictApplyState();
        }
        checkCapacity(group, 1);
        // 同事务：成员入库 + thread 创建 + 申请 APPROVED
        addMemberToGroup(group, applicant);
        apply.setStatus(GroupMemberApplyStatusEnum.APPROVED.name());
        apply.setRejectReason(null);
        apply.setHandledByUid(operator.getUid());
        apply.setHandledAt(ZonedDateTime.now());
        GroupMemberApplyEntity saved = applyRepository.save(apply);
        // 通知申请人（同事务落库）
        notifyApplicantResult(group, saved, applicant, true, null);
        return toResponse(saved, false);
    }

    /**
     * 拒绝：仅 PENDING；记录拒绝原因与处理人
     */
    @Transactional
    public GroupMemberApplyResponse reject(GroupMemberApplyRequest request) {
        UserEntity operator = groupAccessService.requireUser();
        GroupMemberApplyEntity apply = lockApplyOrNotFound(request.getUid());
        groupAccessService.requireAdministerableGroup(apply.getGroupUid());
        checkAttempt(apply, request.getAttempt());
        if (!apply.isPending()) {
            throw GroupI18nExceptions.conflictApplyState();
        }
        //
        apply.setStatus(GroupMemberApplyStatusEnum.REJECTED.name());
        apply.setRejectReason(trimToLength(request.getRejectReason(), 255));
        apply.setHandledByUid(operator.getUid());
        apply.setHandledAt(ZonedDateTime.now());
        GroupMemberApplyEntity saved = applyRepository.save(apply);
        // 通知申请人
        Optional<UserEntity> applicant = userRepository.findByUid(apply.getUserUid());
        applicant.ifPresent(user -> notifyApplicantResult(null, saved, user, false,
                trimToLength(request.getRejectReason(), 255)));
        return toResponse(saved, false);
    }

    /**
     * 撤回：仅申请人本人且 PENDING
     */
    @Transactional
    public GroupMemberApplyResponse cancel(GroupMemberApplyRequest request) {
        UserEntity operator = groupAccessService.requireUser();
        GroupMemberApplyEntity apply = lockApplyOrNotFound(request.getUid());
        if (!operator.getUid().equals(apply.getUserUid())) {
            throw GroupI18nExceptions.forbiddenApply();
        }
        checkAttempt(apply, request.getAttempt());
        if (!apply.isPending()) {
            throw GroupI18nExceptions.conflictApplyState();
        }
        apply.setStatus(GroupMemberApplyStatusEnum.CANCELED.name());
        apply.setHandledByUid(operator.getUid());
        apply.setHandledAt(ZonedDateTime.now());
        return toResponse(applyRepository.save(apply), false);
    }

    // ==================== 查询 ====================

    /** 管理端：当前用户管理（群主/管理员）的群的待审批申请 */
    @Transactional(readOnly = true)
    public Page<GroupMemberApplyResponse> queryByAdminGroups(GroupMemberApplyRequest request) {
        UserEntity user = groupAccessService.requireUser();
        Pageable pageable = request.getPageable();
        // 当前用户为群主或管理员的群
        List<String> groupUids = findAdministerableGroupUids(user);
        if (groupUids.isEmpty()) {
            return Page.empty(pageable);
        }
        String status = StringUtils.hasText(request.getStatus()) ? request.getStatus()
                : GroupMemberApplyStatusEnum.PENDING.name();
        Page<GroupMemberApplyEntity> page = applyRepository
                .findByOrgUidAndGroupUidInAndStatusAndDeletedFalse(user.getOrgUid(), groupUids, status, pageable);
        return page.map(apply -> toResponse(apply, true));
    }

    /** 申请人：自己的申请记录（脱敏：私有/失效群不返回群资料） */
    @Transactional(readOnly = true)
    public Page<GroupMemberApplyResponse> queryByUser(GroupMemberApplyRequest request) {
        UserEntity user = groupAccessService.requireUser();
        Pageable pageable = request.getPageable();
        Page<GroupMemberApplyEntity> page = applyRepository
                .findByOrgUidAndUserUidAndDeletedFalse(user.getOrgUid(), user.getUid(), pageable);
        return page.map(apply -> toResponse(apply, false));
    }

    /** 公开群列表标记：本页群中该用户是否有 PENDING 申请 */
    @Transactional(readOnly = true)
    public Map<String, Boolean> mapPendingByGroupUids(String orgUid, String userUid, List<String> groupUids) {
        if (groupUids == null || groupUids.isEmpty()) {
            return new HashMap<>();
        }
        List<GroupMemberApplyEntity> pendings = applyRepository
                .findByOrgUidAndUserUidAndStatusAndGroupUidInAndDeletedFalse(orgUid, userUid,
                        GroupMemberApplyStatusEnum.PENDING.name(), groupUids);
        // 显式 lambda 代替方法引用：未绑定方法引用的接收者 this 经方法描述符传入时
        // 无法保证符合 @Nonnull GroupMemberApplyEntity，触发空类型安全告警
        Set<String> pendingGroupUids = pendings.stream()
                .map(apply -> apply.getGroupUid())
                .collect(Collectors.toSet());
        Map<String, Boolean> result = new HashMap<>();
        for (String groupUid : groupUids) {
            result.put(groupUid, pendingGroupUids.contains(groupUid));
        }
        return result;
    }

    /** 群维度待审批数量（群设置 Badge 用） */
    @Transactional(readOnly = true)
    public long countPending(String orgUid, String groupUid) {
        return applyRepository.countByOrgUidAndGroupUidAndStatusAndDeletedFalse(orgUid, groupUid,
                GroupMemberApplyStatusEnum.PENDING.name());
    }

    /**
     * 群转私有/解散时失效待审批申请（供 GroupRestService 调用，行锁串行化）
     */
    @Transactional
    public int invalidatePendingByGroupUid(String orgUid, String groupUid) {
        return invalidatePendingInternal(orgUid, groupUid);
    }

    /** 转换为响应；includeApplicant=true 时附带申请人摘要（管理端列表） */
    private GroupMemberApplyResponse toResponse(GroupMemberApplyEntity apply, boolean includeApplicant) {
        GroupMemberApplyResponse response = GroupMemberApplyResponse.builder().build();
        response.setUid(apply.getUid());
        response.setGroupUid(apply.getGroupUid());
        response.setApplicantUserUid(apply.getUserUid());
        response.setStatus(apply.getStatus());
        response.setAttempt(apply.getAttempt());
        response.setReason(apply.getReason());
        response.setRejectReason(apply.getRejectReason());
        response.setHandledByUid(apply.getHandledByUid());
        response.setHandledAt(apply.getHandledAt());
        response.setAppliedAt(apply.getCreatedAt());
        response.setOrgUid(apply.getOrgUid());
        response.setCreatedAt(apply.getCreatedAt());
        response.setUpdatedAt(apply.getUpdatedAt());
        //
        if (includeApplicant) {
            try {
                userRepository.findByUid(apply.getUserUid()).ifPresent(user -> {
                    response.setApplicantNickname(user.getNickname());
                    response.setApplicantAvatar(user.getAvatar());
                });
            } catch (Exception ex) {
                log.warn("load applicant for apply response failed: {}", apply.getUid());
            }
        }
        return response;
    }

    private int invalidatePendingInternal(String orgUid, String groupUid) {
        int updated = 0;
        int page = 0;
        int size = 100;
        while (true) {
            Pageable pageable = Pageable.ofSize(size).withPage(page);
            Page<GroupMemberApplyEntity> pendingPage = applyRepository
                    .findByOrgUidAndGroupUidInAndStatusAndDeletedFalse(orgUid, List.of(groupUid),
                            GroupMemberApplyStatusEnum.PENDING.name(), pageable);
            for (GroupMemberApplyEntity apply : pendingPage.getContent()) {
                apply.setStatus(GroupMemberApplyStatusEnum.INVALIDATED.name());
                apply.setHandledAt(ZonedDateTime.now());
                applyRepository.save(apply);
                updated++;
            }
            if (pendingPage.isLast()) {
                break;
            }
            page++;
        }
        return updated;
    }

    // ==================== 内部工具 ====================

    private GroupMemberApplyEntity lockApplyOrNotFound(String applyUid) {
        if (!StringUtils.hasText(applyUid)) {
            throw new IllegalArgumentException("apply uid is required");
        }
        Optional<GroupMemberApplyEntity> optional = StringUtils.hasText(applyUid)
                ? applyRepository.findWithLockByUid(applyUid)
                : Optional.empty();
        if (optional.isEmpty() || optional.get().isDeleted()) {
            throw new NotFoundException("apply not found: " + applyUid);
        }
        return optional.get();
    }

    private void checkAttempt(GroupMemberApplyEntity apply, Integer requestAttempt) {
        if (requestAttempt != null && !requestAttempt.equals(apply.getAttempt())) {
            throw GroupI18nExceptions.conflictApplyState();
        }
    }

    /** 截断字符串到指定长度（申请理由/拒绝原因列宽保护） */
    private String trimToLength(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    private void checkCapacity(GroupEntity group, int additions) {
        int max = group.getMaxMembers() == null ? 500 : group.getMaxMembers();
        if (group.getMembers().size() + additions > max) {
            throw GroupI18nExceptions.groupFull();
        }
    }

    /** 成员入库 + 本人 thread 创建（与 GroupRestService.join 相同语义，同事务） */
    private void addMemberToGroup(GroupEntity group, UserEntity applicant) {
        // 通过 userUid 查找申请人在群组织下的 Member
        Optional<MemberEntity> memberOptional = memberRestService.findByUserUidAndOrgUid(applicant.getUid(), group.getOrgUid());
        if (memberOptional.isEmpty()) {
            throw new NotFoundException("member not found for user: " + applicant.getUid());
        }
        MemberEntity member = memberOptional.get();
        if (group.getMembers().stream().noneMatch(m -> m.getUid().equals(member.getUid()))) {
            group.getMembers().add(member);
            // 订阅 topic：创建本人群会话 thread
            String user = UserProtobuf.builder()
                    .uid(group.getUid())
                    .nickname(group.getName())
                    .avatar(group.getAvatar())
                    .build()
                    .toJson();
            String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
            threadRestService.createGroupMemberThread(user, topic, group.getOrgUid(), applicant);
        }
        groupRepository.save(group);
    }

    private List<String> findAdministerableGroupUids(UserEntity user) {
        // 群主（显式 lambda 代替方法引用，避免未绑定方法引用的接收者触发空类型安全告警）
        List<String> groupUids = new ArrayList<>(groupRepository.findByCreator_UidAndDeletedFalse(user.getUid())
                .stream().map(group -> group.getUid()).collect(Collectors.toList()));
        // 管理员
        groupRepository.findByAdmins_UidAndDeletedFalse(user.getUid())
                .forEach(g -> {
                    if (!groupUids.contains(g.getUid())) {
                        groupUids.add(g.getUid());
                    }
                });
        return groupUids;
    }

    private void notifyGroupAdminsNewApply(GroupEntity group, GroupMemberApplyEntity apply, UserEntity applicant) {
        try {
            Set<String> recipientUids = new HashSet<>();
            if (group.getCreator() != null && StringUtils.hasText(group.getCreator().getUid())) {
                recipientUids.add(group.getCreator().getUid());
            }
            group.getAdmins().forEach(a -> {
                if (a != null && StringUtils.hasText(a.getUid())) {
                    recipientUids.add(a.getUid());
                }
            });
            JSONObject extra = new JSONObject();
            extra.put("groupUid", group.getUid());
            extra.put("applyUid", apply.getUid());
            extra.put("attempt", apply.getAttempt());
            extra.put("applicantUid", applicant.getUid());
            extra.put("applicantNickname", applicant.getNickname());
            extra.put("action", "group.apply.created");
            //
            for (String recipientUid : recipientUids) {
                NotificationRequest notification = NotificationRequest.builder()
                        .userUid(recipientUid)
                        .orgUid(group.getOrgUid())
                        .type(GroupNotificationConsts.TYPE_GROUP)
                        .title(GroupNotificationConsts.TITLE_APPLY_CREATED)
                        .content(applicant.getNickname() + " 申请加入群 " + group.getName())
                        .extra(extra.toJSONString())
                        .build();
                notificationService.dispatchSystemNotificationToUser(notification);
            }
        } catch (Exception ex) {
            log.warn("notify group apply created failed: applyUid={}, error={}", apply.getUid(), ex.getMessage());
        }
    }

    private void notifyApplicantResult(GroupEntity group, GroupMemberApplyEntity apply, UserEntity applicant,
            boolean approved, String rejectReason) {
        try {
            JSONObject extra = new JSONObject();
            extra.put("groupUid", group != null ? group.getUid() : apply.getGroupUid());
            extra.put("applyUid", apply.getUid());
            extra.put("attempt", apply.getAttempt());
            extra.put("action", approved ? "group.apply.approved" : "group.apply.rejected");
            if (StringUtils.hasText(rejectReason)) {
                extra.put("rejectReason", rejectReason);
            }
            // 群资料仅在群仍可读时携带（脱敏：群转私有后不泄露名称）
            String groupDisplayName = null;
            if (group != null && groupAccessService.isPublic(group)) {
                groupDisplayName = group.getName();
            }
            //
            NotificationRequest notification = NotificationRequest.builder()
                    .userUid(applicant.getUid())
                    .orgUid(apply.getOrgUid())
                    .type(GroupNotificationConsts.TYPE_GROUP)
                    .title(approved ? GroupNotificationConsts.TITLE_APPLY_APPROVED : GroupNotificationConsts.TITLE_APPLY_REJECTED)
                    .content(approved
                            ? "你加入群" + (groupDisplayName != null ? " " + groupDisplayName : "") + " 的申请已通过"
                            : "你加入群" + (groupDisplayName != null ? " " + groupDisplayName : "") + " 的申请被拒绝")
                    .extra(extra.toJSONString())
                    .build();
            notificationService.dispatchSystemNotificationToUser(notification);
        } catch (Exception ex) {
            log.warn("notify group apply result failed: applyUid={}, error={}", apply.getUid(), ex.getMessage());
        }
    }
}
