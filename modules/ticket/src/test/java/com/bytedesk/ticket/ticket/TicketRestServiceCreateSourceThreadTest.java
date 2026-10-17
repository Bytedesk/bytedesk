package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.ticket.enums.TicketCreateTypeEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityService;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordRepository;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

/**
 * 访客手动创建外部工单完整流程回归：来源会话写入 visitorThread*，
 * thread* 由公共服务创建独立 TICKET_EXTERNAL 会话后回填。
 *
 * <p>与 {@link TicketRestServiceThreadCreationTest}（直接调用 createTicketThread）不同，
 * 本测试走真实 createVisitor/create 服务路径，捕获实际保存实体断言来源映射、
 * 新会话类型与后端自动回填，不在测试中手动赋值代替被测逻辑。
 */
class TicketRestServiceCreateSourceThreadTest {

    private static final String ORG_UID = "org-1";
    private static final String WG_UID = "wg-1";
    private static final String SOURCE_THREAD_UID = "source-thread-1";
    private static final String SOURCE_THREAD_TOPIC = "org/workgroup/wg-1/visitor-1";

    @AfterEach
    void resetApplicationContext() throws Exception {
        Field field = ApplicationContextHolder.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(null, null);
    }

    /**
     * TicketConvertUtils.convertToResponse 依赖 ApplicationContextHolder 获取 ModelMapper，
     * 单测环境无 Spring 上下文，反射注入携带生产配置 ModelMapper 的 mock 上下文。
     */
    private static void injectModelMapperContext() throws Exception {
        ApplicationContext appContext = mock(ApplicationContext.class);
        when(appContext.getBean(ModelMapper.class)).thenReturn(productionModelMapper());
        Field field = ApplicationContextHolder.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(null, appContext);
    }

    @Test
    void createVisitorShouldMapSourceThreadAndBackfillIndependentTicketThread() throws Exception {
        Fixture f = fixture();
        // ModelMapper 需复刻生产 BytedeskConfig 的 STRICT 配置（ambiguityIgnored 消除多层级歧义）
        injectModelMapperContext();
        f.service = newService(f, productionModelMapper());

        TicketRequest request = visitorExternalRequest(null);

        TicketResponse response = f.service.createVisitor(request);

        // 请求侧：来源 uid 补齐服务端真实 topic（即使前端漏传）
        assertThat(request.getVisitorThreadUid()).isEqualTo(SOURCE_THREAD_UID);
        assertThat(request.getVisitorThreadTopic()).isEqualTo(SOURCE_THREAD_TOPIC);
        assertThat(request.getThreadUid()).isNull();

        // 实体侧：捕获实际保存的工单（save 原样返回同一引用，断言最终态）
        ArgumentCaptor<TicketEntity> savedCaptor = ArgumentCaptor.forClass(TicketEntity.class);
        verify(f.ticketRepository, times(2)).save(savedCaptor.capture());
        TicketEntity savedTicket = savedCaptor.getValue();
        assertThat(savedTicket.getUid()).isNotBlank();
        assertThat(savedTicket.getVisitorThreadUid()).isEqualTo(SOURCE_THREAD_UID);
        assertThat(savedTicket.getVisitorThreadTopic()).isEqualTo(SOURCE_THREAD_TOPIC);
        // 手动入口不传 createType -> MANUAL
        assertThat(savedTicket.getCreateType()).isEqualTo(TicketCreateTypeEnum.MANUAL.name());

        // 回填侧：thread* 指向新建独立工单会话，且不等于来源会话；来源字段不被覆盖
        assertThat(savedTicket.getThreadUid()).isNotBlank();
        assertThat(savedTicket.getThreadTopic())
                .isEqualTo(TopicUtils.formatOrgWorkgroupTicketThreadTopic(WG_UID, savedTicket.getUid()));
        assertThat(savedTicket.getThreadUid()).isNotEqualTo(SOURCE_THREAD_UID);
        assertThat(savedTicket.getThreadTopic()).isNotEqualTo(SOURCE_THREAD_TOPIC);

        // 新会话实体：类型 TICKET_EXTERNAL，uid/topic 即回填到工单的值
        ArgumentCaptor<ThreadEntity> threadCaptor = ArgumentCaptor.forClass(ThreadEntity.class);
        verify(f.threadRestService).save(threadCaptor.capture());
        ThreadEntity createdThread = threadCaptor.getValue();
        assertThat(createdThread.getType()).isEqualTo(ThreadTypeEnum.TICKET_EXTERNAL.name());
        assertThat(createdThread.getUid()).isEqualTo(savedTicket.getThreadUid());
        assertThat(createdThread.getTopic()).isEqualTo(savedTicket.getThreadTopic());
        assertThat(createdThread.getOrgUid()).isEqualTo(ORG_UID);

        // 响应侧：四字段与实体一致
        assertThat(response.getThreadUid()).isEqualTo(savedTicket.getThreadUid());
        assertThat(response.getVisitorThreadUid()).isEqualTo(SOURCE_THREAD_UID);
        assertThat(response.getVisitorThreadTopic()).isEqualTo(SOURCE_THREAD_TOPIC);
    }

