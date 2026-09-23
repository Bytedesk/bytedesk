/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_android;

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
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Android 推送投递记录管理服务
 *
 * <p>记录由 {@link PushAndroidRecordService} 在 PushDeviceRouter 调用供应商后写入（终态直写），
 * 本服务仅提供管理后台查询/删除/导出能力（推送记录不可编辑，Controller 不暴露 create/update）。
 *
 * <p>见 docs/plans/2026-09-23-android-push-record-entity-plan.md
 */
@Slf4j
@Service
@AllArgsConstructor
public class PushAndroidRestService extends BaseRestServiceWithExport<PushAndroidEntity, PushAndroidRequest, PushAndroidResponse, PushAndroidExcel> {

    private final PushAndroidRepository pushAndroidRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    private final PermissionService permissionService;

    @Override
    public Page<PushAndroidEntity> queryByOrgEntity(PushAndroidRequest request) {
        Pageable pageable = request.getPageable();
        Specification<PushAndroidEntity> specs = PushAndroidSpecification.search(request, authService);
        return pushAndroidRepository.findAll(specs, pageable);
    }

    @Override
    public Page<PushAndroidResponse> queryByOrg(PushAndroidRequest request) {
        Page<PushAndroidEntity> pushAndroidPage = queryByOrgEntity(request);
        return pushAndroidPage.map(this::convertToResponse);
    }

    @Override
    public Page<PushAndroidResponse> queryByUser(PushAndroidRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "push_android", key = "#uid", unless = "#result==null")
    @Override
    public Optional<PushAndroidEntity> findByUid(String uid) {
        return pushAndroidRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return pushAndroidRepository.existsByUid(uid);
    }

    /**
     * 记录为系统生成（PushAndroidRecordService 收口写入），
     * 此方法仅为满足 BaseRestService 抽象契约，Controller 不暴露 create 接口。
     */
    @Transactional
    @Override
    public PushAndroidResponse create(PushAndroidRequest request) {
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            request.setLevel(LevelEnum.ORGANIZATION.name());
        }
        PushAndroidEntity entity = modelMapper.map(request, PushAndroidEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        PushAndroidEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    /**
     * 推送记录不可编辑：仅为满足 BaseRestService 抽象契约，Controller 不暴露 update 接口。
     */
    @Transactional
    @Override
    public PushAndroidResponse update(PushAndroidRequest request) {
        Optional<PushAndroidEntity> optional = pushAndroidRepository.findByUid(request.getUid());
        if (optional.isEmpty()) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
        // 记录不可编辑，幂等返回当前内容
        return convertToResponse(optional.get());
    }

    @Override
    protected PushAndroidEntity doSave(PushAndroidEntity entity) {
        return pushAndroidRepository.save(entity);
    }

    @Override
    public PushAndroidEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PushAndroidEntity entity) {
        try {
            Optional<PushAndroidEntity> latest = pushAndroidRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                // 记录为终态直写、不可编辑，冲突时以数据库最新内容为准
                return pushAndroidRepository.save(latest.get());
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
        Optional<PushAndroidEntity> optional = pushAndroidRepository.findByUid(uid);
        if (optional.isPresent()) {
            PushAndroidEntity entity = optional.get();

            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(PushAndroidPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }

            entity.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(PushAndroidRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public PushAndroidResponse convertToResponse(PushAndroidEntity entity) {
        PushAndroidResponse response = modelMapper.map(entity, PushAndroidResponse.class);

        if (StringUtils.hasText(entity.getReceiver())) {
            userRepository.findByUid(entity.getReceiver()).ifPresent(user -> response.setReceiverNickname(user.getNickname()));
        }

        if (StringUtils.hasText(entity.getOrgUid())) {
            organizationRepository.findByUid(entity.getOrgUid()).ifPresent(org -> response.setOrgName(org.getName()));
        }

        return response;
    }

    @Override
    public PushAndroidExcel convertToExcel(PushAndroidEntity entity) {
        return modelMapper.map(entity, PushAndroidExcel.class);
    }

    @Override
    protected Specification<PushAndroidEntity> createSpecification(PushAndroidRequest request) {
        return PushAndroidSpecification.search(request, authService);
    }

    @Override
    protected Page<PushAndroidEntity> executePageQuery(Specification<PushAndroidEntity> spec, Pageable pageable) {
        return pushAndroidRepository.findAll(spec, pageable);
    }

}
