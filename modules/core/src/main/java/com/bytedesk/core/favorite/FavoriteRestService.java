/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:20:18
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavoriteRestService extends BaseRestService<FavoriteEntity, FavoriteRequest, FavoriteResponse> {

    private final FavoriteRepository favoriteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FavoriteResponse> queryByOrg(FavoriteRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<FavoriteEntity> spec = FavoriteSpecification.search(request);
        Page<FavoriteEntity> page = favoriteRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FavoriteResponse> queryByUser(FavoriteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "favorite", key = "#uid", unless="#result==null")
    @Override
    public Optional<FavoriteEntity> findByUid(String uid) {
        return favoriteRepository.findByUid(uid);
    }

    @Override
    public FavoriteResponse initVisitor(FavoriteRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        
        FavoriteEntity entity = modelMapper.map(request, FavoriteEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        entity.setOrgUid(user.getOrgUid());

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
    public FavoriteEntity save(FavoriteEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
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
