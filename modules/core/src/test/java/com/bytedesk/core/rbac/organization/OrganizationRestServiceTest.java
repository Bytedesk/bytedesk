package com.bytedesk.core.rbac.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InOrder;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.exception.ForbiddenException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;

class OrganizationRestServiceTest {

    @Test
    void deleteByUidShouldRemoveUsersBeforeLogicalDelete() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        OrganizationRestService organizationRestService = new OrganizationRestService(
                authService,
                userService,
                organizationRepository,
                mock(BytedeskProperties.class),
                mock(UidUtils.class),
                mock(ModelMapper.class));

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

        OrganizationRestService organizationRestService = new OrganizationRestService(
                authService,
                userService,
                organizationRepository,
                mock(BytedeskProperties.class),
                mock(UidUtils.class),
                mock(ModelMapper.class));

        assertThatThrownBy(() -> organizationRestService.deleteByUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateBySuperShouldRejectDisablingDefaultOrganization() {
        AuthService authService = mock(AuthService.class);
        UserService userService = mock(UserService.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

        OrganizationRestService organizationRestService = new OrganizationRestService(
            authService,
            userService,
            organizationRepository,
            mock(BytedeskProperties.class),
            mock(UidUtils.class),
            mock(ModelMapper.class));

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

        OrganizationRestService organizationRestService = new OrganizationRestService(
                authService,
                userService,
                organizationRepository,
                mock(BytedeskProperties.class),
                mock(UidUtils.class),
                mock(ModelMapper.class));

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
}