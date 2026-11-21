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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.bytedesk.core.group.apply.GroupMemberApplyRequest;
import com.bytedesk.core.group.apply.GroupMemberApplyService;
import com.bytedesk.core.group.enums.GroupVisibilityEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberProtobuf;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

/**
 * 群可见性单测：私有群 join 拒绝、公开群免审批直接加入、幂等、容量、非法可见性回退。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GroupVisibilityTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private MemberRestService memberRestService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private ObjectProvider<GroupRobotValidator> validatorProvider;

    @Mock
    private GroupAccessService groupAccessService;

    @Mock
    private GroupMemberApplyService groupMemberApplyService;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    private GroupRestService groupRestService;

    private UserEntity applicant;
    private MemberEntity applicantMember;
    private GroupEntity publicGroup;
    private GroupEntity privateGroup;

    @BeforeEach
    void setUp() {
        com.bytedesk.core.rbac.organization.OrganizationEntity org = new com.bytedesk.core.rbac.organization.OrganizationEntity();
        org.setUid("org-1");
        applicant = UserEntity.builder().uid("user-1").username("applicant").currentOrganization(org).build();
        lenient().when(authService.getUser()).thenReturn(applicant);
        applicantMember = MemberEntity.builder().uid("member-1").build();
        //
        publicGroup = GroupEntity.builder().build();
        publicGroup.setUid("group-public");
        publicGroup.setOrgUid("org-1");
        publicGroup.setVisibility(GroupVisibilityEnum.PUBLIC.name());
        publicGroup.setNeedApproval(false);
        publicGroup.setMaxMembers(500);
        publicGroup.setCreator(applicant);
        //
        privateGroup = GroupEntity.builder().build();
        privateGroup.setUid("group-private");
        privateGroup.setOrgUid("org-1");
        privateGroup.setVisibility(GroupVisibilityEnum.PRIVATE.name());
        privateGroup.setCreator(applicant);
        privateGroup.setMaxMembers(500);
        //
        lenient().when(authService.getUser()).thenReturn(applicant);
        lenient().when(groupRepository.findByUid("group-public")).thenReturn(Optional.of(publicGroup));
        lenient().when(groupRepository.findByUid("group-private")).thenReturn(Optional.of(privateGroup));
        lenient().when(groupRepository.save(any(GroupEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(memberRestService.findByUserUidAndOrgUid("user-1", "org-1"))
                .thenReturn(Optional.of(applicantMember));
        // 委托 GroupAccessService 的判定（单测聚焦闸门编排逻辑）
        lenient().when(groupAccessService.findGroupOrNotFound(anyString()))
                .thenAnswer(inv -> {
                    String uid = inv.getArgument(0);
                    if ("group-public".equals(uid)) {
                        return publicGroup;
                    }
                    if ("group-private".equals(uid)) {
                        return privateGroup;
                    }
                    throw new RuntimeException("group not found: " + uid);
                });
        lenient().when(groupAccessService.isPrivate(any(GroupEntity.class)))
                .thenAnswer(inv -> {
                    GroupEntity g = inv.getArgument(0);
                    return g == null || GroupVisibilityEnum.PRIVATE.name().equalsIgnoreCase(g.getVisibility());
                });
        lenient().when(groupAccessService.isMemberOrAdmin(any(GroupEntity.class), anyString()))
                .thenAnswer(inv -> {
                    GroupEntity g = inv.getArgument(0);
                    String userUid = inv.getArgument(1);
                    return g.getMembers().stream().anyMatch(m -> m.getUid().equals("member-1"))
                            && "user-1".equals(userUid);
                });
        lenient().when(uidUtils.getUid()).thenReturn("uid-1");
        // 构造被测服务（modelMapper 用桩，避免 NPE；响应用简单的空对象承接）
        groupRestService = new GroupRestService(
                groupRepository,
                authService,
                modelMapper,
                uidUtils,
                memberRestService,
                threadRestService,
                validatorProvider,
                groupAccessService,
                groupMemberApplyService);
        lenient().when(modelMapper.map(any(GroupEntity.class), eq(GroupResponse.class)))
                .thenAnswer(inv -> GroupResponse.builder().build().setUid("group-public"));
        lenient().when(modelMapper.map(any(MemberEntity.class), eq(MemberProtobuf.class)))
                .thenAnswer(inv -> MemberProtobuf.builder().build());
    }

    private GroupRequest joinRequest(String groupUid) {
        GroupRequest request = new GroupRequest();
        request.setUid(groupUid);
        return request;
    }

    @Test
    void joinPrivateGroupShouldBeRejected() {
        assertThatThrownBy(() -> groupRestService.join(joinRequest("group-private")))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("private");
        verify(groupRepository, never()).save(any(GroupEntity.class));
    }

    @Test
    void joinPublicGroupWithoutApprovalShouldAddMember() {
        GroupResponse response = groupRestService.join(joinRequest("group-public"));
        assertThat(response).isNotNull();
        assertThat(publicGroup.getMembers()).extracting(member -> member.getUid()).contains("member-1");
        verify(threadRestService).createGroupMemberThread(anyString(), anyString(), anyString(), any(UserEntity.class));
    }

    @Test
    void joinTwiceShouldBeIdempotent() {
        publicGroup.getMembers().add(applicantMember);
        GroupResponse response = groupRestService.join(joinRequest("group-public"));
        assertThat(response).isNotNull();
        // 幂等返回，不再 save
        verify(groupRepository, never()).save(any(GroupEntity.class));
    }

    @Test
    void joinPublicGroupWithApprovalShouldCreateApply() {
        publicGroup.setNeedApproval(true);
        GroupResponse response = groupRestService.join(joinRequest("group-public"));
        assertThat(response).isNotNull();
        assertThat(response.getPendingApply()).isTrue();
        assertThat(response.getJoined()).isFalse();
        // 分流到申请服务，不直接入群
        verify(groupMemberApplyService).create(any(GroupMemberApplyRequest.class));
        verify(groupRepository, never()).save(any(GroupEntity.class));
    }

    @Test
    void joinFullGroupShouldBeRejected() {
        publicGroup.setMaxMembers(publicGroup.getMembers().size());
        assertThatThrownBy(() -> groupRestService.join(joinRequest("group-public")))
                .hasMessageContaining("exceeded");
    }

    @Test
    void createGroupShouldNotAddCreatorTwiceWhenIncludedInMemberUids() {
        lenient().when(memberRestService.findByUid("member-1")).thenReturn(Optional.of(applicantMember));
        GroupRequest request = new GroupRequest();
        request.setName("new group");
        request.setMemberUids(List.of("member-1"));

        groupRestService.create(request);

        ArgumentCaptor<GroupEntity> groupCaptor = ArgumentCaptor.forClass(GroupEntity.class);
        verify(groupRepository).save(groupCaptor.capture());
        assertThat(groupCaptor.getValue().getMembers())
                .extracting(member -> member.getUid())
                .containsExactly("member-1");
    }

    @Test
    void invalidVisibilityShouldFallbackToPrivate() {
        assertThat(GroupVisibilityEnum.fromValue("HACKED")).isEqualTo(GroupVisibilityEnum.PRIVATE);
        assertThat(GroupVisibilityEnum.fromValue(null)).isEqualTo(GroupVisibilityEnum.PRIVATE);
        assertThat(GroupVisibilityEnum.fromValue("public")).isEqualTo(GroupVisibilityEnum.PUBLIC);
    }

    @Test
    void queryPublicShouldIncludeJoinedGroupsWithFlag() {
        // 已加入的公开群必须出现在公开列表（规划 §4.2/D9），并带 joined=true 标记
        publicGroup.getMembers().add(applicantMember);
        Page<GroupEntity> page = new PageImpl<>(java.util.List.of(publicGroup));
        lenient().when(groupRepository.findAll(
                ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<GroupEntity>>any(),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);
        lenient().when(groupMemberApplyService.mapPendingByGroupUids(anyString(), anyString(), any()))
                .thenReturn(java.util.Map.of("group-public", false));

        Page<GroupResponse> result = groupRestService.queryPublic(new GroupRequest());

        assertThat(result.getContent()).hasSize(1);
        GroupResponse response = result.getContent().get(0);
        assertThat(response.getJoined()).isTrue();
        assertThat(response.getPendingApply()).isFalse();
        // 摘要 DTO：不含成员预览/机器人
        assertThat(response.getMemberPreview()).isNull();
        assertThat(response.getRobotUids()).isNull();
    }

    @Test
    void queryPublicShouldMarkPendingApplyForNotJoinedGroup() {
        // 未加入 + 待审批：joined=false / pendingApply=true
        Page<GroupEntity> page = new PageImpl<>(java.util.List.of(publicGroup));
        lenient().when(groupRepository.findAll(
                ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<GroupEntity>>any(),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);
        lenient().when(groupMemberApplyService.mapPendingByGroupUids(anyString(), anyString(), any()))
                .thenReturn(java.util.Map.of("group-public", true));

        Page<GroupResponse> result = groupRestService.queryPublic(new GroupRequest());

        assertThat(result.getContent()).hasSize(1);
        GroupResponse response = result.getContent().get(0);
        assertThat(response.getJoined()).isFalse();
        assertThat(response.getPendingApply()).isTrue();
    }
}
