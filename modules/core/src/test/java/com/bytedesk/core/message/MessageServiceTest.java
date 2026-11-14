package com.bytedesk.core.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.message.extra.LoginNoticeExtra;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.message.content.NoticeContent;
import com.bytedesk.core.message.enums.MessageNoticeTypeEnum;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserExtraJobTitleUtils;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

class MessageServiceTest {

    @Test
    void sendSystemNoticeShouldFanOutWithoutPersistingNoticeEntity() {
        MessagePersistCache messagePersistCache = mock(MessagePersistCache.class);
        MessageRestService messageRestService = mock(MessageRestService.class);
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService messageSendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);

        MessageService messageService = spy(new MessageService(
                messagePersistCache,
                messageRestService,
                threadRestService,
                messageSendService,
                uidUtils,
                userRepository,
                memberRepository));

        UserEntity userA = UserEntity.builder().uid("user-a").username("user-a").build();
        UserEntity userB = UserEntity.builder().uid("user-b").username("user-b").build();
        MemberEntity memberA = MemberEntity.builder().uid("member-a").orgUid("org-1").user(userA).build();
        MemberEntity memberB = MemberEntity.builder().uid("member-b").orgUid("org-1").user(userB).build();
        NoticeContent request = NoticeContent.builder()
                .title("Invite")
                .content("Agent invited you")
                .type(MessageNoticeTypeEnum.INVITE.name())
                .status("INVITE_PENDING")
                .orgUid("org-1")
                .level("ORGANIZATION")
                .build();

        when(memberRepository.findAll()).thenReturn(List.of(memberA, memberB));
        doReturn("msg-1").when(messageService).sendNoticeMessage(eq(userA), eq("org-1"), any(String.class));
        doReturn("msg-2").when(messageService).sendNoticeMessage(eq(userB), eq("org-1"), any(String.class));

        messageService.sendSystemNotice(request);

