/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-24 21:36:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 23:06:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server_metrics;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import com.bytedesk.core.server.ServerEntity;
import com.bytedesk.core.server.ServerRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for server metrics operations
 * Provides APIs for server metrics CRUD operations and historical data queries
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/server-metrics")
@AllArgsConstructor
@Tag(name = "Server Metrics Management", description = "Server metrics management APIs for monitoring and analyzing server performance data")
@Description("Server Metrics Management Controller - Server performance monitoring and metrics analysis APIs")
public class ServerMetricsRestController extends BaseRestController<ServerMetricsRequest> {

    private final ServerMetricsService serverMetricsService;
    private final ServerRestService serverRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "服务器指标", action = "组织查询", description = "query server metrics by org")
    @Operation(summary = "Query Server Metrics by Organization", description = "Retrieve server metrics for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ServerMetricsRequest request) {
        
        Page<ServerMetricsResponse> metrics = serverMetricsService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(metrics));
    }

    @ActionAnnotation(title = "服务器指标", action = "用户查询", description = "query server metrics by user")
    @Operation(summary = "Query Server Metrics by User", description = "Retrieve server metrics for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ServerMetricsRequest request) {
        
        Page<ServerMetricsResponse> metrics = serverMetricsService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(metrics));
    }

    @ActionAnnotation(title = "服务器指标", action = "查询详情", description = "query server metrics by uid")
    @Operation(summary = "Query Server Metrics by UID", description = "Retrieve a specific server metrics by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ServerMetricsRequest request) {
        
        ServerMetricsResponse metrics = serverMetricsService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(metrics));
    }

    @ActionAnnotation(title = "服务器指标", action = "新建", description = "create server metrics")
    @Operation(summary = "Create Server Metrics", description = "Create a new server metrics record")
    @Override
    public ResponseEntity<?> create(ServerMetricsRequest request) {
        
        ServerMetricsResponse metrics = serverMetricsService.create(request);

        return ResponseEntity.ok(JsonResult.success(metrics));
    }

    @ActionAnnotation(title = "服务器指标", action = "更新", description = "update server metrics")
    @Operation(summary = "Update Server Metrics", description = "Update an existing server metrics record")
    @Override
    public ResponseEntity<?> update(ServerMetricsRequest request) {
        
        ServerMetricsResponse metrics = serverMetricsService.update(request);

        return ResponseEntity.ok(JsonResult.success(metrics));
    }

    @ActionAnnotation(title = "服务器指标", action = "删除", description = "delete server metrics")
    @Operation(summary = "Delete Server Metrics", description = "Delete a server metrics record")
    @Override
    public ResponseEntity<?> delete(ServerMetricsRequest request) {
        
        serverMetricsService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "服务器指标", action = "导出", description = "export server metrics")
    @Operation(summary = "Export Server Metrics", description = "Export server metrics to Excel format")
    @Override
    @GetMapping("/export")
    public Object export(ServerMetricsRequest request, HttpServletResponse response) {
        // TODO: 实现 ServerMetricsExcel 导出功能
        throw new UnsupportedOperationException("Export functionality not implemented yet");
    }

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