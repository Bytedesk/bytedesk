/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-22 11:19:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization_apply;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OrganizationApplyRestService extends BaseRestServiceWithExcel<OrganizationApplyEntity, OrganizationApplyRequest, OrganizationApplyResponse, OrganizationApplyExcel> {

    private final OrganizationApplyRepository organizationRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<OrganizationApplyEntity> queryByOrgEntity(OrganizationApplyRequest request) {
        Pageable pageable = request.getPageable();
        Specification<OrganizationApplyEntity> spec = OrganizationApplySpecification.search(request);
        return organizationRepository.findAll(spec, pageable);
    }

    @Override
    public Page<OrganizationApplyResponse> queryByOrg(OrganizationApplyRequest request) {
        Page<OrganizationApplyEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<OrganizationApplyResponse> queryByUser(OrganizationApplyRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public OrganizationApplyResponse queryByUid(OrganizationApplyRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<OrganizationApplyEntity> findByUid(String uid) {
        return organizationRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return organizationRepository.existsByUid(uid);
    }

    @Override
    public OrganizationApplyResponse create(OrganizationApplyRequest request) {
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
        OrganizationApplyEntity entity = modelMapper.map(request, OrganizationApplyEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        OrganizationApplyEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public OrganizationApplyResponse update(OrganizationApplyRequest request) {
        Optional<OrganizationApplyEntity> optional = organizationRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            OrganizationApplyEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            OrganizationApplyEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("OrganizationApply not found");
        }
    }

    @Override
    protected OrganizationApplyEntity doSave(OrganizationApplyEntity entity) {
        return organizationRepository.save(entity);
    }

    @Override
    public OrganizationApplyEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, OrganizationApplyEntity entity) {
        try {
            Optional<OrganizationApplyEntity> latest = organizationRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                OrganizationApplyEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return organizationRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<OrganizationApplyEntity> optional = organizationRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("OrganizationApply not found");
        }
    }

    @Override
    public void delete(OrganizationApplyRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public OrganizationApplyResponse convertToResponse(OrganizationApplyEntity entity) {
        return modelMapper.map(entity, OrganizationApplyResponse.class);
    }

    @Override
    public OrganizationApplyExcel convertToExcel(OrganizationApplyEntity entity) {
        return modelMapper.map(entity, OrganizationApplyExcel.class);
    }
    
    
}
