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
package com.bytedesk.core.push.apns_push;

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
public class ApnsPushRestService extends BaseRestServiceWithExport<ApnsPushEntity, ApnsPushRequest, ApnsPushResponse, ApnsPushExcel> {

    private final ApnsPushRepository apnsPushRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ApnsPushEntity> queryByOrgEntity(ApnsPushRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ApnsPushEntity> specs = ApnsPushSpecification.search(request, authService);
        return apnsPushRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ApnsPushResponse> queryByOrg(ApnsPushRequest request) {
        Page<ApnsPushEntity> apns_pushPage = queryByOrgEntity(request);
        return apns_pushPage.map(this::convertToResponse);
    }

    @Override
    public Page<ApnsPushResponse> queryByUser(ApnsPushRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "apns_push", key = "#uid", unless="#result==null")
    @Override
    public Optional<ApnsPushEntity> findByUid(String uid) {
        return apnsPushRepository.findByUid(uid);
    }

    @Cacheable(value = "apns_push", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ApnsPushEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return apnsPushRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return apnsPushRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ApnsPushResponse create(ApnsPushRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ApnsPushResponse createSystemApnsPush(ApnsPushRequest request) {
        return createInternal(request, true);
    }

    private ApnsPushResponse createInternal(ApnsPushRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ApnsPushEntity> apns_push = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (apns_push.isPresent()) {
                return convertToResponse(apns_push.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ApnsPushPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        ApnsPushEntity entity = modelMapper.map(request, ApnsPushEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ApnsPushEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ApnsPushResponse update(ApnsPushRequest request) {
        Optional<ApnsPushEntity> optional = apnsPushRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ApnsPushEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ApnsPushPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            ApnsPushEntity savedEntity = save(entity);
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
    protected ApnsPushEntity doSave(ApnsPushEntity entity) {
        return apnsPushRepository.save(entity);
    }

    @Override
    public ApnsPushEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ApnsPushEntity entity) {
        try {
            Optional<ApnsPushEntity> latest = apnsPushRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ApnsPushEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return apnsPushRepository.save(latestEntity);
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
        Optional<ApnsPushEntity> optional = apnsPushRepository.findByUid(uid);
        if (optional.isPresent()) {
            ApnsPushEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ApnsPushPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
            // apns_pushRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(ApnsPushRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ApnsPushResponse convertToResponse(ApnsPushEntity entity) {
        return modelMapper.map(entity, ApnsPushResponse.class);
    }

    @Override
    public ApnsPushExcel convertToExcel(ApnsPushEntity entity) {
        return modelMapper.map(entity, ApnsPushExcel.class);
    }

    @Override
    protected Specification<ApnsPushEntity> createSpecification(ApnsPushRequest request) {
        return ApnsPushSpecification.search(request, authService);
    }

    @Override
    protected Page<ApnsPushEntity> executePageQuery(Specification<ApnsPushEntity> spec, Pageable pageable) {
        return apnsPushRepository.findAll(spec, pageable);
    }
    
    public void initApnsPushs(String orgUid) {
        // log.info("initApnsPushApnsPush");
        // for (String apns_push : ApnsPushInitData.getAllApnsPushs()) {
        //     ApnsPushRequest apns_pushRequest = ApnsPushRequest.builder()
        //             .uid(Utils.formatUid(orgUid, apns_push))
        //             .name(apns_push)
        //             .order(0)
        //             .type(ApnsPushTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemApnsPush(apns_pushRequest);
        // }
    }

    
    
}
