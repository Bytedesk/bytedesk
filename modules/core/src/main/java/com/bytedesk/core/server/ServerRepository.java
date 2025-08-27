/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 09:13:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ServerEntity
 * Provides database operations for server monitoring data
 * Note: Only super administrators can access server monitoring data
 */
public interface ServerRepository extends JpaRepository<ServerEntity, Long>, JpaSpecificationExecutor<ServerEntity> {

    /**
     * Find server by UID
     * @param uid server UID
     * @return Optional&ltServerEntity&amp;amp;gt;
     */
    Optional<ServerEntity> findByUid(String uid);

    /**
     * Find server by server name
     * @param serverName server name
     * @return Optional&ltServerEntity&gt;
     */
    Optional<ServerEntity> findByServerNameAndDeletedFalse(String serverName);

    /**
     * Find all servers
     * @return List&ltServerEntity&gt;
     */
    List<ServerEntity> findByDeletedFalseOrderByCreatedAtDesc();

    /**
     * Find servers by type
     * @param type server type
     * @return List&ltServerEntity&gt;
     */
    List<ServerEntity> findByTypeAndDeletedFalse(String type);

    /**
     * Find servers by status
     * @param status server status
     * @return List&ltServerEntity&gt;
     */
    List<ServerEntity> findByStatusAndDeletedFalse(String status);

    /**
     * Find servers with high resource usage
     * @param cpuThreshold CPU usage threshold
     * @param memoryThreshold memory usage threshold
     * @param diskThreshold disk usage threshold
     * @return List&ltServerEntity&gt;
     */
    @Query("SELECT s FROM ServerEntity s WHERE s.deleted = false " +
           "AND (s.cpuUsage >= :cpuThreshold OR s.memoryUsage >= :memoryThreshold OR s.diskUsage >= :diskThreshold)")
    List<ServerEntity> findServersWithHighUsage(@Param("cpuThreshold") Double cpuThreshold,
                                               @Param("memoryThreshold") Double memoryThreshold,
                                               @Param("diskThreshold") Double diskThreshold);

    /**
     * Find servers that haven't sent heartbeat recently
     * @param cutoffTime cutoff time for heartbeat
     * @return List&ltServerEntity&gt;
     */
    @Query("SELECT s FROM ServerEntity s WHERE s.deleted = false " +
           "AND (s.lastHeartbeat IS NULL OR s.lastHeartbeat < :cutoffTime)")
    List<ServerEntity> findServersWithoutRecentHeartbeat(@Param("cutoffTime") ZonedDateTime cutoffTime);

    /**
     * Count servers by status
     * @param status server status
     * @return count
     */
    long countByStatusAndDeletedFalse(String status);

    /**
     * Count servers by type
     * @param type server type
     * @return count
     */
    long countByTypeAndDeletedFalse(String type);

    /**
     * Find servers by environment
     * @param environment environment (DEV, TEST, PROD, etc.)
     * @return List&ltServerEntity&gt;
     */
    List<ServerEntity> findByEnvironmentAndDeletedFalse(String environment);

    /**
     * Find servers by location
     * @param location server location
     * @return List&ltServerEntity&gt;
     */
    List<ServerEntity> findByLocationAndDeletedFalse(String location);
} 