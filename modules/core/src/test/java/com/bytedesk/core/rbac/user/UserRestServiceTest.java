package com.bytedesk.core.rbac.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;

class UserRestServiceTest {

    @Test
    void switchUserOrganizationShouldUpdateTargetsCurrentOrganization() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);

        UserRestService userRestService = new UserRestService(
                userRepository,
                authService,
                userService,
                mock(UserDetailsServiceImpl.class),
                mock(MemberRepository.class),
                mock(BCryptPasswordEncoder.class));

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
        assertThat(targetUser.getCurrentRoles()).extracting(RoleEntity::getUid).contains("role-b");
        assertThat(response.getCurrentOrganization()).isNotNull();
        assertThat(response.getCurrentOrganization().getUid()).isEqualTo("org-b");
    }
}