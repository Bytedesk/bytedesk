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
package com.bytedesk.core.organization_settings;

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
public class OrganizationSettingsRestService extends BaseRestServiceWithExport<OrganizationSettingsEntity, OrganizationSettingsRequest, OrganizationSettingsResponse, OrganizationSettingsExcel> {

    private final OrganizationSettingsRepository organization_settingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<OrganizationSettingsEntity> queryByOrgEntity(OrganizationSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<OrganizationSettingsEntity> specs = OrganizationSettingsSpecification.search(request, authService);
        return organization_settingsRepository.findAll(specs, pageable);
    }

    @Override
    public Page<OrganizationSettingsResponse> queryByOrg(OrganizationSettingsRequest request) {
        Page<OrganizationSettingsEntity> organization_settingsPage = queryByOrgEntity(request);
        return organization_settingsPage.map(this::convertToResponse);
    }

    @Override
    public Page<OrganizationSettingsResponse> queryByUser(OrganizationSettingsRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "organization_settings", key = "#uid", unless="#result==null")
    @Override
    public Optional<OrganizationSettingsEntity> findByUid(String uid) {
        return organization_settingsRepository.findByUid(uid);
    }

    @Cacheable(value = "organization_settings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<OrganizationSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return organization_settingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return organization_settingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public OrganizationSettingsResponse create(OrganizationSettingsRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public OrganizationSettingsResponse createSystemOrganizationSettings(OrganizationSettingsRequest request) {
        return createInternal(request, true);
    }

    private OrganizationSettingsResponse createInternal(OrganizationSettingsRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<OrganizationSettingsEntity> organization_settings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (organization_settings.isPresent()) {
                return convertToResponse(organization_settings.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(OrganizationSettingsPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        OrganizationSettingsEntity entity = modelMapper.map(request, OrganizationSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        OrganizationSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public OrganizationSettingsResponse update(OrganizationSettingsRequest request) {
        Optional<OrganizationSettingsEntity> optional = organization_settingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            OrganizationSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(OrganizationSettingsPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            OrganizationSettingsEntity savedEntity = save(entity);
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
    protected OrganizationSettingsEntity doSave(OrganizationSettingsEntity entity) {
        return organization_settingsRepository.save(entity);
    }

    @Override
    public OrganizationSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, OrganizationSettingsEntity entity) {
        try {
            Optional<OrganizationSettingsEntity> latest = organization_settingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                OrganizationSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return organization_settingsRepository.save(latestEntity);
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
        Optional<OrganizationSettingsEntity> optional = organization_settingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            OrganizationSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(OrganizationSettingsPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
            // organization_settingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(OrganizationSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public OrganizationSettingsResponse convertToResponse(OrganizationSettingsEntity entity) {
        return modelMapper.map(entity, OrganizationSettingsResponse.class);
    }

    @Override
    public OrganizationSettingsExcel convertToExcel(OrganizationSettingsEntity entity) {
        return modelMapper.map(entity, OrganizationSettingsExcel.class);
    }

    @Override
    protected Specification<OrganizationSettingsEntity> createSpecification(OrganizationSettingsRequest request) {
        return OrganizationSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<OrganizationSettingsEntity> executePageQuery(Specification<OrganizationSettingsEntity> spec, Pageable pageable) {
        return organization_settingsRepository.findAll(spec, pageable);
    }
    
    public void initOrganizationSettingss(String orgUid) {
        // log.info("initOrganizationSettingsOrganizationSettings");
        // for (String organization_settings : OrganizationSettingsInitData.getAllOrganizationSettingss()) {
        //     OrganizationSettingsRequest organization_settingsRequest = OrganizationSettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, organization_settings))
        //             .name(organization_settings)
        //             .order(0)
        //             .type(OrganizationSettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemOrganizationSettings(organization_settingsRequest);
        // }
    }

    
    
}
