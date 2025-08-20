/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 20:58:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase_invite;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class KbaseInviteRestService extends BaseRestService<KbaseInviteEntity, KbaseInviteRequest, KbaseInviteResponse> {

    private final KbaseInviteRepository kbaseInviteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<KbaseInviteEntity> createSpecification(KbaseInviteRequest request) {
        return KbaseInviteSpecification.search(request, authService);
    }

    @Override
    protected Page<KbaseInviteEntity> executePageQuery(Specification<KbaseInviteEntity> spec, Pageable pageable) {
        return kbaseInviteRepository.findAll(spec, pageable);
    }
    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<KbaseInviteEntity> findByUid(String uid) {
        return kbaseInviteRepository.findByUid(uid);
    }

    @Override
    public KbaseInviteResponse create(KbaseInviteRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        
        KbaseInviteEntity entity = modelMapper.map(request, KbaseInviteEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        entity.setOrgUid(user.getOrgUid());

        KbaseInviteEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public KbaseInviteResponse update(KbaseInviteRequest request) {
        Optional<KbaseInviteEntity> optional = kbaseInviteRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            KbaseInviteEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            KbaseInviteEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("KbaseInvite not found");
        }
    }

    @Override
    protected KbaseInviteEntity doSave(KbaseInviteEntity entity) {
        return kbaseInviteRepository.save(entity);
    }

    @Override
    public KbaseInviteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, KbaseInviteEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<KbaseInviteEntity> latest = kbaseInviteRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                KbaseInviteEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return kbaseInviteRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<KbaseInviteEntity> optional = kbaseInviteRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("KbaseInvite not found");
        }
    }

    @Override
    public void delete(KbaseInviteRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public KbaseInviteResponse convertToResponse(KbaseInviteEntity entity) {
        return modelMapper.map(entity, KbaseInviteResponse.class);
    }
    
}
