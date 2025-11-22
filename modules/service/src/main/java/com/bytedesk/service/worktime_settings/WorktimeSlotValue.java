package com.bytedesk.service.worktime_settings;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight value object representing a single worktime window.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorktimeSlotValue implements Serializable {

    private static final long serialVersionUID = 1L;

    private String startTime;

    private String endTime;

    /**
     * Comma separated day-of-week values (1-7) where 1 represents Monday.
     */
    @Builder.Default
    private String workDays = "1,2,3,4,5";

    /**
     * Check whether the supplied date/time falls inside this slot.
     */
    @JsonIgnore
    public boolean isActive(LocalDate date, LocalTime time) {
        if (!StringUtils.hasText(startTime) || !StringUtils.hasText(endTime)) {
            return false;
        }
        if (!matchesDay(date)) {
            return false;
        }
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            if (!end.isBefore(start)) {
                return !time.isBefore(start) && !time.isAfter(end);
            }
            return !time.isBefore(start) || !time.isAfter(end);
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean matchesDay(LocalDate date) {
        if (!StringUtils.hasText(workDays)) {
            return true;
        }
        final int dayValue = date.getDayOfWeek().getValue();
        return Arrays.stream(workDays.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .anyMatch(token -> token.equals(String.valueOf(dayValue)));
    }
}
