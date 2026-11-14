package com.bytedesk.remote.session;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.bytedesk.remote.session.DesktopActionRequest.Action;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DesktopSessionControllerTest {
    @Test
    void guestRoutesRequireScopedHeaderAndNeverExposeHostSecretsInSnapshots() throws Exception {
        var service = mock(DesktopSessionService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new DesktopHostController(service, mock(DesktopIceService.class))).build();
        var snapshot = new DesktopSessionResponse("session", "thread", "room", "Agent",
                DesktopSessionStatusEnum.CONNECTING, false, false, 1, 100, 0, 0, List.of());
        when(service.heartbeat("session", "scoped-token", 0)).thenReturn(snapshot);
        mvc.perform(post("/visitor/api/v1/remote/sessions/session/heartbeat")
                .contentType("application/json").content("{\"inputCount\":0}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/visitor/api/v1/remote/sessions/session/heartbeat")
                .header("X-Remote-Token", "scoped-token").contentType("application/json").content("{\"inputCount\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.uid").value("session"))
                .andExpect(jsonPath("$.data.hostToken").doesNotExist())
                .andExpect(jsonPath("$.data.hostTokenHash").doesNotExist());
        mvc.perform(post("/visitor/api/v1/remote/sessions/session/heartbeat")
                .header("X-Remote-Token", "scoped-token").contentType("application/json").content("{\"inputCount\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void viewerCannotRouteAHostApprovalThroughTheViewerEndpoint() throws Exception {
        var service = mock(DesktopSessionService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new DesktopSessionController(service, mock(DesktopIceService.class))).build();
        when(service.action("session", null, Action.GRANT_CONTROL))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(post("/api/v1/remote/sessions/session/action").contentType("application/json")
                .content("{\"action\":\"GRANT_CONTROL\"}")).andExpect(status().isForbidden());
    }
}
