/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 17:31:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.flow;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FlowRestService extends BaseRestService<FlowEntity, FlowRequest, FlowResponse> {

    private final FlowRepository flowRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<FlowResponse> queryByOrg(FlowRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<FlowEntity> spec = FlowSpecification.search(request);
        Page<FlowEntity> page = flowRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FlowResponse> queryByUser(FlowRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "flow", key = "#uid", unless="#result==null")
    @Override
    public Optional<FlowEntity> findByUid(String uid) {
        return flowRepository.findByUid(uid);
    }

    @Override
    public FlowResponse create(FlowRequest request) {
        
        FlowEntity entity = modelMapper.map(request, FlowEntity.class);
        entity.setUid(uidUtils.getUid());

        FlowEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create flow failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FlowResponse update(FlowRequest request) {
        Optional<FlowEntity> optional = flowRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FlowEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FlowEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update flow failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Flow not found");
        }
    }

    @Override
    public FlowEntity save(FlowEntity entity) {
        try {
            return flowRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FlowEntity> optional = flowRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // flowRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Flow not found");
        }
    }

    @Override
    public void delete(FlowRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FlowEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public FlowResponse convertToResponse(FlowEntity entity) {
        return modelMapper.map(entity, FlowResponse.class);
    }
    
}
