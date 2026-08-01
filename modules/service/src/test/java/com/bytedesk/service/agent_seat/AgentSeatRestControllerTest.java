package com.bytedesk.service.agent_seat;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

class AgentSeatRestControllerTest {

    @Test
    void queryByAssignedAgentUidRequiresGenericReadPermission() throws NoSuchMethodException {
        Method method = AgentSeatRestController.class.getDeclaredMethod(
                "queryByAssignedAgentUid",
                AgentSeatRequest.class);

        GetMapping mapping = method.getAnnotation(GetMapping.class);
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

        assertThat(mapping).isNotNull();
        assertThat(mapping.value()).containsExactly("/query/assigned/agent");
        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo(AgentSeatPermissions.HAS_AGENT_SEAT_READ);
    }
}