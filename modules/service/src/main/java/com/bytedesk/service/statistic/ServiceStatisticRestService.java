/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 17:09:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 17:16:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceStatisticRestService extends BaseRestService<ServiceStatisticEntity, ServiceStatisticRequest, ServiceStatisticResponse> {

    private final ServiceStatisticRepository serviceStatisticRepository;

    @Override
    public Page<ServiceStatisticResponse> queryByOrg(ServiceStatisticRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ServiceStatisticEntity> spec = ServiceStatisticSpecification.search(request);
        Page<ServiceStatisticEntity> serviceStatisticPage = serviceStatisticRepository.findAll(spec, pageable);
        return serviceStatisticPage.map(this::convertToResponse);
    }

    @Override
    public Page<ServiceStatisticResponse> queryByUser(ServiceStatisticRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ServiceStatisticEntity> spec = ServiceStatisticSpecification.search(request);
        Page<ServiceStatisticEntity> serviceStatisticPage = serviceStatisticRepository.findAll(spec, pageable);
        return serviceStatisticPage.map(this::convertToResponse);
    }

    @Override
    public Optional<ServiceStatisticEntity> findByUid(String uid) {
        return serviceStatisticRepository.findByUid(uid);
    }

    @Override
    public ServiceStatisticResponse create(ServiceStatisticRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ServiceStatisticResponse update(ServiceStatisticRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ServiceStatisticEntity save(ServiceStatisticEntity entity) {
        try {
            return serviceStatisticRepository.save(entity);
        } catch (Exception e) {
            log.error("save service statistic error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ServiceStatisticEntity> serviceStatisticOptional = serviceStatisticRepository.findByUid(uid);
        if (serviceStatisticOptional.isPresent()) {
            ServiceStatisticEntity serviceStatistic = serviceStatisticOptional.get();
            serviceStatistic.setDeleted(true);
            save(serviceStatistic);
        }
    }

    @Override
    public void delete(ServiceStatisticRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ServiceStatisticEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public ServiceStatisticResponse convertToResponse(ServiceStatisticEntity entity) {
        return ServiceConvertUtils.convertToServiceStatisticResponse(entity);
    }
    
}
