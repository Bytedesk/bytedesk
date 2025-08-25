/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.maxkb;

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
public class MaxkbRestService extends BaseRestServiceWithExport<MaxkbEntity, MaxkbRequest, MaxkbResponse, MaxkbExcel> {

    private final MaxkbRepository maxkbRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<MaxkbEntity> createSpecification(MaxkbRequest request) {
        return MaxkbSpecification.search(request, authService);
    }

    @Override
    protected Page<MaxkbEntity> executePageQuery(Specification<MaxkbEntity> spec, Pageable pageable) {
        return maxkbRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "maxkb", key = "#uid", unless="#result==null")
    @Override
    public Optional<MaxkbEntity> findByUid(String uid) {
        return maxkbRepository.findByUid(uid);
    }

    @Cacheable(value = "maxkb", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<MaxkbEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return maxkbRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return maxkbRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public MaxkbResponse create(MaxkbRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<MaxkbEntity> maxkb = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (maxkb.isPresent()) {
                return convertToResponse(maxkb.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        MaxkbEntity entity = modelMapper.map(request, MaxkbEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MaxkbEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create maxkb failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public MaxkbResponse update(MaxkbRequest request) {
        Optional<MaxkbEntity> optional = maxkbRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MaxkbEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MaxkbEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update maxkb failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Maxkb not found");
        }
    }

    @Override
    protected MaxkbEntity doSave(MaxkbEntity entity) {
        return maxkbRepository.save(entity);
    }

    @Override
    public MaxkbEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MaxkbEntity entity) {
        try {
            Optional<MaxkbEntity> latest = maxkbRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MaxkbEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return maxkbRepository.save(latestEntity);
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
        Optional<MaxkbEntity> optional = maxkbRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // maxkbRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Maxkb not found");
        }
    }

    @Override
    public void delete(MaxkbRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MaxkbResponse convertToResponse(MaxkbEntity entity) {
        return modelMapper.map(entity, MaxkbResponse.class);
    }

    @Override
    public MaxkbExcel convertToExcel(MaxkbEntity entity) {
        return modelMapper.map(entity, MaxkbExcel.class);
    }
    
    public void initMaxkbs(String orgUid) {
        // log.info("initThreadMaxkb")
    }

    
}
