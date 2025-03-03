/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 14:51:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.unified;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UnifiedRestService extends BaseRestService<UnifiedEntity, UnifiedRequest, UnifiedResponse> {

    private final UnifiedRepository unifiedRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final WorkgroupRestService workgroupRestService;

    @Override
    public Page<UnifiedResponse> queryByOrg(UnifiedRequest request) {
        Pageable pageable = request.getPageable();
        Specification<UnifiedEntity> spec = UnifiedSpecification.search(request);
        Page<UnifiedEntity> page = unifiedRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<UnifiedResponse> queryByUser(UnifiedRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "unified", key = "#uid", unless="#result==null")
    @Override
    public Optional<UnifiedEntity> findByUid(String uid) {
        return unifiedRepository.findByUid(uid);
    }

    @Override
    public UnifiedResponse create(UnifiedRequest request) {
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
            request.setOrgUid(user.getOrgUid());
        }
        // 
        UnifiedEntity entity = modelMapper.map(request, UnifiedEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        if (request.getWorkgroupUids() != null) {
            for (String workgroupUid : request.getWorkgroupUids()) {
                Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
                if (workgroupOptional.isPresent()) {
                    entity.getWorkgroups().add(workgroupOptional.get());
                }
            }
        }
        // 
        UnifiedEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create unified failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public UnifiedResponse update(UnifiedRequest request) {
        Optional<UnifiedEntity> optional = unifiedRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            UnifiedEntity entity = optional.get();
            modelMapper.map(request, entity);

            // 更新技能组, 首先清理原先的
            entity.getWorkgroups().clear();
            if (request.getWorkgroupUids() != null) {
                for (String workgroupUid : request.getWorkgroupUids()) {
                    Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
                    if (workgroupOptional.isPresent()) {
                        entity.getWorkgroups().add(workgroupOptional.get());
                    }
                }
            }

            //
            UnifiedEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update unified failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Unified not found");
        }
    }

    @Override
    public UnifiedEntity save(UnifiedEntity entity) {
        try {
            return unifiedRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<UnifiedEntity> optional = unifiedRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // unifiedRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Unified not found");
        }
    }

    @Override
    public void delete(UnifiedRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, UnifiedEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public UnifiedResponse convertToResponse(UnifiedEntity entity) {
        return modelMapper.map(entity, UnifiedResponse.class);
    }
    
}
