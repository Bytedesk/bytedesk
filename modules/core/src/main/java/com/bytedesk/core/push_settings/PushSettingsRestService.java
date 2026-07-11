/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_settings;

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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PushSettingsRestService extends BaseRestServiceWithExport<PushSettingsEntity, PushSettingsRequest, PushSettingsResponse, PushSettingsExcel> {

    private final PushSettingsRepository push_settingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<PushSettingsEntity> queryByOrgEntity(PushSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<PushSettingsEntity> specs = PushSettingsSpecification.search(request, authService);
        return push_settingsRepository.findAll(specs, pageable);
    }

    @Override
    public Page<PushSettingsResponse> queryByOrg(PushSettingsRequest request) {
        Page<PushSettingsEntity> push_settingsPage = queryByOrgEntity(request);
        return push_settingsPage.map(this::convertToResponse);
    }

    @Override
    public Page<PushSettingsResponse> queryByUser(PushSettingsRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "push_settings", key = "#uid", unless="#result==null")
    @Override
    public Optional<PushSettingsEntity> findByUid(String uid) {
        return push_settingsRepository.findByUid(uid);
    }

    @Cacheable(value = "push_settings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<PushSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return push_settingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return push_settingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public PushSettingsResponse create(PushSettingsRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public PushSettingsResponse createSystemPushSettings(PushSettingsRequest request) {
        return createInternal(request, true);
    }

    private PushSettingsResponse createInternal(PushSettingsRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<PushSettingsEntity> push_settings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (push_settings.isPresent()) {
                return convertToResponse(push_settings.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(PushSettingsPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        PushSettingsEntity entity = modelMapper.map(request, PushSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        PushSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public PushSettingsResponse update(PushSettingsRequest request) {
        Optional<PushSettingsEntity> optional = push_settingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            PushSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(PushSettingsPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            PushSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected PushSettingsEntity doSave(PushSettingsEntity entity) {
        return push_settingsRepository.save(entity);
    }

    @Override
    public PushSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PushSettingsEntity entity) {
        try {
            Optional<PushSettingsEntity> latest = push_settingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                PushSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return push_settingsRepository.save(latestEntity);
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
        Optional<PushSettingsEntity> optional = push_settingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            PushSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(PushSettingsPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(PushSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public PushSettingsResponse convertToResponse(PushSettingsEntity entity) {
        return modelMapper.map(entity, PushSettingsResponse.class);
    }

    @Override
    public PushSettingsExcel convertToExcel(PushSettingsEntity entity) {
        return modelMapper.map(entity, PushSettingsExcel.class);
    }

    @Override
    protected Specification<PushSettingsEntity> createSpecification(PushSettingsRequest request) {
        return PushSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<PushSettingsEntity> executePageQuery(Specification<PushSettingsEntity> spec, Pageable pageable) {
        return push_settingsRepository.findAll(spec, pageable);
    }
    
    public void initPushSettingss(String orgUid) {
        // log.info("initPushSettingsPushSettings");
        // for (String push_settings : PushSettingsInitData.getAllPushSettingss()) {
        //     PushSettingsRequest push_settingsRequest = PushSettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, push_settings))
        //             .name(push_settings)
        //             .order(0)
        //             .type(PushSettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemPushSettings(push_settingsRequest);
        // }
    }
    
}
