package com.bytedesk.core.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.assertj.core.api.Assertions;

import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;

class NotificationServiceTest {

    @Test
        void dispatchNotificationShouldCreateOneNotificationPerOrganizationMember() {
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        AuthService authService = mock(AuthService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        ModelMapper modelMapper = mock(ModelMapper.class);

        NotificationService notificationService = new NotificationService(
                notificationRepository,
                userRepository,
                memberRepository,
                authService,
                uidUtils,
                modelMapper);

        UserEntity sender = UserEntity.builder()
                .uid("sender-1")
                .nickname("Sender")
                .superUser(true)
                .build();
        UserEntity userA = UserEntity.builder().uid("user-a").username("user-a").build();
        UserEntity userB = UserEntity.builder().uid("user-b").username("user-b").build();

        MemberEntity memberA = MemberEntity.builder().uid("member-a").orgUid("org-1").user(userA).build();
        MemberEntity memberB = MemberEntity.builder().uid("member-b").orgUid("org-1").user(userB).build();

        NotificationRequest request = NotificationRequest.builder()
                .title("System maintenance")
                .content("Window starts at 23:00")
                .level(LevelEnum.ORGANIZATION.name())
                .orgUid("org-1")
                .build();

        when(authService.getUser()).thenReturn(sender);
        when(memberRepository.findAll()).thenReturn(List.of(memberA, memberB));
        AtomicInteger sequence = new AtomicInteger(1);
        when(uidUtils.getUid()).thenAnswer(invocation -> "notification-" + sequence.getAndIncrement());
                when(modelMapper.map(eq(request), eq(NotificationEntity.class))).thenAnswer(invocation -> {
                        NotificationEntity entity = new NotificationEntity();
            entity.setTitle(request.getTitle());
            entity.setContent(request.getContent());
            entity.setDeptUid(request.getDeptUid());
            return entity;
        });
        when(modelMapper.map(eq(request), eq(NotificationProtobuf.class))).thenReturn(NotificationProtobuf.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .extra(request.getExtra())
                .build());
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationDispatchResponse response = notificationService.dispatchNotification(request);

        ArgumentCaptor<NotificationEntity> notificationCaptor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository, times(2)).save(notificationCaptor.capture());
        assertThat(response.getSentCount()).isEqualTo(2);
        assertThat(notificationCaptor.getAllValues())
                .extracting(NotificationEntity::getUserUid)
                .containsExactlyInAnyOrder("user-a", "user-b");
        assertThat(notificationCaptor.getAllValues())
                .extracting(NotificationEntity::getStatus)
                .containsOnly(NotificationStatusEnum.UNREAD.name());
    }

    @Test
    void dispatchNotificationShouldRejectNonAdminManualBroadcast() {
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        AuthService authService = mock(AuthService.class);
        UidUtils uidUtils = mock(UidUtils.class);
        ModelMapper modelMapper = mock(ModelMapper.class);

        NotificationService notificationService = new NotificationService(
                notificationRepository,
                userRepository,
                memberRepository,
                authService,
                uidUtils,
                modelMapper);

        UserEntity sender = UserEntity.builder()
                .uid("agent-1")
                .username("agent-1")
                .superUser(false)
                .build();

        NotificationRequest request = NotificationRequest.builder()
                .title("System maintenance")
                .content("Window starts at 23:00")
                .level(LevelEnum.ORGANIZATION.name())
                .orgUid("org-1")
                .build();

        when(authService.getUser()).thenReturn(sender);

        Assertions.assertThatThrownBy(() -> notificationService.dispatchNotification(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only admin can send notifications");

        verify(notificationRepository, never()).save(any(NotificationEntity.class));
    }

        @Test
        void dispatchNotificationShouldFallbackInvalidTypeToGeneral() {
                NotificationRepository notificationRepository = mock(NotificationRepository.class);
                UserRepository userRepository = mock(UserRepository.class);
                MemberRepository memberRepository = mock(MemberRepository.class);
                AuthService authService = mock(AuthService.class);
                UidUtils uidUtils = mock(UidUtils.class);
                ModelMapper modelMapper = mock(ModelMapper.class);

                NotificationService notificationService = new NotificationService(
                                notificationRepository,
                                userRepository,
                                memberRepository,
                                authService,
                                uidUtils,
                                modelMapper);

                UserEntity sender = UserEntity.builder()
                                .uid("sender-1")
                                .nickname("Sender")
                                .superUser(true)
                                .build();
                UserEntity recipient = UserEntity.builder().uid("user-a").username("user-a").build();

                NotificationRequest request = NotificationRequest.builder()
                                .title("System maintenance")
                                .content("Window starts at 23:00")
                                .type("unsupported")
                                .level(LevelEnum.USER.name())
                                .userUid("user-a")
                                .build();

                when(authService.getUser()).thenReturn(sender);
                when(userRepository.findByUid("user-a")).thenReturn(Optional.of(recipient));
                when(uidUtils.getUid()).thenReturn("notification-1");
                when(modelMapper.map(eq(request), eq(NotificationEntity.class))).thenAnswer(invocation -> {
                        NotificationEntity entity = new NotificationEntity();
                        entity.setTitle(request.getTitle());
                        entity.setContent(request.getContent());
                        entity.setType(request.getType());
                        return entity;
                });
                when(modelMapper.map(eq(request), eq(NotificationProtobuf.class))).thenReturn(NotificationProtobuf.builder()
                                .title(request.getTitle())
                                .content(request.getContent())
                                .type(request.getType())
                                .extra(request.getExtra())
                                .build());
                when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

                notificationService.dispatchNotification(request);

                ArgumentCaptor<NotificationEntity> notificationCaptor = ArgumentCaptor.forClass(NotificationEntity.class);
                verify(notificationRepository).save(notificationCaptor.capture());
                assertThat(notificationCaptor.getValue().getType()).isEqualTo(NotificationTypeEnum.GENERAL.name());
        }
}