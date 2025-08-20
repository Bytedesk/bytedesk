/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:52:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.conference;

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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CallConferenceRestService extends BaseRestServiceWithExcel<CallConferenceEntity, CallConferenceRequest, CallConferenceResponse, CallConferenceExcel> {

    private final CallConferenceRepository freeSwitchConferenceRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    public Page<CallConferenceEntity> queryByUserEntity(CallConferenceRequest request) {

        UserEntity user = authService.getUser();
        request.setOrgUid(user.getOrgUid());

        return queryByOrgEntity(request);
    }

    @Override
    public Optional<CallConferenceEntity> findByUid(String uid) {
        return freeSwitchConferenceRepository.findByUid(uid);
    }

    @Override
    public CallConferenceResponse convertToResponse(CallConferenceEntity entity) {
        return modelMapper.map(entity, CallConferenceResponse.class);
    }

    public CallConferenceEntity convertToEntity(CallConferenceRequest request) {
        return modelMapper.map(request, CallConferenceEntity.class);
    }

    @Override
    public CallConferenceExcel convertToExcel(CallConferenceEntity entity) {
        return modelMapper.map(entity, CallConferenceExcel.class);
    }

    @Override
    public CallConferenceResponse create(CallConferenceRequest request) {

        UserEntity user = authService.getUser();
        if (StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        
        if (StringUtils.hasText(request.getUid())) {
            request.setUid(uidUtils.getCacheSerialUid());
        }

        // 检查会议室名称是否已存在
        if (freeSwitchConferenceRepository.existsByConferenceName(request.getConferenceName())) {
            throw new RuntimeException("会议室名称已存在: " + request.getConferenceName());
        }

        CallConferenceEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        CallConferenceEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建Call会议室失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public CallConferenceResponse update(CallConferenceRequest request) {

        Optional<CallConferenceEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("Call会议室不存在");
        }

        CallConferenceEntity entity = optional.get();
        
        // 更新字段
        if (StringUtils.hasText(request.getDescription())) {
            entity.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getPassword())) {
            entity.setPassword(request.getPassword());
        }
        if (request.getMaxMembers() != null) {
            entity.setMaxMembers(request.getMaxMembers());
        }
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }
        if (request.getRecordEnabled() != null) {
            entity.setRecordEnabled(request.getRecordEnabled());
        }

        CallConferenceEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新Call会议室失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public CallConferenceEntity save(CallConferenceEntity entity) {
        try {
            return freeSwitchConferenceRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public CallConferenceEntity doSave(CallConferenceEntity entity) {
        return freeSwitchConferenceRepository.save(entity);
    }

    @Override
    public void delete(CallConferenceRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CallConferenceEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallConferenceEntity entity) {
        log.warn("Call会议室保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        // 重新查询最新版本并重试
        try {
            Optional<CallConferenceEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallConferenceEntity latestEntity = latest.get();
                // 将当前修改应用到最新版本
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setPassword(entity.getPassword());
                latestEntity.setMaxMembers(entity.getMaxMembers());
                latestEntity.setEnabled(entity.getEnabled());
                latestEntity.setRecordEnabled(entity.getRecordEnabled());
                return doSave(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<CallConferenceEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    @Cacheable(value = "freeswitch_conference", key = "#uid", unless = "#result == null")
    public CallConferenceResponse queryByUid(CallConferenceRequest request) {
        Optional<CallConferenceEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("Call会议室不存在");
    }

    @Override
    protected Specification<CallConferenceEntity> createSpecification(CallConferenceRequest request) {
        return CallConferenceSpecification.build(request);
    }

    @Override
    protected Page<CallConferenceEntity> executePageQuery(Specification<CallConferenceEntity> specification, Pageable pageable) {
        return freeSwitchConferenceRepository.findAll(specification, pageable);
    }

}
