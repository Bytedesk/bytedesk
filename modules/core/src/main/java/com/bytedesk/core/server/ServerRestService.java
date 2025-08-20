/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-25 07:44:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST service for server monitoring operations
 * Provides CRUD operations and business logic for server management
 */
@Slf4j
@Service
@AllArgsConstructor
public class ServerRestService extends BaseRestService<ServerEntity, ServerRequest, ServerResponse> {

    private final ServerRepository serverRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    // Helper method for internal use
    private Page<ServerEntity> queryByOrgEntity(ServerRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ServerEntity> spec = ServerSpecification.search(request);
        return serverRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ServerResponse> queryByOrg(ServerRequest request) {
        Page<ServerEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ServerResponse> queryByUser(ServerRequest request) {
        // For server monitoring, user query is same as org query
        return queryByOrg(request);
    }

    @Override
    public ServerResponse queryByUid(ServerRequest request) {
        Optional<ServerEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ServerEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("Server not found");
        }
    }

    @Cacheable(value = "server", key = "#uid", unless="#result==null")
    @Override
    public Optional<ServerEntity> findByUid(String uid) {
        return serverRepository.findByUid(uid);
    }

    @Override
    public ServerResponse create(ServerRequest request) {
        ServerEntity entity = modelMapper.map(request, ServerEntity.class);
        entity.setUid(uidUtils.getUid());
        
        ServerEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create server failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ServerResponse update(ServerRequest request) {
        Optional<ServerEntity> optional = serverRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ServerEntity existingEntity = optional.get();
            existingEntity = modelMapper.map(request, ServerEntity.class);
            
            ServerEntity savedEntity = save(existingEntity);
            if (savedEntity == null) {
                throw new RuntimeException("Update server failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Server not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ServerEntity> optional = serverRepository.findByUid(uid);
        if (optional.isPresent()) {
            ServerEntity server = optional.get();
            server.setDeleted(true);
            serverRepository.save(server);
            log.info("Deleted server: {}", server.getServerName());
        }
    }

    @Override
    public void delete(ServerRequest request) {
        deleteByUid(request.getUid());
    }
    
    @Override
    protected String getUidFromRequest(ServerRequest request) {
        return request.getUid();
    }

    @Override
    public ServerEntity doSave(ServerEntity entity) {
        return serverRepository.save(entity);
    }

    @Override
    public ServerEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ServerEntity entity) {
        log.warn("Optimistic locking failure for server: {}", entity.getUid());
        return entity;
    }

    // ========== 从 ServerService 迁移的方法 ==========

    /**
     * Find all servers
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findAllServers() {
        return serverRepository.findByDeletedFalseOrderByCreatedAtDesc();
    }

    /**
     * Find servers by type
     * @param type server type
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findByType(String type) {
        return serverRepository.findByTypeAndDeletedFalse(type);
    }

    /**
     * Find servers by status
     * @param status server status
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findByStatus(String status) {
        return serverRepository.findByStatusAndDeletedFalse(status);
    }

    /**
     * Find servers with high resource usage
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findServersWithHighUsage() {
        return serverRepository.findServersWithHighUsage(80.0, 80.0, 85.0);
    }

    /**
     * Find servers without recent heartbeat
     * @param minutesThreshold minutes threshold for heartbeat
     * @return List<ServerEntity>
     */
    public List<ServerEntity> findServersWithoutRecentHeartbeat(int minutesThreshold) {
        ZonedDateTime cutoffTime = ZonedDateTime.now().minusMinutes(minutesThreshold);
        return serverRepository.findServersWithoutRecentHeartbeat(cutoffTime);
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
            server.setLastHeartbeat(ZonedDateTime.now());
            return Optional.of(serverRepository.save(server));
        }
        return Optional.empty();
    }

    /**
     * Update server resource usage
     * @param uid server UID
     * @param cpuUsage CPU usage percentage
     * @param memoryUsage memory usage percentage
     * @param diskUsage disk usage percentage
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
            server.setLastHeartbeat(ZonedDateTime.now());
            
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
        
        // Calculate disk usage
        double diskUsage = 0.0;
        long totalDiskGb = 0L;
        long usedDiskGb = 0L;
        
        try {
            File root = new File("/");
            long totalSpace = root.getTotalSpace();
            long usableSpace = root.getUsableSpace();
            long usedSpace = totalSpace - usableSpace;
            
            totalDiskGb = totalSpace / (1024 * 1024 * 1024);
            usedDiskGb = usedSpace / (1024 * 1024 * 1024);
            diskUsage = (double) usedSpace / totalSpace * 100;
        } catch (Exception e) {
            log.warn("Failed to get disk usage information: {}", e.getMessage());
        }
        
        // Create server entity with current metrics
        return ServerEntity.builder()
                .serverName(System.getProperty("os.name") + " - " + System.getProperty("user.name"))
                .serverIp("127.0.0.1")
                .type(ServerTypeEnum.APPLICATION.name())
                .status(ServerStatusEnum.ONLINE.name())
                .cpuUsage(cpuUsage)
                .memoryUsage(memoryUsagePercent)
                .diskUsage(diskUsage)
                .totalMemoryMb(totalMemory / (1024 * 1024))
                .usedMemoryMb(usedMemory / (1024 * 1024))
                .totalDiskGb(totalDiskGb)
                .usedDiskGb(usedDiskGb)
                .uptimeSeconds(ManagementFactory.getRuntimeMXBean().getUptime() / 1000)
                .startTime(ZonedDateTime.now().minusSeconds(ManagementFactory.getRuntimeMXBean().getUptime() / 1000))
                .lastHeartbeat(ZonedDateTime.now())
                .osInfo(System.getProperty("os.name") + " " + System.getProperty("os.version"))
                .javaVersion(System.getProperty("java.version"))
                .environment("DEV")
                .monitoringEnabled(true)
                .build();
    }

    /**
     * Find server by server name
     * @param serverName server name
     * @return ServerEntity or null if not found
     */
    public ServerEntity findByServerName(String serverName) {
        Optional<ServerEntity> optional = serverRepository.findByServerNameAndDeletedFalse(serverName);
        return optional.orElse(null);
    }

    /**
     * Create server entity directly
     * @param serverEntity server entity to create
     * @return created server entity
     */
    @Transactional
    public ServerEntity createServer(ServerEntity serverEntity) {
        if (serverEntity.getUid() == null) {
            serverEntity.setUid(uidUtils.getUid());
        }
        return serverRepository.save(serverEntity);
    }

    /**
     * Update server entity directly
     * @param serverEntity server entity to update
     * @return updated server entity
     */
    @Transactional
    public ServerEntity updateServer(ServerEntity serverEntity) {
        return serverRepository.save(serverEntity);
    }

    /**
     * Get server statistics
     * @return ServerStatistics object
     */
    public ServerStatistics getServerStatistics() {
        List<ServerEntity> servers = findAllServers();
        
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

    @Override
    public ServerResponse convertToResponse(ServerEntity entity) {
        ServerResponse response = modelMapper.map(entity, ServerResponse.class);
        
        // Set display names
        if (entity.getType() != null) {
            try {
                ServerTypeEnum typeEnum = ServerTypeEnum.valueOf(entity.getType());
                response.setServerType(typeEnum.name());
            } catch (IllegalArgumentException e) {
                response.setServerType(entity.getType());
            }
        }
        
        if (entity.getStatus() != null) {
            try {
                ServerStatusEnum statusEnum = ServerStatusEnum.valueOf(entity.getStatus());
                response.setServerStatus(statusEnum.name());
                response.setIsHealthy(statusEnum.isHealthy());
                response.setIsOperational(statusEnum.isOperational());
            } catch (IllegalArgumentException e) {
                response.setServerStatus(entity.getStatus());
            }
        }
        
        // Set health indicators
        response.setHasHighCpuUsage(entity.getCpuUsage() != null && entity.getCpuUsage() > 80);
        response.setHasHighMemoryUsage(entity.getMemoryUsage() != null && entity.getMemoryUsage() > 80);
        response.setHasHighDiskUsage(entity.getDiskUsage() != null && entity.getDiskUsage() > 85);
        response.setHasRecentHeartbeat(entity.getLastHeartbeat() != null && 
                                     entity.getLastHeartbeat().isAfter(java.time.ZonedDateTime.now().minusMinutes(5)));
        
        return response;
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

    @Override
    protected Specification<ServerEntity> createSpecification(ServerRequest request) {
        return ServerSpecification.search(request);
    }

    @Override
    protected Page<ServerEntity> executePageQuery(Specification<ServerEntity> spec, Pageable pageable) {
        return serverRepository.findAll(spec, pageable);
    }

} 