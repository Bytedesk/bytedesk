package com.bytedesk.core.rbac.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;

class UserRestServiceTest {

        private UserRepository userRepository;
        private AuthService authService;
        private UserService userService;
        private UserRestService userRestService;

        @BeforeEach
        void setUp() {
                userRepository = mock(UserRepository.class);
                authService = mock(AuthService.class);
                userService = mock(UserService.class);
                userRestService = new UserRestService(
                                userRepository,
                                authService,
                                userService,
                                mock(UserDetailsServiceImpl.class),
                                mock(MemberRepository.class),
                                mock(BCryptPasswordEncoder.class));
        }

    @Test
    void switchUserOrganizationShouldUpdateTargetsCurrentOrganization() {
        UserEntity superUser = UserEntity.builder()
                .uid("super-1")
                .superUser(true)
                .build();

        OrganizationEntity orgA = OrganizationEntity.builder().uid("org-a").name("Org A").build();
        OrganizationEntity orgB = OrganizationEntity.builder().uid("org-b").name("Org B").build();

        RoleEntity roleA = RoleEntity.builder().uid("role-a").name("Role A").build();
        RoleEntity roleB = RoleEntity.builder().uid("role-b").name("Role B").build();

        UserEntity targetUser = UserEntity.builder()
                .uid("user-1")
                .username("target")
                .platform("BYTEDESK")
                .currentOrganization(orgA)
                .build();
        targetUser.getCurrentRoles().add(roleA);
        targetUser.getUserOrganizationRoles().add(UserOrganizationRoleEntity.builder()
                .id(1L)
                .user(targetUser)
                .organization(orgA)
                .roles(new LinkedHashSet<>(Set.of(roleA)))
                .build());
        targetUser.getUserOrganizationRoles().add(UserOrganizationRoleEntity.builder()
                .id(2L)
                .user(targetUser)
                .organization(orgB)
                .roles(new LinkedHashSet<>(Set.of(roleB)))
                .build());

        when(authService.getUser()).thenReturn(superUser);
        when(userRepository.findByUidWithOrganizations("user-1")).thenReturn(Optional.of(targetUser));
        doAnswer(invocation -> {
            targetUser.setCurrentOrganization(orgB);
            return targetUser;
        }).when(userService).ensureCurrentOrganization(eq(targetUser), eq("org-b"));
        when(userService.addRoleUser(targetUser)).thenReturn(targetUser);
        when(userService.save(targetUser)).thenReturn(targetUser);

        UserResponse response = userRestService.switchUserOrganization("user-1", "org-b");

        assertThat(targetUser.getCurrentOrganization()).isNotNull();
        assertThat(targetUser.getCurrentOrganization().getUid()).isEqualTo("org-b");
        assertThat(targetUser.getCurrentRoles()).extracting(r -> r.getUid()).contains("role-b");
        assertThat(response.getCurrentOrganization()).isNotNull();
        assertThat(response.getCurrentOrganization().getUid()).isEqualTo("org-b");
    }

    @Test
        void queryTransferableShouldSearchByUidAndTreatDefaultOrganizationAsUnassigned() {
        UserEntity superUser = UserEntity.builder()
                .uid("super-1")
                .superUser(true)
                .build();
        UserRequest request = UserRequest.builder()
                .orgUid("org-a")
                .searchText("2007902428595242")
                .build();

        when(authService.getUser()).thenReturn(superUser);
        when(userRepository.findTransferableAdminCandidates(
                eq("%2007902428595242%"),
                eq(Set.of(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID, BytedeskConsts.DEFAULT_SYSTEM_UID)),
                eq(request.getPageable()))).thenReturn(Page.empty(request.getPageable()));

        userRestService.queryTransferable(request);

        verify(userRepository).findTransferableAdminCandidates(
                eq("%2007902428595242%"),
                eq(Set.of(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID, BytedeskConsts.DEFAULT_SYSTEM_UID)),
                eq(request.getPageable()));
    }

    @Test
    void queryTransferableShouldPassNullPatternWhenSearchTextEmpty() {
        UserEntity superUser = UserEntity.builder()
                .uid("super-1")
                .superUser(true)
                .build();
        UserRequest request = UserRequest.builder()
                .orgUid("org-a")
                .searchText("")
                .build();

        when(authService.getUser()).thenReturn(superUser);
        when(userRepository.findTransferableAdminCandidates(
                isNull(),
                eq(Set.of(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID, BytedeskConsts.DEFAULT_SYSTEM_UID)),
                eq(request.getPageable()))).thenReturn(Page.empty(request.getPageable()));

        userRestService.queryTransferable(request);

        // 空搜索时 pattern 必须以 null 传入（repository 参数已标注 @Nullable），
        // 否则 Spring Data JPA 会抛 "Parameter pattern ... must not be null" 导致接口 500
        verify(userRepository).findTransferableAdminCandidates(
                isNull(),
                eq(Set.of(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID, BytedeskConsts.DEFAULT_SYSTEM_UID)),
                eq(request.getPageable()));
    }
}