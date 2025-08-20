/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:41:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

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
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RelationRestService extends BaseRestServiceWithExport<RelationEntity, RelationRequest, RelationResponse, RelationExcel> {

    private final RelationRepository relationRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<RelationEntity> queryByOrgEntity(RelationRequest request) {
        Pageable pageable = request.getPageable();
        Specification<RelationEntity> spec = RelationSpecification.search(request, authService);
        return relationRepository.findAll(spec, pageable);
    }

    @Override
    public Page<RelationResponse> queryByOrg(RelationRequest request) {
        Page<RelationEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<RelationResponse> queryByUser(RelationRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public RelationResponse queryByUid(RelationRequest request) {
        Optional<RelationEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            RelationEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("Relation not found");
        }
    }

    @Cacheable(value = "relation", key = "#uid", unless="#result==null")
    @Override
    public Optional<RelationEntity> findByUid(String uid) {
        return relationRepository.findByUid(uid);
    }

    // @Cacheable(value = "relation", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    // public Optional<RelationEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
    //     return relationRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    // }

    public Boolean existsByUid(String uid) {
        return relationRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public RelationResponse create(RelationRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<RelationEntity> relation = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (relation.isPresent()) {
        //         return convertToResponse(relation.get());
        //     }
        // }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        RelationEntity entity = modelMapper.map(request, RelationEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        RelationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create relation failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public RelationResponse update(RelationRequest request) {
        Optional<RelationEntity> optional = relationRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            RelationEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            RelationEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update relation failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Relation not found");
        }
    }

    @Override
    protected RelationEntity doSave(RelationEntity entity) {
        return relationRepository.save(entity);
    }

    @Override
    public RelationEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RelationEntity entity) {
        try {
            Optional<RelationEntity> latest = relationRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RelationEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return relationRepository.save(latestEntity);
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
        Optional<RelationEntity> optional = relationRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // relationRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Relation not found");
        }
    }

    @Override
    public void delete(RelationRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public RelationResponse convertToResponse(RelationEntity entity) {
        return modelMapper.map(entity, RelationResponse.class);
    }

    @Override
    public RelationExcel convertToExcel(RelationEntity entity) {
        return modelMapper.map(entity, RelationExcel.class);
    }

    @Override
    protected Specification<RelationEntity> createSpecification(RelationRequest request) {
        return RelationSpecification.search(request, authService);
    }

    @Override
    protected Page<RelationEntity> executePageQuery(Specification<RelationEntity> spec, Pageable pageable) {
        return relationRepository.findAll(spec, pageable);
    }

    
}
