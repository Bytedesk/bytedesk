/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-24 21:36:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 22:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server_metrics;

import com.bytedesk.core.base.BaseRequestNoOrg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * Server metrics request DTO
 * Used for server metrics operations
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ServerMetricsRequest extends BaseRequestNoOrg {

    /**
     * Server UID reference
     */
    private String serverUid;

    /**
     * Timestamp when metrics were recorded
     */
    private ZonedDateTime timestamp;

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
     * Disk usage percentage (0-100)
     */
    @Builder.Default
    private Double diskUsage = 0.0;

    /**
     * Used memory in MB
     */
    private Long usedMemoryMb;

    /**
     * Used disk space in GB
     */
    private Long usedDiskGb;

    /**
     * Server uptime in seconds
     */
    private Long uptimeSeconds;

    /**
     * Collection interval in minutes
     */
    @Builder.Default
    private Integer collectionInterval = 5;

    /**
     * Search filters
     */
    private String serverUidFilter;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Double cpuThreshold;
    private Double memoryThreshold;
    private Double diskThreshold;
    private Boolean highUsageFilter; // true to show only metrics with high resource usage
    private Integer retentionDays; // for cleanup operations
} 