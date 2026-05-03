package com.bytedesk.service.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

class VisitorInitializerTest {

    @Test
    void afterSingletonsInstantiatedShouldInitThreeDemoVisitorsForDefaultOrganizationOnly() {
        AuthorityRestService authorityRestService = mock(AuthorityRestService.class);
        VisitorRestService visitorRestService = mock(VisitorRestService.class);

        VisitorInitializer initializer = new VisitorInitializer(
                authorityRestService,
                visitorRestService);

        initializer.afterSingletonsInstantiated();

        ArgumentCaptor<VisitorRequest> captor = ArgumentCaptor.forClass(VisitorRequest.class);
        verify(visitorRestService, times(3)).create(captor.capture());

        List<VisitorRequest> requests = captor.getAllValues();
        assertThat(requests)
                .extracting(VisitorRequest::getOrgUid, VisitorRequest::getVisitorUid, VisitorRequest::getAvatar, VisitorRequest::getVipLevel)
                .containsExactly(
                        tuple(BytedeskConsts.DEFAULT_ORGANIZATION_UID, "visitor_001", "https://weiyuai.cn/assets/images/avatar/02.jpg", 0),
                        tuple(BytedeskConsts.DEFAULT_ORGANIZATION_UID, "visitor_002", "https://weiyuai.cn/assets/images/avatar/01.jpg", 1),
                        tuple(BytedeskConsts.DEFAULT_ORGANIZATION_UID, "visitor_003", "https://weiyuai.cn/assets/images/avatar/03.jpg", 2));
    }
}