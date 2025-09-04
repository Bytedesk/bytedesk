/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 10:51:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class McpServerRestService extends BaseRestServiceWithExport<McpServerEntity, McpServerRequest, McpServerResponse, McpServerExcel> {

    private final McpServerRepository mcpServerRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<McpServerEntity> createSpecification(McpServerRequest request) {
        return McpServerSpecification.search(request, authService);
    }

    @Override
    protected Page<McpServerEntity> executePageQuery(Specification<McpServerEntity> spec, Pageable pageable) {
        return mcpServerRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "mcpServer", key = "#uid", unless="#result==null")
    @Override
    public Optional<McpServerEntity> findByUid(String uid) {
        return mcpServerRepository.findByUid(uid);
    }

    @Cacheable(value = "mcpServer", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<McpServerEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return mcpServerRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return mcpServerRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public McpServerResponse create(McpServerRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<McpServerEntity> mcpServer = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (mcpServer.isPresent()) {
                return convertToResponse(mcpServer.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        McpServerEntity entity = modelMapper.map(request, McpServerEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        McpServerEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create mcpServer failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public McpServerResponse update(McpServerRequest request) {
        Optional<McpServerEntity> optional = mcpServerRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            McpServerEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            McpServerEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update mcpServer failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("McpServer not found");
        }
    }

    @Override
    protected McpServerEntity doSave(McpServerEntity entity) {
        return mcpServerRepository.save(entity);
    }

    @Override
    public McpServerEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, McpServerEntity entity) {
        try {
            Optional<McpServerEntity> latest = mcpServerRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                McpServerEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return mcpServerRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<McpServerEntity> optional = mcpServerRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // mcpServerRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("McpServer not found");
        }
    }

    @Override
    public void delete(McpServerRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public McpServerResponse convertToResponse(McpServerEntity entity) {
        return modelMapper.map(entity, McpServerResponse.class);
    }

    @Override
    public McpServerExcel convertToExcel(McpServerEntity entity) {
        return modelMapper.map(entity, McpServerExcel.class);
    }
    
    public void initMcpServers(String orgUid) {
        // log.info("initThreadMcpServer");
    }

    
    
}
