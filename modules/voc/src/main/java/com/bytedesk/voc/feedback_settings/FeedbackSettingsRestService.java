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
package com.bytedesk.voc.feedback_settings;

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
import com.bytedesk.voc.feedback.FeedbackTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FeedbackSettingsRestService extends BaseRestServiceWithExport<FeedbackSettingsEntity, FeedbackSettingsRequest, FeedbackSettingsResponse, FeedbackSettingsExcel> {

    private final FeedbackSettingsRepository audioFileRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<FeedbackSettingsEntity> queryByOrgEntity(FeedbackSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FeedbackSettingsEntity> specs = FeedbackSettingsSpecification.search(request, authService);
        return audioFileRepository.findAll(specs, pageable);
    }

    @Override
    public Page<FeedbackSettingsResponse> queryByOrg(FeedbackSettingsRequest request) {
        Page<FeedbackSettingsEntity> feedback_settingsPage = queryByOrgEntity(request);
        return feedback_settingsPage.map(this::convertToResponse);
    }

    @Override
    public Page<FeedbackSettingsResponse> queryByUser(FeedbackSettingsRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "feedback_settings", key = "#uid", unless="#result==null")
    @Override
    public Optional<FeedbackSettingsEntity> findByUid(String uid) {
        return audioFileRepository.findByUid(uid);
    }

    @Cacheable(value = "feedback_settings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<FeedbackSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return audioFileRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return audioFileRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public FeedbackSettingsResponse create(FeedbackSettingsRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public FeedbackSettingsResponse createSystemFeedbackSettings(FeedbackSettingsRequest request) {
        return createInternal(request, true);
    }

    private FeedbackSettingsResponse createInternal(FeedbackSettingsRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 兼容：检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<FeedbackSettingsEntity> existing = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (existing.isPresent()) {
                return convertToResponse(existing.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(FeedbackSettingsPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        FeedbackSettingsEntity entity = modelMapper.map(request, FeedbackSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        FeedbackSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create feedback_settings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public FeedbackSettingsResponse update(FeedbackSettingsRequest request) {
        Optional<FeedbackSettingsEntity> optional = audioFileRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FeedbackSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(FeedbackSettingsPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            FeedbackSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update feedback_settings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("FeedbackSettings not found");
        }
    }

    @Override
    protected FeedbackSettingsEntity doSave(FeedbackSettingsEntity entity) {
        return audioFileRepository.save(entity);
    }

    @Override
    public FeedbackSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FeedbackSettingsEntity entity) {
        try {
            Optional<FeedbackSettingsEntity> latest = audioFileRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FeedbackSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return audioFileRepository.save(latestEntity);
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
        Optional<FeedbackSettingsEntity> optional = audioFileRepository.findByUid(uid);
        if (optional.isPresent()) {
            FeedbackSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(FeedbackSettingsPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // feedback_settingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("FeedbackSettings not found");
        }
    }

    @Override
    public void delete(FeedbackSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Transactional
    public FeedbackSettingsResponse publish(String uid) {
        Optional<FeedbackSettingsEntity> optional = audioFileRepository.findByUid(uid);
        if (optional.isEmpty()) {
            throw new RuntimeException("FeedbackSettings not found");
        }

        FeedbackSettingsEntity entity = optional.get();
        if (!permissionService.hasEntityPermission(FeedbackSettingsPermissions.MODULE_NAME, "UPDATE", entity)) {
            throw new RuntimeException("无权限发布该标签数据");
        }

        entity.setEnabled(true);
        FeedbackSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Publish feedback_settings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FeedbackSettingsResponse convertToResponse(FeedbackSettingsEntity entity) {
        return modelMapper.map(entity, FeedbackSettingsResponse.class);
    }

    @Override
    public FeedbackSettingsExcel convertToExcel(FeedbackSettingsEntity entity) {
        return modelMapper.map(entity, FeedbackSettingsExcel.class);
    }

    @Override
    protected Specification<FeedbackSettingsEntity> createSpecification(FeedbackSettingsRequest request) {
        return FeedbackSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<FeedbackSettingsEntity> executePageQuery(Specification<FeedbackSettingsEntity> spec, Pageable pageable) {
        return audioFileRepository.findAll(spec, pageable);
    }
    
    public void initFeedbackSettingss(String orgUid) {
        // Default satisfaction widget settings for ticket management
        try {
            String type = FeedbackTypeEnum.NPS.name();

            Optional<FeedbackSettingsEntity> existing = findByNameAndOrgUidAndType("工单管理满意度", orgUid, type);
            if (existing.isPresent()) {
                return;
            }

            FeedbackSettingsRequest request = FeedbackSettingsRequest.builder()
                    .orgUid(orgUid)
                    .type(type)
                    .name("工单管理满意度")
                    .enabled(Boolean.TRUE)
                    .positiveScoreMin(9)
                    .maxReasons(3)
                    .title("请您对\"工单管理\"进行满意度评价")
                    .positiveQuestion("您感到满意的原因是？")
                    .negativeQuestion("您在使用过程中遇到哪些问题？")
                    .commentPlaceholder("【使用场景】：\n【优化意见】：")
                    .positiveReasons(java.util.List.of(
                            "流程清晰",
                            "操作简单",
                            "提示明确",
                            "提交/流转速度快",
                            "其他"
                    ))
                    .negativeReasons(java.util.List.of(
                            "工单分类不好找",
                            "信息填写不方便",
                            "流程太复杂",
                            "处理进度不清晰",
                            "其他"
                    ))
                    .build();

            createSystemFeedbackSettings(request);
        } catch (Exception e) {
            log.warn("initFeedbackSettingss failed: {}", e.getMessage());
        }
    }

    
    
}
