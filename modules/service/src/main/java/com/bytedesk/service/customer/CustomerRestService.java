/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:21:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerRestService extends BaseRestServiceWithExcel<CustomerEntity, CustomerRequest, CustomerResponse, CustomerExcel> {
    
    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    @Override
    public Page<CustomerEntity> queryByOrgEntity(CustomerRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CustomerEntity> spec = CustomerSpecification.search(request);
        return customerRepository.findAll(spec, pageable);
    }
    
    @Override
    public Page<CustomerResponse> queryByOrg(CustomerRequest request) {
        Page<CustomerEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CustomerResponse> queryByUser(CustomerRequest request) {
        // 
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<CustomerEntity> spec = CustomerSpecification.search(request);
        Page<CustomerEntity> page = customerRepository.findAll(spec, pageable);
        
        return page.map(this::convertToResponse);
    }

    @Override
    public Optional<CustomerEntity> findByUid(String uid) {
        return customerRepository.findByUid(uid);
    }

    @Override
    public CustomerResponse create(CustomerRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public CustomerResponse update(CustomerRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public CustomerEntity save(CustomerEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }
    
    @Override
    protected CustomerEntity doSave(CustomerEntity entity) {
        return customerRepository.save(entity);
    }

    @Override
    public CustomerEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CustomerEntity entity) {
        try {
            Optional<CustomerEntity> latest = customerRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CustomerEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return customerRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(CustomerRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public CustomerResponse convertToResponse(CustomerEntity entity) {
        return modelMapper.map(entity, CustomerResponse.class);
    }

    @Override
    public CustomerResponse queryByUid(CustomerRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public CustomerExcel convertToExcel(CustomerEntity entity) {
        return modelMapper.map(entity, CustomerExcel.class);
    }
    
}
