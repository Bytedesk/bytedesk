/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 19:56:39
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
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ServerEntity
 * Provides database operations for server monitoring data
 */
@Repository
public interface ServerRepository extends JpaRepository<ServerEntity, Long>, JpaSpecificationExecutor<ServerEntity> {

    /**
     * Find server by UID
     * @param uid server UID
     * @return Optional<ServerEntity>
     */
    Optional<ServerEntity> findByUid(String uid);

    /**
     * Find server by server name and organization UID
     * @param serverName server name
     * @param orgUid organization UID
     * @return Optional<ServerEntity>
     */
    Optional<ServerEntity> findByServerNameAndOrgUidAndDeletedFalse(String serverName, String orgUid);

    /**
     * Find servers by organization UID
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    List<ServerEntity> findByOrgUidAndDeletedFalseOrderByCreatedAtDesc(String orgUid);

    /**
     * Find servers by type
     * @param type server type
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    List<ServerEntity> findByTypeAndOrgUidAndDeletedFalse(String type, String orgUid);

    /**
     * Find servers by status
     * @param status server status
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    List<ServerEntity> findByStatusAndOrgUidAndDeletedFalse(String status, String orgUid);

    /**
     * Find servers with high resource usage
     * @param orgUid organization UID
     * @param cpuThreshold CPU usage threshold
     * @param memoryThreshold memory usage threshold
     * @param diskThreshold disk usage threshold
     * @return List<ServerEntity>
     */
    @Query("SELECT s FROM ServerEntity s WHERE s.orgUid = :orgUid AND s.deleted = false " +
           "AND (s.cpuUsage >= :cpuThreshold OR s.memoryUsage >= :memoryThreshold OR s.diskUsage >= :diskThreshold)")
    List<ServerEntity> findServersWithHighUsage(@Param("orgUid") String orgUid,
                                               @Param("cpuThreshold") Double cpuThreshold,
                                               @Param("memoryThreshold") Double memoryThreshold,
                                               @Param("diskThreshold") Double diskThreshold);

    /**
     * Find servers that haven't sent heartbeat recently
     * @param orgUid organization UID
     * @param cutoffTime cutoff time for heartbeat
     * @return List<ServerEntity>
     */
    @Query("SELECT s FROM ServerEntity s WHERE s.orgUid = :orgUid AND s.deleted = false " +
           "AND (s.lastHeartbeat IS NULL OR s.lastHeartbeat < :cutoffTime)")
    List<ServerEntity> findServersWithoutRecentHeartbeat(@Param("orgUid") String orgUid,
                                                        @Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Count servers by status
     * @param status server status
     * @param orgUid organization UID
     * @return count
     */
    long countByStatusAndOrgUidAndDeletedFalse(String status, String orgUid);

    /**
     * Count servers by type
     * @param type server type
     * @param orgUid organization UID
     * @return count
     */
    long countByTypeAndOrgUidAndDeletedFalse(String type, String orgUid);

    /**
     * Find servers by environment
     * @param environment environment (DEV, TEST, PROD, etc.)
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    List<ServerEntity> findByEnvironmentAndOrgUidAndDeletedFalse(String environment, String orgUid);

    /**
     * Find servers by location
     * @param location server location
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    List<ServerEntity> findByLocationAndOrgUidAndDeletedFalse(String location, String orgUid);
} 