package com.bytedesk.core.message;

import java.time.ZonedDateTime;

import org.jspecify.annotations.Nullable;

/**
 * Lightweight projection for ticket/message statistic aggregation.
 */
public interface MessageStatisticRow {

    @Nullable
    String getThreadUid();

    @Nullable
    ZonedDateTime getCreatedAt();

    @Nullable
    ZonedDateTime getAgentRepliedAt();

    @Nullable
    String getType();

    @Nullable
    String getUser();
}