package com.bytedesk.service.agent_status;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class AgentStatusRestServiceTest {

    @Mock
    private AgentStatusRepository agentStatusRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    private AgentStatusRestService agentStatusRestService;

    @BeforeEach
    void setUp() {
        agentStatusRestService = new AgentStatusRestService(
                agentStatusRepository,
                modelMapper,
                uidUtils);
    }

    @Test
    void convertToExcelShouldMapAgentStatusFields() {
        ZonedDateTime createdAt = ZonedDateTime.parse("2026-03-27T10:15:30+08:00");
        UserProtobuf agent = UserProtobuf.builder()
                .uid("agent-uid")
                .nickname("Alice")
                .build();

        AgentStatusEntity entity = AgentStatusEntity.builder()
                .uid("status-uid")
                .status("REST")
                .restReason("午休")
                .agent(agent.toJson())
                .durationSeconds(3661L)
                .build();
        entity.setCreatedAt(createdAt);

        AgentStatusExcel excel = agentStatusRestService.convertToExcel(entity);

        assertThat(excel.getNickname()).isEqualTo("Alice");
        assertThat(excel.getAgentUid()).isEqualTo("agent-uid");
        assertThat(excel.getStatus()).isEqualTo("小休中");
        assertThat(excel.getRestReason()).isEqualTo("午休");
        assertThat(excel.getDuration()).isEqualTo("1小时1分钟1秒");
        assertThat(excel.getCreatedAt()).isEqualTo(createdAt);
    }
}