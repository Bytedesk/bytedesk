/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 16:55:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.qa;

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
public class QaRestService extends BaseRestService<QaEntity, QaRequest, QaResponse> {

    private final QaRepository qaRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<QaResponse> queryByOrg(QaRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<QaEntity> spec = QaSpecification.search(request);
        Page<QaEntity> page = qaRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QaResponse> queryByUser(QaRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "qa", key = "#uid", unless="#result==null")
    @Override
    public Optional<QaEntity> findByUid(String uid) {
        return qaRepository.findByUid(uid);
    }

    @Override
    public QaResponse create(QaRequest request) {
        // 获取当前登录用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        // 
        QaEntity entity = modelMapper.map(request, QaEntity.class);
        entity.setUid(uidUtils.getUid());

        QaEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create qa failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public QaResponse update(QaRequest request) {
        Optional<QaEntity> optional = qaRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            QaEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            QaEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update qa failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Qa not found");
        }
    }

    @Override
    public QaEntity save(QaEntity entity) {
        try {
            return qaRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<QaEntity> optional = qaRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // qaRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Qa not found");
        }
    }

    @Override
    public void delete(QaRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QaEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public QaResponse convertToResponse(QaEntity entity) {
        return modelMapper.map(entity, QaResponse.class);
    }
    
}
