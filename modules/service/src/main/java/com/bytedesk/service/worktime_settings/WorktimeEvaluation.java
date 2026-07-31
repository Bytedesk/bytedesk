package com.bytedesk.service.worktime_settings;

import java.time.ZonedDateTime;

/**
 * Structured worktime evaluation result returned by {@link WorktimeService#evaluate}.
 *
 * <p>Callers decide their own channel-level actions (route-to-robot, leave-message, skip-route)
 * instead of having the service module dictate call-center domain logic.</p>
 */
public record WorktimeEvaluation(
        boolean inServiceTime,
        WorktimeClosedReason closedReason,
        ZonedDateTime effectiveDateTime,
        String nonWorktimeTip) {

    public static WorktimeEvaluation inService(ZonedDateTime dateTime, String tip) {
        return new WorktimeEvaluation(true, WorktimeClosedReason.NONE, dateTime, tip);
    }

    public static WorktimeEvaluation outOfService(ZonedDateTime dateTime, WorktimeClosedReason reason, String tip) {
        return new WorktimeEvaluation(false, reason, dateTime, tip);
    }
}
