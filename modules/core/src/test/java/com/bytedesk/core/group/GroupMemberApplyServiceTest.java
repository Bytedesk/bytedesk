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
package com.bytedesk.core.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.bytedesk.core.group.apply.GroupMemberApplyEntity;
import com.bytedesk.core.group.apply.GroupMemberApplyRepository;
import com.bytedesk.core.group.apply.GroupMemberApplyRequest;
import com.bytedesk.core.group.apply.GroupMemberApplyResponse;
import com.bytedesk.core.group.apply.GroupMemberApplyService;
import com.bytedesk.core.group.apply.GroupMemberApplyStatusEnum;
import com.bytedesk.core.group.enums.GroupVisibilityEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.notification.NotificationService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

/**
 * 入群申请状态机单测：创建幂等、审批入群、拒绝、撤回、私有群禁止申请、失效。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GroupMemberApplyServiceTest {

    @Mock
    private GroupMemberApplyRepository applyRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupAccessService groupAccessService;

    @Mock
    private MemberRestService memberRestService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UidUtils uidUtils;

    @InjectMocks
    private GroupMemberApplyService applyService;

    private UserEntity applicant;
    private UserEntity admin;
    private MemberEntity applicantMember;
    private GroupEntity publicGroup;
    private GroupEntity privateGroup;
    private GroupMemberApplyEntity pendingApply;

    @BeforeEach
    void setUp() {
        com.bytedesk.core.rbac.organization.OrganizationEntity org = new com.bytedesk.core.rbac.organization.OrganizationEntity();
        org.setUid("org-1");
        applicant = UserEntity.builder().uid("user-1").username("applicant").currentOrganization(org).build();
        admin = UserEntity.builder().uid("admin-1").username("admin").currentOrganization(org).build();
        applicantMember = MemberEntity.builder().uid("member-1").build();
        //
        publicGroup = GroupEntity.builder().build();
        publicGroup.setUid("group-public");
        publicGroup.setOrgUid("org-1");
        publicGroup.setVisibility(GroupVisibilityEnum.PUBLIC.name());
        publicGroup.setNeedApproval(true);
        publicGroup.setMaxMembers(500);
        publicGroup.setCreator(admin);
        //
        privateGroup = GroupEntity.builder().build();
        privateGroup.setUid("group-private");
        privateGroup.setOrgUid("org-1");
        privateGroup.setVisibility(GroupVisibilityEnum.PRIVATE.name());
        privateGroup.setCreator(admin);
        privateGroup.setMaxMembers(500);
        //
        pendingApply = GroupMemberApplyEntity.builder().build();
        pendingApply.setUid("apply-1");
        pendingApply.setGroupUid("group-public");
        pendingApply.setUserUid("user-1");
        pendingApply.setOrgUid("org-1");
        pendingApply.setStatus(GroupMemberApplyStatusEnum.PENDING.name());
        pendingApply.setAttempt(1);
        //
        lenient().when(groupAccessService.requireUser()).thenReturn(applicant);
        lenient().when(groupAccessService.findGroupOrNotFound("group-public")).thenReturn(publicGroup);
        lenient().when(groupAccessService.findGroupOrNotFound("group-private")).thenReturn(privateGroup);
        lenient().when(groupAccessService.isPrivate(any(GroupEntity.class))).thenAnswer(
                inv -> GroupVisibilityEnum.PRIVATE.name().equalsIgnoreCase(((GroupEntity) inv.getArgument(0)).getVisibility()));
        lenient().when(groupAccessService.isMemberOrAdmin(any(GroupEntity.class), anyString())).thenReturn(false);
        lenient().when(applyRepository.save(any(GroupMemberApplyEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(applyRepository.findWithLockByUid("apply-1")).thenReturn(Optional.of(pendingApply));
        lenient().when(userRepository.findByUid("user-1")).thenReturn(Optional.of(applicant));
        lenient().when(uidUtils.getUid()).thenReturn("apply-new");
    }

    private GroupMemberApplyRequest createRequest(String groupUid) {
        GroupMemberApplyRequest request = new GroupMemberApplyRequest();
        request.setGroupUid(groupUid);
        return request;
    }

    @Test
    void createApplyOnPrivateGroupShouldBeRejected() {
        assertThatThrownBy(() -> applyService.create(createRequest("group-private")))
                .hasMessageContaining("apply");
        verify(applyRepository, never()).save(any(GroupMemberApplyEntity.class));
    }

    @Test
    void createDuplicatePendingApplyShouldBeIdempotent() {
        when(applyRepository.findByOrgUidAndGroupUidAndUserUidAndDeletedFalse("org-1", "group-public", "user-1"))
                .thenReturn(Optional.of(pendingApply));
        GroupMemberApplyResponse response = applyService.create(createRequest("group-public"));
        assertThat(response.getStatus()).isEqualTo(GroupMemberApplyStatusEnum.PENDING.name());
        assertThat(response.getAttempt()).isEqualTo(1);
        // 幂等：不再 save
        verify(applyRepository, never()).save(any(GroupMemberApplyEntity.class));
    }

    @Test
    void createAfterRejectedShouldIncrementAttempt() {
        pendingApply.setStatus(GroupMemberApplyStatusEnum.REJECTED.name());
        when(applyRepository.findByOrgUidAndGroupUidAndUserUidAndDeletedFalse("org-1", "group-public", "user-1"))
                .thenReturn(Optional.of(pendingApply));
        GroupMemberApplyResponse response = applyService.create(createRequest("group-public"));
        assertThat(response.getAttempt()).isEqualTo(2);
        assertThat(response.getStatus()).isEqualTo(GroupMemberApplyStatusEnum.PENDING.name());
        verify(applyRepository).save(any(GroupMemberApplyEntity.class));
    }

    @Test
    void approveShouldAddMemberAndCreateThread() {
        // 管理员操作
        lenient().when(groupAccessService.requireUser()).thenReturn(admin);
        when(groupAccessService.requireAdministerableGroup("group-public")).thenReturn(publicGroup);
        when(memberRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(applicantMember));
        //
        GroupMemberApplyRequest request = new GroupMemberApplyRequest();
        request.setUid("apply-1");
        GroupMemberApplyResponse response = applyService.approve(request);
        //
        assertThat(response.getStatus()).isEqualTo(GroupMemberApplyStatusEnum.APPROVED.name());
        assertThat(publicGroup.getMembers()).extracting(member -> member.getUid()).contains("member-1");
        verify(threadRestService).createGroupMemberThread(anyString(), anyString(), anyString(), any(UserEntity.class));
        verify(groupRepository).save(any(GroupEntity.class));
    }

    @Test
    void approveNonPendingShouldConflict() {
        pendingApply.setStatus(GroupMemberApplyStatusEnum.APPROVED.name());
        GroupMemberApplyRequest request = new GroupMemberApplyRequest();
        request.setUid("apply-1");
        assertThatThrownBy(() -> applyService.approve(request))
                .hasMessageContaining("conflict");
        verify(groupRepository, never()).save(any(GroupEntity.class));
    }

    @Test
    void approveStaleAttemptShouldConflict() {
        pendingApply.setAttempt(2);
        GroupMemberApplyRequest request = new GroupMemberApplyRequest();
        request.setUid("apply-1");
        request.setAttempt(1);
        assertThatThrownBy(() -> applyService.approve(request))
                .hasMessageContaining("conflict");
    }

    @Test
    void rejectShouldSetReasonAndHandler() {
        when(groupAccessService.requireAdministerableGroup("group-public")).thenReturn(publicGroup);
        GroupMemberApplyRequest request = new GroupMemberApplyRequest();
        request.setUid("apply-1");
        request.setRejectReason("不适合");
        GroupMemberApplyResponse response = applyService.reject(request);
        assertThat(response.getStatus()).isEqualTo(GroupMemberApplyStatusEnum.REJECTED.name());
        assertThat(response.getRejectReason()).isEqualTo("不适合");
    }

    @Test
    void cancelByOtherUserShouldBeRejected() {
        UserEntity other = UserEntity.builder().uid("user-2").build();
        when(groupAccessService.requireUser()).thenReturn(other);
        GroupMemberApplyRequest request = new GroupMemberApplyRequest();
        request.setUid("apply-1");
        assertThatThrownBy(() -> applyService.cancel(request))
                .hasMessageContaining("apply");
    }

    @Test
    void invalidatePendingShouldMarkInvalidated() {
        // 模拟一页 PENDING 数据
        org.springframework.data.domain.Page<GroupMemberApplyEntity> page =
                new org.springframework.data.domain.PageImpl<>(java.util.List.of(pendingApply));
        when(applyRepository.findByOrgUidAndGroupUidInAndStatusAndDeletedFalse(anyString(), anyList(), anyString(),
                any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        int updated = applyService.invalidatePendingByGroupUid("org-1", "group-public");
        assertThat(updated).isEqualTo(1);
        assertThat(pendingApply.getStatus()).isEqualTo(GroupMemberApplyStatusEnum.INVALIDATED.name());
    }
}
