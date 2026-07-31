package com.bytedesk.ticket.ticket;

import java.time.ZonedDateTime;

import org.springframework.lang.Nullable;

/**
 * Lightweight projection for ticket statistics batch aggregation.
 */
public interface TicketStatisticRow {

    @Nullable
    String getUid();

    @Nullable
    String getOrgUid();

    @Nullable
    String getTicketSettingsUid();

    @Nullable
    String getType();

    @Nullable
    String getStatus();

    @Nullable
    String getPriority();

    @Nullable
    ZonedDateTime getCreatedAt();

    @Nullable
    ZonedDateTime getResolvedTime();

    @Nullable
    String getThreadUid();

    @Nullable
    String getCategoryUid();

    @Nullable
    String getAssignee();

    @Nullable
    Boolean getVerified();
}