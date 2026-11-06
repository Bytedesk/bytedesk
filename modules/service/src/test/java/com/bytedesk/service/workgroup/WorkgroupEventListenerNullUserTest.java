package com.bytedesk.service.workgroup;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.uid.impl.CachedUidGenerator;
import com.bytedesk.service.agent.AgentRestService;

/**
 * WorkgroupEventListener（Order 7）对「无管理员组织」的空保护验证：
 * 跳过默认工作组创建而非 NPE 中断事件链，工单流程初始化（Order 8）不受影响。
 */
@ExtendWith(MockitoExtension.class)
class WorkgroupEventListenerNullUserTest {

    @Mock
    private AgentRestService agentRestService;

    @Mock
    private WorkgroupRestService workgroupRestService;

    @Mock
    private CachedUidGenerator cachedUidGenerator;

    private WorkgroupEventListener listener;

    @BeforeEach
    void setUp() {
        lenient().when(workgroupRestService.findAnyByOrgUid(any())).thenReturn(Optional.empty());
        lenient().when(cachedUidGenerator.getUID()).thenReturn(123456789L);
        UidUtils uidUtils = new UidUtils(cachedUidGenerator);
        uidUtils.init();
        listener = new WorkgroupEventListener(agentRestService, workgroupRestService, uidUtils);
    }

    @Test
    void organizationWithoutAdminUserSkipsWorkgroupCreationInsteadOfNpe() {
        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-no-admin")
                .name("org-no-admin")
                .build();
        organization.setUser(null);
        OrganizationCreateEvent event = new OrganizationCreateEvent(organization);

        listener.onOrganizationCreateEvent(event);

        verify(workgroupRestService, never()).create(any());
    }

    @Test
    void organizationWithAdminUserStillCreatesWorkgroup() {
        UserEntity user = UserEntity.builder()
                .uid("admin-user")
                .mobile("13800138000")
                .build();
        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-with-admin")
                .name("org-with-admin")
                .build();
        organization.setUser(user);
        when(agentRestService.findByMobileAndOrgUid("13800138000", "org-with-admin"))
                .thenReturn(Optional.empty());
        OrganizationCreateEvent event = new OrganizationCreateEvent(organization);

        listener.onOrganizationCreateEvent(event);

        verify(workgroupRestService).create(any());
    }
}
