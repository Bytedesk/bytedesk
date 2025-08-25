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
package com.bytedesk.ai.springai.providers.coze;

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
public class CozeRestService extends BaseRestServiceWithExport<CozeEntity, CozeRequest, CozeResponse, CozeExcel> {

    private final CozeRepository cozeRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<CozeEntity> createSpecification(CozeRequest request) {
        return CozeSpecification.search(request, authService);
    }

    @Override
    protected Page<CozeEntity> executePageQuery(Specification<CozeEntity> spec, Pageable pageable) {
        return cozeRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "coze", key = "#uid", unless="#result==null")
    @Override
    public Optional<CozeEntity> findByUid(String uid) {
        return cozeRepository.findByUid(uid);
    }

    @Cacheable(value = "coze", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CozeEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return cozeRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return cozeRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CozeResponse create(CozeRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CozeEntity> coze = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (coze.isPresent()) {
                return convertToResponse(coze.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        CozeEntity entity = modelMapper.map(request, CozeEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CozeEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create coze failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CozeResponse update(CozeRequest request) {
        Optional<CozeEntity> optional = cozeRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CozeEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            CozeEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update coze failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Coze not found");
        }
    }

    @Override
    protected CozeEntity doSave(CozeEntity entity) {
        return cozeRepository.save(entity);
    }

    @Override
    public CozeEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CozeEntity entity) {
        try {
            Optional<CozeEntity> latest = cozeRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CozeEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return cozeRepository.save(latestEntity);
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
        Optional<CozeEntity> optional = cozeRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // cozeRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Coze not found");
        }
    }

    @Override
    public void delete(CozeRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CozeResponse convertToResponse(CozeEntity entity) {
        return modelMapper.map(entity, CozeResponse.class);
    }

    @Override
    public CozeExcel convertToExcel(CozeEntity entity) {
        return modelMapper.map(entity, CozeExcel.class);
    }
    
    public void initCozes(String orgUid) {
        // log.info("initThreadCoze")
    }

    
}
