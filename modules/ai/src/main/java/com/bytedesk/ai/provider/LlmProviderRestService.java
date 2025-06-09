/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:49:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:21:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.provider.LlmProviderJsonLoader.ProviderJson;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.base.LlmProviderConfigDefault;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.LlmConfigUtils;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;

@Service
@AllArgsConstructor
public class LlmProviderRestService extends BaseRestService<LlmProviderEntity, LlmProviderRequest, LlmProviderResponse> {

    private final LlmProviderRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final Environment environment;

    @Override
    public Page<LlmProviderResponse> queryByOrg(LlmProviderRequest request) {
        Pageable pageable = request.getPageable();
        Specification<LlmProviderEntity> specification = LlmProviderSpecification.search(request);
        Page<LlmProviderEntity> page = repository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<LlmProviderResponse> queryByUser(LlmProviderRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "provider", key = "#uid", unless = "#result == null")
    @Override
    public Optional<LlmProviderEntity> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    @Cacheable(value = "provider", key = "#name", unless = "#result == null")
    public Optional<LlmProviderEntity> findByNameAndOrgUid(String name, String orgUid) {
        return repository.findByNameAndLevelAndOrgUidAndDeletedFalse(name, LevelEnum.ORGANIZATION.name(), orgUid);
    }

    @Cacheable(value = "provider", key = "#name + '-' + #level", unless = "#result == null")
    public List<LlmProviderEntity> findByName(String name, String level) {
        return repository.findByNameAndLevelAndDeletedFalse(name, level);
    }

    public List<LlmProviderEntity> findByStatusAndLevel(String status, String level) {
        return repository.findByStatusAndLevelAndDeletedFalse(status, level);
    }

    public Boolean existsByNameAndLevel(String name, String level) {
        return repository.existsByNameAndLevelAndDeletedFalse(name, level);
    }

    public Boolean existsByNameAndLevelAndStatus(String name, String level, String status) {
        return repository.existsByNameAndLevelAndStatusAndDeletedFalse(name, level, status);
    }

    public Boolean existsByNameAndLevelAndOrgUid(String name, String level, String orgUid) {
        return repository.existsByNameAndLevelAndOrgUidAndDeletedFalse(name, level, orgUid);
    }

    @Override
    public LlmProviderResponse create(LlmProviderRequest request) {
        // 
        if (existsByNameAndLevelAndOrgUid(request.getName(), request.getLevel(), request.getOrgUid())) {
            Optional<LlmProviderEntity> optional = repository.findByNameAndLevelAndOrgUidAndDeletedFalse(request.getName(), request.getLevel(), request.getOrgUid());
            if (optional.isPresent()) {
                return convertToResponse(optional.get());
            }
        }

        LlmProviderEntity entity = modelMapper.map(request, LlmProviderEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        LlmProviderEntity savedProvider = save(entity);
        if (savedProvider == null) {
            throw new RuntimeException("create provider failed");
        }
        return convertToResponse(savedProvider);
    }

    public LlmProviderResponse createFromProviderJson(String providerName, ProviderJson providerJson, String level, String orgUid) {

        LlmProviderRequest request = LlmProviderRequest.builder()
                .name(providerName)
                .nickname(providerJson.getNickname())
                .logo(AvatarConsts.getLlmThreadDefaultAvatarBaseUrl() + providerJson.getLogo())
                .apiUrl(providerJson.getApiUrl())
                .webUrl(providerJson.getWebUrl())
                .status(providerJson.getStatus())
                .orgUid(orgUid)
                .level(level)
                .build();

        return create(request);
    }

    @Override
    public LlmProviderResponse update(LlmProviderRequest request) {
        Optional<LlmProviderEntity> optional = repository.findByUid(request.getUid());
        if (optional.isPresent()) {
            LlmProviderEntity entity = optional.get();
            modelMapper.map(request, entity);
            // entity.setName(request.getName());
            // entity.setNickname(request.getNickname());
            // entity.setLogo(request.getLogo());
            // entity.setApiUrl(request.getApiUrl());
            // entity.setWebUrl(request.getWebUrl());
            // 
            LlmProviderEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("update provider failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("provider not found");
        }
    }

    @Override
    public LlmProviderEntity save(LlmProviderEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected LlmProviderEntity doSave(LlmProviderEntity entity) {
        return repository.save(entity);
    }

    @Override
    public LlmProviderEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LlmProviderEntity entity) {
        try {
            Optional<LlmProviderEntity> latest = repository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                LlmProviderEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 根据业务需求合并实体
                return repository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<LlmProviderEntity> optional = repository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // repository.delete(optional.get());
        } else {
            throw new RuntimeException("provider not found");
        }
    }

    @Override
    public void delete(LlmProviderRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public LlmProviderResponse convertToResponse(LlmProviderEntity entity) {
        return modelMapper.map(entity, LlmProviderResponse.class);
    }

    @Override
    public LlmProviderResponse queryByUid(LlmProviderRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    public LlmProviderConfigDefault getLlmProviderConfigDefault() {
        return LlmConfigUtils.getLlmProviderConfigDefault(environment);
    }

}
