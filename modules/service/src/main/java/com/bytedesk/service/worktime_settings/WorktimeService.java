package com.bytedesk.service.worktime_settings;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Unified worktime evaluation service.
 *
 * <p>All channels (Agent, Workgroup, Call Center) use this single service to
 * determine whether the current time is within service hours. The service does
 * NOT decide channel-level actions (robot redirect, leave-message, queue);
 * each caller decides based on the returned {@link WorktimeEvaluation}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorktimeService {

    private final HolidayService holidayService;

    /**
     * Check the service-time status now.
     */
    public boolean isInServiceTime(@Nullable WorktimeSettingEntity settings) {
        return evaluate(settings, ZonedDateTime.now()).inServiceTime();
    }

    /**
     * Check the service-time status for a specific instant.
     */
    public boolean isInServiceTime(@Nullable WorktimeSettingEntity settings, ZonedDateTime dateTime) {
        return evaluate(settings, dateTime).inServiceTime();
    }

    /**
     * Full evaluation with reason and effective timezone.
     */
    public WorktimeEvaluation evaluate(@Nullable WorktimeSettingEntity settings, ZonedDateTime dateTime) {
        if (settings == null || Boolean.FALSE.equals(settings.getEnabled())) {
            return WorktimeEvaluation.inService(dateTime, getNonWorktimeTip(settings));
        }

        ZonedDateTime localDateTime = resolveDateTime(settings, dateTime);

        boolean holiday = holidayService.isHoliday(settings, localDateTime.toLocalDate());
        boolean effectiveHoliday = Boolean.TRUE.equals(settings.getHolidaySettingsEnabled()) && holiday;
        boolean inSlot = settings.isInWorktime(localDateTime.toLocalDate(),
                localDateTime.toLocalTime(), effectiveHoliday);

        String tip = getNonWorktimeTip(settings);

        if (!inSlot) {
            WorktimeClosedReason reason = effectiveHoliday
                    ? WorktimeClosedReason.OUTSIDE_HOLIDAY_SLOT
                    : WorktimeClosedReason.OUTSIDE_REGULAR_SLOT;
            return WorktimeEvaluation.outOfService(localDateTime, reason, tip);
        }
        return WorktimeEvaluation.inService(localDateTime, tip);
    }

    /**
     * Get the non-worktime tip with default fallback.
     */
    public String getNonWorktimeTip(@Nullable WorktimeSettingEntity settings) {
        return getTip(settings);
    }

    private String getTip(@Nullable WorktimeSettingEntity settings) {
        if (settings != null && StringUtils.hasText(settings.getNonWorktimeTip())) {
            return settings.getNonWorktimeTip();
        }
        return I18Consts.I18N_DEFAULT_OFFLINE_MESSAGE;
    }

    private ZonedDateTime resolveDateTime(WorktimeSettingEntity settings, ZonedDateTime dateTime) {
        // 暂时不启用 timezone 字段，统一使用 Asia/Shanghai
        String tz = "Asia/Shanghai";
        // String tz = settings.getTimezone();
        // if (!StringUtils.hasText(tz)) {
        //     tz = "Asia/Shanghai";
        // }
        try {
            return dateTime.withZoneSameInstant(ZoneId.of(tz));
        } catch (Exception e) {
            log.warn("Invalid timezone '{}' for worktime setting uid={}, falling back to Asia/Shanghai",
                    tz, settings.getUid());
            return dateTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
        }
    }
}
