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
package com.bytedesk.core.push.apns_token;

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
public class ApnsTokenRestService extends BaseRestServiceWithExport<ApnsTokenEntity, ApnsTokenRequest, ApnsTokenResponse, ApnsTokenExcel> {

    private final ApnsTokenRepository apnsTokenRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ApnsTokenEntity> queryByOrgEntity(ApnsTokenRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ApnsTokenEntity> specs = ApnsTokenSpecification.search(request, authService);
        return apnsTokenRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ApnsTokenResponse> queryByOrg(ApnsTokenRequest request) {
        Page<ApnsTokenEntity> apns_tokenPage = queryByOrgEntity(request);
        return apns_tokenPage.map(this::convertToResponse);
    }

    @Override
    public Page<ApnsTokenResponse> queryByUser(ApnsTokenRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "apns_token", key = "#uid", unless="#result==null")
    @Override
    public Optional<ApnsTokenEntity> findByUid(String uid) {
        return apnsTokenRepository.findByUid(uid);
    }

    @Cacheable(value = "apns_token", key = "#token", unless="#result==null")
    public Optional<ApnsTokenEntity> findByToken(String token) {
        return apnsTokenRepository.findByTokenAndDeletedFalse(token);
    }

    public Boolean existsByUid(String uid) {
        return apnsTokenRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ApnsTokenResponse create(ApnsTokenRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ApnsTokenResponse registerCurrentUserToken(ApnsTokenRequest request) {
        return createOrUpdateForCurrentUser(request);
    }

    @Transactional
    public void unregisterCurrentUserToken(ApnsTokenRequest request) {
        validateTokenRequest(request);

        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
        }

        Optional<ApnsTokenEntity> existing = findByToken(request.getToken());
        if (existing.isEmpty()) {
            return;
        }

        ApnsTokenEntity entity = existing.get();
        if (!StringUtils.hasText(entity.getUserUid()) || !entity.getUserUid().equals(user.getUid())) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
        }

        entity.setDeleted(true);
        save(entity);
    }

    @Transactional
    public ApnsTokenResponse createSystemApnsToken(ApnsTokenRequest request) {
        return createInternal(request, true);
    }

    private ApnsTokenResponse createInternal(ApnsTokenRequest request, boolean skipPermissionCheck) {
        validateTokenRequest(request);
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 同一个 token 重新上报时，直接返回已有记录
        Optional<ApnsTokenEntity> existingByToken = findByToken(request.getToken());
        if (existingByToken.isPresent()) {
            return convertToResponse(existingByToken.get());
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
            if (!StringUtils.hasText(request.getOrgUid())) {
                request.setOrgUid(user.getOrgUid());
            }
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ApnsTokenPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        ApnsTokenEntity entity = modelMapper.map(request, ApnsTokenEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ApnsTokenEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    private ApnsTokenResponse createOrUpdateForCurrentUser(ApnsTokenRequest request) {
        validateTokenRequest(request);

        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }

        request.setUserUid(user.getUid());
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        if (!StringUtils.hasText(request.getLevel())) {
            request.setLevel(LevelEnum.ORGANIZATION.name());
        }
        if (!StringUtils.hasText(request.getType())) {
            request.setType("IOS");
        }

        Optional<ApnsTokenEntity> existing = findByToken(request.getToken());
        if (existing.isPresent()) {
            ApnsTokenEntity entity = existing.get();
            entity.setUserUid(request.getUserUid());
            entity.setOrgUid(request.getOrgUid());
            entity.setType(request.getType());
            entity.setLevel(request.getLevel());
            entity.setPlatform(request.getPlatform());
            entity.setDeleted(false);
            ApnsTokenEntity savedEntity = save(entity);
            return convertToResponse(savedEntity);
        }

        ApnsTokenEntity entity = modelMapper.map(request, ApnsTokenEntity.class);
        entity.setUid(uidUtils.getUid());
        ApnsTokenEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    private void validateTokenRequest(ApnsTokenRequest request) {
        if (!StringUtils.hasText(request.getToken())) {
            throw new IllegalArgumentException("apns token is required");
        }
    }

    @Transactional
    @Override
    public ApnsTokenResponse update(ApnsTokenRequest request) {
        Optional<ApnsTokenEntity> optional = apnsTokenRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ApnsTokenEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ApnsTokenPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            ApnsTokenEntity savedEntity = save(entity);
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
    protected ApnsTokenEntity doSave(ApnsTokenEntity entity) {
        return apnsTokenRepository.save(entity);
    }

    @Override
    public ApnsTokenEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ApnsTokenEntity entity) {
        try {
            Optional<ApnsTokenEntity> latest = apnsTokenRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ApnsTokenEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setToken(entity.getToken());
                latestEntity.setType(entity.getType());
                latestEntity.setUserUid(entity.getUserUid());
                latestEntity.setOrgUid(entity.getOrgUid());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return apnsTokenRepository.save(latestEntity);
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
        Optional<ApnsTokenEntity> optional = apnsTokenRepository.findByUid(uid);
        if (optional.isPresent()) {
            ApnsTokenEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ApnsTokenPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
            // apns_tokenRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(ApnsTokenRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ApnsTokenResponse convertToResponse(ApnsTokenEntity entity) {
        return modelMapper.map(entity, ApnsTokenResponse.class);
    }

    @Override
    public ApnsTokenExcel convertToExcel(ApnsTokenEntity entity) {
        return modelMapper.map(entity, ApnsTokenExcel.class);
    }

    @Override
    protected Specification<ApnsTokenEntity> createSpecification(ApnsTokenRequest request) {
        return ApnsTokenSpecification.search(request, authService);
    }

    @Override
    protected Page<ApnsTokenEntity> executePageQuery(Specification<ApnsTokenEntity> spec, Pageable pageable) {
        return apnsTokenRepository.findAll(spec, pageable);
    }
    
    public void initApnsTokens(String orgUid) {
        // log.info("initApnsTokenApnsToken");
        // for (String apns_token : ApnsTokenInitData.getAllApnsTokens()) {
        //     ApnsTokenRequest apns_tokenRequest = ApnsTokenRequest.builder()
        //             .uid(Utils.formatUid(orgUid, apns_token))
        //             .name(apns_token)
        //             .order(0)
        //             .type(ApnsTokenTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemApnsToken(apns_tokenRequest);
        // }
    }

    
    
}
