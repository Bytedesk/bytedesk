/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 16:04:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
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
public class AgentStatusSettingRestService extends BaseRestServiceWithExport<AgentStatusSettingEntity, AgentStatusSettingRequest, AgentStatusSettingResponse, AgentStatusSettingExcel> {

    private final AgentStatusSettingRepository agentStatusSettingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<AgentStatusSettingEntity> findByUid(String uid) {
        return agentStatusSettingRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return agentStatusSettingRepository.existsByUid(uid);
    }

    @Override
    public AgentStatusSettingResponse create(AgentStatusSettingRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        AgentStatusSettingEntity entity = modelMapper.map(request, AgentStatusSettingEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        AgentStatusSettingEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public AgentStatusSettingResponse update(AgentStatusSettingRequest request) {
        Optional<AgentStatusSettingEntity> optional = agentStatusSettingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            AgentStatusSettingEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            AgentStatusSettingEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("AgentStatusSetting not found");
        }
    }

    @Override
    protected AgentStatusSettingEntity doSave(AgentStatusSettingEntity entity) {
        return agentStatusSettingRepository.save(entity);
    }

    @Override
    public AgentStatusSettingEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AgentStatusSettingEntity entity) {
        try {
            Optional<AgentStatusSettingEntity> latest = agentStatusSettingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AgentStatusSettingEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return agentStatusSettingRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AgentStatusSettingEntity> optional = agentStatusSettingRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("AgentStatusSetting not found");
        }
    }

    @Override
    public void delete(AgentStatusSettingRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AgentStatusSettingResponse convertToResponse(AgentStatusSettingEntity entity) {
        return modelMapper.map(entity, AgentStatusSettingResponse.class);
    }

    @Override
    public AgentStatusSettingExcel convertToExcel(AgentStatusSettingEntity entity) {
        return modelMapper.map(entity, AgentStatusSettingExcel.class);
    }

    @Override
    protected Specification<AgentStatusSettingEntity> createSpecification(AgentStatusSettingRequest request) {
        return AgentStatusSettingSpecification.search(request, authService);
    }

    @Override
    protected Page<AgentStatusSettingEntity> executePageQuery(Specification<AgentStatusSettingEntity> spec, Pageable pageable) {
        return agentStatusSettingRepository.findAll(spec, pageable);
    }
    
    
}
