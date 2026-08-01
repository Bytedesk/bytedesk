package com.bytedesk.service.worktime_settings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.service.holiday.HolidayEntity;
import com.bytedesk.service.holiday.HolidayRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Unified holiday resolution for {@link WorktimeService}.
 *
 * <p>当前工作时间主链统一按 {@code ORG_ONLY} 解释；平台级与覆盖合并能力仅保留给节假日管理查询。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;

    /**
     * Check whether the given date is a holiday under the worktime settings' scope.
     */
    public boolean isHoliday(WorktimeSettingEntity settings, LocalDate date) {
        if (settings == null || date == null) {
            return false;
        }
        // 暂时不启用 holidayCountryCode/holidayScopeType 字段，统一使用默认值
        String scope = WorktimeHolidayScopeEnum.ORG_ONLY.name();
        // String scope = settings.getHolidayScopeType();
        // if (!StringUtils.hasText(scope)) {
        //     scope = WorktimeHolidayScopeEnum.ORG_ONLY.name();
        // }
        // String countryCode = StringUtils.hasText(settings.getHolidayCountryCode())
        //         ? settings.getHolidayCountryCode()
        //         : "CN";
        String countryCode = "CN";

        List<HolidayEntity> effective = listEffective(settings.getOrgUid(), countryCode,
                date.getYear(), scope);
        return effective.stream().anyMatch(h -> date.equals(h.getHolidayDate())
                && Boolean.TRUE.equals(h.getOffDay()));
    }

    /**
     * List effective holidays after applying scope and org-override rules.
     */
    public List<HolidayEntity> listEffectiveHolidays(String orgUid, String countryCode,
            Integer year, String scopeType) {
        if (!StringUtils.hasText(scopeType)) {
            scopeType = WorktimeHolidayScopeEnum.ORG_ONLY.name();
        }
        if (!StringUtils.hasText(countryCode)) {
            countryCode = "CN";
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return listEffective(orgUid, countryCode, year, scopeType);
    }

    @Cacheable(value = "effective_holidays",
            key = "#orgUid + '_' + #countryCode + '_' + #year + '_' + #scopeType",
            unless = "#result.isEmpty()")
    List<HolidayEntity> listEffective(String orgUid, String countryCode, int year,
            String scopeType) {
        if (WorktimeHolidayScopeEnum.PLATFORM_ONLY.name().equals(scopeType)) {
            return holidayRepository
                    .findByCountryCodeAndHolidayYearAndDeletedFalseOrderByHolidayDateAsc(countryCode, year)
                    .stream()
                    .filter(h -> !StringUtils.hasText(h.getOrgUid()))
                    .toList();
        }
        if (WorktimeHolidayScopeEnum.ORG_ONLY.name().equals(scopeType)) {
            return StringUtils.hasText(orgUid)
                    ? holidayRepository
                            .findByCountryCodeAndHolidayYearAndDeletedFalseOrderByHolidayDateAsc(countryCode, year)
                            .stream()
                            .filter(h -> orgUid.equals(h.getOrgUid()))
                            .toList()
                    : Collections.emptyList();
        }
        // Fallback for explicit merged-scope queries.
        return resolveWithOrgOverride(orgUid, countryCode, year);
    }

    private List<HolidayEntity> resolveWithOrgOverride(String orgUid, String countryCode, int year) {
        List<HolidayEntity> all = holidayRepository
                .findByCountryCodeAndHolidayYearAndDeletedFalseOrderByHolidayDateAsc(countryCode, year);

        Map<LocalDate, HolidayEntity> platform = new LinkedHashMap<>();
        Map<LocalDate, HolidayEntity> org = new LinkedHashMap<>();

        for (var h : all) {
            if (h.getHolidayDate() == null) {
                continue;
            }
            if (!StringUtils.hasText(h.getOrgUid())) {
                platform.putIfAbsent(h.getHolidayDate(), h);
            } else if (StringUtils.hasText(orgUid) && orgUid.equals(h.getOrgUid())) {
                org.put(h.getHolidayDate(), h);
            }
        }

        Map<LocalDate, HolidayEntity> merged = new LinkedHashMap<>(platform);
        merged.putAll(org); // org-level overrides platform for the same date
        return new ArrayList<>(merged.values());
    }

    /**
     * Evict cached holiday lists for every scope variant of the given country/year.
     */
    @CacheEvict(value = "effective_holidays", allEntries = true)
    @Transactional
    public void evictCache(String countryCode, int year) {
        log.debug("Evicted holiday cache for country={} year={}", countryCode, year);
    }
}
