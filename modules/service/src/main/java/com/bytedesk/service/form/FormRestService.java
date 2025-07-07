/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:30:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FormRestService extends BaseRestService<FormEntity, FormRequest, FormResponse> {

    private final FormRepository formRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FormResponse> queryByOrg(FormRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FormEntity> spec = FormSpecification.search(request);
        Page<FormEntity> page = formRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FormResponse> queryByUser(FormRequest request) {
        UserEntity user = authService.getCurrentUser();
        if (user == null) {
            throw new NotLoginException("please login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public FormResponse queryByUid(FormRequest request) {
        Optional<FormEntity> optional = formRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FormEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("Form not found");
        }
    }

    @Override
    public Optional<FormEntity> findByUid(String uid) {
        return formRepository.findByUid(uid);
    }

    @Override
    public FormResponse create(FormRequest request) {
        FormEntity entity = modelMapper.map(request, FormEntity.class);
        entity.setUid(uidUtils.getUid());

        FormEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create form failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FormResponse update(FormRequest request) {
        Optional<FormEntity> optional = formRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FormEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FormEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update form failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Form not found");
        }
    }

    @Override
    protected FormEntity doSave(FormEntity entity) {
        return formRepository.save(entity);
    }

    @Override
    public FormEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FormEntity entity) {
        try {
            Optional<FormEntity> latest = formRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FormEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return formRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FormEntity> optional = formRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("Form not found");
        }
    }

    @Override
    public void delete(FormRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FormResponse convertToResponse(FormEntity entity) {
        return modelMapper.map(entity, FormResponse.class);
    }

    
}
