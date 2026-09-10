package com.bytedesk.ticket.ticket.assignment;

import java.util.EnumSet;
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

    public static final TicketAssignmentModeEnum DEFAULT = ROUND_ROBIN;

    private static final EnumSet<TicketAssignmentModeEnum> TEMPORARILY_DISABLED_RUNTIME_MODES = EnumSet.of(
            LEAST_ACTIVE,
            RANDOM,
            CONSISTENT_HASH,
            RECENT);

    public static TicketAssignmentModeEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT;
        }
        try {
            return TicketAssignmentModeEnum.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return DEFAULT;
        }
    }

    public static String normalize(String value) {
        return fromValue(value).name();
    }

    public static TicketAssignmentModeEnum resolveRuntimeMode(String value) {
        TicketAssignmentModeEnum mode = fromValue(value);
        if (TEMPORARILY_DISABLED_RUNTIME_MODES.contains(mode)) {
            return DEFAULT;
        }
        return mode;
    }

    public static String normalizeRuntime(String value) {
        return resolveRuntimeMode(value).name();
    }
}
