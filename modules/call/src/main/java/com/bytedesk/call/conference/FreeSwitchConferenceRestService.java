/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
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
public class FreeSwitchConferenceRestService extends BaseRestServiceWithExcel<FreeSwitchConferenceEntity, FreeSwitchConferenceRequest, FreeSwitchConferenceResponse, FreeSwitchConferenceExcel> {

    private final FreeSwitchConferenceRepository freeSwitchConferenceRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FreeSwitchConferenceResponse> queryByOrg(FreeSwitchConferenceRequest request) {
        return queryByOrgEntity(request).map(this::convertToResponse);
    }

    @Override
    public Page<FreeSwitchConferenceResponse> queryByUser(FreeSwitchConferenceRequest request) {
        return queryByUserEntity(request).map(this::convertToResponse);
    }

    @Override
    public Page<FreeSwitchConferenceEntity> queryByOrgEntity(FreeSwitchConferenceRequest request) {

        Pageable pageable = request.getPageable();
        Specification<FreeSwitchConferenceEntity> specification = FreeSwitchConferenceSpecification.build(request);

        return freeSwitchConferenceRepository.findAll(specification, pageable);
    }

    public Page<FreeSwitchConferenceEntity> queryByUserEntity(FreeSwitchConferenceRequest request) {

        UserEntity user = authService.getUser();
        request.setOrgUid(user.getOrgUid());

        return queryByOrgEntity(request);
    }

    @Override
    public Optional<FreeSwitchConferenceEntity> findByUid(String uid) {
        return freeSwitchConferenceRepository.findByUid(uid);
    }

    @Override
    public FreeSwitchConferenceResponse convertToResponse(FreeSwitchConferenceEntity entity) {
        return modelMapper.map(entity, FreeSwitchConferenceResponse.class);
    }

    public FreeSwitchConferenceEntity convertToEntity(FreeSwitchConferenceRequest request) {
        return modelMapper.map(request, FreeSwitchConferenceEntity.class);
    }

    @Override
    public FreeSwitchConferenceExcel convertToExcel(FreeSwitchConferenceEntity entity) {
        return modelMapper.map(entity, FreeSwitchConferenceExcel.class);
    }

    @Override
    public FreeSwitchConferenceResponse create(FreeSwitchConferenceRequest request) {

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

        FreeSwitchConferenceEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        FreeSwitchConferenceEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建FreeSwitch会议室失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public FreeSwitchConferenceResponse update(FreeSwitchConferenceRequest request) {

        Optional<FreeSwitchConferenceEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("FreeSwitch会议室不存在");
        }

        FreeSwitchConferenceEntity entity = optional.get();
        
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

        FreeSwitchConferenceEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新FreeSwitch会议室失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public FreeSwitchConferenceEntity save(FreeSwitchConferenceEntity entity) {
        try {
            return freeSwitchConferenceRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public FreeSwitchConferenceEntity doSave(FreeSwitchConferenceEntity entity) {
        return freeSwitchConferenceRepository.save(entity);
    }

    @Override
    public void delete(FreeSwitchConferenceRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FreeSwitchConferenceEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FreeSwitchConferenceEntity entity) {
        log.warn("FreeSwitch会议室保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        // 重新查询最新版本并重试
        try {
            Optional<FreeSwitchConferenceEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                FreeSwitchConferenceEntity latestEntity = latest.get();
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
        Optional<FreeSwitchConferenceEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    @Cacheable(value = "freeswitch_conference", key = "#uid", unless = "#result == null")
    public FreeSwitchConferenceResponse queryByUid(FreeSwitchConferenceRequest request) {
        Optional<FreeSwitchConferenceEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("FreeSwitch会议室不存在");
    }

}
