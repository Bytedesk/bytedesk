package com.bytedesk.core.rbac.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InOrder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.exception.ForbiddenException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;

@SuppressWarnings("unchecked")
class OrganizationRestServiceTest {

    /** Mock helper：屏蔽 OrganizationRestService 构造参数变化对各个用例的影响 */
    private OrganizationRestService newOrganizationRestService(
            AuthService authService,
            UserService userService,
            OrganizationRepository organizationRepository) {
        CacheManager cacheManager = mock(CacheManager.class);
        when(cacheManager.getCache("organization")).thenReturn(mock(Cache.class));
        return newOrganizationRestService(authService, userService, organizationRepository, cacheManager);
    }

    private OrganizationRestService newOrganizationRestService(
            AuthService authService,
            UserService userService,
            OrganizationRepository organizationRepository,
            CacheManager cacheManager) {
        return new OrganizationRestService(
                authService,
                userService,
                organizationRepository,
                mock(BytedeskProperties.class),
                mock(UidUtils.class),
                mock(ModelMapper.class),
                cacheManager,
                mock(ApplicationEventPublisher.class),
                mock(ObjectProvider.class));
    }

    @Test
    void deleteByUidShouldRemoveUsersBeforeLogicalDelete() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        OrganizationRestService organizationRestService = newOrganizationRestService(
                authService, userService, organizationRepository);

        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-a")
                .name("Org A")
                .enabled(true)
                .build();

        when(organizationRepository.findByUid("org-a")).thenReturn(Optional.of(organization));
        when(organizationRepository.save(any(OrganizationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        organizationRestService.deleteByUid("org-a");

        InOrder ordered = inOrder(userService, organizationRepository);
        ordered.verify(userService).removeAllUsersFromOrganization("org-a");
        ordered.verify(organizationRepository).save(eq(organization));

        assertThat(organization.isDeleted()).isTrue();
    }

    @Test
    void deleteByUidShouldRejectDefaultOrganization() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        OrganizationRestService organizationRestService = newOrganizationRestService(
                authService, userService, organizationRepository);

        assertThatThrownBy(() -> organizationRestService.deleteByUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateBySuperShouldRejectDisablingDefaultOrganization() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        OrganizationRestService organizationRestService = newOrganizationRestService(
                authService, userService, organizationRepository);

        OrganizationEntity organization = OrganizationEntity.builder()
                .uid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
            .name("Default Org")
            .code("bytedesk")
            .description("default")
            .enabled(true)
            .build();

        OrganizationRequest request = new OrganizationRequest();
        request.setUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        request.setName("Default Org");
        request.setCode("bytedesk");
        request.setDescription("default");
        request.setEnabled(false);

        when(organizationRepository.findByUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID))
            .thenReturn(Optional.of(organization));

        assertThatThrownBy(() -> organizationRestService.updateBySuper(request))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateEnabledBySuperShouldUpdateRegularOrganization() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        OrganizationRestService organizationRestService = newOrganizationRestService(
                authService, userService, organizationRepository);

        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-a")
                .name("Org A")
                .code("org-a")
                .description("org-a")
                .enabled(true)
                .build();

        OrganizationRequest request = new OrganizationRequest();
        request.setUid("org-a");
        request.setEnabled(false);

        when(organizationRepository.findByUid("org-a")).thenReturn(Optional.of(organization));
        when(organizationRepository.save(any(OrganizationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrganizationResponse response = organizationRestService.updateEnabledBySuper(request);

        assertThat(response.getEnabled()).isFalse();
        assertThat(organization.getEnabled()).isFalse();
    }

        @Test
        void saveShouldEvictOrganizationCacheSoVipLevelChangesAreVisibleToEntitlementGate() {
                AuthService authService = mock(AuthService.class);
                UserService userService = mock(UserService.class);
                OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
                CacheManager cacheManager = mock(CacheManager.class);
                Cache organizationCache = mock(Cache.class);
                when(cacheManager.getCache("organization")).thenReturn(organizationCache);

                OrganizationRestService organizationRestService = newOrganizationRestService(
                                authService, userService, organizationRepository, cacheManager);

                OrganizationEntity organization = OrganizationEntity.builder()
                                .uid("df_org_uid")
                                .name("Default Platform Org")
                                .code("df")
                                .description("default")
                                .vipLevel(2)
                                .enabled(true)
                                .build();
                when(organizationRepository.save(organization)).thenReturn(organization);

                OrganizationEntity saved = organizationRestService.save(organization);

                assertThat(saved.getVipLevel()).isEqualTo(2);
                verify(organizationCache).clear();
        }

    @Test
    void transferAdminShouldRejectDefaultOrganization() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        // 超级管理员登录态
        UserEntity superUser = UserEntity.builder().uid("su-1").username("super").build();
        superUser.setSuperUser(true);
        when(authService.getUser()).thenReturn(superUser);

        OrganizationEntity organization = OrganizationEntity.builder()
                .uid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                .name("Default Org")
                .enabled(true)
                .build();
        when(organizationRepository.findByUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID))
                .thenReturn(Optional.of(organization));

        OrganizationRestService organizationRestService = newOrganizationRestService(
                authService, userService, organizationRepository);

        OrganizationRequest request = new OrganizationRequest();
        request.setUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        request.setUserUid("user-b");

        assertThatThrownBy(() -> organizationRestService.transferAdmin(request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void transferAdminShouldRejectNonOwnerNonSuperUser() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        // 普通用户登录态（非超管、非组织管理员本人）
        UserEntity normalUser = UserEntity.builder().uid("user-c").username("c").build();
        when(authService.getUser()).thenReturn(normalUser);

        UserEntity adminA = UserEntity.builder().uid("user-a").username("a").build();
        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-a")
                .name("Org A")
                .enabled(true)
                .build();
        organization.setUser(adminA);
        when(organizationRepository.findByUid("org-a")).thenReturn(Optional.of(organization));

        OrganizationRestService organizationRestService = newOrganizationRestService(
                authService, userService, organizationRepository);

        OrganizationRequest request = new OrganizationRequest();
        request.setUid("org-a");
        request.setUserUid("user-b");

        assertThatThrownBy(() -> organizationRestService.transferAdmin(request))
                .isInstanceOf(ForbiddenException.class);
    }
}