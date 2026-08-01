package com.bytedesk.service.worktime_settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorktimeServiceTest {

    @Mock
    private HolidayService holidayService;

    @Test
    void shouldIgnoreHolidaySlotsWhenHolidaySettingsDisabled() {
        WorktimeService service = new WorktimeService(holidayService);
        WorktimeSettingEntity settings = WorktimeSettingEntity.builder()
                .enabled(true)
                .holidaySettingsEnabled(false)
                .regularWorktimes(List.of(slot("09:00", "18:00", "1,2,3,4,5,6,7")))
                .specialWorktimes(List.of())
                .build();

        ZonedDateTime dateTime = ZonedDateTime.of(2026, 10, 1, 10, 0, 0, 0, ZoneId.of("Asia/Shanghai"));
        when(holidayService.isHoliday(any(), eq(dateTime.toLocalDate()))).thenReturn(true);

        WorktimeEvaluation evaluation = service.evaluate(settings, dateTime);

        assertTrue(evaluation.inServiceTime());
        assertEquals(WorktimeClosedReason.NONE, evaluation.closedReason());
    }

    @Test
    void shouldUseHolidaySlotsWhenHolidaySettingsEnabled() {
        WorktimeService service = new WorktimeService(holidayService);
        WorktimeSettingEntity settings = WorktimeSettingEntity.builder()
                .enabled(true)
                .holidaySettingsEnabled(true)
                .regularWorktimes(List.of(slot("09:00", "18:00", "1,2,3,4,5,6,7")))
                .specialWorktimes(List.of())
                .build();

        ZonedDateTime dateTime = ZonedDateTime.of(2026, 10, 1, 10, 0, 0, 0, ZoneId.of("Asia/Shanghai"));
        when(holidayService.isHoliday(any(), eq(dateTime.toLocalDate()))).thenReturn(true);

        WorktimeEvaluation evaluation = service.evaluate(settings, dateTime);

        assertFalse(evaluation.inServiceTime());
        assertEquals(WorktimeClosedReason.OUTSIDE_HOLIDAY_SLOT, evaluation.closedReason());
    }

    private WorktimeSlotValue slot(String startTime, String endTime, String workDays) {
        return WorktimeSlotValue.builder()
                .startTime(startTime)
                .endTime(endTime)
                .workDays(workDays)
                .build();
    }
}