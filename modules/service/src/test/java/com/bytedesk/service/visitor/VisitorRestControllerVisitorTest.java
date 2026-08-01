package com.bytedesk.service.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentPublicResponse;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.core.ip.IpService;

class VisitorRestControllerVisitorTest {

    private MessageRestService messageRestService;
    private VisitorRestService visitorRestService;
    private AgentRestService agentRestService;
    private VisitorRestControllerVisitor controller;

    @BeforeEach
    void setUp() {
        messageRestService = mock(MessageRestService.class);
        visitorRestService = mock(VisitorRestService.class);
        agentRestService = mock(AgentRestService.class);
        controller = new VisitorRestControllerVisitor(
                visitorRestService,
                mock(MessageUnreadRestService.class),
                mock(IMessageSendService.class),
                messageRestService,
                mock(ThreadRestService.class),
                mock(IpService.class),
                mock(RobotService.class),
                agentRestService,
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

    @Test
    void queryVisitorByUidShouldReturnVisitorResponse() {
        VisitorEntity entity = VisitorEntity.builder()
            .visitorUid("external-visitor-1")
            .nickname("Visitor")
            .email("visitor@test.com")
            .build();
        entity.setUid("visitor-1");
        entity.setOrgUid("org-1");
        VisitorResponse visitor = VisitorResponse.builder()
                .uid("visitor-1")
                .visitorUid("external-visitor-1")
                .nickname("Visitor")
                .email("visitor@test.com")
                .build();
        when(visitorRestService.findByVisitorUidAndOrgUid("external-visitor-1", "org-1")).thenReturn(Optional.of(entity));
        when(visitorRestService.convertToResponse(entity)).thenReturn(visitor);

        ResponseEntity<?> response = controller.queryVisitorByUid("external-visitor-1", "org-1");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(200);
        assertThat(body.getData()).isEqualTo(visitor);
        verify(visitorRestService).findByVisitorUidAndOrgUid("external-visitor-1", "org-1");
        }

        @Test
        void queryVisitorByUidShouldFallbackToUidWhenVisitorUidMissing() {
            VisitorEntity entity = VisitorEntity.builder()
            .visitorUid("external-visitor-1")
            .nickname("Visitor")
            .build();
            entity.setUid("visitor-1");
            entity.setOrgUid("org-1");
        VisitorResponse visitor = VisitorResponse.builder()
            .uid("visitor-1")
            .visitorUid("external-visitor-1")
            .nickname("Visitor")
            .build();
        when(visitorRestService.findByVisitorUidAndOrgUid("visitor-1", "org-1")).thenReturn(Optional.empty());
        when(visitorRestService.findByUid("visitor-1")).thenReturn(Optional.of(entity));
        when(visitorRestService.convertToResponse(entity)).thenReturn(visitor);

        ResponseEntity<?> response = controller.queryVisitorByUid("visitor-1", "org-1");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(200);
        assertThat(body.getData()).isEqualTo(visitor);
        verify(visitorRestService).findByVisitorUidAndOrgUid("visitor-1", "org-1");
        verify(visitorRestService).findByUid("visitor-1");
    }

    @Test
        void queryAgentByUidShouldReturnPublicAgentResponse() {
        AgentEntity agent = AgentEntity.builder()
                .uid("agent-1")
                .userUid("user-1")
                .nickname("Agent")
                .agentNo("A001")
                .avatar("/avatars/agent.png")
                .status("AVAILABLE")
            .description("Public profile")
                .build();
        when(agentRestService.findByUid("agent-1")).thenReturn(Optional.of(agent));

        ResponseEntity<?> response = controller.queryAgentByUid("agent-1", "org-1");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(200);
        assertThat(body.getData()).isInstanceOf(AgentPublicResponse.class);

        AgentPublicResponse agentResponse = (AgentPublicResponse) body.getData();
        assertThat(agentResponse.uid()).isEqualTo("agent-1");
        assertThat(agentResponse.userUid()).isEqualTo("user-1");
        assertThat(agentResponse.nickname()).isEqualTo("Agent");
        assertThat(agentResponse.agentNo()).isEqualTo("A001");
        assertThat(agentResponse.avatar()).isEqualTo("/avatars/agent.png");
        assertThat(agentResponse.status()).isEqualTo("AVAILABLE");
        assertThat(agentResponse.description()).isEqualTo("Public profile");
    }
}