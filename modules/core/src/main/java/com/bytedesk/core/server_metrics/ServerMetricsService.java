/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-24 21:36:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 22:18:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server_metrics;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.server.ServerEntity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing server metrics data
 * Provides business logic for server metrics operations
 */
@Slf4j
@Service
@AllArgsConstructor
public class ServerMetricsService {

    private final ServerMetricsRepository serverMetricsRepository;

    /**
     * Record server metrics
     * @param serverEntity server entity with current metrics
     * @return created metrics entity
     */
    @Transactional
    public ServerMetricsEntity recordMetrics(ServerEntity serverEntity) {
        ServerMetricsEntity metrics = ServerMetricsEntity.builder()
                .serverUid(serverEntity.getUid())
                .timestamp(ZonedDateTime.now())
                .cpuUsage(serverEntity.getCpuUsage())
                .memoryUsage(serverEntity.getMemoryUsage())
                .diskUsage(serverEntity.getDiskUsage())
                .usedMemoryMb(serverEntity.getUsedMemoryMb())
                .usedDiskGb(serverEntity.getUsedDiskGb())
                .uptimeSeconds(serverEntity.getUptimeSeconds())
                .collectionInterval(5) // 5-minute intervals
                .build();

        ServerMetricsEntity savedMetrics = serverMetricsRepository.save(metrics);
        log.debug("Recorded metrics for server: {} at {}", serverEntity.getServerName(), savedMetrics.getTimestamp());
        return savedMetrics;
    }

    /**
     * Get metrics history for a server
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return list of metrics
     */
    public List<ServerMetricsEntity> getMetricsHistory(String serverUid, ZonedDateTime startTime, ZonedDateTime endTime) {
        return serverMetricsRepository.findByServerUidAndTimeRange(serverUid, startTime, endTime);
    }

    /**
     * Get latest metrics for a server
     * @param serverUid server UID
     * @return latest metrics or empty
     */
    public Optional<ServerMetricsEntity> getLatestMetrics(String serverUid) {
        List<ServerMetricsEntity> latestMetrics = serverMetricsRepository.findLatestMetricsByServerUid(serverUid);
        return latestMetrics.isEmpty() ? Optional.empty() : Optional.of(latestMetrics.get(0));
    }

    /**
     * Get average metrics for a server in a time range
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return average metrics data
     */
    public ServerMetricsAverage getAverageMetrics(String serverUid, ZonedDateTime startTime, ZonedDateTime endTime) {
        Object[] result = serverMetricsRepository.findAverageMetricsByServerUidAndTimeRange(serverUid, startTime, endTime);
        
        if (result != null && result.length >= 5) {
            return ServerMetricsAverage.builder()
                    .avgCpuUsage((Double) result[0])
                    .avgMemoryUsage((Double) result[1])
                    .avgDiskUsage((Double) result[2])
                    .avgUsedMemoryMb((Double) result[3])
                    .avgUsedDiskGb((Double) result[4])
                    .build();
        }
        
        return ServerMetricsAverage.builder().build();
    }

    /**
     * Get peak metrics for a server in a time range
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return peak metrics data
     */
    public ServerMetricsPeak getPeakMetrics(String serverUid, ZonedDateTime startTime, ZonedDateTime endTime) {
        Object[] result = serverMetricsRepository.findPeakMetricsByServerUidAndTimeRange(serverUid, startTime, endTime);
        
        if (result != null && result.length >= 5) {
            return ServerMetricsPeak.builder()
                    .peakCpuUsage((Double) result[0])
                    .peakMemoryUsage((Double) result[1])
                    .peakDiskUsage((Double) result[2])
                    .peakUsedMemoryMb((Long) result[3])
                    .peakUsedDiskGb((Long) result[4])
                    .build();
        }
        
        return ServerMetricsPeak.builder().build();
    }

    /**
     * Find metrics with high resource usage
     * @param cpuThreshold CPU usage threshold
     * @param memoryThreshold memory usage threshold
     * @param diskThreshold disk usage threshold
     * @param startTime start time
     * @param endTime end time
     * @return list of metrics with high usage
     */
    public List<ServerMetricsEntity> findHighUsageMetrics(Double cpuThreshold, Double memoryThreshold, 
                                                         Double diskThreshold, ZonedDateTime startTime, ZonedDateTime endTime) {
        return serverMetricsRepository.findMetricsWithHighUsage(cpuThreshold, memoryThreshold, diskThreshold, startTime, endTime);
    }

    /**
     * Clean up old metrics data
     * @param retentionDays number of days to retain
     * @return number of deleted records
     */
    @Transactional
    public int cleanupOldMetrics(int retentionDays) {
        ZonedDateTime cutoffTime = ZonedDateTime.now().minusDays(retentionDays);
        int deletedCount = serverMetricsRepository.deleteOldMetrics(cutoffTime);
        log.info("Cleaned up {} old metrics records older than {} days", deletedCount, retentionDays);
        return deletedCount;
    }

    /**
     * Get metrics count for a server
     * @param serverUid server UID
     * @return count of metrics records
     */
    public long getMetricsCount(String serverUid) {
        return serverMetricsRepository.countByServerUidAndDeletedFalse(serverUid);
    }

    /**
     * Data class for average metrics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ServerMetricsAverage {
        private Double avgCpuUsage;
        private Double avgMemoryUsage;
        private Double avgDiskUsage;
        private Double avgUsedMemoryMb;
        private Double avgUsedDiskGb;
    }

    /**
     * Data class for peak metrics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ServerMetricsPeak {
        private Double peakCpuUsage;
        private Double peakMemoryUsage;
        private Double peakDiskUsage;
        private Long peakUsedMemoryMb;
        private Long peakUsedDiskGb;
    }
} 