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
package com.bytedesk.webrtc.webrtc_settings;

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
public class WebrtcSettingsRestService extends BaseRestServiceWithExport<WebrtcSettingsEntity, WebrtcSettingsRequest, WebrtcSettingsResponse, WebrtcSettingsExcel> {

    private final WebrtcSettingsRepository webrtcSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<WebrtcSettingsEntity> queryByOrgEntity(WebrtcSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WebrtcSettingsEntity> specs = WebrtcSettingsSpecification.search(request, authService);
        return webrtcSettingsRepository.findAll(specs, pageable);
    }

    @Override
    public Page<WebrtcSettingsResponse> queryByOrg(WebrtcSettingsRequest request) {
        Page<WebrtcSettingsEntity> webrtc_settingsPage = queryByOrgEntity(request);
        return webrtc_settingsPage.map(this::convertToResponse);
    }

    @Override
    public Page<WebrtcSettingsResponse> queryByUser(WebrtcSettingsRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "webrtc_settings", key = "#uid", unless="#result==null")
    @Override
    public Optional<WebrtcSettingsEntity> findByUid(String uid) {
        return webrtcSettingsRepository.findByUid(uid);
    }

    // @Cacheable(value = "webrtc_settings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    // public Optional<WebrtcSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
    //     return webrtc_settingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    // }

    public Boolean existsByUid(String uid) {
        return webrtcSettingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public WebrtcSettingsResponse create(WebrtcSettingsRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public WebrtcSettingsResponse createSystemWebrtcSettings(WebrtcSettingsRequest request) {
        return createInternal(request, true);
    }

    private WebrtcSettingsResponse createInternal(WebrtcSettingsRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<WebrtcSettingsEntity> webrtc_settings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (webrtc_settings.isPresent()) {
        //         return convertToResponse(webrtc_settings.get());
        //     }
        // }
        
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(WebrtcSettingsPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        WebrtcSettingsEntity entity = modelMapper.map(request, WebrtcSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WebrtcSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create webrtc_settings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public WebrtcSettingsResponse update(WebrtcSettingsRequest request) {
        Optional<WebrtcSettingsEntity> optional = webrtcSettingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WebrtcSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(WebrtcSettingsPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            WebrtcSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update webrtc_settings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WebrtcSettings not found");
        }
    }

    @Override
    protected WebrtcSettingsEntity doSave(WebrtcSettingsEntity entity) {
        return webrtcSettingsRepository.save(entity);
    }

    @Override
    public WebrtcSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WebrtcSettingsEntity entity) {
        try {
            Optional<WebrtcSettingsEntity> latest = webrtcSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WebrtcSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return webrtcSettingsRepository.save(latestEntity);
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
        Optional<WebrtcSettingsEntity> optional = webrtcSettingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            WebrtcSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(WebrtcSettingsPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // webrtc_settingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WebrtcSettings not found");
        }
    }

    @Override
    public void delete(WebrtcSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WebrtcSettingsResponse convertToResponse(WebrtcSettingsEntity entity) {
        return modelMapper.map(entity, WebrtcSettingsResponse.class);
    }

    @Override
    public WebrtcSettingsExcel convertToExcel(WebrtcSettingsEntity entity) {
        return modelMapper.map(entity, WebrtcSettingsExcel.class);
    }

    @Override
    protected Specification<WebrtcSettingsEntity> createSpecification(WebrtcSettingsRequest request) {
        return WebrtcSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<WebrtcSettingsEntity> executePageQuery(Specification<WebrtcSettingsEntity> spec, Pageable pageable) {
        return webrtcSettingsRepository.findAll(spec, pageable);
    }
    
    public void initWebrtcSettingss(String orgUid) {
        // log.info("initWebrtcSettingsWebrtcSettings");
        // for (String webrtc_settings : WebrtcSettingsInitData.getAllWebrtcSettingss()) {
        //     WebrtcSettingsRequest webrtc_settingsRequest = WebrtcSettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, webrtc_settings))
        //             .name(webrtc_settings)
        //             .type(WebrtcSettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemWebrtcSettings(webrtc_settingsRequest);
        // }
    }

    
    
}
