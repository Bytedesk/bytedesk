package com.bytedesk.service.agent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;
import com.bytedesk.core.message.IMessageSendService;

/**
 * 组织创建事件监听器对「无管理员组织」（createBySuper 不指定 userUid）的空保护验证：
 * 跳过默认客服/默认工作组创建而非 NPE 中断事件链（MemberEventListener Order 1 既有语义），
 * 后续工单流程等初始化（Order 8）不受影响。
 */
@ExtendWith(MockitoExtension.class)
class AgentEventListenerNullUserTest {

    @Mock
    private AgentRestService agentRestService;

    @Mock
    private KbaseRestService kbaseRestService;

    @Mock
    private IMessageSendService messageSendService;

    @Mock
    private QuickReplyRestService quickReplyRestService;

    @Mock
    private QueueMemberRestService queueMemberRestService;

    private AgentEventListener listener;

    @BeforeEach
    void setUp() {
        lenient().when(agentRestService.findByUserUidAndOrgUid(any(), any())).thenReturn(Optional.empty());
        listener = new AgentEventListener(agentRestService, kbaseRestService,
                messageSendService, quickReplyRestService, queueMemberRestService);
    }

    @Test
    void organizationWithoutAdminUserSkipsAgentCreationInsteadOfNpe() {
        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-no-admin")
                .name("org-no-admin")
                .build();
        organization.setUser(null);
        OrganizationCreateEvent event = new OrganizationCreateEvent(organization);

        listener.onOrganizationCreateEvent(event);

        verify(agentRestService, never()).createFromMember(any(), any());
        verify(agentRestService, never()).createFromMemberByEmail(any(), any());
    }

    @Test
    void organizationWithAdminUserStillCreatesAgent() {
        UserEntity user = UserEntity.builder()
                .uid("admin-user")
                .mobile("13800138000")
                .build();
        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-with-admin")
                .name("org-with-admin")
                .build();
        organization.setUser(user);
        OrganizationCreateEvent event = new OrganizationCreateEvent(organization);

        listener.onOrganizationCreateEvent(event);

        verify(agentRestService).createFromMember("13800138000", "org-with-admin");
    }
}
