/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-29 10:44:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.settings;

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
public class SettingsRestService extends BaseRestServiceWithExport<SettingsEntity, SettingsRequest, SettingsResponse, SettingsExcel> {

    private final SettingsRepository settingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<SettingsEntity> createSpecification(SettingsRequest request) {
        return SettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<SettingsEntity> executePageQuery(Specification<SettingsEntity> spec, Pageable pageable) {
        return settingsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "settings", key = "#uid", unless="#result==null")
    @Override
    public Optional<SettingsEntity> findByUid(String uid) {
        return settingsRepository.findByUid(uid);
    }

    @Cacheable(value = "settings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<SettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return settingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return settingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public SettingsResponse create(SettingsRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<SettingsEntity> settings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (settings.isPresent()) {
                return convertToResponse(settings.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        SettingsEntity entity = modelMapper.map(request, SettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        SettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create settings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public SettingsResponse update(SettingsRequest request) {
        Optional<SettingsEntity> optional = settingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            SettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            SettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update settings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Settings not found");
        }
    }

    @Override
    protected SettingsEntity doSave(SettingsEntity entity) {
        return settingsRepository.save(entity);
    }

    @Override
    public SettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, SettingsEntity entity) {
        try {
            Optional<SettingsEntity> latest = settingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                SettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return settingsRepository.save(latestEntity);
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
        Optional<SettingsEntity> optional = settingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // settingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Settings not found");
        }
    }

    @Override
    public void delete(SettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public SettingsResponse convertToResponse(SettingsEntity entity) {
        return modelMapper.map(entity, SettingsResponse.class);
    }

    @Override
    public SettingsExcel convertToExcel(SettingsEntity entity) {
        return modelMapper.map(entity, SettingsExcel.class);
    }
    
    public void initSettings(String orgUid) {
        // log.info("initThreadSettings");
        // for (String settings : SettingsInitData.getAllSettings()) {
        //     SettingsRequest settingsRequest = SettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, settings))
        //             .name(settings)
        //             .order(0)
        //             .type(SettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(settingsRequest);
        // }
    }

    
    
}
