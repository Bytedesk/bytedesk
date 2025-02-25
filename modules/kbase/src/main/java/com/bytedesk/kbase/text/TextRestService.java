/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 17:27:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.text;

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
public class TextRestService extends BaseRestService<TextEntity, TextRequest, TextResponse> {

    private final TextRepository textRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<TextResponse> queryByOrg(TextRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<TextEntity> spec = TextSpecification.search(request);
        Page<TextEntity> page = textRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TextResponse> queryByUser(TextRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "text", key = "#uid", unless="#result==null")
    @Override
    public Optional<TextEntity> findByUid(String uid) {
        return textRepository.findByUid(uid);
    }

    @Override
    public TextResponse create(TextRequest request) {
        // 获取当前登录用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        // 
        TextEntity entity = modelMapper.map(request, TextEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        TextEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create text failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public TextResponse update(TextRequest request) {
        Optional<TextEntity> optional = textRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TextEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TextEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update text failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Text not found");
        }
    }

    @Override
    public TextEntity save(TextEntity entity) {
        try {
            return textRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Save text failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TextEntity> optional = textRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // textRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Text not found");
        }
    }

    @Override
    public void delete(TextRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TextEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TextResponse convertToResponse(TextEntity entity) {
        return modelMapper.map(entity, TextResponse.class);
    }
    
}
