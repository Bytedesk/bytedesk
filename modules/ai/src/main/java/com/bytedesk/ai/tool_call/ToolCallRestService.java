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
package com.bytedesk.ai.tool_call;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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
import com.bytedesk.ai.tool.ToolEntity;
import com.bytedesk.ai.tool.ToolRepository;
import com.bytedesk.ai.tool.ToolTypeEnum;
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
public class ToolCallRestService extends BaseRestServiceWithExport<ToolCallEntity, ToolCallRequest, ToolCallResponse, ToolCallExcel> {

    private final ToolCallRepository toolCallRepository;

    private final ToolRepository toolRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final PermissionService permissionService;

    @Override
    public Page<ToolCallEntity> queryByOrgEntity(ToolCallRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ToolCallEntity> specs = ToolCallSpecification.search(request, authService);
        return toolCallRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ToolCallResponse> queryByOrg(ToolCallRequest request) {
        Page<ToolCallEntity> toolCallPage = queryByOrgEntity(request);
        return toolCallPage.map(this::convertToResponse);
    }

    @Override
    public Page<ToolCallResponse> queryByUser(ToolCallRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tool_call", key = "#uid", unless = "#result==null")
    @Override
    public Optional<ToolCallEntity> findByUid(String uid) {
        return toolCallRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return toolCallRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ToolCallResponse create(ToolCallRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ToolCallResponse createSystemToolCall(ToolCallRequest request) {
        return createInternal(request, true);
    }

    private ToolCallResponse createInternal(ToolCallRequest request, boolean skipPermissionCheck) {
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

        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ToolCallPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的工具调用记录");
        }

        ToolCallEntity entity = modelMapper.map(request, ToolCallEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        normalizeEntity(entity);
        ToolCallEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tool_call failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ToolCallResponse update(ToolCallRequest request) {
        Optional<ToolCallEntity> optional = toolCallRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ToolCallEntity entity = optional.get();

            if (!permissionService.hasEntityPermission(ToolCallPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该工具调用记录");
            }

            modelMapper.map(request, entity);
            normalizeEntity(entity);
            ToolCallEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tool_call failed");
            }
            return convertToResponse(savedEntity);
        }
        throw new RuntimeException("ToolCall not found");
    }

    @Override
    protected ToolCallEntity doSave(ToolCallEntity entity) {
        return toolCallRepository.save(entity);
    }

    @Override
    public ToolCallEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            ToolCallEntity entity) {
        try {
            Optional<ToolCallEntity> latest = toolCallRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ToolCallEntity latestEntity = latest.get();
                copyMutableFields(latestEntity, entity);
                return toolCallRepository.save(latestEntity);
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
        Optional<ToolCallEntity> optional = toolCallRepository.findByUid(uid);
        if (optional.isPresent()) {
            ToolCallEntity entity = optional.get();

            if (!permissionService.hasEntityPermission(ToolCallPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该工具调用记录");
            }

            entity.setDeleted(true);
            save(entity);
            return;
        }
        throw new RuntimeException("ToolCall not found");
    }

    @Override
    public void delete(ToolCallRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ToolCallResponse convertToResponse(ToolCallEntity entity) {
        return modelMapper.map(entity, ToolCallResponse.class);
    }

    @Override
    public ToolCallExcel convertToExcel(ToolCallEntity entity) {
        return modelMapper.map(entity, ToolCallExcel.class);
    }

    @Override
    protected Specification<ToolCallEntity> createSpecification(ToolCallRequest request) {
        return ToolCallSpecification.search(request, authService);
    }

    @Override
    protected Page<ToolCallEntity> executePageQuery(Specification<ToolCallEntity> spec, Pageable pageable) {
        return toolCallRepository.findAll(spec, pageable);
    }

    @Transactional
    public ToolCallEntity createRuntimeRecord(ToolEntity toolEntity, String runtimeToolName, String requestPayload,
            Map<String, Object> context) {
        UserEntity user = authService.getUser();
        boolean requiresApproval = toolEntity != null && Boolean.TRUE.equals(toolEntity.getRequiresApproval());
        ZonedDateTime now = BdDateUtils.now();

        ToolCallEntity entity = ToolCallEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(resolveOrgUid(toolEntity, user, context))
                .userUid(user != null ? user.getUid() : null)
                .level(LevelEnum.ORGANIZATION.name())
                .toolUid(toolEntity != null ? toolEntity.getUid() : null)
                .toolKey(toolEntity != null && StringUtils.hasText(toolEntity.getKey()) ? toolEntity.getKey() : runtimeToolName)
            .runtimeToolName(runtimeToolName)
                .bindingType(toolEntity != null ? toolEntity.getBindingType() : null)
                .name(resolveDisplayName(toolEntity, runtimeToolName))
                .description(toolEntity != null && StringUtils.hasText(toolEntity.getDescription()) ? toolEntity.getDescription() : I18Consts.I18N_DESCRIPTION)
                .type(toolEntity != null && StringUtils.hasText(toolEntity.getType()) ? toolEntity.getType() : ToolTypeEnum.CUSTOM.name())
                .provider(valueFromContext(context, "provider"))
                .model(valueFromContext(context, "model"))
                .robotUid(valueFromContext(context, "robotUid"))
                .threadUid(valueFromContext(context, "threadUid"))
                .messageUid(valueFromContext(context, "messageUid"))
                .status(requiresApproval ? ToolCallStatusEnum.PENDING_APPROVAL.name() : ToolCallStatusEnum.PENDING.name())
                .requiresApproval(requiresApproval)
                .approved(!requiresApproval)
                .requestPayload(requestPayload)
                .toolContext(toJson(context))
                .startedAt(now)
                .build();
        normalizeEntity(entity);
        return save(entity);
    }

    @Transactional(readOnly = true)
    public ToolEntity resolveRuntimeToolEntity(String runtimeToolName, String orgUid) {
        if (!StringUtils.hasText(runtimeToolName)) {
            return null;
        }

        String normalizedRuntimeToolName = runtimeToolName.trim();
        List<ToolEntity> candidates = toolRepository.findAll().stream()
                .filter(tool -> tool != null && !tool.isDeleted())
                .filter(tool -> !StringUtils.hasText(orgUid)
                        || !StringUtils.hasText(tool.getOrgUid())
                        || orgUid.equals(tool.getOrgUid()))
                .toList();

        return candidates.stream()
                .filter(tool -> matchesRuntimeTool(tool, normalizedRuntimeToolName))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void completeRuntimeRecord(String uid, String responsePayload, long durationMs) {
        updateRuntimeRecord(uid, entity -> {
            entity.setResponsePayload(responsePayload);
            entity.setDurationMs(durationMs);
            entity.setStatus(ToolCallStatusEnum.SUCCESS.name());
            entity.setCompletedAt(BdDateUtils.now());
        });
    }

    @Transactional
    public void failRuntimeRecord(String uid, String errorMessage, long durationMs) {
        updateRuntimeRecord(uid, entity -> {
            entity.setErrorMessage(errorMessage);
            entity.setDurationMs(durationMs);
            entity.setStatus(ToolCallStatusEnum.FAILED.name());
            entity.setCompletedAt(BdDateUtils.now());
        });
    }

    @Transactional
    public void linkAudit(String uid, String auditUid) {
        updateRuntimeRecord(uid, entity -> entity.setAuditUid(auditUid));
    }

    @Transactional
    public void markApprovalDecision(String uid, boolean approved) {
        updateRuntimeRecord(uid, entity -> {
            entity.setApproved(approved);
            entity.setStatus(approved ? ToolCallStatusEnum.APPROVED.name() : ToolCallStatusEnum.REJECTED.name());
            entity.setCompletedAt(BdDateUtils.now());
        });
    }

    @Transactional
    public void beginApprovedExecution(String uid) {
        updateRuntimeRecord(uid, entity -> {
            entity.setApproved(true);
            entity.setStatus(ToolCallStatusEnum.PENDING.name());
            entity.setResponsePayload(null);
            entity.setErrorMessage(null);
            entity.setDurationMs(0L);
            entity.setStartedAt(BdDateUtils.now());
            entity.setCompletedAt(null);
        });
    }

    private void updateRuntimeRecord(String uid, Consumer<ToolCallEntity> consumer) {
        if (!StringUtils.hasText(uid)) {
            return;
        }
        toolCallRepository.findByUid(uid).ifPresent(entity -> {
            consumer.accept(entity);
            normalizeEntity(entity);
            save(entity);
        });
    }

    private void normalizeEntity(ToolCallEntity entity) {
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
            entity.setStatus(ToolCallStatusEnum.PENDING.name());
        }
        if (entity.getRequiresApproval() == null) {
            entity.setRequiresApproval(false);
        }
        if (entity.getApproved() == null) {
            entity.setApproved(false);
        }
        if (entity.getDurationMs() == null) {
            entity.setDurationMs(0L);
        }
    }

    private boolean matchesRuntimeTool(ToolEntity tool, String runtimeToolName) {
        if (tool == null || !StringUtils.hasText(runtimeToolName)) {
            return false;
        }
        return equalsIgnoreCase(tool.getKey(), runtimeToolName)
                || equalsIgnoreCase(tool.getName(), runtimeToolName)
                || equalsIgnoreCase(tool.getMethodName(), runtimeToolName)
                || equalsIgnoreCase(tool.getBeanName(), runtimeToolName);
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return StringUtils.hasText(left) && StringUtils.hasText(right) && left.trim().equalsIgnoreCase(right.trim());
    }

    private void copyMutableFields(ToolCallEntity target, ToolCallEntity source) {
        target.setToolUid(source.getToolUid());
        target.setToolKey(source.getToolKey());
        target.setRuntimeToolName(source.getRuntimeToolName());
        target.setBindingType(source.getBindingType());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setProvider(source.getProvider());
        target.setModel(source.getModel());
        target.setRobotUid(source.getRobotUid());
        target.setThreadUid(source.getThreadUid());
        target.setMessageUid(source.getMessageUid());
        target.setStatus(source.getStatus());
        target.setRequiresApproval(source.getRequiresApproval());
        target.setApproved(source.getApproved());
        target.setAuditUid(source.getAuditUid());
        target.setDurationMs(source.getDurationMs());
        target.setRequestPayload(source.getRequestPayload());
        target.setResponsePayload(source.getResponsePayload());
        target.setErrorMessage(source.getErrorMessage());
        target.setToolContext(source.getToolContext());
        target.setStartedAt(source.getStartedAt());
        target.setCompletedAt(source.getCompletedAt());
        normalizeEntity(target);
    }

    private String resolveDisplayName(ToolEntity toolEntity, String runtimeToolName) {
        if (toolEntity != null && StringUtils.hasText(toolEntity.getName())) {
            return toolEntity.getName();
        }
        return runtimeToolName;
    }

    private String resolveOrgUid(ToolEntity toolEntity, UserEntity user, Map<String, Object> context) {
        if (toolEntity != null && StringUtils.hasText(toolEntity.getOrgUid())) {
            return toolEntity.getOrgUid();
        }
        if (user != null && StringUtils.hasText(user.getOrgUid())) {
            return user.getOrgUid();
        }
        return valueFromContext(context, "orgUid");
    }

    private String valueFromContext(Map<String, Object> context, String key) {
        if (context == null || !context.containsKey(key) || context.get(key) == null) {
            return null;
        }
        return String.valueOf(context.get(key));
    }

    private String toJson(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return null;
        }
        return JSON.toJSONString(context);
    }

    public void initToolCalls(String orgUid) {
        log.debug("Tool call logs are runtime-generated; no initializer seeding for orgUid={}", orgUid);
    }
}
