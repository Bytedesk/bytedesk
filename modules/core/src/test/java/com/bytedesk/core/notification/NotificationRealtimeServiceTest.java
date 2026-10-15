package com.bytedesk.core.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.bytedesk.core.message.MessageSocketService;
import com.bytedesk.core.message.content.NoticeContent;
import com.bytedesk.core.message.content.SystemContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.utils.MessageConvertUtils;
import com.bytedesk.core.notification.event.NotificationCreateEvent;
import com.bytedesk.core.socket.protobuf.model.MessageProto;

import org.mockito.MockedStatic;

class NotificationRealtimeServiceTest {

    @Test
    void onNotificationCreateEventShouldWrapNoticeContentWithSystemContent() {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        MessageSocketService messageSocketService = mock(MessageSocketService.class);
        NotificationRealtimeService service = new NotificationRealtimeService(simpMessagingTemplate, messageSocketService);

        NotificationEntity notification = NotificationEntity.builder()
                .uid("notice-1")
                .userUid("user-1")
                .orgUid("org-1")
                .title("工单通知")
                .content("工单状态已更新")
                .type("TICKET")
                .status(NotificationStatusEnum.UNREAD.name())
                .build();

        service.onNotificationCreateEvent(new NotificationCreateEvent(this, notification));

        ArgumentCaptor<MessageProto.Message> messageCaptor = ArgumentCaptor.forClass(MessageProto.Message.class);
        verify(messageSocketService).sendMqttMessageToUser(eq("user-1"), eq("system/user-1"), messageCaptor.capture());

        String payload = messageCaptor.getValue().getContent();
        NoticeContent noticeContent = NoticeContent.fromJson(payload);
        SystemContent systemContent = SystemContent.fromJson(noticeContent.getContent());

        assertThat(messageCaptor.getValue().getType()).isEqualTo(MessageTypeEnum.NOTICE.name());
        assertThat(noticeContent.getTitle()).isEqualTo("工单通知");
        assertThat(systemContent.getType()).isEqualTo(MessageTypeEnum.SYSTEM.name());
        assertThat(systemContent.getContent()).isEqualTo("工单状态已更新");
        assertThat(systemContent.getTitle()).isEqualTo("工单状态已更新");
    }

    @Test
    void onNotificationCreateEventShouldSwallowLinkageErrorFromProtoConversion() {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        MessageSocketService messageSocketService = mock(MessageSocketService.class);
        NotificationRealtimeService service = new NotificationRealtimeService(simpMessagingTemplate, messageSocketService);

        NotificationEntity notification = NotificationEntity.builder()
                .uid("notice-2")
                .userUid("user-2")
                .orgUid("org-1")
                .title("SLA已超时")
                .content("工单SLA首次响应超时")
                .type("TICKET")
                .status(NotificationStatusEnum.UNREAD.name())
                .build();

        // 模拟 protobuf 生成代码不一致时抛出的 NoSuchMethodError（LinkageError 子类）
        try (MockedStatic<MessageConvertUtils> mocked = org.mockito.Mockito.mockStatic(MessageConvertUtils.class)) {
            mocked.when(() -> MessageConvertUtils.toProtoBean(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
                    .thenThrow(new NoSuchMethodError("'boolean com.bytedesk.core.socket.protobuf.model.MessageProto$Message.access$7()'"));

            assertThatCode(() -> service.onNotificationCreateEvent(new NotificationCreateEvent(this, notification)))
                    .doesNotThrowAnyException();
        }

        verify(messageSocketService, org.mockito.Mockito.never())
                .sendMqttMessageToUser(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }
}