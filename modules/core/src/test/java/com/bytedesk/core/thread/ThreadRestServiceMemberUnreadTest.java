package com.bytedesk.core.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;

/**
 * MEMBER 同事会话未读数修正验证：
 *
 * 背景：同事一对一会话在库中是成对的两个 thread（topic 互为反转），消息只挂载在发送方
 * 自己的 thread 上，实体内 getUnreadCount() 只能看到自己发送的消息（恒为 0）。
 * convertToResponse 需按“正反 topic 对 + 排除自己发送”跨 thread 统计真实未读数。
 *
 * - MEMBER 会话：调用 countMemberUnreadByTopics(topic, reverseTopic, ownerUserUid) 并覆写 unreadCount；
 * - 非 MEMBER 会话：不触发跨 topic 统计；
 * - topic 格式异常（非四段）/ owner 缺失：跳过统计，避免误查。
 */
@ExtendWith(MockitoExtension.class)
class ThreadRestServiceMemberUnreadTest {

    @Mock
    private AuthService authService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private BytedeskEventPublisher bytedeskEventPublisher;

    @Mock
    private TopicSubscriptionRestService topicSubscriptionRestService;

    @Mock
    private ActiveThreadCacheService activeThreadCacheService;

    @Mock
    private MessageRestService messageRestService;

    private ThreadRestService threadRestService;

    private ApplicationContext originalContext;

    @BeforeEach
    void setUp() {
        // ThreadConvertUtils 通过 ApplicationContextHolder 静态获取 ModelMapper，
        // 纯单测环境下注入 mock 上下文，并在测试结束后恢复原值，避免污染其它测试
        originalContext = ApplicationContextHolder.getApplicationContext();
        ApplicationContext mockContext = mock(ApplicationContext.class);
        when(mockContext.getBean(ModelMapper.class)).thenReturn(modelMapper);
        new ApplicationContextHolder().setApplicationContext(mockContext);

        threadRestService = new ThreadRestService(
                authService,
                modelMapper,
                threadRepository,
                uidUtils,
                bytedeskEventPublisher,
                topicSubscriptionRestService,
                activeThreadCacheService,
                messageRestService);
    }

    @AfterEach
    void tearDown() {
        // 恢复 ApplicationContextHolder 原始上下文（可能为 null）
        new ApplicationContextHolder().setApplicationContext(originalContext);
    }

    private ThreadEntity memberThread(String topic, UserEntity owner) {
        ThreadEntity thread = ThreadEntity.builder().build();
        thread.setType(ThreadTypeEnum.MEMBER.name());
        thread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        thread.setTopic(topic);
        thread.setOwner(owner);
        return thread;
    }

    private UserEntity userOf(String uid) {
        UserEntity user = new UserEntity();
        user.setUid(uid);
        return user;
    }

    @Test
    void memberThreadUnreadCountIsComputedAcrossReverseTopics() {
        ThreadEntity thread = memberThread("org/member/mem-a/mem-b", userOf("user-b"));
        when(modelMapper.map(any(ThreadEntity.class), eq(ThreadResponse.class)))
                .thenReturn(new ThreadResponse());
        when(messageRestService.countMemberUnreadByTopics(
                eq("org/member/mem-a/mem-b"), eq("org/member/mem-b/mem-a"), eq("user-b")))
                .thenReturn(3L);

        ThreadResponse response = threadRestService.convertToResponse(thread);

        verify(messageRestService).countMemberUnreadByTopics(
                eq("org/member/mem-a/mem-b"), eq("org/member/mem-b/mem-a"), eq("user-b"));
        assertThat(response.getUnreadCount()).isEqualTo(3);
    }

    @Test
    void nonMemberThreadSkipsCrossTopicUnreadCount() {
        ThreadEntity thread = memberThread("org/agent/df_ag_uid/visitor_uid", userOf("user-b"));
        thread.setType(ThreadTypeEnum.AGENT.name());
        when(modelMapper.map(any(ThreadEntity.class), eq(ThreadResponse.class)))
                .thenReturn(new ThreadResponse());

        ThreadResponse response = threadRestService.convertToResponse(thread);

        verify(messageRestService, never()).countMemberUnreadByTopics(any(), any(), any());
        assertThat(response.getUnreadCount()).isNull();
    }

    @Test
    void memberThreadWithMalformedTopicSkipsUnreadCount() {
        // org/member/{uid} 三段格式为成员通知 topic，非一对一 thread，应跳过
        ThreadEntity thread = memberThread("org/member/mem-a", userOf("user-b"));
        when(modelMapper.map(any(ThreadEntity.class), eq(ThreadResponse.class)))
                .thenReturn(new ThreadResponse());

        ThreadResponse response = threadRestService.convertToResponse(thread);

        verify(messageRestService, never()).countMemberUnreadByTopics(any(), any(), any());
        assertThat(response.getUnreadCount()).isNull();
    }

    @Test
    void memberThreadWithoutOwnerSkipsUnreadCount() {
        ThreadEntity thread = memberThread("org/member/mem-a/mem-b", null);
        when(modelMapper.map(any(ThreadEntity.class), eq(ThreadResponse.class)))
                .thenReturn(new ThreadResponse());

        ThreadResponse response = threadRestService.convertToResponse(thread);

        verify(messageRestService, never()).countMemberUnreadByTopics(any(), any(), any());
        assertThat(response.getUnreadCount()).isNull();
    }
}
