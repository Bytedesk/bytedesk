package com.bytedesk.ticket.ticket_settings_basic;

import java.util.Locale;

/**
 * 统一的工单分配方式，覆盖自动、手动、抢单及智能等模式。
 */
public enum TicketAssignmentModeEnum {
    ROUND_ROBIN,
    LEAST_ACTIVE,
    RANDOM,
    WEIGHTED_RANDOM,
    CONSISTENT_HASH,
    FASTEST_RESPONSE,
    BROADCAST,
    RECENT,
    LLM,
    MANUAL;

    public static TicketAssignmentModeEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return ROUND_ROBIN;
        }
        try {
            return TicketAssignmentModeEnum.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return ROUND_ROBIN;
        }
    }

    public static String normalize(String value) {
        return fromValue(value).name();
    }
}
