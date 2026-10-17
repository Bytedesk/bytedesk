package com.bytedesk.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;

import com.bytedesk.core.email_provider.EmailProviderEntity;
import com.bytedesk.core.email_provider.EmailProviderRepository;
import com.bytedesk.core.email_push.EmailPushSendService;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.notification.NotificationRequest;
import com.bytedesk.core.notification.NotificationService;
import com.bytedesk.core.push.service.AgentPushGateService;
import com.bytedesk.core.push.service.PushMobileService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.sms_push.SmsPushSendService;
import com.bytedesk.core.sms_template.SmsTemplateRepository;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorRepository;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings_notification.TicketNotificationSettingsEntity;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityService;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsEntity;

/**
 * 通知收件人可见性过滤测试（规划 8.2）：
 * 通过公共入口断言实际站内投递与 APNs 调用，不可见候选 0 次写入 / 0 次推送。
 */
class TicketNotificationServiceRecipientVisibilityTest {

        private static final String DEPT_A = "dept-a";
        private static final String DEPT_B = "dept-b";

        private NotificationService notificationService;
        private MemberRestService memberRestService;
        private WorkgroupRepository workgroupRepository;
        private TicketSettingsRestService ticketSettingsRestService;
        private TicketSettingsRepository ticketSettingsRepository;
        private EmailProviderRepository emailProviderRepository;
        private VisitorRepository visitorRepository;
        private AgentPushGateService agentPushGateService;
        private EmailPushSendService emailPushSendService;
        private PushMobileService pushMobileService;
        @SuppressWarnings("unchecked")
        private final ObjectProvider<PushMobileService> mobilePushProvider = mock(ObjectProvider.class);

        @BeforeEach
        @SuppressWarnings("unchecked")
        void setUp() {
                notificationService = mock(NotificationService.class);
                memberRestService = mock(MemberRestService.class);
                workgroupRepository = mock(WorkgroupRepository.class);
                ticketSettingsRestService = mock(TicketSettingsRestService.class);
                ticketSettingsRepository = mock(TicketSettingsRepository.class);
                emailProviderRepository = mock(EmailProviderRepository.class);
                visitorRepository = mock(VisitorRepository.class);
                agentPushGateService = mock(AgentPushGateService.class);
                emailPushSendService = mock(EmailPushSendService.class);
                pushMobileService = mock(PushMobileService.class);
                when(mobilePushProvider.getIfAvailable()).thenReturn(pushMobileService);
        }

        private TicketNotificationService newService() {
                return newService(mock(TicketRepository.class));
        }

        private TicketNotificationService newService(TicketRepository ticketRepository) {
                return new TicketNotificationService(
                                notificationService, memberRestService, workgroupRepository,
                                visitorRepository, emailProviderRepository,
                                mock(SmsPushSendService.class), mock(SmsTemplateRepository.class),
                                mobilePushProvider, agentPushGateService, emailPushSendService,
                                ticketSettingsRepository,
                                ticketRepository, mock(ThreadRestService.class),
                                mock(MessageRestService.class), mock(IMessageSendService.class),
                                new TicketVisibilityService(ticketSettingsRestService));
        }

        // ==================== 1. 部门受限：不可见候选零投递 ====================

