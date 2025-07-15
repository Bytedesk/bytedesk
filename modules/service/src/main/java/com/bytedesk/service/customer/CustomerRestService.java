/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 14:22:38
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Description("Customer Management Service - Customer information and relationship management service")
public class CustomerRestService extends BaseRestServiceWithExcel<CustomerEntity, CustomerRequest, CustomerResponse, CustomerExcel> {
    
    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

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
       UserEntity user = authService.getCurrentUser();
       if (user == null) {
        throw new RuntimeException("用户未登录");
       }
       request.setUserUid(user.getUid());
       // 
       return queryByOrg(request);
    }

    @Override
    public CustomerResponse queryByUid(CustomerRequest request) {
        Optional<CustomerEntity> entity = findByUid(request.getUid());
        if (entity.isPresent()) {
            return convertToResponse(entity.get());
        }
        return null;
    }

    public CustomerResponse queryByVisitorUid(CustomerRequest request) {
        Optional<CustomerEntity> entity = findByVisitorUid(request.getVisitorUid());
        if (entity.isPresent()) {
            return convertToResponse(entity.get());
        }
        return null;
    }

    @Cacheable(value = "customer", key = "#uid", unless = "#result == null")
    @Override
    public Optional<CustomerEntity> findByUid(String uid) {
        if (!StringUtils.hasText(uid)) {
            return Optional.empty();
        }
        return customerRepository.findByUid(uid);
    }

    @Cacheable(value = "customer", key = "#visitorUid", unless = "#result == null")
    public Optional<CustomerEntity> findByVisitorUid(String visitorUid) {
        if (!StringUtils.hasText(visitorUid)) {
            return Optional.empty();
        }
        return customerRepository.findByVisitorUid(visitorUid);
    }

    @Cacheable(value = "customer", key = "#visitorUid", unless = "#result == null")
    public boolean existsByVisitorUid(String visitorUid) {
        if (!StringUtils.hasText(visitorUid)) {
            return false;
        }
        return customerRepository.existsByVisitorUid(visitorUid);
    }

    @Override
    public CustomerResponse create(CustomerRequest request) {
        if (existsByVisitorUid(request.getVisitorUid())) {
            return convertToResponse(findByVisitorUid(request.getVisitorUid()).get());
        }
        // 
        CustomerEntity entity = modelMapper.map(request, CustomerEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        CustomerEntity savedEntity = doSave(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建客户失败");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public CustomerResponse update(CustomerRequest request) {
        Optional<CustomerEntity> entity = findByUid(request.getUid());
        if (entity.isPresent()) {
            CustomerEntity updatedEntity = entity.get();
            updatedEntity.setNickname(request.getNickname());
            updatedEntity.setEmail(request.getEmail());
            updatedEntity.setMobile(request.getMobile());
            updatedEntity.setDescription(request.getDescription());
            updatedEntity.setNotes(request.getNotes());
            updatedEntity.setTagList(request.getTagList());
            updatedEntity.setExtra(request.getExtra());
            // 
            CustomerEntity savedEntity = doSave(updatedEntity);
            if (savedEntity == null) {
                throw new RuntimeException("更新客户失败");
            }
            return convertToResponse(savedEntity);
        }
        return null;
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
                latestEntity.setNickname(entity.getNickname());
                latestEntity.setEmail(entity.getEmail());
                latestEntity.setMobile(entity.getMobile());
                latestEntity.setNotes(entity.getNotes());
                latestEntity.setTagList(entity.getTagList());
                latestEntity.setExtra(entity.getExtra());
                latestEntity.setVisitorUid(entity.getVisitorUid());
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
        Optional<CustomerEntity> entity = findByUid(uid);
        if (entity.isPresent()) {
            entity.get().setDeleted(true);
            doSave(entity.get());
        }
    }

    @Override
    public void delete(CustomerRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public CustomerResponse convertToResponse(CustomerEntity entity) {
        return modelMapper.map(entity, CustomerResponse.class);
    }

    @Override
    public CustomerExcel convertToExcel(CustomerEntity entity) {
        return modelMapper.map(entity, CustomerExcel.class);
    }
    
}
