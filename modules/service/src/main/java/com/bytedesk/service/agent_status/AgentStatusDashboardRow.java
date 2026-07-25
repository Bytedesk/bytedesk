package com.bytedesk.service.agent_status;

import java.time.ZonedDateTime;

/**
 * Lightweight projection for call dashboard agent-status aggregation.
 */
public interface AgentStatusDashboardRow {

    String getStatus();

    Long getDurationSeconds();

    ZonedDateTime getCreatedAt();
}