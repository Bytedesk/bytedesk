/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-24 21:36:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 22:16:08
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.server.ServerEntity;
import com.bytedesk.core.server.ServerRestService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for server metrics operations
 * Provides APIs for querying server metrics historical data
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/server-metrics")
@AllArgsConstructor
public class ServerMetricsController {

    private final ServerMetricsService serverMetricsService;
    private final ServerRestService serverRestService;

    /**
     * Get metrics history for a server
     * @param serverUid server UID
     * @param startTime start time (ISO format: yyyy-MM-ddTHH:mm:ssZ)
     * @param endTime end time (ISO format: yyyy-MM-ddTHH:mm:ssZ)
     * @return list of metrics
     */
    @GetMapping("/history/{serverUid}")
    public ResponseEntity<List<ServerMetricsEntity>> getMetricsHistory(
            @PathVariable String serverUid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        
        log.info("Getting metrics history for server UID: {} from {} to {}", serverUid, startTime, endTime);
        
        List<ServerMetricsEntity> metrics = serverMetricsService.getMetricsHistory(serverUid, startTime, endTime);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get latest metrics for a server
     * @param serverUid server UID
     * @return latest metrics
     */
    @GetMapping("/latest/{serverUid}")
    public ResponseEntity<ServerMetricsEntity> getLatestMetrics(@PathVariable String serverUid) {
        log.info("Getting latest metrics for server UID: {}", serverUid);
        
        Optional<ServerMetricsEntity> metrics = serverMetricsService.getLatestMetrics(serverUid);
        return metrics.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get average metrics for a server in a time range
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return average metrics
     */
    @GetMapping("/average/{serverUid}")
    public ResponseEntity<ServerMetricsService.ServerMetricsAverage> getAverageMetrics(
            @PathVariable String serverUid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        
        log.info("Getting average metrics for server UID: {} from {} to {}", serverUid, startTime, endTime);
        
        ServerMetricsService.ServerMetricsAverage average = serverMetricsService.getAverageMetrics(serverUid, startTime, endTime);
        return ResponseEntity.ok(average);
    }

    /**
     * Get peak metrics for a server in a time range
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return peak metrics
     */
    @GetMapping("/peak/{serverUid}")
    public ResponseEntity<ServerMetricsService.ServerMetricsPeak> getPeakMetrics(
            @PathVariable String serverUid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        
        log.info("Getting peak metrics for server UID: {} from {} to {}", serverUid, startTime, endTime);
        
        ServerMetricsService.ServerMetricsPeak peak = serverMetricsService.getPeakMetrics(serverUid, startTime, endTime);
        return ResponseEntity.ok(peak);
    }

    /**
     * Find metrics with high resource usage
     * @param cpuThreshold CPU usage threshold (default: 80.0)
     * @param memoryThreshold memory usage threshold (default: 80.0)
     * @param diskThreshold disk usage threshold (default: 85.0)
     * @param startTime start time
     * @param endTime end time
     * @return list of metrics with high usage
     */
    @GetMapping("/high-usage")
    public ResponseEntity<List<ServerMetricsEntity>> findHighUsageMetrics(
            @RequestParam(defaultValue = "80.0") Double cpuThreshold,
            @RequestParam(defaultValue = "80.0") Double memoryThreshold,
            @RequestParam(defaultValue = "85.0") Double diskThreshold,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        
        log.info("Finding high usage metrics from {} to {} with thresholds: CPU={}, Memory={}, Disk={}", 
                startTime, endTime, cpuThreshold, memoryThreshold, diskThreshold);
        
        List<ServerMetricsEntity> metrics = serverMetricsService.findHighUsageMetrics(
                cpuThreshold, memoryThreshold, diskThreshold, startTime, endTime);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics count for a server
     * @param serverUid server UID
     * @return metrics count
     */
    @GetMapping("/count/{serverUid}")
    public ResponseEntity<Long> getMetricsCount(@PathVariable String serverUid) {
        log.info("Getting metrics count for server UID: {}", serverUid);
        
        long count = serverMetricsService.getMetricsCount(serverUid);
        return ResponseEntity.ok(count);
    }

    /**
     * Clean up old metrics data (admin only)
     * @param retentionDays number of days to retain (default: 30)
     * @return number of deleted records
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Integer> cleanupOldMetrics(@RequestParam(defaultValue = "30") Integer retentionDays) {
        log.info("Cleaning up old metrics data with {} days retention", retentionDays);
        
        int deletedCount = serverMetricsService.cleanupOldMetrics(retentionDays);
        return ResponseEntity.ok(deletedCount);
    }

    /**
     * Get metrics history for current server
     * @param startTime start time
     * @param endTime end time
     * @return list of metrics for current server
     */
    @GetMapping("/current/history")
    public ResponseEntity<List<ServerMetricsEntity>> getCurrentServerMetricsHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {
        
        log.info("Getting current server metrics history from {} to {}", startTime, endTime);
        
        // 获取当前服务器信息
        ServerEntity currentServer = serverRestService.getCurrentServerMetrics();
        String serverName = currentServer.getServerName();
        
        // 查找服务器记录
        ServerEntity existingServer = serverRestService.findByServerName(serverName);
        if (existingServer == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<ServerMetricsEntity> metrics = serverMetricsService.getMetricsHistory(
                existingServer.getUid(), startTime, endTime);
        return ResponseEntity.ok(metrics);
    }
} 