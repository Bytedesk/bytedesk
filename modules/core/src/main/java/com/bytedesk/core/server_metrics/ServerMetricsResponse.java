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

import com.bytedesk.core.base.BaseResponseNoOrg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * Server metrics response DTO
 * Used for server metrics operations
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServerMetricsResponse extends BaseResponseNoOrg {

    /**
     * Server UID reference
     */
    private String serverUid;

    /**
     * Server name (for display purposes)
     */
    private String serverName;

    /**
     * Timestamp when metrics were recorded
     */
    private ZonedDateTime timestamp;

    /**
     * Timestamp formatted string
     */
    private String timestampFormatted;

    /**
     * CPU usage percentage (0-100)
     */
    private Double cpuUsage;

    /**
     * Memory usage percentage (0-100)
     */
    private Double memoryUsage;

    /**
     * Disk usage percentage (0-100)
     */
    private Double diskUsage;

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
     * Server uptime formatted string
     */
    private String uptimeFormatted;

    /**
     * Collection interval in minutes
     */
    private Integer collectionInterval;

    /**
     * Health status indicators
     */
    private Boolean hasHighCpuUsage;
    private Boolean hasHighMemoryUsage;
    private Boolean hasHighDiskUsage;
    private Boolean isHealthy;

    /**
     * Get formatted uptime string
     * @return formatted uptime string
     */
    public String getUptimeFormatted() {
        if (uptimeSeconds == null || uptimeSeconds <= 0) {
            return "Unknown";
        }
        
        long days = uptimeSeconds / 86400;
        long hours = (uptimeSeconds % 86400) / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        
        if (days > 0) {
            return String.format("%d days %d hours %d minutes", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%d hours %d minutes", hours, minutes);
        } else {
            return String.format("%d minutes", minutes);
        }
    }
} 