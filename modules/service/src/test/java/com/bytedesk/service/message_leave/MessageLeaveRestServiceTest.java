package com.bytedesk.service.message_leave;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.service.queue_member.QueueMemberRestService;

@ExtendWith(MockitoExtension.class)
class MessageLeaveRestServiceTest {

    @Mock
    private MessageLeaveRepository messageLeaveRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthService authService;

    @Mock
    private MessageRestService messageRestService;

    @Mock
    private QueueMemberRestService queueMemberRestService;

    @Mock
    private ThreadRestService threadRestService;

    private MessageLeaveRestService messageLeaveRestService;

    @BeforeEach
    void setUp() {
        messageLeaveRestService = spy(new MessageLeaveRestService(
                messageLeaveRepository,
                uidUtils,
                modelMapper,
                authService,
                messageRestService,
                queueMemberRestService,
                threadRestService));

        doAnswer(invocation -> {
            MessageLeaveEntity entity = invocation.getArgument(0);
            MessageLeaveResponse response = new MessageLeaveResponse();
            response.setUid(entity.getUid());
            response.setNickname(entity.getNickname());
            response.setContact(entity.getContact());
            response.setContent(entity.getContent());
            response.setType(entity.getType());
            ReflectionTestUtils.setField(response, "formData", ReflectionTestUtils.getField(entity, "formData"));
            return response;
        }).when(messageLeaveRestService).convertToResponse(any(MessageLeaveEntity.class));
    }

    @Test
    void createShouldPersistGenericFormDataAndWriteMessageExtra() {
        String formData = "{\"title\":\"Issue\",\"replyMethod\":\"email\"}";

        MessageLeaveRequest request = new MessageLeaveRequest();
        request.setOrgUid("org-1");
        request.setThreadUid("thread-1");
        request.setMessageUid("message-1");
        request.setNickname("Visitor");
        request.setContact("visitor@test.com");
        request.setContent("Need help");
        request.setType("consultation");
        ReflectionTestUtils.setField(request, "formData", formData);

        MessageLeaveEntity mappedEntity = MessageLeaveEntity.builder()
            .nickname(request.getNickname())
            .contact(request.getContact())
            .content(request.getContent())
            .type(request.getType())
            .threadUid(request.getThreadUid())
            .messageUid(request.getMessageUid())
            .build();
        mappedEntity.setOrgUid(request.getOrgUid());
        ReflectionTestUtils.setField(mappedEntity, "formData", formData);

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setUid("message-1");

        when(modelMapper.map(request, MessageLeaveEntity.class)).thenReturn(mappedEntity);
        when(uidUtils.getUid()).thenReturn("leave-1");
        when(messageLeaveRepository.save(any(MessageLeaveEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRestService.findByUid("message-1")).thenReturn(Optional.of(messageEntity));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(queueMemberRestService.findByThreadUid("thread-1")).thenReturn(Optional.empty());

        MessageLeaveResponse response = messageLeaveRestService.create(request);

        ArgumentCaptor<MessageEntity> messageCaptor = ArgumentCaptor.forClass(MessageEntity.class);
        org.mockito.Mockito.verify(messageRestService).save(messageCaptor.capture());

        MessageLeaveExtra extra = MessageLeaveExtra.fromJson(messageCaptor.getValue().getExtra());

        assertThat(ReflectionTestUtils.getField(response, "formData")).isEqualTo(formData);
        assertThat(mappedEntity.getUid()).isEqualTo("leave-1");
        assertThat(ReflectionTestUtils.getField(mappedEntity, "formData")).isEqualTo(formData);
        assertThat(ReflectionTestUtils.getField(extra, "formData"))
            .isEqualTo(JSON.parseObject(formData, new TypeReference<java.util.Map<String, Object>>() {}));
        assertThat(extra.getContent()).isEqualTo(request.getContent());
    }
}