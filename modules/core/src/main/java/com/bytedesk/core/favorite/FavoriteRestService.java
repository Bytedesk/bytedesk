/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 10:17:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.favorite;

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

@Service
@AllArgsConstructor
public class FavoriteRestService extends BaseRestService<FavoriteEntity, FavoriteRequest, FavoriteResponse> {

    private final FavoriteRepository favoriteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<FavoriteEntity> createSpecification(FavoriteRequest request) {
        return FavoriteSpecification.search(request, authService);
    }

    @Override
    protected Page<FavoriteEntity> executePageQuery(Specification<FavoriteEntity> spec, Pageable pageable) {
        return favoriteRepository.findAll(spec, pageable);
    }

    @Override
    public Page<FavoriteResponse> queryByOrg(FavoriteRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FavoriteEntity> spec = createSpecification(request);
        Page<FavoriteEntity> page = executePageQuery(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FavoriteResponse> queryByUser(FavoriteRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "favorite", key = "#uid", unless="#result==null")
    @Override
    public Optional<FavoriteEntity> findByUid(String uid) {
        return favoriteRepository.findByUid(uid);
    }

    @Override
    public FavoriteResponse create(FavoriteRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        
        FavoriteEntity entity = modelMapper.map(request, FavoriteEntity.class);
        entity.setUid(uidUtils.getUid());
        // entity.setOrgUid(user.getOrgUid());

        FavoriteEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create favorite failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FavoriteResponse update(FavoriteRequest request) {
        Optional<FavoriteEntity> optional = favoriteRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FavoriteEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FavoriteEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update favorite failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Favorite not found");
        }
    }

    @Override
    protected FavoriteEntity doSave(FavoriteEntity entity) {
        return favoriteRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FavoriteEntity> optional = favoriteRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // favoriteRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Favorite not found");
        }
    }

    @Override
    public void delete(FavoriteRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FavoriteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FavoriteEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public FavoriteResponse convertToResponse(FavoriteEntity entity) {
        return modelMapper.map(entity, FavoriteResponse.class);
    }
    
}
