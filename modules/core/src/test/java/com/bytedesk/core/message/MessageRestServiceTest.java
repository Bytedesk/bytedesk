package com.bytedesk.core.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

@ExtendWith(MockitoExtension.class)
class MessageRestServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AuthService authService;

    private MessageRestService messageRestService;

    @BeforeEach
    void setUp() {
        messageRestService = spy(new MessageRestService(messageRepository, authService));
    }

    @Test
    void updateShouldPersistLatestContent() {
        UserEntity user = new UserEntity();
        user.setUid("agent-1");

        MessageEntity message = MessageEntity.builder()
                .uid("msg-1")
                .type(MessageTypeEnum.IMAGE.name())
                .content("{\"url\":\"https://example.com/a.png\"}")
                .build();

        MessageRequest request = MessageRequest.builder()
                .uid("msg-1")
                .content("{\"url\":\"https://example.com/a.png\",\"ocrText\":\"hello\"}")
                .build();

        when(authService.getUser()).thenReturn(user);
        when(messageRepository.findByUid("msg-1")).thenReturn(Optional.of(message));
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doReturn(MessageResponse.builder()
            .uid("msg-1")
            .content("{\"url\":\"https://example.com/a.png\",\"ocrText\":\"hello\"}")
            .build())
            .when(messageRestService).convertToResponse(any(MessageEntity.class));

        MessageResponse response = messageRestService.update(request);

        assertThat(response.getUid()).isEqualTo("msg-1");
        assertThat(response.getContent()).contains("ocrText").contains("hello");
        assertThat(message.getContent()).contains("ocrText").contains("hello");
    }
}