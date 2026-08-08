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
package com.bytedesk.ai.tool_audit;

import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.tool.ToolInvocationAuditService;
import com.bytedesk.ai.tool.ToolTypeEnum;
import com.bytedesk.ai.tool_call.ToolCallEntity;
import com.bytedesk.ai.tool_call.ToolCallRepository;
import com.bytedesk.ai.tool_call.ToolCallStatusEnum;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ToolAuditRestService extends BaseRestServiceWithExport<ToolAuditEntity, ToolAuditRequest, ToolAuditResponse, ToolAuditExcel> {

    private final ToolAuditRepository toolAuditRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    private final ToolCallRepository toolCallRepository;

    private final ObjectProvider<ToolInvocationAuditService> toolInvocationAuditServiceProvider;
    
    @Override
    public Page<ToolAuditEntity> queryByOrgEntity(ToolAuditRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ToolAuditEntity> specs = ToolAuditSpecification.search(request, authService);
        return toolAuditRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ToolAuditResponse> queryByOrg(ToolAuditRequest request) {
        Page<ToolAuditEntity> toolAuditPage = queryByOrgEntity(request);
        return toolAuditPage.map(this::convertToResponse);
    }

    @Override
    public Page<ToolAuditResponse> queryByUser(ToolAuditRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tool_audit", key = "#uid", unless = "#result==null")
    @Override
    public Optional<ToolAuditEntity> findByUid(String uid) {
        return toolAuditRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return toolAuditRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ToolAuditResponse create(ToolAuditRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ToolAuditResponse createSystemToolAudit(ToolAuditRequest request) {
        return createInternal(request, true);
    }

    private ToolAuditResponse createInternal(ToolAuditRequest request, boolean skipPermissionCheck) {
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
            if (!StringUtils.hasText(request.getOrgUid())) {
                request.setOrgUid(user.getOrgUid());
            }
        }

        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }

        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ToolAuditPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的工具审批记录");
        }

        ToolAuditEntity entity = modelMapper.map(request, ToolAuditEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        normalizeEntity(entity);
        ToolAuditEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tool_audit failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ToolAuditResponse update(ToolAuditRequest request) {
        Optional<ToolAuditEntity> optional = toolAuditRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ToolAuditEntity entity = optional.get();
            
            if (!permissionService.hasEntityPermission(ToolAuditPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该工具审批记录");
            }
            
            String previousStatus = entity.getStatus();
            modelMapper.map(request, entity);
            boolean shouldExecuteApprovedCall = applyAuditDecisionMetadata(entity, previousStatus);
            normalizeEntity(entity);
            ToolAuditEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tool_audit failed");
            }

            if (shouldExecuteApprovedCall) {
                ToolInvocationAuditService toolInvocationAuditService = toolInvocationAuditServiceProvider.getIfAvailable();
                if (toolInvocationAuditService != null) {
                    toolInvocationAuditService.executeApprovedToolCall(savedEntity.getToolCallUid());
                }
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ToolAudit not found");
        }
    }

    @Override
    protected ToolAuditEntity doSave(ToolAuditEntity entity) {
        return toolAuditRepository.save(entity);
    }

    @Override
    public ToolAuditEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ToolAuditEntity entity) {
        try {
            Optional<ToolAuditEntity> latest = toolAuditRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ToolAuditEntity latestEntity = latest.get();
                copyMutableFields(latestEntity, entity);
                return toolAuditRepository.save(latestEntity);
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
        Optional<ToolAuditEntity> optional = toolAuditRepository.findByUid(uid);
        if (optional.isPresent()) {
            ToolAuditEntity entity = optional.get();
            
            if (!permissionService.hasEntityPermission(ToolAuditPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该工具审批记录");
            }
            
            entity.setDeleted(true);
            save(entity);
            return;
        }
        throw new RuntimeException("ToolAudit not found");
    }

    @Override
    public void delete(ToolAuditRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ToolAuditResponse convertToResponse(ToolAuditEntity entity) {
        return modelMapper.map(entity, ToolAuditResponse.class);
    }

    @Override
    public ToolAuditExcel convertToExcel(ToolAuditEntity entity) {
        return modelMapper.map(entity, ToolAuditExcel.class);
    }

    @Override
    protected Specification<ToolAuditEntity> createSpecification(ToolAuditRequest request) {
        return ToolAuditSpecification.search(request, authService);
    }

    @Override
    protected Page<ToolAuditEntity> executePageQuery(Specification<ToolAuditEntity> spec, Pageable pageable) {
        return toolAuditRepository.findAll(spec, pageable);
    }

    @Transactional
    public ToolAuditEntity createApprovalRequest(ToolCallEntity toolCallEntity, Map<String, Object> context) {
        ToolAuditEntity entity = ToolAuditEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(toolCallEntity.getOrgUid())
                .userUid(toolCallEntity.getUserUid())
                .level(LevelEnum.ORGANIZATION.name())
                .toolCallUid(toolCallEntity.getUid())
                .toolUid(toolCallEntity.getToolUid())
                .toolKey(toolCallEntity.getToolKey())
                .name(toolCallEntity.getName())
                .description(toolCallEntity.getDescription())
                .type(toolCallEntity.getType())
                .status(ToolAuditStatusEnum.PENDING.name())
                .action("SUBMITTED")
                .approved(false)
                .requesterUserUid(toolCallEntity.getUserUid())
                .requestPayload(toolCallEntity.getRequestPayload())
                .auditContext(toJson(context))
                .build();
        normalizeEntity(entity);
        return save(entity);
    }

    private boolean applyAuditDecisionMetadata(ToolAuditEntity entity, String previousStatus) {
        if (!StringUtils.hasText(entity.getStatus()) || entity.getStatus().equals(previousStatus)) {
            return false;
        }

        UserEntity user = authService.getUser();
        boolean approved = ToolAuditStatusEnum.APPROVED.name().equalsIgnoreCase(entity.getStatus());
        boolean rejected = ToolAuditStatusEnum.REJECTED.name().equalsIgnoreCase(entity.getStatus());
        if (!approved && !rejected) {
            return false;
        }

        entity.setApproved(approved);
        entity.setAction(approved ? "APPROVED" : "REJECTED");
        entity.setAuditedAt(BdDateUtils.now());
        if (user != null) {
            entity.setApproverUserUid(user.getUid());
        }

        if (StringUtils.hasText(entity.getToolCallUid())) {
            toolCallRepository.findByUid(entity.getToolCallUid()).ifPresent(toolCall -> {
                toolCall.setApproved(approved);
                toolCall.setStatus(approved ? ToolCallStatusEnum.APPROVED.name() : ToolCallStatusEnum.REJECTED.name());
                toolCall.setCompletedAt(BdDateUtils.now());
                toolCallRepository.save(toolCall);
            });
        }

        return approved && StringUtils.hasText(entity.getToolCallUid());
    }

    private void normalizeEntity(ToolAuditEntity entity) {
        if (!StringUtils.hasText(entity.getName())) {
            entity.setName(entity.getToolKey());
        }
        if (!StringUtils.hasText(entity.getDescription())) {
            entity.setDescription(I18Consts.I18N_DESCRIPTION);
        }
        if (!StringUtils.hasText(entity.getType())) {
            entity.setType(ToolTypeEnum.CUSTOM.name());
        }
        if (!StringUtils.hasText(entity.getStatus())) {
            entity.setStatus(ToolAuditStatusEnum.PENDING.name());
        }
        if (!StringUtils.hasText(entity.getAction())) {
            entity.setAction("SUBMITTED");
        }
        if (entity.getApproved() == null) {
            entity.setApproved(false);
        }
    }

    private void copyMutableFields(ToolAuditEntity target, ToolAuditEntity source) {
        target.setToolCallUid(source.getToolCallUid());
        target.setToolUid(source.getToolUid());
        target.setToolKey(source.getToolKey());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setStatus(source.getStatus());
        target.setAction(source.getAction());
        target.setApproved(source.getApproved());
        target.setRequesterUserUid(source.getRequesterUserUid());
        target.setApproverUserUid(source.getApproverUserUid());
        target.setRequestPayload(source.getRequestPayload());
        target.setDecisionComment(source.getDecisionComment());
        target.setAuditContext(source.getAuditContext());
        target.setAuditedAt(source.getAuditedAt());
        normalizeEntity(target);
    }

    private String toJson(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return null;
        }
        return JSON.toJSONString(context);
    }

    public void initToolAudits(String orgUid) {
        log.debug("Tool audit logs are runtime-generated; no initializer seeding for orgUid={}", orgUid);
    }
}