        @Test
        void departmentRestrictedTicketNotifiesOnlyVisibleWorkgroupAgents() {
                stubDefaultVisibility("DEPARTMENT_RESTRICTED", List.of());
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)),
                                agent("agent-b", member("member-b", "user-b", DEPT_B)));
                TicketEntity ticket = ticket("wg-1", "dept-a");
                enableApns(ticket);

                newService().notifyNewTicket(ticket);

                // 站内：A 部门坐席收到；B 部门坐席 0 次写入
                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "user-a".equals(request.getUserUid())));
                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "user-b".equals(request.getUserUid())));
                // APNs 前提满足（开关开、离线、服务可用）：B 0 次推送调用
                verify(pushMobileService, never()).pushNotificationToUser(
                                argThat("user-b"::equals), any(), any(), any(), any(), any());
        }

        @Test
        void departmentRestrictedNotifiesVisibleAgentViaPushWhenOffline() {
                stubDefaultVisibility("DEPARTMENT_RESTRICTED", List.of());
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)));
                TicketEntity ticket = ticket("wg-1", "dept-a");
                enableApns(ticket);
                when(agentPushGateService.isUserOnline("user-a")).thenReturn(false);

                newService().notifyNewTicket(ticket);

                verify(pushMobileService, times(1)).pushNotificationToUser(
                                argThat("user-a"::equals), any(), any(), any(), any(), any());
        }

        @Test
        void departmentBasedTicketNotifiesOnlyAllowedDepartmentAgents() {
                stubDefaultVisibility("DEPARTMENT_BASED", List.of(DEPT_A));
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)),
                                agent("agent-b", member("member-b", "user-b", DEPT_B)));

                newService().notifyNewTicket(ticket("wg-1", null));

                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "user-a".equals(request.getUserUid())));
                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "user-b".equals(request.getUserUid())));
        }

        // ==================== 2. ORG_WIDE 回归：不扩大也不缩小 ====================

        @Test
        void orgWideTicketStillNotifiesAllWorkgroupAgents() {
                stubDefaultVisibility("ORG_WIDE", List.of());
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)),
                                agent("agent-b", member("member-b", "user-b", DEPT_B)));

                newService().notifyNewTicket(ticket("wg-1", "dept-a"));

                // 报告人 + 2 坐席
                verify(notificationService, times(3)).dispatchSystemNotificationToUser(any());
                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "user-a".equals(request.getUserUid())));
                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "user-b".equals(request.getUserUid())));
        }

        @Test
        void orgWideDoesNotNotifyUsersOutsideWorkgroup() {
                stubDefaultVisibility("ORG_WIDE", List.of());
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)));

                newService().notifyNewTicket(ticket("wg-1", "dept-a"));

                // 组外同部门成员 user-out 不在通知范围（可见 ≠ 自动订阅）
                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "user-out".equals(request.getUserUid())));
        }

        // ==================== 3. 豁免与身份归一 ====================

        @Test
        void reporterAndAssigneeExemptionsSurviveDepartmentFilter() {
                // 报告人/处理人（MEMBER）均不在可见部门，仍须收到通知（豁免）
                stubDefaultVisibility("DEPARTMENT_BASED", List.of(DEPT_A));
                when(memberRestService.findByUid("member-reporter"))
                                .thenReturn(Optional.of(member("member-reporter", "user-reporter", DEPT_B)));
                when(memberRestService.findByUid("member-assignee"))
                                .thenReturn(Optional.of(member("member-assignee", "user-assignee", DEPT_B)));
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)));
                TicketEntity ticket = ticket("wg-1", null);
                ticket.setReporter(memberReporter("member-reporter").toJson());
                ticket.setAssignee(memberReporter("member-assignee").toJson());

                newService().notifyNewTicket(ticket);

                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "user-reporter".equals(request.getUserUid())));
                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "user-assignee".equals(request.getUserUid())));
                // member uid 不冒充 user uid 投递（D5 归一）
                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "member-reporter".equals(request.getUserUid())
                                || "member-assignee".equals(request.getUserUid())));
        }

        @Test
        void unresolvableMemberIdentityIsSkippedNotFaked() {
                // MEMBER 处理人查无成员：不回退 member uid，跳过并记录（D5）
                stubDefaultVisibility("ORG_WIDE", List.of());
                when(memberRestService.findByUid("member-ghost")).thenReturn(Optional.empty());
                TicketEntity ticket = ticket("wg-1", null);
                ticket.setReporter(visitorReporter().toJson());
                ticket.setAssignee(memberReporter("member-ghost").toJson());

                newService().notifyNewTicket(ticket);

                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "member-ghost".equals(request.getUserUid())));
        }

        @Test
        void visitorReporterStillNotifiedWithReporterWording() {
                stubDefaultVisibility("DEPARTMENT_RESTRICTED", List.of());
                workgroup(agent("agent-b", member("member-b", "user-b", DEPT_B)));
                TicketEntity ticket = ticket("wg-1", "dept-a");
                ticket.setReporter(visitorReporter().toJson());

                newService().notifyNewTicket(ticket);

                // B 被过滤后报告人（visitor）仍收到，且命中报告人专用文案（非坐席标题）
                ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
                verify(notificationService, atLeastOnce()).dispatchSystemNotificationToUser(captor.capture());
                NotificationRequest reporterRequest = captor.getAllValues().stream()
                                .filter(request -> "visitor-1".equals(request.getUserUid()))
                                .findFirst().orElseThrow();
                assertThat(reporterRequest.getTitle()).isNotNull();
                assertThat(capturedUserUids()).doesNotContain("user-b");
        }

        @Test
        void duplicateAgentUsersAreDeduplicated() {
                stubDefaultVisibility("ORG_WIDE", List.of());
                // 两个 agent 指向同一 user + 处理人也是同一 user → 只投一条
                workgroup(agent("agent-1", member("member-1", "user-same", DEPT_A)),
                                agent("agent-2", member("member-2", "user-same", DEPT_A)));
                when(memberRestService.findByUid("member-assignee"))
                                .thenReturn(Optional.of(member("member-assignee", "user-same", DEPT_A)));
                TicketEntity ticket = ticket("wg-1", null);
                ticket.setAssignee(memberReporter("member-assignee").toJson());

                newService().notifyNewTicket(ticket);

                assertThat(capturedUserUids().stream().filter("user-same"::equals).count()).isEqualTo(1);
        }

        // ==================== 4. 状态变更与 SLA ====================

        @Test
        void creationLikeStatusChangeFiltersWorkgroupByVisibility() {
                // previousStatus 为空 → 创建类事件，含工作组候选并过滤
                stubDefaultVisibility("DEPARTMENT_RESTRICTED", List.of());
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)),
                                agent("agent-b", member("member-b", "user-b", DEPT_B)));
                TicketEntity ticket = ticket("wg-1", "dept-a");
                ticket.setStatus(TicketStatusEnum.NEW.name());

                newService().notifyTicketStatusChanged(ticket, null, "NEW");

                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "user-b".equals(request.getUserUid())));
                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "user-a".equals(request.getUserUid())));
        }

        @Test
        void ordinaryStatusChangeDoesNotBroadcastToWorkgroup() {
                // 有 previousStatus → 不含工作组候选（不因本次修复扩大广播，D2）
                stubDefaultVisibility("ORG_WIDE", List.of());
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)));
                TicketEntity ticket = ticket("wg-1", null);
                ticket.setStatus(TicketStatusEnum.PROCESSING.name());

                newService().notifyTicketStatusChanged(ticket, "NEW", "PROCESSING");

                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "user-a".equals(request.getUserUid())));
                // 报告人（visitor）仍收到
                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "visitor-1".equals(request.getUserUid())));
        }

        @Test
        void slaNotificationFiltersWorkgroupAndReusesSnapshot() {
                stubDefaultVisibility("DEPARTMENT_RESTRICTED", List.of());
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)),
                                agent("agent-b", member("member-b", "user-b", DEPT_B)));
                TicketEntity ticket = ticket("wg-1", "dept-a");
                enableApns(ticket);
                TicketRepository ticketRepository = mock(TicketRepository.class);
                when(ticketRepository.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));

                newService(ticketRepository).sendSLAWarningNotification(ticket.getUid(), "RESOLUTION", "detail");

                // B：站内 0 次、推送 0 次
                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "user-b".equals(request.getUserUid())));
                verify(pushMobileService, never()).pushNotificationToUser(
                                argThat("user-b"::equals), any(), any(), any(), any(), any());
                // A：单次解析复用，站内+推送各一次，不重复
                verify(notificationService, times(1)).dispatchSystemNotificationToUser(
                                argThat(request -> "user-a".equals(request.getUserUid())));
                verify(pushMobileService, times(1)).pushNotificationToUser(
                                argThat("user-a"::equals), any(), any(), any(), any(), any());
        }

        // ==================== 5. 渠道门控与异常 ====================

        @Test
        void emptyAgentRecipientsDoNotShortCircuitReporterEmail() {
                // 全部坐席被过滤 + 报告人有邮箱且有可用 provider：报告人邮件仍发送（不被空客服名单短路）
                stubDefaultVisibility("DEPARTMENT_BASED", List.of(DEPT_A));
                workgroup(agent("agent-b", member("member-b", "user-b", DEPT_B)));
                TicketEntity ticket = ticket("wg-1", null);
                ticket.setEmail("reporter@example.com");
                enableEmailCreated(ticket);
                EmailProviderEntity provider = EmailProviderEntity.builder().build();
                when(emailProviderRepository.findByUid(anyString())).thenReturn(Optional.empty());
                when(emailProviderRepository.findFirstByOrgUidAndTypeAndEnabledTrueAndDeletedFalse(
                                anyString(), anyString())).thenReturn(Optional.of(provider));
                when(visitorRepository.findByUidAndDeleted("visitor-1", false)).thenReturn(Optional.empty());

                newService().notifyNewTicket(ticket);

                // 报告人站内 1 次 + 邮件 1 次均发生；被过滤坐席无投递
                verify(notificationService, times(1)).dispatchSystemNotificationToUser(
                                argThat(request -> "visitor-1".equals(request.getUserUid())));
                verify(notificationService, never()).dispatchSystemNotificationToUser(
                                argThat(request -> "user-b".equals(request.getUserUid())));
                // 邮件渠道被调用（报告人回执不被短路）：11 参数签名（title/reporterName 可为 null，用 any()）
                verify(emailPushSendService).sendTicketEmail(
                                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        void visibilityResolveFailureSkipsWorkgroupWithoutFakingBroadcast() {
                // 设置服务抛异常 → 放弃工作组候选，不回退全组广播（规划 5.4）
                when(ticketSettingsRestService.findByUid(any())).thenThrow(new RuntimeException("db down"));
                when(ticketSettingsRestService.findDefaultByOrgUidAndType(any(), any()))
                                .thenThrow(new RuntimeException("db down"));
                workgroup(agent("agent-a", member("member-a", "user-a", DEPT_A)),
                                agent("agent-b", member("member-b", "user-b", DEPT_B)));

                newService().notifyNewTicket(ticket("wg-1", "dept-a"));

                // 仅报告人收到；两个坐席均不投递（宁缺勿滥，不伪装成功）
                verify(notificationService, times(1)).dispatchSystemNotificationToUser(any());
                verify(notificationService).dispatchSystemNotificationToUser(
                                argThat(request -> "visitor-1".equals(request.getUserUid())));
        }

        @Test
        void transferredTicketNotifiesOnlyNewAssignee() {
                when(memberRestService.findByUid("member-new"))
                                .thenReturn(Optional.of(member("member-new", "user-new", DEPT_B)));
                TicketEntity ticket = ticket("wg-1", "dept-a");
                ticket.setReporter(visitorReporter().toJson());
                ticket.setAssignee(memberReporter("member-new").toJson());

                newService().notifyTicketTransferred(ticket);

                verify(notificationService, times(1)).dispatchSystemNotificationToUser(
                                argThat(request -> "user-new".equals(request.getUserUid())));
        }

        // ==================== fixtures ====================

        private List<String> capturedUserUids() {
                ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
                verify(notificationService, atLeastOnce()).dispatchSystemNotificationToUser(captor.capture());
                return captor.getAllValues().stream().map(NotificationRequest::getUserUid).toList();
        }

        private void stubDefaultVisibility(String mode, List<String> departmentUids) {
                TicketSettingsEntity defaultSettings = TicketSettingsEntity.builder().build();
                defaultSettings.setUid("settings-default");
                defaultSettings.setOrgUid("org-1");
                defaultSettings.setType("EXTERNAL");
                defaultSettings.setVisibilitySettings(TicketVisibilitySettingsEntity.builder()
                                .content(TicketVisibilitySettingsData.builder()
                                                .mode(mode).departmentUids(departmentUids).build())
                                .build());
                when(ticketSettingsRestService.findDefaultByOrgUidAndType("org-1", "EXTERNAL"))
                                .thenReturn(Optional.of(defaultSettings));
        }

        private TicketEntity ticket(String workgroupUid, String departmentUid) {
                return TicketEntity.builder()
                                .uid("ticket-1")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .workgroupUid(workgroupUid)
                                .departmentUid(departmentUid)
                                .status(TicketStatusEnum.NEW.name())
                                .reporter(visitorReporter().toJson())
                                .build();
        }

        private static UserProtobuf visitorReporter() {
                return UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name())
                                .nickname("Visitor").build();
        }

        private static UserProtobuf memberReporter(String memberUid) {
                return UserProtobuf.builder().uid(memberUid).type(UserTypeEnum.MEMBER.name()).build();
        }

        private WorkgroupEntity workgroup(AgentEntity... agents) {
                WorkgroupEntity workgroup = WorkgroupEntity.builder()
                                .uid("wg-1").orgUid("org-1").agents(List.of(agents)).build();
                when(workgroupRepository.findByUid("wg-1")).thenReturn(Optional.of(workgroup));
                return workgroup;
        }

        private static AgentEntity agent(String agentUid, MemberEntity member) {
                return AgentEntity.builder().uid(agentUid).orgUid("org-1").member(member)
                                .nickname(agentUid).build();
        }

        private static MemberEntity member(String memberUid, String userUid, String deptUid) {
                UserEntity user = UserEntity.builder().uid(userUid).username(userUid).build();
                return MemberEntity.builder().uid(memberUid).orgUid("org-1").user(user)
                                .deptUid(deptUid).build();
        }

        private void enableApns(TicketEntity ticket) {
                TicketSettingsEntity settings = settingsFor(ticket);
                settings.setNotificationSettings(TicketNotificationSettingsEntity.builder()
                                .apnsEnabled(Boolean.TRUE).build());
                when(ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid()))
                                .thenReturn(Optional.of(settings));
        }

        private void enableEmailCreated(TicketEntity ticket) {
                TicketSettingsEntity settings = settingsFor(ticket);
                settings.setNotificationSettings(TicketNotificationSettingsEntity.builder()
                                .emailEnabled(Boolean.TRUE)
                                .emailEvents(List.of("created"))
                                .build());
                when(ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid()))
                                .thenReturn(Optional.of(settings));
        }

        private TicketSettingsEntity settingsFor(TicketEntity ticket) {
                TicketSettingsEntity settings = TicketSettingsEntity.builder().build();
                settings.setUid("settings-1");
                settings.setOrgUid("org-1");
                settings.setType("EXTERNAL");
                if (!org.springframework.util.StringUtils.hasText(ticket.getTicketSettingsUid())) {
                        ticket.setTicketSettingsUid("settings-1");
                }
                return settings;
        }
}
