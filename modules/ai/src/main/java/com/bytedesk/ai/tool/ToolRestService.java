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
package com.bytedesk.ai.tool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

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
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.robot_settings.tools.RobotToolConfig;
import com.bytedesk.ai.tool.utils.McpExposureModeEnum;
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
public class ToolRestService extends BaseRestServiceWithExport<ToolEntity, ToolRequest, ToolResponse, ToolExcel> {

    private static final Pattern READ_ONLY_TOOL_PATTERN = Pattern.compile(".*(Query|Search|Find|Get|List|Count).*",
            Pattern.CASE_INSENSITIVE);

    private final ToolRepository toolRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ToolEntity> queryByOrgEntity(ToolRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ToolEntity> specs = ToolSpecification.search(request, authService);
        return toolRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ToolResponse> queryByOrg(ToolRequest request) {
        Page<ToolEntity> toolPage = queryByOrgEntity(request);
        return toolPage.map(this::convertToResponse);
    }

    @Override
    public Page<ToolResponse> queryByUser(ToolRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tool", key = "#uid", unless="#result==null")
    @Override
    public Optional<ToolEntity> findByUid(String uid) {
        return toolRepository.findByUid(uid);
    }

    @Cacheable(value = "tool", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ToolEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return toolRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    @Cacheable(value = "tool", key = "#key + '_' + #orgUid", unless="#result==null")
    public Optional<ToolEntity> findByKeyAndOrgUid(String key, String orgUid) {
        return toolRepository.findByKeyAndOrgUidAndDeletedFalse(key, orgUid);
    }

    public Optional<ToolEntity> resolveRuntimeTool(String runtimeToolName) {
        UserEntity user = authService.getUser();
        return resolveRuntimeTool(runtimeToolName, user != null ? user.getOrgUid() : null);
    }

    public Optional<ToolEntity> resolveRuntimeTool(String runtimeToolName, String orgUid) {
        if (!StringUtils.hasText(runtimeToolName)) {
            return Optional.empty();
        }

        String normalizedToolName = runtimeToolName.trim();
        List<ToolEntity> matchingTools = toolRepository.findAll().stream()
                .filter(tool -> tool != null && !tool.isDeleted())
                .filter(tool -> matchesRuntimeTool(tool, normalizedToolName))
                .toList();

        if (matchingTools.isEmpty()) {
            return Optional.empty();
        }

        if (StringUtils.hasText(orgUid)) {
            Optional<ToolEntity> orgTool = matchingTools.stream()
                    .filter(tool -> orgUid.equals(tool.getOrgUid()))
                    .findFirst();
            if (orgTool.isPresent()) {
                return orgTool;
            }
        }

        Optional<ToolEntity> platformTool = matchingTools.stream()
                .filter(tool -> LevelEnum.PLATFORM.name().equalsIgnoreCase(tool.getLevel()))
                .filter(tool -> BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(tool.getOrgUid()))
                .findFirst();
        if (platformTool.isPresent()) {
            return platformTool;
        }

        return matchingTools.stream().findFirst();
    }

    public boolean isRuntimeToolEnabled(String runtimeToolName) {
        UserEntity user = authService.getUser();
        return isRuntimeToolEnabled(runtimeToolName, user != null ? user.getOrgUid() : null);
    }

    public boolean isRuntimeToolEnabled(String runtimeToolName, String orgUid) {
        Optional<ToolEntity> toolEntity = resolveRuntimeTool(runtimeToolName, orgUid);
        return toolEntity.map(tool -> !Boolean.FALSE.equals(tool.getEnabled())).orElse(true);
    }

    public List<ToolEntity> listPlatformTools(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            return List.of();
        }
        return toolRepository.findAllByOrgUidAndLevelAndDeletedFalse(orgUid, LevelEnum.PLATFORM.name());
    }

    public boolean isToolExposedToMcp(ToolEntity toolEntity, String runtimeToolName) {
        if (toolEntity == null || Boolean.FALSE.equals(toolEntity.getEnabled())) {
            return false;
        }

        McpExposureModeEnum exposureMode = resolveMcpExposureMode(toolEntity.getMcpExposureMode());
        if (exposureMode == McpExposureModeEnum.NONE) {
            return false;
        }

        if (!isAllowedMethod(toolEntity, runtimeToolName)) {
            return false;
        }

        if (exposureMode == McpExposureModeEnum.READONLY) {
            return isReadOnlyToolName(runtimeToolName)
                    || isReadOnlyToolName(toolEntity.getMethodName())
                    || isReadOnlyToolName(toolEntity.getName());
        }

        return true;
    }

    public Boolean existsByUid(String uid) {
        return toolRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ToolResponse create(ToolRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ToolResponse createSystemTool(ToolRequest request) {
        return createInternal(request, true);
    }

    @Transactional
    public ToolResponse syncSystemTool(ToolRequest request) {
        if (!StringUtils.hasText(request.getKey())) {
            throw new IllegalArgumentException("tool key is required");
        }
        if (!StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required");
        }

        Optional<ToolEntity> optional = findByKeyAndOrgUid(request.getKey(), request.getOrgUid());
        if (optional.isEmpty()) {
            return createSystemTool(request);
        }

        ToolEntity entity = optional.get();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setType(request.getType());
        entity.setBindingType(request.getBindingType());
        entity.setBeanName(request.getBeanName());
        entity.setClassName(request.getClassName());
        entity.setMethodName(request.getMethodName());
        entity.setEndpoint(request.getEndpoint());
        entity.setInputSchema(request.getInputSchema());
        entity.setOutputSchema(request.getOutputSchema());
        entity.setSystemPrompt(request.getSystemPrompt());
        entity.setLevel(request.getLevel());
        entity.setOrgUid(request.getOrgUid());
        entity.setDeleted(false);
        entity.setMetadata(mergeSystemMetadata(entity.getMetadata(), request.getMetadata(), false));
        normalizeEntity(entity, request);

        ToolEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Sync tool failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    public void disableStalePlatformSystemTools(String orgUid, Set<String> activeKeys) {
        List<ToolEntity> toolEntities = toolRepository.findAllByOrgUidAndLevelAndDeletedFalse(orgUid, LevelEnum.PLATFORM.name());
        Set<String> normalizedKeys = activeKeys == null ? Set.of() : new HashSet<>(activeKeys);
        for (ToolEntity entity : toolEntities) {
            if (!isCodeSyncedTool(entity)) {
                continue;
            }
            if (normalizedKeys.contains(entity.getKey())) {
                entity.setMetadata(mergeSystemMetadata(entity.getMetadata(), null, false));
                save(entity);
                continue;
            }
            entity.setEnabled(false);
            entity.setMetadata(mergeSystemMetadata(entity.getMetadata(), null, true));
            save(entity);
        }
    }

    private ToolResponse createInternal(ToolRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        if (StringUtils.hasText(request.getKey()) && StringUtils.hasText(request.getOrgUid())) {
            Optional<ToolEntity> tool = findByKeyAndOrgUid(request.getKey(), request.getOrgUid());
            if (tool.isPresent()) {
                return convertToResponse(tool.get());
            }
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ToolEntity> tool = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tool.isPresent()) {
                return convertToResponse(tool.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ToolPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ToolEntity entity = modelMapper.map(request, ToolEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        normalizeEntity(entity, request);
        // 
        ToolEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tool failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ToolResponse update(ToolRequest request) {
        Optional<ToolEntity> optional = toolRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ToolEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ToolPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            normalizeEntity(entity, request);
            //
            ToolEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tool failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Tool not found");
        }
    }

    @Override
    protected ToolEntity doSave(ToolEntity entity) {
        return toolRepository.save(entity);
    }

    @Override
    public ToolEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ToolEntity entity) {
        try {
            Optional<ToolEntity> latest = toolRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ToolEntity latestEntity = latest.get();
                copyMutableFields(latestEntity, entity);
                return toolRepository.save(latestEntity);
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
        Optional<ToolEntity> optional = toolRepository.findByUid(uid);
        if (optional.isPresent()) {
            ToolEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ToolPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // toolRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Tool not found");
        }
    }

    @Override
    public void delete(ToolRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ToolResponse convertToResponse(ToolEntity entity) {
        return modelMapper.map(entity, ToolResponse.class);
    }

    @Override
    public ToolExcel convertToExcel(ToolEntity entity) {
        return modelMapper.map(entity, ToolExcel.class);
    }

    @Override
    protected Specification<ToolEntity> createSpecification(ToolRequest request) {
        return ToolSpecification.search(request, authService);
    }

    @Override
    protected Page<ToolEntity> executePageQuery(Specification<ToolEntity> spec, Pageable pageable) {
        return toolRepository.findAll(spec, pageable);
    }

    private void normalizeEntity(ToolEntity entity, ToolRequest request) {
        if (!StringUtils.hasText(entity.getKey())) {
            entity.setKey(request.getName());
        }
        if (!StringUtils.hasText(entity.getType())) {
            entity.setType(ToolTypeEnum.CUSTOM.name());
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        if (entity.getOrderIndex() == null) {
            entity.setOrderIndex(0);
        }
        if (entity.getRequiresApproval() == null) {
            entity.setRequiresApproval(false);
        }
        if (!StringUtils.hasText(entity.getIntentMatchMode())) {
            entity.setIntentMatchMode("KEYWORD");
        }
        if (!StringUtils.hasText(entity.getMcpExposureMode())) {
            entity.setMcpExposureMode(McpExposureModeEnum.NONE.name());
        }
        if (StringUtils.hasText(entity.getAllowedMethods())) {
            entity.setAllowedMethods(entity.getAllowedMethods().trim());
        }
        if (!StringUtils.hasText(entity.getMetadata())) {
            entity.setMetadata(request.getMetadata());
        }
    }

    private void copyMutableFields(ToolEntity target, ToolEntity source) {
        target.setKey(source.getKey());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setCategory(source.getCategory());
        target.setIcon(source.getIcon());
        target.setEnabled(source.getEnabled());
        target.setBindingType(source.getBindingType());
        target.setBeanName(source.getBeanName());
        target.setClassName(source.getClassName());
        target.setMethodName(source.getMethodName());
        target.setEndpoint(source.getEndpoint());
        target.setInputSchema(source.getInputSchema());
        target.setOutputSchema(source.getOutputSchema());
        target.setSystemPrompt(source.getSystemPrompt());
        target.setOrderIndex(source.getOrderIndex());
        target.setRequiresApproval(source.getRequiresApproval());
        target.setIntentKeywords(source.getIntentKeywords());
        target.setIntentMatchMode(source.getIntentMatchMode());
        target.setMcpExposureMode(source.getMcpExposureMode());
        target.setAllowedMethods(source.getAllowedMethods());
        target.setMetadata(source.getMetadata());
        normalizeEntity(target, new ToolRequest());
    }

    private boolean isCodeSyncedTool(ToolEntity entity) {
        JSONObject metadata = parseMetadata(entity.getMetadata());
        return "CODE_SYNC".equalsIgnoreCase(metadata.getString("registrySource"));
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

    private McpExposureModeEnum resolveMcpExposureMode(String rawMode) {
        if (!StringUtils.hasText(rawMode)) {
            return McpExposureModeEnum.NONE;
        }
        try {
            return McpExposureModeEnum.valueOf(rawMode.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return McpExposureModeEnum.NONE;
        }
    }

    private boolean isAllowedMethod(ToolEntity toolEntity, String runtimeToolName) {
        if (!StringUtils.hasText(toolEntity.getAllowedMethods())) {
            return true;
        }

        Set<String> allowedMethods = Arrays.stream(toolEntity.getAllowedMethods().split("[,\n\r]+"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());
        if (allowedMethods.isEmpty()) {
            return true;
        }

        return matchesAllowedMethod(allowedMethods, runtimeToolName)
                || matchesAllowedMethod(allowedMethods, toolEntity.getMethodName())
                || matchesAllowedMethod(allowedMethods, toolEntity.getName())
                || matchesAllowedMethod(allowedMethods, toolEntity.getKey());
    }

    private boolean matchesAllowedMethod(Set<String> allowedMethods, String candidate) {
        return StringUtils.hasText(candidate) && allowedMethods.contains(candidate.trim().toLowerCase());
    }

    private boolean isReadOnlyToolName(String toolName) {
        return StringUtils.hasText(toolName) && READ_ONLY_TOOL_PATTERN.matcher(toolName.trim()).matches();
    }

    private String mergeSystemMetadata(String existingMetadata, String requestMetadata, boolean stale) {
        JSONObject metadata = parseMetadata(existingMetadata);
        if (StringUtils.hasText(requestMetadata)) {
            JSONObject requestObject = parseMetadata(requestMetadata);
            metadata.putAll(requestObject);
        }
        metadata.put("registrySource", "CODE_SYNC");
        metadata.put("stale", stale);
        return metadata.toJSONString();
    }

    private JSONObject parseMetadata(String metadata) {
        if (!StringUtils.hasText(metadata)) {
            return new JSONObject();
        }
        try {
            return JSON.parseObject(metadata);
        } catch (Exception ignore) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("raw", metadata);
            return jsonObject;
        }
    }
    
    public void initTools(String orgUid) {
        for (RobotToolConfig config : RobotToolConfig.defaultSpringAiTools()) {
            ToolRequest toolRequest = ToolRequest.builder()
                    .key(config.getKey())
                    .name(config.getName())
                    .description(config.getDescription())
                    .type(ToolTypeEnum.BUILTIN.name())
                    .category(config.getCategory())
                    .icon(config.getIcon())
                    .enabled(config.getEnabled())
                    .bindingType(config.getBindingType())
                    .beanName(config.getBeanName())
                    .className(config.getClassName())
                    .methodName(config.getMethodName())
                    .endpoint(config.getEndpoint())
                    .inputSchema(config.getInputSchema())
                    .outputSchema(config.getOutputSchema())
                    .systemPrompt(config.getSystemPrompt())
                    .orderIndex(config.getOrderIndex())
                    .requiresApproval(config.getRequiresApproval())
                    .intentKeywords(config.getIntentKeywords() == null || config.getIntentKeywords().isEmpty()
                            ? null
                            : JSON.toJSONString(config.getIntentKeywords()))
                    .intentMatchMode(config.getIntentMatchMode())
                    .metadata(config.getMetadata() == null || config.getMetadata().isEmpty() ? null : JSON.toJSONString(config.getMetadata()))
                    .level(LevelEnum.ORGANIZATION.name())
                    .orgUid(orgUid)
                    .build();
            createSystemTool(toolRequest);
        }
    }

    
    
}
