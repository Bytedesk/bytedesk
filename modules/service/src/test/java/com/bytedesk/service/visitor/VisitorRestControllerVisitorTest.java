package com.bytedesk.service.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageRequest;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message_unread.MessageUnreadRestService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.auto_reply.AutoReplyService;
import com.bytedesk.kbase.taboo.TabooService;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.core.ip.IpService;

class VisitorRestControllerVisitorTest {

    private MessageRestService messageRestService;
    private VisitorRestControllerVisitor controller;

    @BeforeEach
    void setUp() {
        messageRestService = mock(MessageRestService.class);
        controller = new VisitorRestControllerVisitor(
                mock(VisitorRestService.class),
                mock(MessageUnreadRestService.class),
                mock(IMessageSendService.class),
                messageRestService,
                mock(ThreadRestService.class),
                mock(IpService.class),
                mock(RobotService.class),
                mock(AgentRestService.class),
                mock(TabooService.class),
                mock(AutoReplyService.class),
                mock(ExecutorService.class));
    }

    @Test
    void queryByThreadTopicShouldRejectMissingOrgUid() {
        MessageRequest request = MessageRequest.builder()
                .topic("org/visitor/topic")
                .build();

        ResponseEntity<?> response = controller.queryByThreadTopic(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(500);
        assertThat(body.getMessage()).isEqualTo("orgUid required");
        verifyNoInteractions(messageRestService);
    }

    @Test
    void queryByThreadUidShouldRejectMissingOrgUid() {
        MessageRequest request = MessageRequest.builder()
                .threadUid("thread-1")
                .build();

        ResponseEntity<?> response = controller.queryByThreadUid(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(500);
        assertThat(body.getMessage()).isEqualTo("orgUid required");
        verifyNoInteractions(messageRestService);
    }
}