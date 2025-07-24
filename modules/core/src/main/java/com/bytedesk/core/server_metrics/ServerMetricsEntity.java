/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-24 21:36:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 21:36:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server_metrics;

import com.bytedesk.core.base.BaseEntityNoOrg;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
 * Server metrics entity for recording historical server resource usage
 * Provides time-series data for server monitoring and trend analysis
 * 
 * Database Table: bytedesk_core_server_metrics
 * Purpose: Stores historical server metrics data for trend analysis
 * Note: Only super administrators can access server monitoring data
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_server_metrics", indexes = {
    @Index(name = "idx_server_metrics_server_uid", columnList = "serverUid"),
    @Index(name = "idx_server_metrics_timestamp", columnList = "timestamp"),
    @Index(name = "idx_server_metrics_server_timestamp", columnList = "serverUid, timestamp")
})
public class ServerMetricsEntity extends BaseEntityNoOrg {

    /**
     * Reference to the server entity
     */
    @Column(nullable = false)
    private String serverUid;

    /**
     * Timestamp when metrics were recorded
     */
    @Column(nullable = false)
    private ZonedDateTime timestamp;

    /**
     * CPU usage percentage (0-100) - 主要变化曲线数据
     */
    @Builder.Default
    private Double cpuUsage = 0.0;

    /**
     * Memory usage percentage (0-100) - 主要变化曲线数据
     */
    @Builder.Default
    private Double memoryUsage = 0.0;

    /**
     * Disk usage percentage (0-100) - 主要变化曲线数据
     */
    @Builder.Default
    private Double diskUsage = 0.0;

    /**
     * Used memory in MB - 用于计算内存使用量变化
     */
    private Long usedMemoryMb;

    /**
     * Used disk space in GB - 用于计算磁盘使用量变化
     */
    private Long usedDiskGb;

    /**
     * Server uptime in seconds - 用于监控服务器运行状态
     */
    private Long uptimeSeconds;

    /**
     * Collection interval in minutes (e.g., 5 for 5-minute intervals)
     */
    @Builder.Default
    private Integer collectionInterval = 5;
} 