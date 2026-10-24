package com.bytedesk.core.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.exception.ForbiddenException;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;

/**
 * 文件助手会话“打开即已读”服务层验证：
 *
 * - 本人 ASSISTANT + 精确 topic：批量置 READ、清手动未读标记、返回权威未读数；
 * - observedMessageUids：未落库的消息进入 pendingMessageUids，已落库且归属正确的退出 pending；
 * - 越权拒绝：非本人、非 ASSISTANT 类型、topic 不精确匹配（含 file 前缀伪造）均 Forbidden，
 *   且不执行任何消息状态更新；
 * - 幂等：无未读时 updatedCount=0 仍成功；
 * - 手动未读标记已为 false 时不重复保存会话（避免版本号自增）。
 */
@ExtendWith(MockitoExtension.class)
class ThreadRestServiceFileAssistantReadTest {

    private static final String THREAD_UID = "th_fa_001";
    private static final String USER_UID = "user-a";

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

    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        originalContext = ApplicationContextHolder.getApplicationContext();
        ApplicationContext mockContext = mock(ApplicationContext.class);
        lenient().when(mockContext.getBean(ModelMapper.class)).thenReturn(modelMapper);
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

        currentUser = new UserEntity();
        currentUser.setUid(USER_UID);
        lenient().when(authService.getUser()).thenReturn(currentUser);
    }

    @AfterEach
    void tearDown() {
        new ApplicationContextHolder().setApplicationContext(originalContext);
    }

    private ThreadEntity fileAssistantThread() {
        ThreadEntity thread = ThreadEntity.builder().build();
        thread.setUid(THREAD_UID);
        thread.setType(ThreadTypeEnum.ASSISTANT.name());
        thread.setStatus(ThreadProcessStatusEnum.NEW.name());
        thread.setTopic("file/" + USER_UID);
        thread.setOwner(currentUser);
        return thread;
    }

    private MessageEntity messageOf(String uid, String status) {
        MessageEntity message = new MessageEntity();
        message.setUid(uid);
        message.setStatus(status);
        return message;
    }

    @Test
    void ownFileAssistantThreadMarksUnreadAndClearsManualFlag() {
        ThreadEntity thread = fileAssistantThread();
        thread.setUnread(true);
        when(threadRepository.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));
        when(messageRestService.markThreadUnreadAsRead(THREAD_UID)).thenReturn(3);
        // 权威未读数按消息重查（isUnread: SENDING/SUCCESS/DELIVERED）
        when(messageRestService.findByThreadUid(THREAD_UID))
                .thenReturn(List.of(
                        messageOf("m1", MessageStatusEnum.READ.name()),
                        messageOf("m2", MessageStatusEnum.READ.name()),
                        messageOf("m3", MessageStatusEnum.READ.name())));

        ThreadFileAssistantReadResponse response = threadRestService.markFileAssistantRead(THREAD_UID, null);

        verify(messageRestService).markThreadUnreadAsRead(THREAD_UID);
        assertThat(thread.getUnread()).isFalse(); // 手动未读标记被清除
        assertThat(response.getUpdatedCount()).isEqualTo(3);
        assertThat(response.getUnreadCount()).isZero();
        assertThat(response.getPendingMessageUids()).isEmpty();
        assertThat(response.getThreadUid()).isEqualTo(THREAD_UID);
    }

    @Test
    void observedUidsNotYetPersistedReturnedAsPending() {
        ThreadEntity thread = fileAssistantThread();
        when(threadRepository.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));
        when(messageRestService.markThreadUnreadAsRead(THREAD_UID)).thenReturn(1);
        // 已落库消息（含一条属于其他会话的，也应视为 pending，不允许跨会话确认）
        ThreadEntity threadEntity = thread;
        MessageEntity persisted = messageOf("m-observed-1", MessageStatusEnum.SUCCESS.name());
        persisted.setThread(threadEntity);
        MessageEntity foreignThread = messageOf("m-foreign", MessageStatusEnum.SUCCESS.name());
        ThreadEntity otherThread = ThreadEntity.builder().build();
        otherThread.setUid("th_other");
        foreignThread.setThread(otherThread);
        when(messageRestService.findByUidIn(anyList())).thenReturn(List.of(persisted, foreignThread));
        when(messageRestService.findByThreadUid(THREAD_UID)).thenReturn(List.of(persisted));

        ThreadFileAssistantReadResponse response = threadRestService
                .markFileAssistantRead(THREAD_UID, List.of("m-observed-1", "m-not-persisted", "m-foreign"));

        // 落库且属于本会话 → 已随批量置 READ，退出 pending；未落库/归属异常 → pending
        assertThat(response.getPendingMessageUids())
                .containsExactlyInAnyOrder("m-not-persisted", "m-foreign");
    }

    @Test
    void nonOwnerThreadIsForbidden() {
        ThreadEntity thread = fileAssistantThread();
        UserEntity other = new UserEntity();
        other.setUid("user-b");
        thread.setOwner(other);
        when(threadRepository.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> threadRestService.markFileAssistantRead(THREAD_UID, null))
                .isInstanceOf(ForbiddenException.class);
        verify(messageRestService, never()).markThreadUnreadAsRead(any());
    }

    @Test
    void nonAssistantTypeIsForbidden() {
        ThreadEntity thread = fileAssistantThread();
        thread.setType(ThreadTypeEnum.CHANNEL.name()); // 系统通知会话，topic 同为 system/{uid} 形态
        thread.setTopic("system/" + USER_UID);
        when(threadRepository.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> threadRestService.markFileAssistantRead(THREAD_UID, null))
                .isInstanceOf(ForbiddenException.class);
        verify(messageRestService, never()).markThreadUnreadAsRead(any());
    }

    @Test
    void fuzzyTopicPrefixIsForbidden() {
        // 伪造：topic 仅以 file 开头但不是本人精确 topic（如 file/other-user）
        ThreadEntity thread = fileAssistantThread();
        thread.setTopic("file/other-user");
        when(threadRepository.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> threadRestService.markFileAssistantRead(THREAD_UID, null))
                .isInstanceOf(ForbiddenException.class);
        verify(messageRestService, never()).markThreadUnreadAsRead(any());
    }

    @Test
    void idempotentWhenNoUnreadMessages() {
        ThreadEntity thread = fileAssistantThread();
        thread.setUnread(false);
        when(threadRepository.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));
        when(messageRestService.markThreadUnreadAsRead(THREAD_UID)).thenReturn(0);
        when(messageRestService.findByThreadUid(THREAD_UID)).thenReturn(List.of());

        ThreadFileAssistantReadResponse response = threadRestService.markFileAssistantRead(THREAD_UID, null);

        assertThat(response.getUpdatedCount()).isZero();
        assertThat(response.getUnreadCount()).isZero();
    }

    @Test
    void unreadCountReflectsRemainingUnreadMessages() {
        ThreadEntity thread = fileAssistantThread();
        when(threadRepository.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));
        when(messageRestService.markThreadUnreadAsRead(THREAD_UID)).thenReturn(2);
        // 模拟竞态：批量更新后仍有新消息落库为未读
        when(messageRestService.findByThreadUid(THREAD_UID)).thenReturn(List.of(
                messageOf("m1", MessageStatusEnum.READ.name()),
                messageOf("m2", MessageStatusEnum.SUCCESS.name())));

        ThreadFileAssistantReadResponse response = threadRestService.markFileAssistantRead(THREAD_UID, null);

        assertThat(response.getUnreadCount()).isEqualTo(1);
    }
}
