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
package com.bytedesk.freeswitch.user;

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
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FreeSwitchUserRestService extends BaseRestServiceWithExcel<FreeSwitchUserEntity, FreeSwitchUserRequest, FreeSwitchUserResponse, FreeSwitchUserExcel> {

    private final FreeSwitchUserRepository freeSwitchUserRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FreeSwitchUserEntity> queryByOrgEntity(FreeSwitchUserRequest request) {

        Pageable pageable = Utils.getPageable(request);
        Specification<FreeSwitchUserEntity> specification = FreeSwitchUserSpecification.search(request);

        return freeSwitchUserRepository.findAll(specification, pageable);
    }

    @Override
    public Page<FreeSwitchUserEntity> queryByUserEntity(FreeSwitchUserRequest request) {

        UserEntity user = authService.getCurrentUser();
        request.setOrgUid(user.getOrgUid());

        return queryByOrgEntity(request);
    }

    @Override
    public Optional<FreeSwitchUserEntity> findByUid(String uid) {
        return freeSwitchUserRepository.findByUidAndDeleted(uid, false);
    }

    @Override
    public FreeSwitchUserResponse convertToResponse(FreeSwitchUserEntity entity) {
        return modelMapper.map(entity, FreeSwitchUserResponse.class);
    }

    @Override
    public FreeSwitchUserEntity convertToEntity(FreeSwitchUserRequest request) {
        return modelMapper.map(request, FreeSwitchUserEntity.class);
    }

    @Override
    public FreeSwitchUserExcel convertToExcel(FreeSwitchUserEntity entity) {
        return modelMapper.map(entity, FreeSwitchUserExcel.class);
    }

    @Override
    public FreeSwitchUserResponse create(FreeSwitchUserRequest request) {

        UserEntity user = authService.getCurrentUser();
        if (StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        
        if (StringUtils.hasText(request.getUid())) {
            request.setUid(uidUtils.getCacheSerialUid());
        }

        // 检查用户名是否已存在
        if (freeSwitchUserRepository.existsByUsernameAndDomain(request.getUsername(), request.getDomain())) {
            throw new RuntimeException("用户名已存在: " + request.getUsername() + "@" + request.getDomain());
        }

        FreeSwitchUserEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        FreeSwitchUserEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建FreeSwitch用户失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public FreeSwitchUserResponse update(FreeSwitchUserRequest request) {

        Optional<FreeSwitchUserEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("FreeSwitch用户不存在");
        }

        FreeSwitchUserEntity entity = optional.get();
        
        // 更新字段
        if (StringUtils.hasText(request.getDisplayName())) {
            entity.setDisplayName(request.getDisplayName());
        }
        if (StringUtils.hasText(request.getEmail())) {
            entity.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getAccountcode())) {
            entity.setAccountcode(request.getAccountcode());
        }
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }
        if (StringUtils.hasText(request.getRemarks())) {
            entity.setRemarks(request.getRemarks());
        }

        FreeSwitchUserEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新FreeSwitch用户失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public FreeSwitchUserEntity save(FreeSwitchUserEntity entity) {
        try {
            return freeSwitchUserRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FreeSwitchUserEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    @Cacheable(value = "freeswitch_user", key = "#uid", unless = "#result == null")
    public FreeSwitchUserResponse queryByUid(FreeSwitchUserRequest request) {
        Optional<FreeSwitchUserEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("FreeSwitch用户不存在");
    }

}
