/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:27:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_service;

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
public class ServiceSettingsRestService extends BaseRestServiceWithExcel<ServiceSettingsEntity, ServiceSettingsRequest, ServiceSettingsResponse, ServiceSettingsExcel> {

    private final ServiceSettingsRepository serviceSettingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<ServiceSettingsEntity> queryByOrgEntity(ServiceSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ServiceSettingsEntity> spec = ServiceSettingsSpecification.search(request);
        return serviceSettingRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ServiceSettingsResponse> queryByOrg(ServiceSettingsRequest request) {
        Page<ServiceSettingsEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ServiceSettingsResponse> queryByUser(ServiceSettingsRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public ServiceSettingsResponse queryByUid(ServiceSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "serviceSetting", key = "#uid", unless="#result==null")
    @Override
    public Optional<ServiceSettingsEntity> findByUid(String uid) {
        return serviceSettingRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return serviceSettingRepository.existsByUid(uid);
    }

    @Override
    public ServiceSettingsResponse create(ServiceSettingsRequest request) {
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
        ServiceSettingsEntity entity = modelMapper.map(request, ServiceSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ServiceSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create serviceSetting failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ServiceSettingsResponse update(ServiceSettingsRequest request) {
        Optional<ServiceSettingsEntity> optional = serviceSettingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ServiceSettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ServiceSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update serviceSetting failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ServiceSettings not found");
        }
    }

    @Override
    protected ServiceSettingsEntity doSave(ServiceSettingsEntity entity) {
        return serviceSettingRepository.save(entity);
    }

    @Override
    public ServiceSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ServiceSettingsEntity entity) {
        try {
            Optional<ServiceSettingsEntity> latest = serviceSettingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ServiceSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                return serviceSettingRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ServiceSettingsEntity> optional = serviceSettingRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // serviceSettingRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("ServiceSettings not found");
        }
    }

    @Override
    public void delete(ServiceSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ServiceSettingsResponse convertToResponse(ServiceSettingsEntity entity) {
        return modelMapper.map(entity, ServiceSettingsResponse.class);
    }

    @Override
    public ServiceSettingsExcel convertToExcel(ServiceSettingsEntity entity) {
        return modelMapper.map(entity, ServiceSettingsExcel.class);
    }

    @Override
    protected Specification<ServiceSettingsEntity> createSpecification(ServiceSettingsRequest request) {
        return ServiceSettingsSpecification.search(request);
    }

    @Override
    protected Page<ServiceSettingsEntity> executePageQuery(Specification<ServiceSettingsEntity> spec, Pageable pageable) {
        return serviceSettingRepository.findAll(spec, pageable);
    }
    
    
}
