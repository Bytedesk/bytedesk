package com.bytedesk.service.worktime_settings;

/**
 * Holiday scope for {@link WorktimeSettingEntity}.
 *
 * <ul>
 *   <li>{@code PLATFORM_ONLY} – only platform-level holidays ({@code HolidayEntity.orgUid} is null)</li>
 *   <li>{@code ORG_ONLY} – only this organization's custom holidays</li>
 *   <li>{@code ORG_AND_PLATFORM} – platform holidays with same-day org overrides</li>
 * </ul>
 */
public enum WorktimeHolidayScopeEnum {
    PLATFORM_ONLY,
    ORG_ONLY,
    ORG_AND_PLATFORM
}
