package com.bytedesk.kbase.settings_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class ServiceSettingsResponseTest {

    @Test
    void shouldIncludeToolbarWhenBuildingResponseFromEntity() {
        ServiceSettingsEntity entity = ServiceSettingsEntity.builder()
                .toolbar(ToolbarSettings.builder()
                        .goods(false)
                        .orderSelector(false)
                        .ticket(false)
                        .order(List.of("smile", "goods", "ticket"))
                        .build())
                .build();

        ServiceSettingsResponse response = ServiceSettingsResponse.fromEntity(entity);

        assertNotNull(response);
        assertNotNull(response.getToolbar());
        assertEquals(Boolean.FALSE, response.getToolbar().getGoods());
        assertEquals(Boolean.FALSE, response.getToolbar().getOrderSelector());
        assertEquals(Boolean.FALSE, response.getToolbar().getTicket());
        assertEquals(List.of("smile", "goods", "ticket"), response.getToolbar().getOrder());
    }
}