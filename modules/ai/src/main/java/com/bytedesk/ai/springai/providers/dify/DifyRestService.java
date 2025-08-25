/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:05:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

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
public class DifyRestService extends BaseRestServiceWithExport<DifyEntity, DifyRequest, DifyResponse, DifyExcel> {

    private final DifyRepository difyRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<DifyEntity> createSpecification(DifyRequest request) {
        return DifySpecification.search(request, authService);
    }

    @Override
    protected Page<DifyEntity> executePageQuery(Specification<DifyEntity> spec, Pageable pageable) {
        return difyRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "dify", key = "#uid", unless="#result==null")
    @Override
    public Optional<DifyEntity> findByUid(String uid) {
        return difyRepository.findByUid(uid);
    }

    @Cacheable(value = "dify", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<DifyEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return difyRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return difyRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public DifyResponse create(DifyRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<DifyEntity> dify = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (dify.isPresent()) {
                return convertToResponse(dify.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        DifyEntity entity = modelMapper.map(request, DifyEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        DifyEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create dify failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public DifyResponse update(DifyRequest request) {
        Optional<DifyEntity> optional = difyRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            DifyEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            DifyEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update dify failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Dify not found");
        }
    }

    @Override
    protected DifyEntity doSave(DifyEntity entity) {
        return difyRepository.save(entity);
    }

    @Override
    public DifyEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, DifyEntity entity) {
        try {
            Optional<DifyEntity> latest = difyRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                DifyEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return difyRepository.save(latestEntity);
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
        Optional<DifyEntity> optional = difyRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // difyRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Dify not found");
        }
    }

    @Override
    public void delete(DifyRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public DifyResponse convertToResponse(DifyEntity entity) {
        return modelMapper.map(entity, DifyResponse.class);
    }

    @Override
    public DifyExcel convertToExcel(DifyEntity entity) {
        return modelMapper.map(entity, DifyExcel.class);
    }
    
    public void initDifys(String orgUid) {
        // log.info("initThreadDify");
    }

    
    
}
