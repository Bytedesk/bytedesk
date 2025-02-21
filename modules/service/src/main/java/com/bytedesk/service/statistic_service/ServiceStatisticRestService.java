/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 11:17:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_service;

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
public class ServiceStatisticRestService extends BaseRestService<ServiceStatisticEntity, ServiceStatisticRequest, ServiceStatisticResponse> {

    private final ServiceStatisticRepository ServiceStatisticRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<ServiceStatisticResponse> queryByOrg(ServiceStatisticRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<ServiceStatisticEntity> spec = ServiceStatisticSpecification.search(request);
        Page<ServiceStatisticEntity> page = ServiceStatisticRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ServiceStatisticResponse> queryByUser(ServiceStatisticRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "ServiceStatistic", key = "#uid", unless="#result==null")
    @Override
    public Optional<ServiceStatisticEntity> findByUid(String uid) {
        return ServiceStatisticRepository.findByUid(uid);
    }

    @Override
    public ServiceStatisticResponse create(ServiceStatisticRequest request) {
        
        ServiceStatisticEntity entity = modelMapper.map(request, ServiceStatisticEntity.class);
        entity.setUid(uidUtils.getUid());

        ServiceStatisticEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create ServiceStatistic failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ServiceStatisticResponse update(ServiceStatisticRequest request) {
        Optional<ServiceStatisticEntity> optional = ServiceStatisticRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ServiceStatisticEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ServiceStatisticEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ServiceStatistic failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ServiceStatistic not found");
        }
    }

    @Override
    public ServiceStatisticEntity save(ServiceStatisticEntity entity) {
        try {
            return ServiceStatisticRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ServiceStatisticEntity> optional = ServiceStatisticRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // ServiceStatisticRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("ServiceStatistic not found");
        }
    }

    @Override
    public void delete(ServiceStatisticRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ServiceStatisticEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public ServiceStatisticResponse convertToResponse(ServiceStatisticEntity entity) {
        return modelMapper.map(entity, ServiceStatisticResponse.class);
    }
    
}
