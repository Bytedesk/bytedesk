/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:20:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 16:25:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.LocalDateTime;
import java.util.List;
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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlackRestService extends BaseRestService<BlackEntity, BlackRequest, BlackResponse> {

    private final BlackRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<BlackResponse> queryByOrg(BlackRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Specification<BlackEntity> specification = BlackSpecification.search(request);

        Page<BlackEntity> blacks = repository.findAll(specification, pageable);

        return blacks.map(this::convertToResponse);
    }

    @Override
    public Page<BlackResponse> queryByUser(BlackRequest request) {

        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Specification<BlackEntity> specification = BlackSpecification.search(request);

        Page<BlackEntity> blacks = repository.findAll(specification, pageable);

        return blacks.map(this::convertToResponse);
    }

    @Cacheable(value = "black", key = "#uid", unless = "#result == null")
    @Override
    public Optional<BlackEntity> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    public List<BlackEntity> findByEndTimeBefore(LocalDateTime endTime) {
        return repository.findByEndTimeBefore(endTime);
    }

    // 根据黑名单用户uid查询
    @Cacheable(value = "black", key = "#blackUid", unless = "#result == null")
    public Optional<BlackEntity> findByBlackUid(String blackUid) {
        return repository.findFirstByBlackUid(blackUid);
    }

    @Override
    public BlackResponse create(BlackRequest request) {
        // 判断是否已经存在黑名单用户uid
        Optional<BlackEntity> black = findByBlackUid(request.getBlackUid());
        if (black.isPresent()) {
            return convertToResponse(black.get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_USER_NOT_FOUND);
        }
        //
        BlackEntity entity = modelMapper.map(request, BlackEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setUserUid(user.getUid());
        entity.setUserNickname(user.getNickname());
        entity.setUserAvatar(user.getAvatar());
        //
        BlackEntity savedBlack = save(entity);
        if (savedBlack == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedBlack);
    }

    @Override
    public BlackResponse update(BlackRequest request) {

        Optional<BlackEntity> black = findByUid(request.getUid());
        if (black.isPresent()) {
            BlackEntity entity = black.get();
            modelMapper.map(request, entity);
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("Black not found");
        }
    }

    @Override
    public BlackEntity save(BlackEntity entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<BlackEntity> black = findByUid(uid);
        if (black.isPresent()) {
            repository.delete(black.get());
        } else {
            throw new RuntimeException("Black not found");
        }
    }

    @Override
    public void delete(BlackRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, BlackEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public BlackResponse convertToResponse(BlackEntity entity) {
        return modelMapper.map(entity, BlackResponse.class);
    }

}
