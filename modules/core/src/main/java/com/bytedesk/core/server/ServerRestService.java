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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ServerService serverService;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    // Helper method for internal use
    private Page<ServerEntity> queryByOrgEntity(ServerRequest request) {
        Pageable pageable = request.getPageable();
        // TODO: Implement ServerSpecification.search(request)
        return serverRepository.findAll(pageable);
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
        ServerEntity entity = convertToEntity(request);
        entity.setUid(uidUtils.getUid());
        entity.setCreatedAt(ZonedDateTime.now());
        entity.setUpdatedAt(ZonedDateTime.now());
        
        ServerEntity savedEntity = serverService.createServer(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    public ServerResponse update(ServerRequest request) {
        Optional<ServerEntity> optional = serverRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ServerEntity existingEntity = optional.get();
            updateEntityFromRequest(existingEntity, request);
            existingEntity.setUpdatedAt(ZonedDateTime.now());
            
            ServerEntity savedEntity = serverService.updateServer(existingEntity);
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Server not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        serverService.deleteServer(uid);
    }

    @Override
    public void delete(ServerRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ServerResponse convertToResponse(ServerEntity entity) {
        ServerResponse response = modelMapper.map(entity, ServerResponse.class);
        
        // Set display names
        if (entity.getType() != null) {
            try {
                ServerTypeEnum typeEnum = ServerTypeEnum.valueOf(entity.getType());
                response.setServerTypeDisplay(typeEnum.getChineseName());
            } catch (IllegalArgumentException e) {
                response.setServerTypeDisplay(entity.getType());
            }
        }
        
        if (entity.getStatus() != null) {
            try {
                ServerStatusEnum statusEnum = ServerStatusEnum.valueOf(entity.getStatus());
                response.setServerStatusDisplay(statusEnum.getChineseName());
                response.setIsHealthy(statusEnum.isHealthy());
                response.setIsOperational(statusEnum.isOperational());
            } catch (IllegalArgumentException e) {
                response.setServerStatusDisplay(entity.getStatus());
            }
        }
        
        // Set health indicators
        response.setHasHighCpuUsage(entity.getCpuUsage() != null && entity.getCpuUsage() > 80);
        response.setHasHighMemoryUsage(entity.getMemoryUsage() != null && entity.getMemoryUsage() > 80);
        response.setHasHighDiskUsage(entity.getDiskUsage() != null && entity.getDiskUsage() > 85);
        response.setHasRecentHeartbeat(entity.getLastHeartbeat() != null && 
                                     entity.getLastHeartbeat().isAfter(java.time.LocalDateTime.now().minusMinutes(5)));
        
        return response;
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
    protected ServerEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ServerEntity entity) {
        log.warn("Optimistic locking failure for server: {}", entity.getUid());
        return entity;
    }

    // Helper methods
    private ServerEntity convertToEntity(ServerRequest request) {
        ServerEntity entity = new ServerEntity();
        entity.setServerName(request.getServerName());
        entity.setServerIp(request.getServerIp());
        entity.setType(request.getServerType());
        entity.setStatus(request.getServerStatus());
        entity.setDescription(request.getDescription());
        entity.setCpuUsage(request.getCpuUsage());
        entity.setMemoryUsage(request.getMemoryUsage());
        entity.setTotalMemoryMb(request.getTotalMemoryMb());
        entity.setUsedMemoryMb(request.getUsedMemoryMb());
        entity.setDiskUsage(request.getDiskUsage());
        entity.setTotalDiskGb(request.getTotalDiskGb());
        entity.setUsedDiskGb(request.getUsedDiskGb());
        entity.setUptimeSeconds(request.getUptimeSeconds());
        entity.setStartTime(request.getStartTime());
        entity.setLastHeartbeat(request.getLastHeartbeat());
        entity.setServerPort(request.getServerPort());
        entity.setOsInfo(request.getOsInfo());
        entity.setJavaVersion(request.getJavaVersion());
        entity.setAppVersion(request.getAppVersion());
        entity.setEnvironment(request.getEnvironment());
        entity.setLocation(request.getLocation());
        entity.setMonitoringEnabled(request.getMonitoringEnabled());
        entity.setCpuAlertThreshold(request.getCpuAlertThreshold());
        entity.setMemoryAlertThreshold(request.getMemoryAlertThreshold());
        entity.setDiskAlertThreshold(request.getDiskAlertThreshold());
        entity.setOrgUid(request.getOrgUid());
        entity.setDeleted(false);
        
        return entity;
    }

    private void updateEntityFromRequest(ServerEntity entity, ServerRequest request) {
        if (request.getServerName() != null) {
            entity.setServerName(request.getServerName());
        }
        if (request.getServerType() != null) {
            entity.setType(request.getServerType());
        }
        if (request.getServerStatus() != null) {
            entity.setStatus(request.getServerStatus());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getCpuUsage() != null) {
            entity.setCpuUsage(request.getCpuUsage());
        }
        if (request.getMemoryUsage() != null) {
            entity.setMemoryUsage(request.getMemoryUsage());
        }
        if (request.getDiskUsage() != null) {
            entity.setDiskUsage(request.getDiskUsage());
        }
        if (request.getEnvironment() != null) {
            entity.setEnvironment(request.getEnvironment());
        }
        if (request.getLocation() != null) {
            entity.setLocation(request.getLocation());
        }
    }
} 