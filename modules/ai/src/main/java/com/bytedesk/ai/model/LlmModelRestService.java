/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:19:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:41:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Description;

import com.bytedesk.ai.model.LlmModelJsonLoader.ModelJson;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Description("LLM Model Service - Large Language Model management and configuration service")
public class LlmModelRestService extends BaseRestService<LlmModelEntity, LlmModelRequest, LlmModelResponse> {

    private final LlmModelRepository llmModelRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<LlmModelEntity> createSpecification(LlmModelRequest request) {
        return LlmModelSpecification.search(request, authService);
    }

    @Override
    protected Page<LlmModelEntity> executePageQuery(Specification<LlmModelEntity> spec, Pageable pageable) {
        return llmModelRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<LlmModelEntity> findByUid(String uid) {
        return llmModelRepository.findByUid(uid);
    }
    
    public List<LlmModelEntity> findByProviderUid(String providerUid) {
        return llmModelRepository.findByProviderUid(providerUid);
    }

    // public List<LlmModelEntity> findByProviderNameAndOrgUid(String providerName, String orgUid) {
    //     return llmModelRepository.findByProviderNameAndOrgUidAndDeletedFalse(providerName, orgUid);
    // }

    public Boolean existsByNameAndProviderUid(String name, String providerUid) {
        return llmModelRepository.existsByNameAndProviderUid(name, providerUid);
    }

    @Override
    public LlmModelResponse create(LlmModelRequest request) {

        if (existsByNameAndProviderUid(request.getName(), request.getProviderUid())) {
            Optional<LlmModelEntity> optional = llmModelRepository.findByNameAndProviderUid(request.getName(), request.getProviderUid());
            if (optional.isPresent()) {
                return convertToResponse(optional.get());
            }
        }
        //
        LlmModelEntity entity = modelMapper.map(request, LlmModelEntity.class);
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        } else {
            entity.setUid(uidUtils.getUid());
        }
        UserEntity user = authService.getCurrentUser();
        if (user != null) {
            entity.setUserUid(user.getUid());
        }
        //
        LlmModelEntity savedModel = save(entity);
        if (savedModel == null) {
            throw new RuntimeException("Create LlmModel failed");
        }
        return convertToResponse(savedModel);
    }

    public LlmModelResponse createFromModelJson(String providerUid, String providerName, ModelJson modelJson, String level, String orgUid) {

        LlmModelRequest request = LlmModelRequest.builder()
                .providerUid(providerUid)
                // .providerName(providerName)
                .name(modelJson.getName())
                .nickname(modelJson.getNickname())
                .description(modelJson.getDescription())
                .type(modelJson.getType())
                .enabled(true)
                .level(level)
                .orgUid(orgUid)
                .build();

        return create(request);
    }

    @Override
    public LlmModelResponse update(LlmModelRequest request) {
        Optional<LlmModelEntity> optional = llmModelRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            LlmModelEntity entity = optional.get();
            modelMapper.map(request, entity);
            // 
            LlmModelEntity savedModel = save(entity);
            if (savedModel == null) {
                throw new RuntimeException("Update LlmModel failed");
            }
            return convertToResponse(savedModel);
        } else {
            throw new RuntimeException("LlmModel not found");
        }
    }

    @Override
    public LlmModelEntity save(LlmModelEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected LlmModelEntity doSave(LlmModelEntity entity) {
        return llmModelRepository.save(entity);
    }

    @Override
    public LlmModelEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LlmModelEntity entity) {
        try {
            Optional<LlmModelEntity> latest = llmModelRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                LlmModelEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 根据业务需求合并实体
                return llmModelRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<LlmModelEntity> optional = llmModelRepository.findByUid(uid);
        if (optional.isPresent()) {
            LlmModelEntity entity = optional.get();
            entity.setDeleted(true);
            save(entity);
            // repository.delete(entity);
        }
        else {
            throw new RuntimeException("LlmModel not found");
        }
    }

    @Override
    public void delete(LlmModelRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public LlmModelResponse convertToResponse(LlmModelEntity entity) {
        return modelMapper.map(entity, LlmModelResponse.class);
    }

    

    

}
