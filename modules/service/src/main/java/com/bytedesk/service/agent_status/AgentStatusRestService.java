/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 11:15:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:52:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgentStatusRestService extends BaseRestService<AgentStatusEntity, AgentStatusRequest, AgentStatusResponse> {
    
    private final AgentStatusRepository agentStatusRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<AgentStatusEntity> createSpecification(AgentStatusRequest request) {
        return AgentStatusSpecification.search(request, authService);
    }

    @Override
    protected Page<AgentStatusEntity> executePageQuery(Specification<AgentStatusEntity> spec, Pageable pageable) {
        return agentStatusRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "agentStatus", key = "#uid", unless = "#result == null")
    @Override
    public Optional<AgentStatusEntity> findByUid(String uid) {
        return agentStatusRepository.findByUid(uid);
    }

    @Override
    public AgentStatusResponse create(AgentStatusRequest request) {

        AgentStatusEntity agentStatus = modelMapper.map(request, AgentStatusEntity.class);
        agentStatus.setUid(uidUtils.getUid());

        AgentStatusEntity savedAgentStatus = save(agentStatus);
        if (savedAgentStatus == null) {
            throw new RuntimeException("AgentStatus create failed");
        }

        return convertToResponse(savedAgentStatus);
    }

    @Override
    public AgentStatusResponse update(AgentStatusRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public AgentStatusEntity save(AgentStatusEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }
    
    @Override
    protected AgentStatusEntity doSave(AgentStatusEntity entity) {
        return agentStatusRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AgentStatusEntity> agentStatus = findByUid(uid);
        if (agentStatus.isPresent()) {
            AgentStatusEntity entity = agentStatus.get();
            entity.setDeleted(true);
            save(entity);
            // agentStatusRepository.delete(agentStatus.get());
        } else {
            throw new RuntimeException("AgentStatus not found");
        }
    }

    @Override
    public void delete(AgentStatusRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public AgentStatusEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AgentStatusEntity entity) {
        try {
            Optional<AgentStatusEntity> latest = agentStatusRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AgentStatusEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return agentStatusRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public AgentStatusResponse convertToResponse(AgentStatusEntity entity) {
        AgentStatusResponse response = modelMapper.map(entity, AgentStatusResponse.class);
        response.setAgent(entity.getAgent());
        return response;
    }

    
    
    
}
