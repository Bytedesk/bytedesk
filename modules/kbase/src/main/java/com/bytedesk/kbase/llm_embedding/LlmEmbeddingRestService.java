/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_embedding;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class LlmEmbeddingRestService extends BaseRestServiceWithExport<LlmEmbeddingEntity, LlmEmbeddingRequest, LlmEmbeddingResponse, LlmEmbeddingExcel> {

    private final LlmEmbeddingRepository llm_embeddingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<LlmEmbeddingEntity> queryByOrgEntity(LlmEmbeddingRequest request) {
        Pageable pageable = request.getPageable();
        Specification<LlmEmbeddingEntity> specs = LlmEmbeddingSpecification.search(request, authService);
        return llm_embeddingRepository.findAll(specs, pageable);
    }

    @Override
    public Page<LlmEmbeddingResponse> queryByOrg(LlmEmbeddingRequest request) {
        Page<LlmEmbeddingEntity> llm_embeddingPage = queryByOrgEntity(request);
        return llm_embeddingPage.map(this::convertToResponse);
    }

    @Override
    public Page<LlmEmbeddingResponse> queryByUser(LlmEmbeddingRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "llm_embedding", key = "#uid", unless="#result==null")
    @Override
    public Optional<LlmEmbeddingEntity> findByUid(String uid) {
        return llm_embeddingRepository.findByUid(uid);
    }

    @Cacheable(value = "llm_embedding", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<LlmEmbeddingEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return llm_embeddingRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return llm_embeddingRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public LlmEmbeddingResponse create(LlmEmbeddingRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public LlmEmbeddingResponse createSystemLlmEmbedding(LlmEmbeddingRequest request) {
        return createInternal(request, true);
    }

    private LlmEmbeddingResponse createInternal(LlmEmbeddingRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<LlmEmbeddingEntity> llm_embedding = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (llm_embedding.isPresent()) {
                return convertToResponse(llm_embedding.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(LlmEmbeddingPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        LlmEmbeddingEntity entity = modelMapper.map(request, LlmEmbeddingEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        LlmEmbeddingEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public LlmEmbeddingResponse update(LlmEmbeddingRequest request) {
        Optional<LlmEmbeddingEntity> optional = llm_embeddingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            LlmEmbeddingEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(LlmEmbeddingPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            LlmEmbeddingEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected LlmEmbeddingEntity doSave(LlmEmbeddingEntity entity) {
        return llm_embeddingRepository.save(entity);
    }

    @Override
    public LlmEmbeddingEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LlmEmbeddingEntity entity) {
        try {
            Optional<LlmEmbeddingEntity> latest = llm_embeddingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                LlmEmbeddingEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return llm_embeddingRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<LlmEmbeddingEntity> optional = llm_embeddingRepository.findByUid(uid);
        if (optional.isPresent()) {
            LlmEmbeddingEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(LlmEmbeddingPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(LlmEmbeddingRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public LlmEmbeddingResponse convertToResponse(LlmEmbeddingEntity entity) {
        return modelMapper.map(entity, LlmEmbeddingResponse.class);
    }

    @Override
    public LlmEmbeddingExcel convertToExcel(LlmEmbeddingEntity entity) {
        return modelMapper.map(entity, LlmEmbeddingExcel.class);
    }

    @Override
    protected Specification<LlmEmbeddingEntity> createSpecification(LlmEmbeddingRequest request) {
        return LlmEmbeddingSpecification.search(request, authService);
    }

    @Override
    protected Page<LlmEmbeddingEntity> executePageQuery(Specification<LlmEmbeddingEntity> spec, Pageable pageable) {
        return llm_embeddingRepository.findAll(spec, pageable);
    }

    /**
     * 向量化服务调用：记录向量化结果
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void recordEmbedding(String sourceType, String sourceUid, String orgUid,
                                 String provider, String model, Integer dimensions,
                                 String content, String status, String errorMessage, Long costMs) {
        try {
            String name = sourceType + "_" + sourceUid;
            String type = resolveType(sourceType);
            LlmEmbeddingEntity entity = LlmEmbeddingEntity.builder()
                    .uid(uidUtils.getUid())
                    .name(name)
                    .description(sourceType + " vectorization record")
                    .type(type)
                    .sourceType(sourceType)
                    .sourceUid(sourceUid)
                    .orgUid(orgUid)
                    .provider(provider)
                    .model(model)
                    .dimensions(dimensions)
                    .content(content != null && content.length() > 500 ? content.substring(0, 500) : content)
                    .status(status)
                    .errorMessage(errorMessage)
                    .costMs(costMs)
                    .build();
            save(entity);
        } catch (Exception e) {
            log.warn("Failed to record embedding history: sourceType={}, sourceUid={}, error={}",
                    sourceType, sourceUid, e.getMessage());
        }
    }
    
    private String resolveType(String sourceType) {
        if ("FAQ".equalsIgnoreCase(sourceType)) return LlmEmbeddingTypeEnum.FAQ.name();
        if ("CHUNK".equalsIgnoreCase(sourceType)) return LlmEmbeddingTypeEnum.CHUNK.name();
        if ("TEXT".equalsIgnoreCase(sourceType)) return LlmEmbeddingTypeEnum.TEXT.name();
        if ("WEBPAGE".equalsIgnoreCase(sourceType)) return LlmEmbeddingTypeEnum.WEBPAGE.name();
        return LlmEmbeddingTypeEnum.KBASE.name();
    }

    public void initLlmEmbeddings(String orgUid) {
        // log.info("initLlmEmbeddingLlmEmbedding");
        // for (String llm_embedding : LlmEmbeddingInitData.getAllLlmEmbeddings()) {
        //     LlmEmbeddingRequest llm_embeddingRequest = LlmEmbeddingRequest.builder()
        //             .uid(Utils.formatUid(orgUid, llm_embedding))
        //             .name(llm_embedding)
        //             .order(0)
        //             .type(LlmEmbeddingTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemLlmEmbedding(llm_embeddingRequest);
        // }
    }

    
    
}
