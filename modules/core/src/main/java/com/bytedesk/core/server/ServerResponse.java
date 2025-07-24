/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 20:48:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import com.bytedesk.core.base.BaseResponseNoOrg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Server response DTO
 * Used for server monitoring operations
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServerResponse extends BaseResponseNoOrg {

    /**
     * Server hostname or identifier
     */
    private String serverName;

    /**
     * Server IP address
     */
    private String serverIp;

    /**
     * Server type (APPLICATION, DATABASE, CACHE, etc.)
     */
    private String serverType;

    /**
     * Server type display name
     */
    private String serverTypeDisplay;

    /**
     * Server status (ONLINE, OFFLINE, MAINTENANCE, etc.)
     */
    private String serverStatus;

    /**
     * Server status display name
     */
    private String serverStatusDisplay;

    /**
     * Server description
     */
    private String description;

    /**
     * CPU usage percentage (0-100)
     */
    private Double cpuUsage;

    /**
     * Memory usage percentage (0-100)
     */
    private Double memoryUsage;

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
    private Double diskUsage;

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
     * Server uptime formatted string (e.g., "2 days 3 hours 45 minutes")
     */
    // private String uptimeFormatted;

    /**
     * Server start time
     */
    private LocalDateTime startTime;

    /**
     * Server start time formatted string
     */
    private String startTimeFormatted;

    /**
     * Last heartbeat time
     */
    private LocalDateTime lastHeartbeat;

    /**
     * Last heartbeat time formatted string
     */
    private String lastHeartbeatFormatted;

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
    private String environment;

    /**
     * Server location or data center
     */
    private String location;

    /**
     * Monitoring enabled flag
     */
    private Boolean monitoringEnabled;

    /**
     * Alert threshold for CPU usage
     */
    private Double cpuAlertThreshold;

    /**
     * Alert threshold for memory usage
     */
    private Double memoryAlertThreshold;

    /**
     * Alert threshold for disk usage
     */
    private Double diskAlertThreshold;

    /**
     * Health status indicators
     */
    private Boolean isHealthy;
    private Boolean isOperational;
    private Boolean hasHighCpuUsage;
    private Boolean hasHighMemoryUsage;
    private Boolean hasHighDiskUsage;
    private Boolean hasRecentHeartbeat;

    /**
     * Status color for UI display
     */
    // private String statusColor;

    /**
     * Status icon for UI display
     */
    // private String statusIcon;

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

    /**
     * Get status color for UI display
     * @return status color
     */
    public String getStatusColor() {
        if (serverStatus == null) {
            return "gray";
        }
        
        switch (serverStatus) {
            case "ONLINE":
                return "green";
            case "OFFLINE":
                return "red";
            case "MAINTENANCE":
                return "orange";
            case "WARNING":
                return "yellow";
            case "OVERLOADED":
                return "red";
            case "ERROR":
                return "red";
            case "STARTING":
            case "STOPPING":
            case "RESTARTING":
                return "blue";
            default:
                return "gray";
        }
    }

    /**
     * Get status icon for UI display
     * @return status icon
     */
    public String getStatusIcon() {
        if (serverStatus == null) {
            return "question-circle";
        }
        
        switch (serverStatus) {
            case "ONLINE":
                return "check-circle";
            case "OFFLINE":
                return "times-circle";
            case "MAINTENANCE":
                return "wrench";
            case "WARNING":
                return "exclamation-triangle";
            case "OVERLOADED":
                return "exclamation-circle";
            case "ERROR":
                return "times-circle";
            case "STARTING":
                return "play-circle";
            case "STOPPING":
                return "stop-circle";
            case "RESTARTING":
                return "sync";
            default:
                return "question-circle";
        }
    }

    /**
     * Check if server has high resource usage
     * @return true if any resource usage is high
     */
    public Boolean getHasHighResourceUsage() {
        return (cpuUsage != null && cpuUsage > 80) ||
               (memoryUsage != null && memoryUsage > 80) ||
               (diskUsage != null && diskUsage > 85);
    }
}
