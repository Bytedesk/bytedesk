package com.bytedesk.service.worktime_settings;

/**
 * Why the worktime evaluation returned non-service-time.
 */
public enum WorktimeClosedReason {
    NONE,
    OUTSIDE_REGULAR_SLOT,
    OUTSIDE_HOLIDAY_SLOT
}
