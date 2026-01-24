/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-24
 * @Description: Trigger city SQL import after app is ready (async, non-blocking)
 */
package com.bytedesk.core.city;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.city.import", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CitySqlImportRunner {

    private final CitySqlImportService citySqlImportService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("city import scheduled in background");
        citySqlImportService.importInBackgroundIfNeeded();
    }
}
