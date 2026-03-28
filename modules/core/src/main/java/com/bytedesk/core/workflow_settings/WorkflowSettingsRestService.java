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
package com.bytedesk.core.workflow_settings;

import java.util.Optional;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.BytedeskConsts;
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
public class WorkflowSettingsRestService extends BaseRestServiceWithExport<WorkflowSettingsEntity, WorkflowSettingsRequest, WorkflowSettingsResponse, WorkflowSettingsExcel> {

    private static final String DEFAULT_WORKFLOW_SETTINGS_NAME = "默认流程配置";
    private static final String DEFAULT_WORKFLOW_SETTINGS_DESCRIPTION = "系统默认流程配置";
    private static final String DEFAULT_WORKFLOW_SETTINGS_TYPE = WorkflowSettingsTypeEnum.THREAD.name();

    private final WorkflowSettingsRepository workflow_settingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<WorkflowSettingsEntity> queryByOrgEntity(WorkflowSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WorkflowSettingsEntity> specs = WorkflowSettingsSpecification.search(request, authService);
        return workflow_settingsRepository.findAll(specs, pageable);
    }

    @Override
    public Page<WorkflowSettingsResponse> queryByOrg(WorkflowSettingsRequest request) {
        Page<WorkflowSettingsEntity> workflow_settingsPage = queryByOrgEntity(request);
        return workflow_settingsPage.map(this::convertToResponse);
    }

    @Override
    public Page<WorkflowSettingsResponse> queryByUser(WorkflowSettingsRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    // @Cacheable(value = "workflow_settings", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkflowSettingsEntity> findByUid(String uid) {
        return workflow_settingsRepository.findByUid(uid);
    }

    @Cacheable(value = "workflow_settings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<WorkflowSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return workflow_settingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return workflow_settingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public WorkflowSettingsResponse create(WorkflowSettingsRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public WorkflowSettingsResponse createSystemWorkflowSettings(WorkflowSettingsRequest request) {
        return createInternal(request, true);
    }

    private WorkflowSettingsResponse createInternal(WorkflowSettingsRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<WorkflowSettingsEntity> workflow_settings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (workflow_settings.isPresent()) {
                return convertToResponse(workflow_settings.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(WorkflowSettingsPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        WorkflowSettingsEntity entity = modelMapper.map(request, WorkflowSettingsEntity.class);
        entity.setEnabled(normalizeEnabled(request.getEnabled()));
        applyIframeTabs(entity, request.getIframeTabs());
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WorkflowSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create workflow_settings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public WorkflowSettingsResponse update(WorkflowSettingsRequest request) {
        Optional<WorkflowSettingsEntity> optional = workflow_settingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(WorkflowSettingsPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            entity.setEnabled(normalizeEnabled(request.getEnabled()));
            applyIframeTabs(entity, request.getIframeTabs());
            //
            WorkflowSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update workflow_settings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WorkflowSettings not found");
        }
    }

    @Override
    protected WorkflowSettingsEntity doSave(WorkflowSettingsEntity entity) {
        return workflow_settingsRepository.save(entity);
    }

    @Override
    public WorkflowSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowSettingsEntity entity) {
        try {
            Optional<WorkflowSettingsEntity> latest = workflow_settingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkflowSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setEnabled(normalizeEnabled(entity.getEnabled()));
                latestEntity.setType(entity.getType());
                latestEntity.setIframeTabsJson(entity.getIframeTabsJson());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return workflow_settingsRepository.save(latestEntity);
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
        Optional<WorkflowSettingsEntity> optional = workflow_settingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            WorkflowSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(WorkflowSettingsPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // workflow_settingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WorkflowSettings not found");
        }
    }

    @Override
    public void delete(WorkflowSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowSettingsResponse convertToResponse(WorkflowSettingsEntity entity) {
        WorkflowSettingsResponse response = modelMapper.map(entity, WorkflowSettingsResponse.class);
        response.setEnabled(normalizeEnabled(entity.getEnabled()));
        response.setIframeTabs(parseIframeTabs(entity.getIframeTabsJson()));
        return response;
    }

    @Override
    public WorkflowSettingsExcel convertToExcel(WorkflowSettingsEntity entity) {
        return modelMapper.map(entity, WorkflowSettingsExcel.class);
    }

    @Override
    protected Specification<WorkflowSettingsEntity> createSpecification(WorkflowSettingsRequest request) {
        return WorkflowSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkflowSettingsEntity> executePageQuery(Specification<WorkflowSettingsEntity> spec, Pageable pageable) {
        return workflow_settingsRepository.findAll(spec, pageable);
    }
    
    public void initWorkflowSettingss(String orgUid) {
        getOrCreateDefault(orgUid);
    }

    @Transactional
    public WorkflowSettingsEntity getOrCreateDefault(String orgUid) {
        String normalizedOrgUid = StringUtils.hasText(orgUid) ? orgUid : BytedeskConsts.DEFAULT_ORGANIZATION_UID;

        Optional<WorkflowSettingsEntity> existing = findByNameAndOrgUidAndType(
                DEFAULT_WORKFLOW_SETTINGS_NAME,
                normalizedOrgUid,
                DEFAULT_WORKFLOW_SETTINGS_TYPE);
        if (existing.isPresent()) {
            return existing.get();
        }

        WorkflowSettingsRequest request = WorkflowSettingsRequest.builder()
                .name(DEFAULT_WORKFLOW_SETTINGS_NAME)
                .description(DEFAULT_WORKFLOW_SETTINGS_DESCRIPTION)
                .type(DEFAULT_WORKFLOW_SETTINGS_TYPE)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .orgUid(normalizedOrgUid)
                .build();
        WorkflowSettingsResponse response = createSystemWorkflowSettings(request);
        return findByUid(response.getUid())
                .orElseThrow(() -> new RuntimeException("Default workflow settings created but cannot be loaded: " + response.getUid()));
    }

    private void applyIframeTabs(WorkflowSettingsEntity entity, List<WorkflowIframeTab> iframeTabs) {
        entity.setIframeTabsJson(JSON.toJSONString(sanitizeIframeTabs(iframeTabs)));
    }

    private List<WorkflowIframeTab> parseIframeTabs(String iframeTabsJson) {
        if (!StringUtils.hasText(iframeTabsJson)) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseObject(iframeTabsJson, new TypeReference<List<WorkflowIframeTab>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse workflow iframe tabs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<WorkflowIframeTab> sanitizeIframeTabs(List<WorkflowIframeTab> iframeTabs) {
        if (iframeTabs == null || iframeTabs.isEmpty()) {
            return Collections.emptyList();
        }
        return iframeTabs.stream()
                .filter(item -> item != null && StringUtils.hasText(item.getUrl()))
                .map(item -> WorkflowIframeTab.builder()
                        .title(StringUtils.hasText(item.getTitle()) ? item.getTitle().trim() : null)
                        .url(item.getUrl().trim())
                        .build())
                .toList();
    }

    private Boolean normalizeEnabled(Boolean enabled) {
        return enabled == null ? Boolean.TRUE : enabled;
    }

    
    
}
