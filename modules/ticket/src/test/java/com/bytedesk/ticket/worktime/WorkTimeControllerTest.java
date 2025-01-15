package com.bytedesk.ticket.worktime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.bytedesk.ticket.agi.worktime.WorkTimeConfig;
import com.bytedesk.ticket.agi.worktime.WorkTimeController;
import com.bytedesk.ticket.agi.worktime.WorkTimeService;
import com.bytedesk.ticket.agi.worktime.dto.WorkTimeConfigRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkTimeController.class)
public class WorkTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkTimeService workTimeService;

    @Autowired
    private ObjectMapper objectMapper;

    private WorkTimeConfigRequest validRequest;
    private final Long CONFIG_ID = 1L;

    @BeforeEach
    void setUp() {
        validRequest = new WorkTimeConfigRequest();
        validRequest.setTimezone("Asia/Shanghai");
        validRequest.setWorkDays("1,2,3,4,5");
        validRequest.setWorkHours("0900-1800");
        validRequest.setLunchBreak("1200-1300");
        validRequest.setHolidays(Arrays.asList("2024-01-01", "2024-02-10"));
        validRequest.setSpecialWorkdays(Arrays.asList("2024-02-04"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createConfig_ValidRequest_ShouldReturn200() throws Exception {
        when(workTimeService.createConfig(any())).thenReturn(new WorkTimeConfig());

        mockMvc.perform(post("/api/v1/worktime/configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createConfig_NonAdminUser_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/v1/worktime/configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getConfig_ExistingConfig_ShouldReturn200() throws Exception {
        when(workTimeService.getConfig(CONFIG_ID)).thenReturn(new WorkTimeConfig());

        mockMvc.perform(get("/api/v1/worktime/configs/{configId}", CONFIG_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void isWorkingHours_ValidRequest_ShouldReturn200() throws Exception {
        when(workTimeService.isWorkingHours(eq(CONFIG_ID), any(LocalDateTime.class)))
                .thenReturn(true);

        mockMvc.perform(get("/api/v1/worktime/configs/{configId}/working-hours", CONFIG_ID)
                .param("dateTime", "2024-02-05T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateWorkDays_ValidRequest_ShouldReturn200() throws Exception {
        mockMvc.perform(put("/api/v1/worktime/configs/{configId}/work-days", CONFIG_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1, 2, 3, 4, 5))))
                .andExpect(status().isOk());

        verify(workTimeService).updateWorkDays(eq(CONFIG_ID), anyList());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addHolidays_ValidRequest_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/worktime/configs/{configId}/holidays", CONFIG_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList("2024-01-01"))))
                .andExpect(status().isOk());

        verify(workTimeService).addHolidays(eq(CONFIG_ID), anyList());
    }
} 