        verify(messageService, times(1)).sendNoticeMessage(eq(userA), eq("org-1"), any(String.class));
        verify(messageService, times(1)).sendNoticeMessage(eq(userB), eq("org-1"), any(String.class));
    }

    @Test
    void sendSystemLoginNoticeShouldSendSingleSystemMessage() {
        MessagePersistCache messagePersistCache = mock(MessagePersistCache.class);
        MessageRestService messageRestService = mock(MessageRestService.class);
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService messageSendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);

        MessageService messageService = spy(new MessageService(
                messagePersistCache,
                messageRestService,
                threadRestService,
                messageSendService,
                uidUtils,
                userRepository,
                memberRepository));

        UserEntity user = UserEntity.builder().uid("user-1").username("user-1").build();
        LoginNoticeExtra loginNoticeExtra = LoginNoticeExtra.builder()
                .loginIp("127.0.0.1")
                .loginLocation("Shanghai")
                .loginTime("2026-04-21 10:00:00")
                .build();

        NoticeContent request = NoticeContent.builder()
                .title("login")
                .content("login.username")
                .type(MessageNoticeTypeEnum.LOGIN.name())
                .status(MessageStatusEnum.READ.name())
                .extra(loginNoticeExtra.toJson())
                .userUid("user-1")
                .build();

        when(userRepository.findByUid("user-1")).thenReturn(Optional.of(user));
        doReturn("msg-1").when(messageService).sendLoginNoticeMessage(eq(user), any(String.class), any(String.class));

        messageService.sendSystemLoginNotice(request);

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageService, times(1)).sendLoginNoticeMessage(eq(user), any(String.class), contentCaptor.capture());

        NoticeContent noticeContent = NoticeContent.fromJson(contentCaptor.getValue());
        LoginNoticeExtra parsedExtra = LoginNoticeExtra.fromJson(noticeContent.getExtra());

        assertThat(noticeContent.getType()).isEqualTo(MessageNoticeTypeEnum.LOGIN.name());
        assertThat(noticeContent.getStatus()).isEqualTo(MessageStatusEnum.READ.name());
        assertThat(parsedExtra.getLoginIp()).isEqualTo("127.0.0.1");
        assertThat(parsedExtra.getLoginLocation()).isEqualTo("Shanghai");
    }

    private MessageService buildService(MessagePersistCache cache, MessageRestService messageRestService,
            ThreadRestService threadRestService, IMessageSendService sendService, UidUtils uidUtils,
            UserRepository userRepository, MemberRepository memberRepository) {
        return spy(new MessageService(cache, messageRestService, threadRestService, sendService, uidUtils,
                userRepository, memberRepository));
    }

    private String groupMessageJson(String messageUid, String threadUid, String userUid, String userExtra) {
        return "{"
                + "\"uid\":\"" + messageUid + "\","
                + "\"type\":\"TEXT\","
                + "\"content\":\"hello\","
                + "\"status\":\"SENDING\","
                + "\"createdAt\":\"2026-11-09 10:00:00\","
                + "\"thread\":{\"uid\":\"" + threadUid + "\",\"type\":\"GROUP\",\"topic\":\"org/group/g1\"},"
                + "\"user\":{\"uid\":\"" + userUid + "\",\"nickname\":\"n\",\"extra\":" 
                + (userExtra == null ? "null" : "\"" + userExtra.replace("\"", "\\\"") + "\"") + "},"
                + "\"extra\":\"{\\\"orgUid\\\":\\\"org-1\\\"}\""
                + "}";
    }

    @Test
    void processMessageJsonShouldInjectGroupSenderJobTitleFromMember() {
        MessagePersistCache cache = mock(MessagePersistCache.class);
        MessageRestService messageRestService = mock(MessageRestService.class);
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService sendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        MessageService messageService = buildService(cache, messageRestService, threadRestService, sendService,
                uidUtils, userRepository, memberRepository);

        when(messageRestService.isMessageExists("msg-1")).thenReturn(false);
        when(threadRestService.findByUid("thread-1"))
                .thenReturn(Optional.of(ThreadEntity.builder().uid("thread-1").orgUid("org-1").build()));
        UserEntity sender = UserEntity.builder().uid("user-1").username("user-1").build();
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-1"))
                .thenReturn(Optional.of(MemberEntity.builder().uid("member-1").orgUid("org-1")
                        .user(sender).jobTitle("技术总监").build()));

        String result = messageService.processMessageJson(
                groupMessageJson("msg-1", "thread-1", "user-1", "{\"foo\":\"bar\",\"jobTitle\":\"伪造\"}"), false);

        MessageProtobuf processed = MessageProtobuf.fromJson(result);
        assertThat(UserExtraJobTitleUtils.getJobTitle(processed.getUser().getExtra())).isEqualTo("技术总监");
        // 未知键保留
        assertThat(processed.getUser().getExtra()).contains("\"foo\":\"bar\"");
        // 已进入持久化队列
        verify(cache, times(1)).pushForPersist(result);
    }

    @Test
    void processMessageJsonShouldStripForgedJobTitleWhenMemberHasNone() {
        MessagePersistCache cache = mock(MessagePersistCache.class);
        MessageRestService messageRestService = mock(MessageRestService.class);
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService sendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        MessageService messageService = buildService(cache, messageRestService, threadRestService, sendService,
                uidUtils, userRepository, memberRepository);

        when(messageRestService.isMessageExists("msg-2")).thenReturn(false);
        when(threadRestService.findByUid("thread-1"))
                .thenReturn(Optional.of(ThreadEntity.builder().uid("thread-1").orgUid("org-1").build()));
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-1"))
                .thenReturn(Optional.empty());

        String result = messageService.processMessageJson(
                groupMessageJson("msg-2", "thread-1", "user-1", "{\"jobTitle\":\"伪造\"}"), false);

        MessageProtobuf processed = MessageProtobuf.fromJson(result);
        // 非成员/无职务：伪造值被移除，不显示职务
        assertThat(UserExtraJobTitleUtils.getJobTitle(processed.getUser().getExtra())).isEmpty();
    }

    @Test
    void processMessageJsonShouldKeepPersistedSnapshotForReplay() {
        MessagePersistCache cache = mock(MessagePersistCache.class);
        MessageRestService messageRestService = mock(MessageRestService.class);
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService sendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        MessageService messageService = buildService(cache, messageRestService, threadRestService, sendService,
                uidUtils, userRepository, memberRepository);

        // 消息已存在（回执/重放）：不查成员，保留既有快照
        when(messageRestService.isMessageExists("msg-3")).thenReturn(true);

        String result = messageService.processMessageJson(
                groupMessageJson("msg-3", "thread-1", "user-1", "{\"jobTitle\":\"旧快照\"}"), false);

        MessageProtobuf processed = MessageProtobuf.fromJson(result);
        assertThat(UserExtraJobTitleUtils.getJobTitle(processed.getUser().getExtra())).isEqualTo("旧快照");
        verify(memberRepository, never()).findByUser_UidAndOrgUidAndDeletedFalse(any(), any());
    }

    @Test
    void processMessageJsonShouldSkipNonGroupThread() {
        MessagePersistCache cache = mock(MessagePersistCache.class);
        MessageRestService messageRestService = mock(MessageRestService.class);
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService sendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        MessageService messageService = buildService(cache, messageRestService, threadRestService, sendService,
                uidUtils, userRepository, memberRepository);

        String json = "{"
                + "\"uid\":\"msg-4\",\"type\":\"TEXT\",\"content\":\"hi\",\"status\":\"SENDING\","
                + "\"createdAt\":\"2026-11-09 10:00:00\","
                + "\"thread\":{\"uid\":\"thread-2\",\"type\":\"AGENT\",\"topic\":\"org/agent/a1/v1\"},"
                + "\"user\":{\"uid\":\"user-1\",\"nickname\":\"n\"}"
                + "}";

        messageService.processMessageJson(json, false);

        // 非群聊：不触发成员查询
        verify(memberRepository, never()).findByUser_UidAndOrgUidAndDeletedFalse(any(), any());
        verifyNoInteractions(threadRestService);
    }
}