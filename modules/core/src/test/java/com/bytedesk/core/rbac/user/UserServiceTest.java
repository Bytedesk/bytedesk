package com.bytedesk.core.rbac.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.RoleConsts;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.uid.UidUtils;

import jakarta.persistence.EntityManager;

class UserServiceTest {

    @Test
    void removeUserFromOrganizationShouldSwitchToNextAvailableOrganization() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRestService roleRestService = mock(RoleRestService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        UserService userService = createUserService(userRepository, roleRestService, organizationRepository, entityManager);

        OrganizationEntity orgA = OrganizationEntity.builder().uid("org-a").name("Org A").enabled(true).build();
        OrganizationEntity orgB = OrganizationEntity.builder().uid("org-b").name("Org B").enabled(true).build();

        RoleEntity roleA = RoleEntity.builder().id(11L).uid("role-a").name("Role A").build();
        RoleEntity roleB = RoleEntity.builder().id(12L).uid("role-b").name("Role B").build();
        RoleEntity roleUser = RoleEntity.builder().id(99L).uid("role-user").name(RoleConsts.ROLE_USER).build();

        UserEntity user = UserEntity.builder()
                .uid("user-1")
                .username("target")
                .platform("BYTEDESK")
                .currentOrganization(orgA)
                .build();
        user.getCurrentRoles().add(roleA);
        user.getUserOrganizationRoles().add(UserOrganizationRoleEntity.builder()
                .id(1L)
                .user(user)
                .organization(orgA)
                .roles(new LinkedHashSet<>(Set.of(roleA)))
                .build());
        user.getUserOrganizationRoles().add(UserOrganizationRoleEntity.builder()
                .id(2L)
                .user(user)
                .organization(orgB)
                .roles(new LinkedHashSet<>(Set.of(roleB)))
                .build());

        when(userRepository.findByUidWithOrganizations("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(organizationRepository.findByUid("org-b")).thenReturn(Optional.of(orgB));
        when(roleRestService.findByNamePlatform(RoleConsts.ROLE_USER)).thenReturn(Optional.of(roleUser));
        when(entityManager.find(RoleEntity.class, 99L)).thenReturn(roleUser);

        UserEntity updated = userService.removeUserFromOrganization("user-1", "org-a");

        assertThat(updated.getCurrentOrganization()).isNotNull();
        assertThat(updated.getCurrentOrganization().getUid()).isEqualTo("org-b");
        assertThat(updated.getUserOrganizationRoles())
                .extracting(uor -> uor.getOrganization().getUid())
                .containsExactly("org-b");
        assertThat(updated.getCurrentRoles())
                .extracting(RoleEntity::getUid)
                .contains("role-b");
    }

    @Test
    void removeUserFromOrganizationShouldClearCurrentOrganizationWhenNoAvailableOrganizationRemains() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRestService roleRestService = mock(RoleRestService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        UserService userService = createUserService(userRepository, roleRestService, organizationRepository, entityManager);

        OrganizationEntity orgA = OrganizationEntity.builder().uid("org-a").name("Org A").enabled(true).build();
        OrganizationEntity orgB = OrganizationEntity.builder().uid("org-b").name("Org B").enabled(false).build();

        RoleEntity roleA = RoleEntity.builder().id(11L).uid("role-a").name("Role A").build();
        RoleEntity roleB = RoleEntity.builder().id(12L).uid("role-b").name("Role B").build();
        RoleEntity roleUser = RoleEntity.builder().id(99L).uid("role-user").name(RoleConsts.ROLE_USER).build();

        UserEntity user = UserEntity.builder()
                .uid("user-2")
                .username("target-2")
                .platform("BYTEDESK")
                .currentOrganization(orgA)
                .build();
        user.getCurrentRoles().add(roleA);
        user.getUserOrganizationRoles().add(UserOrganizationRoleEntity.builder()
                .id(1L)
                .user(user)
                .organization(orgA)
                .roles(new LinkedHashSet<>(Set.of(roleA)))
                .build());
        user.getUserOrganizationRoles().add(UserOrganizationRoleEntity.builder()
                .id(2L)
                .user(user)
                .organization(orgB)
                .roles(new LinkedHashSet<>(Set.of(roleB)))
                .build());

        when(userRepository.findByUidWithOrganizations("user-2")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleRestService.findByNamePlatform(RoleConsts.ROLE_USER)).thenReturn(Optional.of(roleUser));
        when(entityManager.find(RoleEntity.class, 99L)).thenReturn(roleUser);

        UserEntity updated = userService.removeUserFromOrganization("user-2", "org-a");

        assertThat(updated.getCurrentOrganization()).isNull();
        assertThat(updated.getUserOrganizationRoles())
                .extracting(uor -> uor.getOrganization().getUid())
                .containsExactly("org-b");
        assertThat(updated.getCurrentRoles())
                .extracting(RoleEntity::getUid)
                .contains("role-user");
    }

    private UserService createUserService(
            UserRepository userRepository,
            RoleRestService roleRestService,
            OrganizationRepository organizationRepository,
            EntityManager entityManager) {
        UserService userService = new UserService(
                userRepository,
                mock(ModelMapper.class),
                roleRestService,
                mock(BytedeskProperties.class),
                mock(BCryptPasswordEncoder.class),
                mock(UidUtils.class),
                organizationRepository,
                mock(BytedeskEventPublisher.class),
                mock(AuthService.class),
                mock(TokenRestService.class));
        ReflectionTestUtils.setField(userService, "entityManager", entityManager);
        return userService;
    }
}