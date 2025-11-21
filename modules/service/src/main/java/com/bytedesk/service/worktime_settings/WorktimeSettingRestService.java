/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:02:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime_settings;

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
public class WorktimeSettingRestService extends BaseRestServiceWithExport<WorktimeSettingEntity, WorktimeSettingRequest, WorktimeSettingResponse, WorktimeSettingExcel> {

    private final WorktimeSettingRepository tagRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorktimeSettingEntity> findByUid(String uid) {
        return tagRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return tagRepository.existsByUid(uid);
    }

    @Override
    public WorktimeSettingResponse create(WorktimeSettingRequest request) {
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
        WorktimeSettingEntity entity = modelMapper.map(request, WorktimeSettingEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WorktimeSettingEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public WorktimeSettingResponse update(WorktimeSettingRequest request) {
        Optional<WorktimeSettingEntity> optional = tagRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorktimeSettingEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WorktimeSettingEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WorktimeSetting not found");
        }
    }

    @Override
    protected WorktimeSettingEntity doSave(WorktimeSettingEntity entity) {
        return tagRepository.save(entity);
    }

    @Override
    public WorktimeSettingEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorktimeSettingEntity entity) {
        try {
            Optional<WorktimeSettingEntity> latest = tagRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorktimeSettingEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tagRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WorktimeSettingEntity> optional = tagRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WorktimeSetting not found");
        }
    }

    @Override
    public void delete(WorktimeSettingRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorktimeSettingResponse convertToResponse(WorktimeSettingEntity entity) {
        return modelMapper.map(entity, WorktimeSettingResponse.class);
    }

    @Override
    public WorktimeSettingExcel convertToExcel(WorktimeSettingEntity entity) {
        return modelMapper.map(entity, WorktimeSettingExcel.class);
    }

    @Override
    protected Specification<WorktimeSettingEntity> createSpecification(WorktimeSettingRequest request) {
        return WorktimeSettingSpecification.search(request, authService);
    }

    @Override
    protected Page<WorktimeSettingEntity> executePageQuery(Specification<WorktimeSettingEntity> spec, Pageable pageable) {
        return tagRepository.findAll(spec, pageable);
    }
    
    
}
