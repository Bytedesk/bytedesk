/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:25:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention;

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
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class IntentionSettingsRestService extends BaseRestServiceWithExport<IntentionSettingsEntity, IntentionSettingsRequest, IntentionSettingsResponse, IntentionSettingsExcel> {

    private final IntentionSettingsRepository intentionSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<IntentionSettingsEntity> createSpecification(IntentionSettingsRequest request) {
        return IntentionSettingsSpecification.search(request);
    }

    @Override
    protected Page<IntentionSettingsEntity> executePageQuery(Specification<IntentionSettingsEntity> spec, Pageable pageable) {
        return intentionSettingsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "intentionSettings", key = "#uid", unless="#result==null")
    @Override
    public Optional<IntentionSettingsEntity> findByUid(String uid) {
        return intentionSettingsRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return intentionSettingsRepository.existsByUid(uid);
    }

    @Override
    public IntentionSettingsResponse create(IntentionSettingsRequest request) {
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
        IntentionSettingsEntity entity = modelMapper.map(request, IntentionSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        IntentionSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create intentionSettings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public IntentionSettingsResponse update(IntentionSettingsRequest request) {
        Optional<IntentionSettingsEntity> optional = intentionSettingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            IntentionSettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            IntentionSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update intentionSettings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("IntentionSettings not found");
        }
    }

    @Override
    protected IntentionSettingsEntity doSave(IntentionSettingsEntity entity) {
        return intentionSettingsRepository.save(entity);
    }

    @Override
    public IntentionSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, IntentionSettingsEntity entity) {
        try {
            Optional<IntentionSettingsEntity> latest = intentionSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                IntentionSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                return intentionSettingsRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<IntentionSettingsEntity> optional = intentionSettingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // intentionSettingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("IntentionSettings not found");
        }
    }

    @Override
    public void delete(IntentionSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public IntentionSettingsResponse convertToResponse(IntentionSettingsEntity entity) {
        return modelMapper.map(entity, IntentionSettingsResponse.class);
    }

    @Override
    public IntentionSettingsExcel convertToExcel(IntentionSettingsEntity entity) {
        return modelMapper.map(entity, IntentionSettingsExcel.class);
    }
    
    
}
