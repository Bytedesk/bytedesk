package com.bytedesk.core.rbac.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.rbac.role.RoleInitializer;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserInitializer;
import com.bytedesk.core.rbac.user.UserService;

@ExtendWith(MockitoExtension.class)
class OrganizationInitializerDefaultsTest {

    @Mock
    private RoleInitializer roleInitializer;

    @Mock
    private UserInitializer userInitializer;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserService userService;

    @Mock
    private OrganizationRestService organizationRestService;

    @Mock
    private AuthorityRestService authorityRestService;

    @Test
    void initShouldUseConfiguredDefaultVipLevelForDefaultOrganization() {
        BytedeskProperties bytedeskProperties = new BytedeskProperties();
        bytedeskProperties.getOrganization().setName("MyCompany");
        bytedeskProperties.getOrganization().setCode("bytedesk");
        bytedeskProperties.getOrganization().setDefaultVipLevel(0);

        OrganizationInitializer initializer = new OrganizationInitializer(
                roleInitializer,
                userInitializer,
                organizationRepository,
                bytedeskProperties,
                userService,
                organizationRestService,
                authorityRestService);

        UserEntity superUser = UserEntity.builder()
                .uid("super-1")
                .build();

        when(organizationRepository.count()).thenReturn(0L);
        when(userService.getSuper()).thenReturn(Optional.of(superUser));
        when(organizationRestService.save(any(OrganizationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userService.addRoleSuper(superUser)).thenReturn(superUser);

        initializer.init();

        ArgumentCaptor<OrganizationEntity> organizationCaptor = ArgumentCaptor.forClass(OrganizationEntity.class);
        org.mockito.Mockito.verify(organizationRestService).save(organizationCaptor.capture());
        OrganizationEntity savedOrganization = organizationCaptor.getValue();
        assertThat(savedOrganization.getVipLevel()).isEqualTo(0);
        assertThat(savedOrganization.getVipExpireDate()).isNull();
    }
}