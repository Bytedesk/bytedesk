/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:49:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-13 10:27:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.provider.LlmProviderJsonService.ProviderJson;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LlmProviderRestService extends BaseRestService<LlmProviderEntity, LlmProviderRequest, LlmProviderResponse> {

    private final LlmProviderRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<LlmProviderResponse> queryByOrg(LlmProviderRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "createdAt");
        Specification<LlmProviderEntity> specification = LlmProviderSpecification.search(request);
        Page<LlmProviderEntity> page = repository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<LlmProviderResponse> queryByUser(LlmProviderRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<LlmProviderEntity> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    public Optional<LlmProviderEntity> findByName(String name) {
        return repository.findByNameAndLevel(name, LevelEnum.PLATFORM.name());
    }

    public Boolean existsByNameAndLevel(String name) {
        return repository.existsByNameAndLevel(name, LevelEnum.PLATFORM.name());
    }

    public Boolean existsByNameAndLevelAndOrgUid(String name, String level, String orgUid) {
        return repository.existsByNameAndLevelAndOrgUidAndDeletedFalse(name, level, orgUid);
    }

    @Override
    public LlmProviderResponse create(LlmProviderRequest request) {
        // 
        if (existsByNameAndLevelAndOrgUid(request.getName(), request.getLevel(), request.getOrgUid())) {
            throw new RuntimeException("Provider already exists");
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

    public LlmProviderResponse createFromProviderJson(String providerName, ProviderJson providerJson) {

        LlmProviderRequest request = LlmProviderRequest.builder()
                .name(providerName)
                .nickname(providerJson.getNickname())
                .logo(AvatarConsts.LLM_THREAD_DEFAULT_AVATAR_BASE_URL + providerJson.getLogo())
                .apiUrl(providerJson.getApiUrl())
                .webUrl(providerJson.getWebUrl())
                // .apiKeyUrl(providerJson.getWebsites().getApiKeyUrl())
                // .docsUrl(providerJson.getWebsites().getDocsUrl())
                // .modelsUrl(providerJson.getWebsites().getModelsUrl())
                .status(providerJson.getStatus())
                .build();
        request.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        request.setLevel(LevelEnum.PLATFORM.name());

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
            return repository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LlmProviderEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public LlmProviderResponse convertToResponse(LlmProviderEntity entity) {
        return modelMapper.map(entity, LlmProviderResponse.class);
    }

}
