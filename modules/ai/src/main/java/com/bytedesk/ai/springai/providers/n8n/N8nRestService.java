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
package com.bytedesk.ai.springai.providers.n8n;

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
public class N8nRestService extends BaseRestServiceWithExport<N8nEntity, N8nRequest, N8nResponse, N8nExcel> {

    private final N8nRepository n8nRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<N8nEntity> createSpecification(N8nRequest request) {
        return N8nSpecification.search(request, authService);
    }

    @Override
    protected Page<N8nEntity> executePageQuery(Specification<N8nEntity> spec, Pageable pageable) {
        return n8nRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "n8n", key = "#uid", unless="#result==null")
    @Override
    public Optional<N8nEntity> findByUid(String uid) {
        return n8nRepository.findByUid(uid);
    }

    @Cacheable(value = "n8n", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<N8nEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return n8nRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return n8nRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public N8nResponse create(N8nRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<N8nEntity> n8n = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (n8n.isPresent()) {
                return convertToResponse(n8n.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        N8nEntity entity = modelMapper.map(request, N8nEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        N8nEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create n8n failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public N8nResponse update(N8nRequest request) {
        Optional<N8nEntity> optional = n8nRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            N8nEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            N8nEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update n8n failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("N8n not found");
        }
    }

    @Override
    protected N8nEntity doSave(N8nEntity entity) {
        return n8nRepository.save(entity);
    }

    @Override
    public N8nEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, N8nEntity entity) {
        try {
            Optional<N8nEntity> latest = n8nRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                N8nEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return n8nRepository.save(latestEntity);
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
        Optional<N8nEntity> optional = n8nRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // n8nRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("N8n not found");
        }
    }

    @Override
    public void delete(N8nRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public N8nResponse convertToResponse(N8nEntity entity) {
        return modelMapper.map(entity, N8nResponse.class);
    }

    @Override
    public N8nExcel convertToExcel(N8nEntity entity) {
        return modelMapper.map(entity, N8nExcel.class);
    }
    
    public void initN8ns(String orgUid) {
        // log.info("initThreadN8n");
    }

    
    
}
