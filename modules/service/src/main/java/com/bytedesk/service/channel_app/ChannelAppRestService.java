/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:45:22
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
public class ChannelAppRestService extends BaseRestServiceWithExcel<ChannelAppEntity, ChannelAppRequest, ChannelAppResponse, ChannelAppExcel> {

    private final ChannelAppRepository appRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<ChannelAppEntity> createSpecification(ChannelAppRequest request) {
        return ChannelAppSpecification.search(request);
    }

    @Override
    protected Page<ChannelAppEntity> executePageQuery(Specification<ChannelAppEntity> spec, Pageable pageable) {
        return appRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ChannelAppEntity> queryByOrgEntity(ChannelAppRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ChannelAppEntity> spec = ChannelAppSpecification.search(request);
        return appRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ChannelAppResponse> queryByOrg(ChannelAppRequest request) {
        Page<ChannelAppEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ChannelAppResponse> queryByUser(ChannelAppRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public ChannelAppResponse queryByUid(ChannelAppRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "app", key = "#uid", unless="#result==null")
    @Override
    public Optional<ChannelAppEntity> findByUid(String uid) {
        return appRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return appRepository.existsByUid(uid);
    }

    @Override
    public ChannelAppResponse create(ChannelAppRequest request) {
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
        ChannelAppEntity entity = modelMapper.map(request, ChannelAppEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ChannelAppEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create app failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ChannelAppResponse update(ChannelAppRequest request) {
        Optional<ChannelAppEntity> optional = appRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ChannelAppEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ChannelAppEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update app failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ChannelApp not found");
        }
    }

    @Override
    protected ChannelAppEntity doSave(ChannelAppEntity entity) {
        return appRepository.save(entity);
    }

    @Override
    public ChannelAppEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ChannelAppEntity entity) {
        try {
            Optional<ChannelAppEntity> latest = appRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ChannelAppEntity latestEntity = latest.get();
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
        Optional<ChannelAppEntity> optional = appRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // appRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("ChannelApp not found");
        }
    }

    @Override
    public void delete(ChannelAppRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ChannelAppResponse convertToResponse(ChannelAppEntity entity) {
        return modelMapper.map(entity, ChannelAppResponse.class);
    }

    @Override
    public ChannelAppExcel convertToExcel(ChannelAppEntity entity) {
        return modelMapper.map(entity, ChannelAppExcel.class);
    }
    
    
}
