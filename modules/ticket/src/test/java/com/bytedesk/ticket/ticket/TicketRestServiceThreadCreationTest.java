package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.rbac.auth.AuthService;


import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket.enums.TicketCreateTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordRepository;

/**
 * 工单处理会话创建回归：自动创建外部工单时不得复用来源客服会话。
 *
 * <p>场景来源：TicketAutoCreateService 自动建单请求不再携带 threadUid（仅 visitorThread*
 * 关联来源会话），本测试验证 {@link TicketRestService#createTicketThread(TicketEntity)}
 * 在该前提下按工作组/组织规则新建 TICKET_EXTERNAL 会话。
 */
class TicketRestServiceThreadCreationTest {

        @Test
        void createTicketThreadShouldCreateExternalThreadWhenThreadUidEmpty() {
                // Arrange
                ThreadRestService threadRestService = mock(ThreadRestService.class);
                UidUtils uidUtils = mock(UidUtils.class);
                when(uidUtils.getUid()).thenReturn("thread-external-new");
                when(threadRestService.save(any(ThreadEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                TicketRestService service = newService(threadRestService, uidUtils);

                // 模拟自动建单落库后的工单实体：threadUid 为空，仅 visitorThread* 关联来源会话
                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-auto-1")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .workgroupUid("wg-1")
                                .channel(ChannelEnum.SYSTEM.name())
                                .visitorThreadUid("visitor-thread-1")
                                .visitorThreadTopic("org/workgroup/wg-1/visitor-1")
                                .reporter("{}")
                                .build();

                // Act
                ThreadEntity created = service.createTicketThread(ticket);

                // Assert：新建会话而非复用来源会话
                assertThat(created).isNotNull();
                assertThat(created.getUid()).isEqualTo("thread-external-new")
                                .isNotEqualTo(ticket.getVisitorThreadUid());
                assertThat(created.getType()).isEqualTo(ThreadTypeEnum.TICKET_EXTERNAL.name());
                assertThat(created.getTopic())
                                .isEqualTo(TopicUtils.formatOrgWorkgroupTicketThreadTopic("wg-1", "ticket-auto-1"))
                                .isNotEqualTo(ticket.getVisitorThreadTopic());
                assertThat(created.getOrgUid()).isEqualTo("org-1");
                assertThat(created.getChannel()).isEqualTo(ChannelEnum.SYSTEM.name());

                // 工单侧回填语义：新会话 uid/topic 回填 threadUid/threadTopic，visitorThread* 不受影响
                assertThat(ticket.getThreadUid()).isNull();
                ticket.setThreadUid(created.getUid());
                ticket.setThreadTopic(created.getTopic());
                assertThat(ticket.getThreadUid()).isNotEqualTo(ticket.getVisitorThreadUid());
                assertThat(ticket.getThreadTopic()).isNotEqualTo(ticket.getVisitorThreadTopic());
        }

        @Test
        void createTicketThreadShouldFallBackToOrgTopicWhenWorkgroupMissing() {
                ThreadRestService threadRestService = mock(ThreadRestService.class);
                UidUtils uidUtils = mock(UidUtils.class);
                when(uidUtils.getUid()).thenReturn("thread-org-fallback");
                when(threadRestService.save(any(ThreadEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                TicketRestService service = newService(threadRestService, uidUtils);

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-auto-2")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .channel(ChannelEnum.SYSTEM.name())
                                .visitorThreadUid("visitor-thread-2")
                                .build();

                ThreadEntity created = service.createTicketThread(ticket);

                assertThat(created).isNotNull();
                assertThat(created.getType()).isEqualTo(ThreadTypeEnum.TICKET_EXTERNAL.name());
                assertThat(created.getTopic())
                                .isEqualTo(TopicUtils.formatOrgTicketThreadTopic("org-1", "ticket-auto-2"));
                assertThat(created.getUid()).isNotEqualTo("visitor-thread-2");
        }

        @Test
        void createTicketThreadShouldCreateInternalThreadForInternalTicket() {
                ThreadRestService threadRestService = mock(ThreadRestService.class);
                UidUtils uidUtils = mock(UidUtils.class);
                when(uidUtils.getUid()).thenReturn("thread-internal-new");
                when(threadRestService.save(any(ThreadEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                TicketRestService service = newService(threadRestService, uidUtils);

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-internal-1")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.INTERNAL.name())
                                .departmentUid("dept-1")
                                .visitorThreadUid("visitor-thread-3")
                                .build();

                ThreadEntity created = service.createTicketThread(ticket);

                assertThat(created).isNotNull();
                assertThat(created.getType()).isEqualTo(ThreadTypeEnum.TICKET_INTERNAL.name());
                assertThat(created.getTopic())
                                .isEqualTo(TopicUtils.formatOrgDepartmentTicketThreadTopic("dept-1",
                                                "ticket-internal-1"));
                assertThat(created.getUid()).isNotEqualTo("visitor-thread-3");
        }

        @Test
        void createTicketThreadShouldStillBindExistingThreadWhenThreadUidProvided() {
                // 显式绑定已有会话的公共服务行为保持不变（其他调用方依赖）
                ThreadRestService threadRestService = mock(ThreadRestService.class);
                UidUtils uidUtils = mock(UidUtils.class);
                TicketRestService service = newService(threadRestService, uidUtils);

                ThreadEntity existing = ThreadEntity.builder()
                                .uid("thread-existing-1")
                                .orgUid("org-1")
                                .topic("org/workgroup/wg-1/thread-existing-1")
                                .build();
                when(threadRestService.findByUidReadOnly("thread-existing-1")).thenReturn(Optional.of(existing));

                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-bind-1")
                                .orgUid("org-1")
                                .type(TicketTypeEnum.EXTERNAL.name())
                                .workgroupUid("wg-1")
                                .threadUid("thread-existing-1")
                                .build();

                ThreadEntity bound = service.createTicketThread(ticket);

                assertThat(bound).isSameAs(existing);
                ArgumentCaptor<ThreadEntity> saver = ArgumentCaptor.forClass(ThreadEntity.class);
                org.mockito.Mockito.verify(threadRestService, org.mockito.Mockito.never()).save(saver.capture());
        }

        @Test
        void createTypeEnumShouldFallbackToManual() {
                // 手动入口不传 createType 时，枚举解析默认 MANUAL（createInternal 兜底使用同一规则）
                assertThat(TicketCreateTypeEnum.fromValue(null)).isEqualTo(TicketCreateTypeEnum.MANUAL);
                assertThat(TicketCreateTypeEnum.fromValue("")).isEqualTo(TicketCreateTypeEnum.MANUAL);
                assertThat(TicketCreateTypeEnum.fromValue("auto")).isEqualTo(TicketCreateTypeEnum.AUTO);
                assertThat(TicketCreateTypeEnum.fromValue("invalid")).isEqualTo(TicketCreateTypeEnum.MANUAL);
                assertThat(TicketCreateTypeEnum.getNameFromValue("AUTO")).isEqualTo("AUTO");
                // 实体默认值：未显式指定时为 MANUAL
                TicketEntity ticket = TicketEntity.builder().build();
                assertThat(ticket.getCreateType()).isEqualTo(TicketCreateTypeEnum.MANUAL.name());
        }

        private static TicketRestService newService(ThreadRestService threadRestService, UidUtils uidUtils) {
                return new TicketRestService(
                                mock(TicketRepository.class),
                                mock(TicketAttachmentRepository.class),
                                mock(ModelMapper.class),
                                mock(AuthService.class),
                                uidUtils,
                                threadRestService,
                                mock(MessageRepository.class),
                                mock(TopicSubscriptionRepository.class),
                                mock(TicketSlaRecordRepository.class),
                                mock(UploadRestService.class),
                                mock(ApplicationEventPublisher.class),
                                mock(CategoryRestService.class),
                                mock(TicketSettingsRestService.class),
                                mock(MemberRepository.class));
        }
}
