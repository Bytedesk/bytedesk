/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:05:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ragflow;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RagflowRestService extends BaseRestServiceWithExport<RagflowEntity, RagflowRequest, RagflowResponse, RagflowExcel> {

    private final RagflowRepository ragflowRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<RagflowEntity> createSpecification(RagflowRequest request) {
        return RagflowSpecification.search(request, authService);
    }

    @Override
    protected Page<RagflowEntity> executePageQuery(Specification<RagflowEntity> spec, Pageable pageable) {
        return ragflowRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "ragflow", key = "#uid", unless="#result==null")
    @Override
    public Optional<RagflowEntity> findByUid(String uid) {
        return ragflowRepository.findByUid(uid);
    }

    @Cacheable(value = "ragflow", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<RagflowEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return ragflowRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return ragflowRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public RagflowResponse create(RagflowRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<RagflowEntity> ragflow = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (ragflow.isPresent()) {
                return convertToResponse(ragflow.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        RagflowEntity entity = modelMapper.map(request, RagflowEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        RagflowEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create ragflow failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public RagflowResponse update(RagflowRequest request) {
        Optional<RagflowEntity> optional = ragflowRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            RagflowEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            RagflowEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ragflow failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Ragflow not found");
        }
    }

    @Override
    protected RagflowEntity doSave(RagflowEntity entity) {
        return ragflowRepository.save(entity);
    }

    @Override
    public RagflowEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RagflowEntity entity) {
        try {
            Optional<RagflowEntity> latest = ragflowRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RagflowEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return ragflowRepository.save(latestEntity);
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
        Optional<RagflowEntity> optional = ragflowRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // ragflowRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Ragflow not found");
        }
    }

    @Override
    public void delete(RagflowRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public RagflowResponse convertToResponse(RagflowEntity entity) {
        return modelMapper.map(entity, RagflowResponse.class);
    }

    @Override
    public RagflowExcel convertToExcel(RagflowEntity entity) {
        return modelMapper.map(entity, RagflowExcel.class);
    }
    
    public void initRagflows(String orgUid) {
        // log.info("initThreadRagflow");
    }

    
    
}
