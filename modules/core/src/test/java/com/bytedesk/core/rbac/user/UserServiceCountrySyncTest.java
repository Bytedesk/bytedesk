package com.bytedesk.core.rbac.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.member.MemberRequest;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.CountryCodeUtils;

import jakarta.persistence.ManyToMany;

class UserServiceCountrySyncTest {

        @Test
        void userOrganizationRolesShouldNotCascadeRoleRemoval() throws NoSuchFieldException {
                ManyToMany annotation = UserOrganizationRoleEntity.class
                                .getDeclaredField("roles")
                                .getAnnotation(ManyToMany.class);

                assertTrue(annotation != null);
                assertFalse(java.util.Arrays.asList(annotation.cascade()).contains(jakarta.persistence.CascadeType.REMOVE));
                assertFalse(java.util.Arrays.asList(annotation.cascade()).contains(jakarta.persistence.CascadeType.ALL));
        }

    @Test
    void updateUserFromMemberShouldCopyCountryBeforeUpdatingRoles() {
        UserRepository userRepository = mock(UserRepository.class);

        UserService userService = new UserService(
                userRepository,
                mock(ModelMapper.class),
                mock(RoleRestService.class),
                mock(BytedeskProperties.class),
                mock(BCryptPasswordEncoder.class),
                mock(UidUtils.class),
                mock(OrganizationRepository.class),
                mock(BytedeskEventPublisher.class),
                mock(AuthService.class),
                mock(TokenRestService.class));

        UserEntity user = UserEntity.builder()
                .id(1L)
                .country("86")
                .build();
        OrganizationEntity organization = OrganizationEntity.builder()
                .id(10L)
                .uid("org-1")
                .name("Test Org")
                .build();
        RoleEntity role = RoleEntity.builder()
                .id(100L)
                .uid(BytedeskConsts.DEFAULT_ROLE_USER_UID)
                .name("ROLE_USER")
                .build();
        user.setCurrentOrganization(organization);
        user.getUserOrganizationRoles().add(UserOrganizationRoleEntity.builder()
                .user(user)
                .organization(organization)
                .roles(new java.util.LinkedHashSet<>(java.util.Set.of(role)))
                .build());
        MemberRequest request = MemberRequest.builder()
                .country("1")
                .build();
        request.setRoleUids(new java.util.LinkedHashSet<>(java.util.Set.of(BytedeskConsts.DEFAULT_ROLE_USER_UID)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserEntity updated = userService.updateUserFromMember(user, request);

        assertSame(user, updated);
        assertEquals("1", updated.getCountry());
        verify(userRepository).findById(1L);
    }

    @Test
    void changeMobileShouldUpdateCountryWhenProvided() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthService authService = mock(AuthService.class);

        UserService userService = new UserService(
                userRepository,
                mock(ModelMapper.class),
                mock(RoleRestService.class),
                mock(BytedeskProperties.class),
                mock(BCryptPasswordEncoder.class),
                mock(UidUtils.class),
                mock(OrganizationRepository.class),
                mock(BytedeskEventPublisher.class),
                authService,
                mock(TokenRestService.class));

        UserEntity authUser = UserEntity.builder().uid("auth-user").build();
        UserEntity storedUser = UserEntity.builder()
                .uid("auth-user")
                .mobile("13800138000")
                .country("86")
                .platform("BYTEDESK")
                .build();

        when(authService.getUser()).thenReturn(authUser);
        when(userRepository.findByUid("auth-user")).thenReturn(Optional.of(storedUser));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRequest request = UserRequest.builder()
                .mobile("13800138000")
                .country("1")
                .platform("BYTEDESK")
                .build();

        UserResponse response = userService.changeMobile(request);

        assertEquals("1", storedUser.getCountry());
        assertEquals("1", response.getCountry());
        verify(userRepository).save(storedUser);
    }

        @Test
        void existsByMobileAndPlatformShouldNormalizeBlankCountryToDefault() {
                UserRepository userRepository = mock(UserRepository.class);

                UserService userService = new UserService(
                                userRepository,
                                mock(ModelMapper.class),
                                mock(RoleRestService.class),
                                mock(BytedeskProperties.class),
                                mock(BCryptPasswordEncoder.class),
                                mock(UidUtils.class),
                                mock(OrganizationRepository.class),
                                mock(BytedeskEventPublisher.class),
                                mock(AuthService.class),
                                mock(TokenRestService.class));

                when(userRepository.existsByMobileAndCountryAndPlatformAndDeletedFalse(
                                "13800138000",
                                CountryCodeUtils.DEFAULT_COUNTRY,
                                "BYTEDESK")).thenReturn(true);

                assertTrue(userService.existsByMobileAndPlatform("13800138000", null, "BYTEDESK"));
                verify(userRepository).existsByMobileAndCountryAndPlatformAndDeletedFalse(
                                "13800138000",
                                CountryCodeUtils.DEFAULT_COUNTRY,
                                "BYTEDESK");
        }
}