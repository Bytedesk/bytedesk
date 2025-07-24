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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task for cleaning up old server metrics data
 * Helps manage database storage by removing outdated metrics records
 */
@Slf4j
@Component
@AllArgsConstructor
public class ServerMetricsCleanupTask {

    private final ServerMetricsRestService serverMetricsService;

    /**
     * Clean up old server metrics data daily at 2:00 AM
     * Retains data for 30 days by default
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2:00 AM
    public void cleanupOldMetrics() {
        log.info("Starting server metrics cleanup task");
        
        try {
            // 保留30天的数据
            int retentionDays = 30;
            int deletedCount = serverMetricsService.cleanupOldMetrics(retentionDays);
            
            log.info("Server metrics cleanup completed. Deleted {} records older than {} days", 
                    deletedCount, retentionDays);
        } catch (Exception e) {
            log.error("Error during server metrics cleanup: ", e);
        }
    }

    /**
     * Clean up old server metrics data with custom retention period
     * @param retentionDays number of days to retain
     */
    public void cleanupOldMetrics(int retentionDays) {
        log.info("Starting server metrics cleanup task with {} days retention", retentionDays);
        
        try {
            int deletedCount = serverMetricsService.cleanupOldMetrics(retentionDays);
            
            log.info("Server metrics cleanup completed. Deleted {} records older than {} days", 
                    deletedCount, retentionDays);
        } catch (Exception e) {
            log.error("Error during server metrics cleanup: ", e);
        }
    }
} 