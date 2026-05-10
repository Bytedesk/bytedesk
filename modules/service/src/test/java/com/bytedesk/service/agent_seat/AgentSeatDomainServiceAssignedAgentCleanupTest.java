package com.bytedesk.service.agent_seat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRequest;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRepository;

@ExtendWith(MockitoExtension.class)
class AgentSeatDomainServiceAssignedAgentCleanupTest {

    @Mock
    private AgentSeatRepository agentSeatRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private MemberRestService memberRestService;

    private AgentSeatDomainService agentSeatDomainService;

    @BeforeEach
    void setUp() {
        agentSeatDomainService = new AgentSeatDomainService(agentSeatRepository, agentRepository, memberRestService);
        ReflectionTestUtils.setField(agentSeatDomainService, "agentSeatEnabled", true);
    }

    @Test
    void refreshSeatStateShouldRemoveMemberViaAssignedAgentWhenSeatExpires() {
        MemberEntity member = MemberEntity.builder()
                .uid("member-1")
                .orgUid("org-1")
                .build();
        AgentEntity agent = AgentEntity.builder()
                .uid("agent-1")
                .member(member)
                .enabled(true)
                .forceLogout(false)
                .build();
        AgentSeatEntity seat = AgentSeatEntity.builder()
                .uid("seat-1")
                .orgUid("org-1")
                .seatNo("seat-1")
                .status(AgentSeatStatusEnum.OCCUPIED.name())
                .assignedAgentUid("agent-1")
                .expireAt(ZonedDateTime.now().minusMinutes(5))
                .build();

        when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1")).thenReturn(List.of(seat));
        when(agentRepository.findByUid("agent-1")).thenReturn(Optional.of(agent));
    when(memberRestService.findByUid("member-1")).thenReturn(Optional.of(member));
        when(agentRepository.save(any(AgentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        agentSeatDomainService.refreshSeatState("org-1");

        verify(memberRestService).removeUserFromOrg(any(MemberRequest.class));
    }
}