package com.bytedesk.core.message;

import java.time.ZonedDateTime;

/**
 * Lightweight projection for ticket/message statistic aggregation.
 */
public interface MessageStatisticRow {

    String getThreadUid();

    ZonedDateTime getCreatedAt();

    ZonedDateTime getAgentRepliedAt();

    String getType();

    String getUser();
}