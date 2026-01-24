/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-24
 * @Description: Async city SQL importer (non-blocking startup)
 */
package com.bytedesk.core.city;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bytedesk.core.config.DatabaseTypeChecker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitySqlImportService {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;
    private final DatabaseTypeChecker databaseTypeChecker;

    @Value("${bytedesk.city.import.enabled:false}")
    private boolean enabled;

    @Value("${bytedesk.city.import.force:false}")
    private boolean force;

    @Value("${bytedesk.city.import.delay-ms:10000}")
    private long delayMs;

    @Value("${bytedesk.city.import.lock-name:bytedesk:city:import}")
    private String lockName;

    @Value("${bytedesk.city.import.lock-timeout-seconds:0}")
    private int lockTimeoutSeconds;

    @Value("${bytedesk.city.import.staging-script:classpath:sql/city/bytedesk_core_city.sql}")
    private String stagingScript;

    @Value("${bytedesk.city.import.transform-script:}")
    private String transformScript;

    @Value("${bytedesk.city.import.fallback-staging-file:${user.dir}/city.sql}")
    private String fallbackStagingFile;

    @Value("${bytedesk.city.import.drop-staging-after:true}")
    private boolean dropStagingAfter;

    @Async("applicationTaskExecutor")
    public void importInBackgroundIfNeeded() {
        doImport(true, this.force, this.delayMs);
    }

    /**
     * Manual trigger (e.g. admin click). Bypasses the 'enabled' gate and runs immediately.
     */
    @Async("applicationTaskExecutor")
    public void importNowAsync(boolean force) {
        doImport(false, force, 0L);
    }

    private void doImport(boolean requireEnabled, boolean forceImport, long delayMsToUse) {
        if (requireEnabled && !enabled) {
            return;
        }

        String databaseType = databaseTypeChecker.getDatabaseType();
        if (!"MySQL".equalsIgnoreCase(databaseType)) {
            log.info("city import skipped: database type is {} (city.sql is MySQL-only)", databaseType);
            return;
        }

        if (delayMsToUse > 0) {
            try {
                Thread.sleep(delayMsToUse);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        try {
            if (!forceImport && isTargetHasData()) {
                log.info("city import skipped: bytedesk_core_city already has data");
                return;
            }

            Integer locked = jdbcTemplate.queryForObject(
                    "SELECT GET_LOCK(?, ?)", Integer.class, lockName, lockTimeoutSeconds);
            if (locked == null || locked != 1) {
                log.info("city import skipped: lock not acquired: {}", lockName);
                return;
            }

            long startNs = System.nanoTime();
            try {
                Resource staging = resolveStagingResource();
                if (staging == null) {
                    log.warn(
                            "city import aborted: staging script not found. Set bytedesk.city.import.staging-script or provide fallback file {}",
                            fallbackStagingFile);
                    return;
                }

                Resource transform = null;
                if (transformScript != null && !transformScript.isBlank()) {
                    transform = resolveResource(transformScript);
                    if (transform == null) {
                        log.warn("city import: transform script not found, will continue with staging only: {}",
                                transformScript);
                    }
                }

                log.info("city import start: staging={}, transform={}, force={}",
                        staging.getDescription(),
                        transform == null ? "<embedded-in-staging-or-disabled>" : transform.getDescription(),
                        forceImport);

                executeSqlScript(staging);
                if (transform != null) {
                    executeSqlScript(transform);
                }

                if (dropStagingAfter) {
                    try {
                        jdbcTemplate.execute("DROP TABLE IF EXISTS liangshibao_city");
                    } catch (Exception ex) {
                        log.warn("city import: failed to drop staging table liangshibao_city: {}", ex.getMessage());
                    }
                }

                long costMs = (System.nanoTime() - startNs) / 1_000_000L;
                log.info("city import finished in {} ms", costMs);
            } finally {
                try {
                    jdbcTemplate.queryForObject("SELECT RELEASE_LOCK(?)", Integer.class, lockName);
                } catch (Exception ex) {
                    log.debug("city import: release lock failed: {}", ex.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("city import failed: {}", e.getMessage(), e);
        }
    }

    private boolean isTargetHasData() {
        try {
            Long cnt = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM bytedesk_core_city", Long.class);
            return cnt != null && cnt > 0;
        } catch (Exception e) {
            // 表还未创建/无权限等情况：不在这里强行失败，交给后续日志提示
            log.warn("city import: cannot check bytedesk_core_city row count: {}", e.getMessage());
            return false;
        }
    }

    private Resource resolveStagingResource() {
        Resource resource = resolveResource(stagingScript);
        if (resource != null) {
            return resource;
        }
        String fallbackLocation = fallbackStagingFile;
        if (fallbackLocation != null && !fallbackLocation.isBlank()
                && !(fallbackLocation.startsWith("classpath:") || fallbackLocation.startsWith("file:"))) {
            fallbackLocation = "file:" + fallbackLocation;
        }
        Resource fallback = resolveResource(fallbackLocation);
        if (fallback != null) {
            return fallback;
        }
        return null;
    }

    private Resource resolveResource(String location) {
        if (location == null || location.isBlank()) {
            return null;
        }
        try {
            Resource resource = resourceLoader.getResource(location);
            if (resource.exists()) {
                return resource;
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private void executeSqlScript(Resource script) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(false);
        populator.setIgnoreFailedDrops(true);
        populator.addScript(script);
        populator.execute(dataSource);
    }
}
