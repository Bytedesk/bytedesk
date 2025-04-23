/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 15:09:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.text;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TextRestService extends BaseRestServiceWithExcel<TextEntity, TextRequest, TextResponse, TextExcel> {

    private final TextRepository textRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final KbaseRestService kbaseRestService;

    @Override
    public Page<TextEntity> queryByOrgEntity(TextRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TextEntity> spec = TextSpecification.search(request);
        return textRepository.findAll(spec, pageable);
    }

    @Override
    public Page<TextResponse> queryByOrg(TextRequest request) {
        Page<TextEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TextResponse> queryByUser(TextRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "text", key = "#uid", unless = "#result==null")
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
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }

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
        } else {
            throw new RuntimeException("Text not found");
        }
    }

    @Override
    public TextEntity save(TextEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    protected TextEntity doSave(TextEntity entity) {
        return textRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TextEntity> optional = textRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // textRepository.delete(optional.get());
        } else {
            throw new RuntimeException("Text not found");
        }
    }

    @Override
    public void delete(TextRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TextEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TextEntity entity) {
        try {
            Optional<TextEntity> latest = textRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TextEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setContent(entity.getContent());
                latestEntity.setEnabled(entity.isEnabled());
                latestEntity.setType(entity.getType());

                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setStatus(entity.getStatus());

                return textRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    @Override
    public TextResponse convertToResponse(TextEntity entity) {
        return modelMapper.map(entity, TextResponse.class);
    }

    @Override
    public TextExcel convertToExcel(TextEntity text) {
        return modelMapper.map(text, TextExcel.class);
    }
}
