package com.bytedesk.service.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;

class VisitorInitializerTest {

    @Test
    void afterSingletonsInstantiatedShouldInitThreeDemoVisitorsForEachOrganization() {
        AuthorityRestService authorityRestService = mock(AuthorityRestService.class);
        OrganizationRestService organizationRestService = mock(OrganizationRestService.class);
        VisitorRestService visitorRestService = mock(VisitorRestService.class);

        OrganizationEntity org = OrganizationEntity.builder()
                .name("Demo Org")
                .build();
        org.setUid("org-1");

        when(organizationRestService.findAll()).thenReturn(List.of(org));

        VisitorInitializer initializer = new VisitorInitializer(
                authorityRestService,
                organizationRestService,
                visitorRestService);

        initializer.afterSingletonsInstantiated();

        ArgumentCaptor<VisitorRequest> captor = ArgumentCaptor.forClass(VisitorRequest.class);
        verify(visitorRestService, times(3)).create(captor.capture());

        List<VisitorRequest> requests = captor.getAllValues();
        assertThat(requests)
                .extracting(VisitorRequest::getOrgUid, VisitorRequest::getVisitorUid, VisitorRequest::getAvatar, VisitorRequest::getVipLevel)
                .containsExactly(
                        tuple("org-1", "visitor_001", "https://weiyuai.cn/assets/images/avatar/02.jpg", 0),
                        tuple("org-1", "visitor_002", "https://weiyuai.cn/assets/images/avatar/01.jpg", 1),
                        tuple("org-1", "visitor_003", "https://weiyuai.cn/assets/images/avatar/03.jpg", 2));
    }
}