package com.bytedesk.core.server_metrics;

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

import com.bytedesk.core.server.ServerEntity;
import com.bytedesk.core.server.ServerRestService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ServerMetricsRestService extends BaseRestService<ServerMetricsEntity, ServerMetricsRequest, ServerMetricsResponse> {

    private final ServerMetricsRepository serverMetricsRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    private final ServerRestService serverRestService;

    @Override
    public Page<ServerMetricsResponse> queryByOrg(ServerMetricsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ServerMetricsEntity> spec = ServerMetricsSpecification.search(request);
        Page<ServerMetricsEntity> page = serverMetricsRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ServerMetricsResponse> queryByUser(ServerMetricsRequest request) {
        return queryByOrg(request);
    }

    @Override
    public ServerMetricsResponse queryByUid(ServerMetricsRequest request) {
        Optional<ServerMetricsEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        return null;
    }

    @Cacheable(value = "server_metrics", key = "#uid", unless="#result==null")
    @Override
    public Optional<ServerMetricsEntity> findByUid(String uid) {
        return serverMetricsRepository.findByUid(uid);
    }

    @Override
    public ServerMetricsResponse create(ServerMetricsRequest request) {
        ServerMetricsEntity entity = modelMapper.map(request, ServerMetricsEntity.class);
        entity.setUid(uidUtils.getUid());
        ServerMetricsEntity savedEntity = save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    public ServerMetricsResponse update(ServerMetricsRequest request) {
        Optional<ServerMetricsEntity> optional = serverMetricsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ServerMetricsEntity existingEntity = optional.get();
            // 使用 modelMapper 更新字段，但保持 uid 不变
            modelMapper.map(request, existingEntity);
            existingEntity.setUid(request.getUid()); // 确保 uid 不被覆盖
            ServerMetricsEntity savedEntity = save(existingEntity);
            return convertToResponse(savedEntity);
        }
        throw new RuntimeException("Server metrics not found");
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ServerMetricsEntity> optional = serverMetricsRepository.findByUid(uid);
        if (optional.isPresent()) {
            ServerMetricsEntity metrics = optional.get();
            metrics.setDeleted(true);
            serverMetricsRepository.save(metrics);
        }
    }

    @Override
    public void delete(ServerMetricsRequest request) {
        deleteByUid(request.getUid());
    }
    
    @Override
    protected String getUidFromRequest(ServerMetricsRequest request) {
        return request.getUid();
    }

    @Override
    public ServerMetricsEntity doSave(ServerMetricsEntity entity) {
        return serverMetricsRepository.save(entity);
    }

    @Override
    public ServerMetricsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ServerMetricsEntity entity) {
        log.warn("Optimistic locking failure for server metrics: {}", entity.getUid());
        return entity;
    }

    @Override
    public ServerMetricsResponse convertToResponse(ServerMetricsEntity entity) {
        ServerMetricsResponse response = modelMapper.map(entity, ServerMetricsResponse.class);
        
        if (entity.getServerUid() != null) {
            Optional<ServerEntity> serverOptional = serverRestService.findByUid(entity.getServerUid());
            if (serverOptional.isPresent()) {
                response.setServerName(serverOptional.get().getServerName());
            }
        }
        
        response.setHasHighCpuUsage(entity.getCpuUsage() != null && entity.getCpuUsage() > 80);
        response.setHasHighMemoryUsage(entity.getMemoryUsage() != null && entity.getMemoryUsage() > 80);
        response.setHasHighDiskUsage(entity.getDiskUsage() != null && entity.getDiskUsage() > 85);
        response.setIsHealthy(entity.getCpuUsage() != null && entity.getCpuUsage() < 90 && 
                            entity.getMemoryUsage() != null && entity.getMemoryUsage() < 90 && 
                            entity.getDiskUsage() != null && entity.getDiskUsage() < 95);
        
        return response;
    }

    // 原有的业务方法
    @Transactional
    public ServerMetricsEntity recordMetrics(ServerEntity serverEntity) {
        ServerMetricsEntity metrics = ServerMetricsEntity.builder()
                .uid(uidUtils.getUid())
                .serverUid(serverEntity.getUid())
                .timestamp(ZonedDateTime.now())
                .cpuUsage(serverEntity.getCpuUsage())
                .memoryUsage(serverEntity.getMemoryUsage())
                .diskUsage(serverEntity.getDiskUsage())
                .usedMemoryMb(serverEntity.getUsedMemoryMb())
                .usedDiskGb(serverEntity.getUsedDiskGb())
                .uptimeSeconds(serverEntity.getUptimeSeconds())
                .collectionInterval(5)
                .build();

        return serverMetricsRepository.save(metrics);
    }

    public List<ServerMetricsEntity> getMetricsHistory(String serverUid, ZonedDateTime startTime, ZonedDateTime endTime) {
        return serverMetricsRepository.findByServerUidAndTimeRange(serverUid, startTime, endTime);
    }

    public Optional<ServerMetricsEntity> getLatestMetrics(String serverUid) {
        List<ServerMetricsEntity> latestMetrics = serverMetricsRepository.findLatestMetricsByServerUid(serverUid);
        return latestMetrics.isEmpty() ? Optional.empty() : Optional.of(latestMetrics.get(0));
    }

    public ServerMetricsAverage getAverageMetrics(String serverUid, ZonedDateTime startTime, ZonedDateTime endTime) {
        Object[] result = serverMetricsRepository.findAverageMetricsByServerUidAndTimeRange(serverUid, startTime, endTime);
        
        if (result != null && result.length >= 5) {
            return ServerMetricsAverage.builder()
                    .avgCpuUsage((Double) result[0])
                    .avgMemoryUsage((Double) result[1])
                    .avgDiskUsage((Double) result[2])
                    .avgUsedMemoryMb((Double) result[3])
                    .avgUsedDiskGb((Double) result[4])
                    .build();
        }
        
        return ServerMetricsAverage.builder().build();
    }

    public ServerMetricsPeak getPeakMetrics(String serverUid, ZonedDateTime startTime, ZonedDateTime endTime) {
        Object[] result = serverMetricsRepository.findPeakMetricsByServerUidAndTimeRange(serverUid, startTime, endTime);
        
        if (result != null && result.length >= 5) {
            return ServerMetricsPeak.builder()
                    .peakCpuUsage((Double) result[0])
                    .peakMemoryUsage((Double) result[1])
                    .peakDiskUsage((Double) result[2])
                    .peakUsedMemoryMb((Long) result[3])
                    .peakUsedDiskGb((Long) result[4])
                    .build();
        }
        
        return ServerMetricsPeak.builder().build();
    }

    public List<ServerMetricsEntity> findHighUsageMetrics(Double cpuThreshold, Double memoryThreshold, 
                                                         Double diskThreshold, ZonedDateTime startTime, ZonedDateTime endTime) {
        return serverMetricsRepository.findMetricsWithHighUsage(cpuThreshold, memoryThreshold, diskThreshold, startTime, endTime);
    }

    @Transactional
    public int cleanupOldMetrics(int retentionDays) {
        ZonedDateTime cutoffTime = ZonedDateTime.now().minusDays(retentionDays);
        int deletedCount = serverMetricsRepository.deleteOldMetrics(cutoffTime);
        log.info("Cleaned up {} old metrics records older than {} days", deletedCount, retentionDays);
        return deletedCount;
    }

    public long getMetricsCount(String serverUid) {
        return serverMetricsRepository.countByServerUidAndDeletedFalse(serverUid);
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ServerMetricsAverage {
        private Double avgCpuUsage;
        private Double avgMemoryUsage;
        private Double avgDiskUsage;
        private Double avgUsedMemoryMb;
        private Double avgUsedDiskGb;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ServerMetricsPeak {
        private Double peakCpuUsage;
        private Double peakMemoryUsage;
        private Double peakDiskUsage;
        private Long peakUsedMemoryMb;
        private Long peakUsedDiskGb;
    }
}
