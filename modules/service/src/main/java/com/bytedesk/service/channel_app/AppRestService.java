/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:28:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.channel_app;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AppRestService extends BaseRestServiceWithExcel<AppEntity, AppRequest, AppResponse, AppExcel> {

    private final AppRepository appRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<AppEntity> queryByOrgEntity(AppRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AppEntity> spec = AppSpecification.search(request);
        return appRepository.findAll(spec, pageable);
    }

    @Override
    public Page<AppResponse> queryByOrg(AppRequest request) {
        Page<AppEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<AppResponse> queryByUser(AppRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public AppResponse queryByUid(AppRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "app", key = "#uid", unless="#result==null")
    @Override
    public Optional<AppEntity> findByUid(String uid) {
        return appRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return appRepository.existsByUid(uid);
    }

    @Override
    public AppResponse create(AppRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        AppEntity entity = modelMapper.map(request, AppEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        AppEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create app failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public AppResponse update(AppRequest request) {
        Optional<AppEntity> optional = appRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            AppEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            AppEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update app failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("App not found");
        }
    }

    @Override
    protected AppEntity doSave(AppEntity entity) {
        return appRepository.save(entity);
    }

    @Override
    public AppEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AppEntity entity) {
        try {
            Optional<AppEntity> latest = appRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AppEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return appRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AppEntity> optional = appRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // appRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("App not found");
        }
    }

    @Override
    public void delete(AppRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AppResponse convertToResponse(AppEntity entity) {
        return modelMapper.map(entity, AppResponse.class);
    }

    @Override
    public AppExcel convertToExcel(AppEntity entity) {
        return modelMapper.map(entity, AppExcel.class);
    }
    
    
}
