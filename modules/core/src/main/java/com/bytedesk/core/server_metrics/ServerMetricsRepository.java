/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-24 21:36:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:26:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server_metrics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ServerMetricsEntity
 * Provides database operations for server metrics historical data
 * Note: Only super administrators can access server monitoring data
 */
public interface ServerMetricsRepository extends JpaRepository<ServerMetricsEntity, Long>, JpaSpecificationExecutor<ServerMetricsEntity> {

    /**
     * Find metrics by UID
     * @param uid metrics UID
     * @return Optional<ServerMetricsEntity&amp;amp;gt;
     */
    Optional<ServerMetricsEntity> findByUid(String uid);

    /**
     * Find metrics by server UID
     * @param serverUid server UID
     * @return List<ServerMetricsEntity&gt;
     */
    List<ServerMetricsEntity> findByServerUidAndDeletedFalseOrderByTimestampDesc(String serverUid);

    /**
     * Find metrics by server UID and time range
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return List<ServerMetricsEntity&gt;
     */
    @Query("SELECT m FROM ServerMetricsEntity m WHERE m.deleted = false " +
           "AND m.serverUid = :serverUid " +
           "AND m.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY m.timestamp ASC")
    List<ServerMetricsEntity> findByServerUidAndTimeRange(@Param("serverUid") String serverUid,
                                                         @Param("startTime") ZonedDateTime startTime,
                                                         @Param("endTime") ZonedDateTime endTime);

    /**
     * Find latest metrics for a server
     * @param serverUid server UID
     * @return ServerMetricsEntity or null
     */
    @Query("SELECT m FROM ServerMetricsEntity m WHERE m.deleted = false " +
           "AND m.serverUid = :serverUid " +
           "ORDER BY m.timestamp DESC")
    List<ServerMetricsEntity> findLatestMetricsByServerUid(@Param("serverUid") String serverUid);

    /**
     * Find metrics with high resource usage
     * @param cpuThreshold CPU usage threshold
     * @param memoryThreshold memory usage threshold
     * @param diskThreshold disk usage threshold
     * @param startTime start time
     * @param endTime end time
     * @return List<ServerMetricsEntity&gt;
     */
    @Query("SELECT m FROM ServerMetricsEntity m WHERE m.deleted = false " +
           "AND m.timestamp BETWEEN :startTime AND :endTime " +
           "AND (m.cpuUsage >= :cpuThreshold OR m.memoryUsage >= :memoryThreshold OR m.diskUsage >= :diskThreshold) " +
           "ORDER BY m.timestamp DESC")
    List<ServerMetricsEntity> findMetricsWithHighUsage(@Param("cpuThreshold") Double cpuThreshold,
                                                      @Param("memoryThreshold") Double memoryThreshold,
                                                      @Param("diskThreshold") Double diskThreshold,
                                                      @Param("startTime") ZonedDateTime startTime,
                                                      @Param("endTime") ZonedDateTime endTime);

    /**
     * Find average metrics for a server in a time range
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return Object array with average values
     */
    @Query("SELECT AVG(m.cpuUsage), AVG(m.memoryUsage), AVG(m.diskUsage), " +
           "AVG(m.usedMemoryMb), AVG(m.usedDiskGb) " +
           "FROM ServerMetricsEntity m WHERE m.deleted = false " +
           "AND m.serverUid = :serverUid " +
           "AND m.timestamp BETWEEN :startTime AND :endTime")
    Object[] findAverageMetricsByServerUidAndTimeRange(@Param("serverUid") String serverUid,
                                                      @Param("startTime") ZonedDateTime startTime,
                                                      @Param("endTime") ZonedDateTime endTime);

    /**
     * Find peak metrics for a server in a time range
     * @param serverUid server UID
     * @param startTime start time
     * @param endTime end time
     * @return Object array with peak values
     */
    @Query("SELECT MAX(m.cpuUsage), MAX(m.memoryUsage), MAX(m.diskUsage), " +
           "MAX(m.usedMemoryMb), MAX(m.usedDiskGb) " +
           "FROM ServerMetricsEntity m WHERE m.deleted = false " +
           "AND m.serverUid = :serverUid " +
           "AND m.timestamp BETWEEN :startTime AND :endTime")
    Object[] findPeakMetricsByServerUidAndTimeRange(@Param("serverUid") String serverUid,
                                                   @Param("startTime") ZonedDateTime startTime,
                                                   @Param("endTime") ZonedDateTime endTime);

    /**
     * Delete old metrics data (for data retention)
     * @param cutoffTime cutoff time
     * @return number of deleted records
     */
    @Query("DELETE FROM ServerMetricsEntity m WHERE m.deleted = false " +
           "AND m.timestamp < :cutoffTime")
    int deleteOldMetrics(@Param("cutoffTime") ZonedDateTime cutoffTime);

    /**
     * Count metrics records for a server
     * @param serverUid server UID
     * @return count
     */
    long countByServerUidAndDeletedFalse(String serverUid);


} 