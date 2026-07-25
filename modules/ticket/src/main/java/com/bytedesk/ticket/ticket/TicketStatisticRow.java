package com.bytedesk.ticket.ticket;

import java.time.ZonedDateTime;

/**
 * Lightweight projection for ticket statistics batch aggregation.
 */
public interface TicketStatisticRow {

    String getUid();

    String getOrgUid();

    String getTicketSettingsUid();

    String getType();

    String getStatus();

    String getPriority();

    ZonedDateTime getCreatedAt();

    ZonedDateTime getResolvedTime();

    String getThreadUid();

    String getCategoryUid();

    String getAssignee();

    Boolean getVerified();
}