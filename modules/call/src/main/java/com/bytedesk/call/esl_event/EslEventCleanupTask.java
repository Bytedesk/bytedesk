package com.bytedesk.call.esl_event;

import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EslEventCleanupTask {

    private final EslEventRepository eslEventRepository;
    private final EslEventIngestProperties ingestProperties;

    @Transactional
    @Scheduled(cron = "${bytedesk.call.freeswitch.esl-event.cleanup-cron:0 30 3 * * ?}")
    public void cleanupHistory() {
        int retentionDays = ingestProperties.getRetentionDays();
        if (retentionDays <= 0) {
            return;
        }

        ZonedDateTime cutoffTime = ZonedDateTime.now().minusDays(retentionDays);
        long deleted = eslEventRepository.deleteByCreatedAtBefore(cutoffTime);
        if (deleted > 0) {
            log.info("ESL事件历史清理完成: retentionDays={} deleted={}", retentionDays, deleted);
        }
    }
}
