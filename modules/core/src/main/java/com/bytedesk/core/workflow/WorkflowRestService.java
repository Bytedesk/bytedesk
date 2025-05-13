/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:20:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

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
public class WorkflowRestService extends BaseRestService<WorkflowEntity, WorkflowRequest, WorkflowResponse> {

    private final WorkflowRepository workflowRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<WorkflowResponse> queryByOrg(WorkflowRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WorkflowEntity> spec = WorkflowSpecification.search(request);
        Page<WorkflowEntity> page = workflowRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<WorkflowResponse> queryByUser(WorkflowRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "workflow", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkflowEntity> findByUid(String uid) {
        return workflowRepository.findByUid(uid);
    }

    @Override
    public WorkflowResponse create(WorkflowRequest request) {
        
        WorkflowEntity entity = modelMapper.map(request, WorkflowEntity.class);
        entity.setUid(uidUtils.getUid());

        WorkflowEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create workflow failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public WorkflowResponse update(WorkflowRequest request) {
        Optional<WorkflowEntity> optional = workflowRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WorkflowEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update workflow failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Workflow not found");
        }
    }

    @Override
    protected WorkflowEntity doSave(WorkflowEntity entity) {
        return workflowRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WorkflowEntity> optional = workflowRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("Workflow not found");
        }
    }

    @Override
    public void delete(WorkflowRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public WorkflowResponse convertToResponse(WorkflowEntity entity) {
        return modelMapper.map(entity, WorkflowResponse.class);
    }

    @Override
    public WorkflowResponse queryByUid(WorkflowRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
