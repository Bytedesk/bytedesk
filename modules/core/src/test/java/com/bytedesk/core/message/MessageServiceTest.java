package com.bytedesk.core.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.message.extra.LoginNoticeExtra;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.message.content.NoticeContent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

class MessageServiceTest {

    @Test
    void sendSystemNoticeShouldFanOutWithoutPersistingNoticeEntity() {
        MessagePersistCache messagePersistCache = mock(MessagePersistCache.class);
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService messageSendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);

        MessageService messageService = spy(new MessageService(
                messagePersistCache,
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
        ThreadRestService threadRestService = mock(ThreadRestService.class);
        IMessageSendService messageSendService = mock(IMessageSendService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);

        MessageService messageService = spy(new MessageService(
                messagePersistCache,
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
}