/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 20:36:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import com.bytedesk.core.base.BaseEntityNoOrg;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * Server entity for monitoring server resources and status
 * Provides server monitoring functionality for system administration
 * 
 * Database Table: bytedesk_core_server
 * Purpose: Stores server information, resource usage, and monitoring data
 * Note: Only super administrators can access server monitoring data
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_server")
public class ServerEntity extends BaseEntityNoOrg {

    /**
     * Server hostname or identifier
     */
    @Column(nullable = false)
    private String serverName;

    /**
     * Server IP address
     */
    private String serverIp;

    /**
     * Server type (APPLICATION, DATABASE, CACHE, etc.)
     */
    @Builder.Default
    @Column(name = "server_type")
    private String type = ServerTypeEnum.APPLICATION.name();

    /**
     * Server status (ONLINE, OFFLINE, MAINTENANCE, etc.)
     */
    @Builder.Default
    @Column(name = "server_status")
    private String status = ServerStatusEnum.ONLINE.name();

    /**
     * Server description
     */
    @Column(name = "server_description")
    private String description;

    /**
     * CPU usage percentage (0-100)
     */
    @Builder.Default
    private Double cpuUsage = 0.0;

    /**
     * Memory usage percentage (0-100)
     */
    @Builder.Default
    private Double memoryUsage = 0.0;

    /**
     * Total memory in MB
     */
    private Long totalMemoryMb;

    /**
     * Used memory in MB
     */
    private Long usedMemoryMb;

    /**
     * Disk usage percentage (0-100)
     */
    @Builder.Default
    private Double diskUsage = 0.0;

    /**
     * Total disk space in GB
     */
    private Long totalDiskGb;

    /**
     * Used disk space in GB
     */
    private Long usedDiskGb;

    /**
     * Server uptime in seconds
     */
    private Long uptimeSeconds;

    /**
     * Server start time
     */
    private ZonedDateTime startTime;

    /**
     * Last heartbeat time
     */
    private ZonedDateTime lastHeartbeat;

    /**
     * Server port (if applicable)
     */
    private Integer serverPort;

    /**
     * Operating system information
     */
    private String osInfo;

    /**
     * Java version (if applicable)
     */
    private String javaVersion;

    /**
     * Application version
     */
    private String appVersion;

    /**
     * Environment (DEV, TEST, PROD, etc.)
     */
    @Builder.Default
    private String environment = "DEV";

    /**
     * Server location or data center
     */
    private String location;

    /**
     * Monitoring enabled flag
     */
    @Builder.Default
    private Boolean monitoringEnabled = true;

    /**
     * Alert threshold for CPU usage
     */
    @Builder.Default
    private Double cpuAlertThreshold = 80.0;

    /**
     * Alert threshold for memory usage
     */
    @Builder.Default
    private Double memoryAlertThreshold = 80.0;

    /**
     * Alert threshold for disk usage
     */
    @Builder.Default
    private Double diskAlertThreshold = 85.0;
} 