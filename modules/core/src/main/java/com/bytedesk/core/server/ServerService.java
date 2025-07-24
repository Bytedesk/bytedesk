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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for server monitoring and management
 * Provides business logic for server monitoring operations
 */
@Slf4j
@Service
@AllArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;

    /**
     * Create a new server record
     * @param serverEntity server entity to create
     * @return created server entity
     */
    @Transactional
    public ServerEntity createServer(ServerEntity serverEntity) {
        log.info("Creating server: {}", serverEntity.getServerName());
        return serverRepository.save(serverEntity);
    }

    /**
     * Update server information
     * @param serverEntity server entity to update
     * @return updated server entity
     */
    @Transactional
    public ServerEntity updateServer(ServerEntity serverEntity) {
        log.info("Updating server: {}", serverEntity.getServerName());
        return serverRepository.save(serverEntity);
    }

    /**
     * Find server by UID
     * @param uid server UID
     * @return Optional<ServerEntity>
     */
    public Optional<ServerEntity> findByUid(String uid) {
        return serverRepository.findByUid(uid);
    }

    /**
     * Find all servers for an organization
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findByOrgUid(String orgUid) {
        return serverRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtDesc(orgUid);
    }

    /**
     * Find servers by type
     * @param type server type
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findByType(String type, String orgUid) {
        return serverRepository.findByTypeAndOrgUidAndDeletedFalse(type, orgUid);
    }

    /**
     * Find servers by status
     * @param status server status
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findByStatus(String status, String orgUid) {
        return serverRepository.findByStatusAndOrgUidAndDeletedFalse(status, orgUid);
    }

    /**
     * Find servers with high resource usage
     * @param orgUid organization UID
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findServersWithHighUsage(String orgUid) {
        return serverRepository.findServersWithHighUsage(orgUid, 80.0, 80.0, 85.0);
    }

    /**
     * Find servers without recent heartbeat
     * @param orgUid organization UID
     * @param minutesThreshold minutes threshold for heartbeat
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findServersWithoutRecentHeartbeat(String orgUid, int minutesThreshold) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(minutesThreshold);
        return serverRepository.findServersWithoutRecentHeartbeat(orgUid, cutoffTime);
    }

    /**
     * Update server heartbeat
     * @param uid server UID
     * @return updated server entity
     */
    @Transactional
    public Optional<ServerEntity> updateHeartbeat(String uid) {
        Optional<ServerEntity> optional = serverRepository.findByUid(uid);
        if (optional.isPresent()) {
            ServerEntity server = optional.get();
            server.setLastHeartbeat(LocalDateTime.now());
            return Optional.of(serverRepository.save(server));
        }
        return Optional.empty();
    }

    /**
     * Update server resource usage
     * @param uid server UID
     * @param cpuUsage CPU usage percenservere
     * @param memoryUsage memory usage percenservere
     * @param diskUsage disk usage percenservere
     * @return updated server entity
     */
    @Transactional
    public Optional<ServerEntity> updateResourceUsage(String uid, Double cpuUsage, Double memoryUsage, Double diskUsage) {
        Optional<ServerEntity> optional = serverRepository.findByUid(uid);
        if (optional.isPresent()) {
            ServerEntity server = optional.get();
            server.setCpuUsage(cpuUsage);
            server.setMemoryUsage(memoryUsage);
            server.setDiskUsage(diskUsage);
            server.setLastHeartbeat(LocalDateTime.now());
            
            // Update status based on resource usage
            if (cpuUsage > 90 || memoryUsage > 90 || diskUsage > 95) {
                server.setStatus(ServerStatusEnum.OVERLOADED.name());
            } else if (cpuUsage > 80 || memoryUsage > 80 || diskUsage > 85) {
                server.setStatus(ServerStatusEnum.WARNING.name());
            } else {
                server.setStatus(ServerStatusEnum.ONLINE.name());
            }
            
            return Optional.of(serverRepository.save(server));
        }
        return Optional.empty();
    }

    /**
     * Get current server metrics (for self-monitoring)
     * @return ServerEntity with current metrics
     */
    public ServerEntity getCurrentServerMetrics() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Calculate memory usage
        long totalMemory = memoryBean.getHeapMemoryUsage().getMax();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        double memoryUsagePercent = (double) usedMemory / totalMemory * 100;
        
        // Get CPU usage (approximate)
        double cpuUsage = osBean.getSystemLoadAverage() * 100;
        if (cpuUsage > 100) cpuUsage = 100;
        
        // Create server entity with current metrics
        return ServerEntity.builder()
                .serverName(System.getProperty("os.name") + " - " + System.getProperty("user.name"))
                .serverIp("127.0.0.1")
                .type(ServerTypeEnum.APPLICATION.name())
                .status(ServerStatusEnum.ONLINE.name())
                .cpuUsage(cpuUsage)
                .memoryUsage(memoryUsagePercent)
                .totalMemoryMb(totalMemory / (1024 * 1024))
                .usedMemoryMb(usedMemory / (1024 * 1024))
                .uptimeSeconds(ManagementFactory.getRuntimeMXBean().getUptime() / 1000)
                .startTime(LocalDateTime.now().minusSeconds(ManagementFactory.getRuntimeMXBean().getUptime() / 1000))
                .lastHeartbeat(LocalDateTime.now())
                .osInfo(System.getProperty("os.name") + " " + System.getProperty("os.version"))
                .javaVersion(System.getProperty("java.version"))
                .environment("DEV")
                .monitoringEnabled(true)
                .build();
    }

    /**
     * Delete server (soft delete)
     * @param uid server UID
     */
    @Transactional
    public void deleteServer(String uid) {
        Optional<ServerEntity> optional = serverRepository.findByUid(uid);
        if (optional.isPresent()) {
            ServerEntity server = optional.get();
            server.setDeleted(true);
            serverRepository.save(server);
            log.info("Deleted server: {}", server.getServerName());
        }
    }

    /**
     * Get server statistics for an organization
     * @param orgUid organization UID
     * @return ServerStatistics object
     */
    public ServerStatistics getServerStatistics(String orgUid) {
        List<ServerEntity> servers = findByOrgUid(orgUid);
        
        long totalServers = servers.size();
        long onlineServers = servers.stream()
                .filter(s -> ServerStatusEnum.ONLINE.name().equals(s.getStatus()))
                .count();
        long offlineServers = servers.stream()
                .filter(s -> ServerStatusEnum.OFFLINE.name().equals(s.getStatus()))
                .count();
        long warningServers = servers.stream()
                .filter(s -> ServerStatusEnum.WARNING.name().equals(s.getStatus()))
                .count();
        long overloadedServers = servers.stream()
                .filter(s -> ServerStatusEnum.OVERLOADED.name().equals(s.getStatus()))
                .count();
        
        return ServerStatistics.builder()
                .totalServers(totalServers)
                .onlineServers(onlineServers)
                .offlineServers(offlineServers)
                .warningServers(warningServers)
                .overloadedServers(overloadedServers)
                .build();
    }

    /**
     * Server statistics data class
     */
    public static class ServerStatistics {
        private long totalServers;
        private long onlineServers;
        private long offlineServers;
        private long warningServers;
        private long overloadedServers;

        // Getters and setters
        public long getTotalServers() { return totalServers; }
        public void setTotalServers(long totalServers) { this.totalServers = totalServers; }
        
        public long getOnlineServers() { return onlineServers; }
        public void setOnlineServers(long onlineServers) { this.onlineServers = onlineServers; }
        
        public long getOfflineServers() { return offlineServers; }
        public void setOfflineServers(long offlineServers) { this.offlineServers = offlineServers; }
        
        public long getWarningServers() { return warningServers; }
        public void setWarningServers(long warningServers) { this.warningServers = warningServers; }
        
        public long getOverloadedServers() { return overloadedServers; }
        public void setOverloadedServers(long overloadedServers) { this.overloadedServers = overloadedServers; }

        // Builder pattern
        public static ServerStatisticsBuilder builder() {
            return new ServerStatisticsBuilder();
        }

        public static class ServerStatisticsBuilder {
            private ServerStatistics statistics = new ServerStatistics();

            public ServerStatisticsBuilder totalServers(long totalServers) {
                statistics.totalServers = totalServers;
                return this;
            }

            public ServerStatisticsBuilder onlineServers(long onlineServers) {
                statistics.onlineServers = onlineServers;
                return this;
            }

            public ServerStatisticsBuilder offlineServers(long offlineServers) {
                statistics.offlineServers = offlineServers;
                return this;
            }

            public ServerStatisticsBuilder warningServers(long warningServers) {
                statistics.warningServers = warningServers;
                return this;
            }

            public ServerStatisticsBuilder overloadedServers(long overloadedServers) {
                statistics.overloadedServers = overloadedServers;
                return this;
            }

            public ServerStatistics build() {
                return statistics;
            }
        }
    }
} 