    @Test
    void createVisitorShouldOverrideMismatchedFrontendTopicWithServerTopic() throws Exception {
        Fixture f = fixture();
        injectModelMapperContext();
        f.service = newService(f, productionModelMapper());

        // 前端 topic 与服务端不一致（可伪造/过期）：以服务端真实值为准
        TicketRequest request = visitorExternalRequest("forged/topic/value");

        TicketResponse response = f.service.createVisitor(request);

        assertThat(request.getVisitorThreadTopic()).isEqualTo(SOURCE_THREAD_TOPIC);
        assertThat(response.getVisitorThreadTopic()).isEqualTo(SOURCE_THREAD_TOPIC);
        assertThat(response.getVisitorThreadTopic()).isNotEqualTo("forged/topic/value");
    }

    @Test
    void createVisitorShouldFailWhenSourceThreadMissing() throws Exception {
        Fixture f = fixture();
        when(f.threadRestService.findByUidReadOnly(SOURCE_THREAD_UID)).thenReturn(Optional.empty());
        injectModelMapperContext();
        f.service = newService(f, productionModelMapper());

        TicketRequest request = visitorExternalRequest(null);

        assertThatThrownBy(() -> f.service.createVisitor(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("visitor thread not found");
        // 失败发生在持久化之前：未保存工单、未创建工单会话
        verify(f.ticketRepository, times(0)).save(any(TicketEntity.class));
        verify(f.threadRestService, times(0)).save(any(ThreadEntity.class));
    }

    @Test
    void createVisitorShouldFailWhenSourceThreadOrgMismatch() throws Exception {
        Fixture f = fixture();
        ThreadEntity crossOrg = sourceThread("org-other");
        when(f.threadRestService.findByUidReadOnly(SOURCE_THREAD_UID)).thenReturn(Optional.of(crossOrg));
        injectModelMapperContext();
        f.service = newService(f, productionModelMapper());

        assertThatThrownBy(() -> f.service.createVisitor(visitorExternalRequest(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("org mismatch");
        verify(f.ticketRepository, times(0)).save(any(TicketEntity.class));
    }

    @Test
    void createVisitorShouldFailWhenTopicWithoutUid() throws Exception {
        Fixture f = fixture();
        injectModelMapperContext();
        f.service = newService(f, productionModelMapper());

        TicketRequest request = visitorExternalRequest(null);
        request.setVisitorThreadUid(null);
        request.setVisitorThreadTopic(SOURCE_THREAD_TOPIC);

        assertThatThrownBy(() -> f.service.createVisitor(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("visitorThreadTopic requires visitorThreadUid");
        verify(f.ticketRepository, times(0)).save(any(TicketEntity.class));
    }

    @Test
    void createVisitorWithoutSourceShouldCreateTicketThreadOnly() throws Exception {
        Fixture f = fixture();
        injectModelMapperContext();
        f.service = newService(f, productionModelMapper());

        TicketRequest request = visitorExternalRequest(null);
        request.setVisitorThreadUid(null);
        request.setVisitorThreadTopic(null);

        TicketResponse response = f.service.createVisitor(request);

        // 无来源：visitorThread* 为空，thread* 仍指向新工单会话
        assertThat(response.getVisitorThreadUid()).isNull();
        assertThat(response.getVisitorThreadTopic()).isNull();
        assertThat(response.getThreadUid()).isNotBlank();
        assertThat(response.getThreadTopic())
                .isEqualTo(TopicUtils.formatOrgWorkgroupTicketThreadTopic(WG_UID, response.getUid()));
        assertThat(response.getThreadUid()).isNotEqualTo(SOURCE_THREAD_UID);
    }

    @Test
    void createVisitorShouldAllowSameSourceForMultipleTickets() throws Exception {
        Fixture f = fixture();
        injectModelMapperContext();
        f.service = newService(f, productionModelMapper());

        TicketResponse first = f.service.createVisitor(visitorExternalRequest(null));
        TicketResponse second = f.service.createVisitor(visitorExternalRequest(null));

        // 一对多：同一来源关联两张工单，各自独立处理会话
        assertThat(first.getVisitorThreadUid()).isEqualTo(SOURCE_THREAD_UID);
        assertThat(second.getVisitorThreadUid()).isEqualTo(SOURCE_THREAD_UID);
        assertThat(first.getUid()).isNotEqualTo(second.getUid());
        // 工单会话按各自 ticketUid 生成 topic，互不相同
        assertThat(first.getThreadTopic())
                .isEqualTo(TopicUtils.formatOrgWorkgroupTicketThreadTopic(WG_UID, first.getUid()));
        assertThat(second.getThreadTopic())
                .isEqualTo(TopicUtils.formatOrgWorkgroupTicketThreadTopic(WG_UID, second.getUid()));
        assertThat(first.getThreadTopic()).isNotEqualTo(second.getThreadTopic());
    }

    private static TicketRequest visitorExternalRequest(String visitorThreadTopic) {
        UserProtobuf reporter = new UserProtobuf();
        reporter.setUid("visitor-1");
        reporter.setNickname("visitor");
        reporter.setType(UserTypeEnum.VISITOR.name());
        TicketRequest request = new TicketRequest();
        request.setOrgUid(ORG_UID);
        request.setType(TicketTypeEnum.EXTERNAL.name());
        request.setWorkgroupUid(WG_UID);
        request.setTitle("title");
        request.setDescription("description");
        request.setReporter(reporter);
        request.setReporterUid("visitor-1");
        request.setVisitorThreadUid(SOURCE_THREAD_UID);
        request.setVisitorThreadTopic(visitorThreadTopic);
        request.setChannel(ChannelEnum.WEB_VISITOR.name());
        return request;
    }

    private static ThreadEntity sourceThread(String orgUid) {
        return ThreadEntity.builder()
                .uid(SOURCE_THREAD_UID)
                .orgUid(orgUid)
                .topic(SOURCE_THREAD_TOPIC)
                .build();
    }

    private static TicketSettingsEntity settings() {
        TicketSettingsEntity settings = new TicketSettingsEntity();
        settings.setUid("ts-1");
        settings.setOrgUid(ORG_UID);
        return settings;
    }

    private static ModelMapper productionModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setAmbiguityIgnored(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
        return modelMapper;
    }

    private static Fixture fixture() {
        Fixture f = new Fixture();
        f.ticketRepository = mock(TicketRepository.class);
        f.threadRestService = mock(ThreadRestService.class);
        f.uidUtils = mock(UidUtils.class);
        f.ticketSettingsRestService = mock(TicketSettingsRestService.class);

        // 工单 uid / 新会话 uid / 工单编号：返回唯一值（不假设调用顺序）
        final java.util.concurrent.atomic.AtomicLong seq = new java.util.concurrent.atomic.AtomicLong();
        when(f.uidUtils.getUid()).thenAnswer(invocation -> "uid-" + seq.incrementAndGet());
        // 来源会话只读加载
        when(f.threadRestService.findByUidReadOnly(SOURCE_THREAD_UID))
                .thenReturn(Optional.of(sourceThread(ORG_UID)));
        // 保存原样返回
        when(f.ticketRepository.save(any(TicketEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(f.threadRestService.save(any(ThreadEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(f.ticketRepository.existsByOrgUidAndTicketNumber(anyString(), anyString())).thenReturn(false);
        // 工单设置：外部工单 + 工作组 -> resolveEntityByWorkgroup
        when(f.ticketSettingsRestService.resolveEntityByWorkgroup(anyString(), anyString(), anyString()))
                .thenReturn(settings());
        return f;
    }

    private static TicketRestService newService(Fixture f, ModelMapper modelMapper) {
        return new TicketRestService(
                f.ticketRepository,
                mock(TicketAttachmentRepository.class),
                modelMapper,
                mock(AuthService.class),
                f.uidUtils,
                f.threadRestService,
                mock(MessageRepository.class),
                mock(TopicSubscriptionRepository.class),
                mock(TicketSlaRecordRepository.class),
                mock(UploadRestService.class),
                mock(ApplicationEventPublisher.class),
                mock(CategoryRestService.class),
                f.ticketSettingsRestService,
                mock(MemberRepository.class),
                mock(TicketVisibilityService.class));
    }

    private static class Fixture {
        TicketRepository ticketRepository;
        ThreadRestService threadRestService;
        UidUtils uidUtils;
        TicketSettingsRestService ticketSettingsRestService;
        TicketRestService service;
    }
}
