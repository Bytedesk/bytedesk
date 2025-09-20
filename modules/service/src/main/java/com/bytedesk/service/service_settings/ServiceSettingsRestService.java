/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 18:05:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.service_settings;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ServiceSettingsRestService extends BaseRestServiceWithExport<ServiceSettingsEntity, ServiceSettingsRequest, ServiceSettingsResponse, ServiceSettingsExcel> {

    private final ServiceSettingsRepository serviceSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<ServiceSettingsEntity> createSpecification(ServiceSettingsRequest request) {
        return ServiceSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<ServiceSettingsEntity> executePageQuery(Specification<ServiceSettingsEntity> spec, Pageable pageable) {
        return serviceSettingsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "serviceSettings", key = "#uid", unless="#result==null")
    @Override
    public Optional<ServiceSettingsEntity> findByUid(String uid) {
        return serviceSettingsRepository.findByUid(uid);
    }

    @Cacheable(value = "serviceSettings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ServiceSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return serviceSettingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return serviceSettingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ServiceSettingsResponse create(ServiceSettingsRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ServiceSettingsEntity> serviceSettings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (serviceSettings.isPresent()) {
                return convertToResponse(serviceSettings.get());
            }
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
            throw new RuntimeException("Create serviceSettings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ServiceSettingsResponse update(ServiceSettingsRequest request) {
        Optional<ServiceSettingsEntity> optional = serviceSettingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ServiceSettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ServiceSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update serviceSettings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ServiceSettings not found");
        }
    }

    @Override
    protected ServiceSettingsEntity doSave(ServiceSettingsEntity entity) {
        return serviceSettingsRepository.save(entity);
    }

    @Override
    public ServiceSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ServiceSettingsEntity entity) {
        try {
            Optional<ServiceSettingsEntity> latest = serviceSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ServiceSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return serviceSettingsRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<ServiceSettingsEntity> optional = serviceSettingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // serviceSettingsRepository.delete(optional.get());
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
    
    public void initServiceSettings(String orgUid) {
        // log.info("initThreadServiceSettings");
        // for (String serviceSettings : ServiceSettingsInitData.getAllServiceSettingss()) {
        //     ServiceSettingsRequest serviceSettingsRequest = ServiceSettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, serviceSettings))
        //             .name(serviceSettings)
        //             .order(0)
        //             .type(ServiceSettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(serviceSettingsRequest);
        // }
    }

    
    
}
