/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-09
 * @Description: Populate tel_code (telephone area code) from provinces.json after city SQL import
 */
package com.bytedesk.core.city;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.city.import", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CityTelCodeInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    private static final String PROVINCES_JSON = "classpath:sql/city/provinces.json";

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        populateTelCodes();
    }

    public void populateTelCodes() {
        try {
            Resource resource = resourceLoader.getResource(PROVINCES_JSON);
            if (!resource.exists()) {
                log.info("telCode init skipped: provinces.json not found at {}", PROVINCES_JSON);
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<ProvinceEntry> entries;
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                entries = mapper.readValue(reader, new TypeReference<List<ProvinceEntry>>() {});
            }

            if (entries == null || entries.isEmpty()) {
                log.info("telCode init skipped: provinces.json is empty");
                return;
            }

            // Check if tel_code is already populated
            Long populated = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM bytedesk_core_city WHERE tel_code IS NOT NULL AND by_type = 'city'",
                    Long.class);
            if (populated != null && populated > 0) {
                log.info("telCode init skipped: {} city rows already have tel_code", populated);
                return;
            }

            int updated = 0;
            int skipped = 0;
            for (ProvinceEntry entry : entries) {
                if (entry.getCode() == null || entry.getCity() == null) {
                    continue;
                }
                // Match by city name at city level
                int rows = jdbcTemplate.update(
                        "UPDATE bytedesk_core_city SET tel_code = ? WHERE name = ? AND by_type = 'city'",
                        entry.getCode(), entry.getCity());
                if (rows > 0) {
                    updated += rows;
                } else {
                    skipped++;
                    log.debug("telCode init: no city-level match for name='{}'", entry.getCity());
                }
            }

            // Also match the 4 direct municipalities (北京/上海/天津/重庆) which are
            // province level but have tel codes
            List<ProvinceEntry> directCities = new ArrayList<>();
            for (ProvinceEntry entry : entries) {
                if (entry.getProvince() == null) {
                    directCities.add(entry);
                }
            }
            for (ProvinceEntry entry : directCities) {
                jdbcTemplate.update(
                        "UPDATE bytedesk_core_city SET tel_code = ? WHERE name = ? AND by_type = 'province'",
                        entry.getCode(), entry.getCity());
            }

            log.info("telCode init finished: updated={} city rows, skipped={} unmatched entries",
                    updated, skipped);

        } catch (Exception e) {
            log.warn("telCode init failed (non-fatal): {}", e.getMessage());
        }
    }

    @lombok.Data
    private static class ProvinceEntry {
        private String code;
        private String city;
        private String pinyin;
        private String province;
    }
}
