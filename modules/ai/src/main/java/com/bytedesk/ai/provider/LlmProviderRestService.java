/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:49:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 11:13:27
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
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Description;

import com.bytedesk.ai.provider.LlmProviderJsonLoader.ProviderJson;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.llm.LlmProviderConfigDefault;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.llm.LlmConfigUtils;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;

@Service
@AllArgsConstructor
@Description("LLM Provider Service - Large Language Model provider management and configuration service")
public class LlmProviderRestService extends BaseRestService<LlmProviderEntity, LlmProviderRequest, LlmProviderResponse> {

    private final LlmProviderRepository llmProviderRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final Environment environment;

    @Override
    protected Specification<LlmProviderEntity> createSpecification(LlmProviderRequest request) {
        return LlmProviderSpecification.search(request, authService);
    }

    @Override
    protected Page<LlmProviderEntity> executePageQuery(Specification<LlmProviderEntity> spec, Pageable pageable) {
        return llmProviderRepository.findAll(spec, pageable);
    }

    // @Cacheable(value = "provider", key = "#uid", unless = "#result == null || #result.isEmpty()")
    @Override
    public Optional<LlmProviderEntity> findByUid(String uid) {
        return llmProviderRepository.findByUid(uid);
    }

    // @Cacheable(value = "provider", key = "#type + '-' + #orgUid", unless = "#result == null || #result.isEmpty()")
    public Optional<LlmProviderEntity> findByTypeAndOrgUid(String type, String orgUid) {
        return llmProviderRepository.findByTypeAndLevelAndOrgUidAndDeletedFalse(type, LevelEnum.ORGANIZATION.name(), orgUid);
    }

    // @Cacheable(value = "provider", key = "#type + '-' + #level", unless = "#result == null || #result.isEmpty()")
    public List<LlmProviderEntity> findByType(String type, String level) {
        return llmProviderRepository.findByTypeAndLevelAndDeletedFalse(type, level);
    }

    public List<LlmProviderEntity> findByStatusAndLevel(String status, String level) {
        return llmProviderRepository.findByStatusAndLevelAndDeletedFalse(status, level);
    }

    public Boolean existsByTypeAndLevel(String type, String level) {
        return llmProviderRepository.existsByTypeAndLevelAndDeletedFalse(type, level);
    }

    public Boolean existsByTypeAndLevelAndStatus(String type, String level, String status) {
        return llmProviderRepository.existsByTypeAndLevelAndStatusAndDeletedFalse(type, level, status);
    }

    public Boolean existsByTypeAndLevelAndOrgUid(String type, String level, String orgUid) {
        return llmProviderRepository.existsByTypeAndLevelAndOrgUidAndDeletedFalse(type, level, orgUid);
    }

    // @CacheEvict(value = "provider", allEntries = true, beforeInvocation = true)
    @Override
    public LlmProviderResponse create(LlmProviderRequest request) {
        // 
        LlmProviderEntity entity = modelMapper.map(request, LlmProviderEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        LlmProviderEntity savedProvider = save(entity);
        if (savedProvider == null) {
            throw new RuntimeException("create provider failed");
        }
        return convertToResponse(savedProvider);
    }

    public LlmProviderResponse createFromProviderJson(String providerType, ProviderJson providerJson, String level, String orgUid) {

        LlmProviderRequest request = LlmProviderRequest.builder()
                // .name(providerType)
                .nickname(providerJson.getNickname())
                .type(providerType)
                .logo(AvatarConsts.getLlmThreadDefaultAvatarBaseUrl() + providerJson.getLogo())
                .baseUrl(providerJson.getBaseUrl())
                .webUrl(providerJson.getWebUrl())
                .status(providerJson.getStatus())
                .orgUid(orgUid)
                .level(level)
                .build();

        return create(request);
    }

    // @CacheEvict(value = "provider", allEntries = true, beforeInvocation = true)
    @Override
    public LlmProviderResponse update(LlmProviderRequest request) {
        Optional<LlmProviderEntity> optional = llmProviderRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            LlmProviderEntity entity = optional.get();
            modelMapper.map(request, entity);
            // 
            // entity.setType(request.getType());
            // entity.setNickname(request.getNickname());
            // entity.setDescription(request.getDescription());
            // entity.setLogo(request.getLogo());
            // // 
            // entity.setApiUrl(request.getApiUrl());
            // entity.setApiKey(request.getApiKey());
            // entity.setWebUrl(request.getWebUrl());
            // entity.setStatus(request.getStatus());
            // // 
            // entity.setEnabled(request.getEnabled());
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
        return llmProviderRepository.save(entity);
    }

    @Override
    public LlmProviderEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LlmProviderEntity entity) {
        try {
            Optional<LlmProviderEntity> latest = llmProviderRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                LlmProviderEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 根据业务需求合并实体
                return llmProviderRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    // @CacheEvict(value = "provider", allEntries = true, beforeInvocation = true)
    @Override
    public void deleteByUid(String uid) {
        Optional<LlmProviderEntity> optional = llmProviderRepository.findByUid(uid);
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

    public LlmProviderConfigDefault getLlmProviderConfigDefault() {
        return LlmConfigUtils.getLlmProviderConfigDefault(environment);
    }

    
}
