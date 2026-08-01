/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 12:10:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import java.util.Optional;
import java.util.List;

import org.modelmapper.ModelMapper;
// import org.springframework.cache.Cache;
// import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.core.workflow_settings.WorkflowSettingsEntity;
import com.bytedesk.core.workflow_settings.WorkflowSettingsRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkflowRestService extends BaseRestService<WorkflowEntity, WorkflowRequest, WorkflowResponse> {

    private static final String CACHE_WORKFLOW = "workflow";

    private final WorkflowRepository workflowRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final WorkflowSettingsRestService workflowSettingsRestService;

    // private final CacheManager cacheManager;

    private static final String DEFAULT_NICKNAME = WorkflowInitData.DEFAULT_WORKFLOW_NAME;
    private static final String DEFAULT_DESCRIPTION = WorkflowInitData.DEFAULT_WORKFLOW_DESCRIPTION;
    private static final String DEFAULT_IVR_NICKNAME = WorkflowInitData.DEFAULT_IVR_WORKFLOW_NAME;
    private static final String DEFAULT_IVR_DESCRIPTION = WorkflowInitData.DEFAULT_IVR_WORKFLOW_DESCRIPTION;
    private static final String DEFAULT_IVR_SATISFACTION_NICKNAME = WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_NAME;
    private static final String DEFAULT_IVR_SATISFACTION_DESCRIPTION = WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_DESCRIPTION;
    private static final String DEFAULT_IVR_PASSWORD_VERIFICATION_NICKNAME = WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_NAME;
    private static final String DEFAULT_IVR_PASSWORD_VERIFICATION_DESCRIPTION = WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_DESCRIPTION;
    private static final String DEFAULT_IVR_BOT_NICKNAME = WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_NAME;
    private static final String DEFAULT_IVR_BOT_DESCRIPTION = WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_DESCRIPTION;

    @Override
    protected Specification<WorkflowEntity> createSpecification(WorkflowRequest request) {
        return WorkflowSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkflowEntity> executePageQuery(Specification<WorkflowEntity> spec, Pageable pageable) {
        return workflowRepository.findAll(spec, pageable);
    }

    public Page<WorkflowEntity> queryByOrgEntity(WorkflowRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WorkflowEntity> specs = WorkflowSpecification.search(request, authService);
        return workflowRepository.findAll(specs, pageable);
    }

    public List<WorkflowTemplateOptionResponse> queryChatDemoTemplateOptions() {
        return List.of(
            createTemplateOption(
                "demo-lead-collection",
                WorkflowInitData.DEFAULT_WORKFLOW_NAME,
                WorkflowInitData.DEFAULT_WORKFLOW_DESCRIPTION,
                WorkflowInitData.buildDefaultLeadCollectionWorkflowSchemaJson()));
    }

    public List<WorkflowTemplateOptionResponse> queryIvrDemoTemplateOptions() {
        return List.of(
            createTemplateOption(
                "demo-default",
                WorkflowInitData.DEFAULT_IVR_WORKFLOW_NAME,
                WorkflowInitData.DEFAULT_IVR_WORKFLOW_DESCRIPTION,
                WorkflowInitData.buildDefaultHotlineIvrWorkflowSchemaJson()),
            createTemplateOption(
                "demo-satisfaction",
                WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_NAME,
                WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_DESCRIPTION,
                WorkflowInitData.buildDefaultSatisfactionIvrWorkflowSchemaJson()),
            createTemplateOption(
                "demo-password-verification",
                WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_NAME,
                WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_DESCRIPTION,
                WorkflowInitData.buildDefaultPasswordVerificationIvrWorkflowSchemaJson()),
            createTemplateOption(
                "demo-bot",
                WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_NAME,
                WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_DESCRIPTION,
                WorkflowInitData.buildDefaultBotIvrWorkflowSchemaJson()));
            }
    
    // @Cacheable(value = CACHE_WORKFLOW, key = "#uid", unless = "#result == null || #result.isEmpty()")
    @Override
    public Optional<WorkflowEntity> findByUid(String uid) {
        Optional<WorkflowEntity> optional = workflowRepository.findByUid(uid);
        if (optional.isEmpty()) {
            return optional;
        }

        WorkflowEntity entity = optional.get();
        return Optional.of(refreshManagedDefaultWorkflowIfNeeded(entity));
    }

    @Override
    public WorkflowResponse create(WorkflowRequest request) {
        WorkflowEntity entity = modelMapper.map(request, WorkflowEntity.class);
        entity.setUid(uidUtils.getUid());
        if (!StringUtils.hasText(entity.getType())) {
            entity.setType(WorkflowTypeEnum.CHATBOT.name());
        }
        bindSettings(entity, request.getSettingsUid(), request.getOrgUid(), true);
        // 
        WorkflowEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        refreshWorkflowCache(savedEntity);
        return convertToResponse(savedEntity);
    }
    
    @Override
    @Transactional
    public WorkflowResponse update(WorkflowRequest request) {
        // 参数验证
        if (!StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("Workflow UID cannot be null or empty");
        }
        
        log.debug("开始更新工作流，UID: {}", request.getUid());
        
        Optional<WorkflowEntity> optional = workflowRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowEntity entity = optional.get();
            
            log.debug("找到工作流实体，ID: {}, 当前版本: {}", entity.getId(), entity.getVersion());
            
            // 使用ModelMapper将请求数据映射到实体，但需要保留一些重要字段
            String originalUid = entity.getUid();
            Long originalId = entity.getId();
            int originalVersion = entity.getVersion();
            
            // 映射请求数据到实体
            modelMapper.map(request, entity);
            
            // 手动处理字段名不一致的映射
            if (request.getCurrentNode() != null) {
                entity.setCurrentNodeId(request.getCurrentNode());
            }
            bindSettings(entity, request.getSettingsUid(), entity.getOrgUid(), false);
            
            // 恢复关键字段，防止被覆盖
            entity.setUid(originalUid);
            entity.setId(originalId);
            entity.setVersion(originalVersion);
            
            // 设置默认头像
            if (entity.getAvatar() == null || entity.getAvatar().isEmpty()) {
                entity.setAvatar(AvatarConsts.getDefaultWorkflowAvatar());
            }
            
            WorkflowEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            refreshWorkflowCache(savedEntity);
            log.debug("工作流更新成功，ID: {}, 新版本: {}", savedEntity.getId(), savedEntity.getVersion());
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException(I18Consts.withArgs(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID, request.getUid()));
        }
    }

    @Transactional
    public WorkflowResponse reset(WorkflowRequest request) {
        Assert.hasText(request.getUid(), "Workflow UID must not be empty");

        WorkflowEntity entity = workflowRepository.findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException(I18Consts.withArgs(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID,
                        request.getUid())));

        String resetSchema = resolveResetSchema(entity);
        String resetStartNodeId = resolveResetStartNodeId(entity);

        entity.setSchema(resetSchema);
        entity.setCurrentNodeId(resetStartNodeId);
        if (!StringUtils.hasText(entity.getType())) {
            entity.setType(resolveResetWorkflowType(entity).name());
        }

        WorkflowEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
        }
        refreshWorkflowCache(savedEntity);
        return convertToResponse(savedEntity);
    }

    @Override
    protected WorkflowEntity doSave(WorkflowEntity entity) {
        return workflowRepository.save(entity);
    }

    @CacheEvict(value = CACHE_WORKFLOW, key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<WorkflowEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @CacheEvict(value = CACHE_WORKFLOW, key = "#request.uid")
    @Override
    public void delete(WorkflowRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowEntity entity) {
        log.warn("处理工作流乐观锁冲突，实体ID: {}, UID: {}", entity.getId(), entity.getUid());
        
        try {
            // 重新获取最新的实体版本
            Optional<WorkflowEntity> latestOptional = workflowRepository.findByUid(entity.getUid());
            if (latestOptional.isPresent()) {
                WorkflowEntity latestEntity = latestOptional.get();
                
                // 将当前实体的修改应用到最新版本上
                if (entity.getNickname() != null) {
                    latestEntity.setNickname(entity.getNickname());
                }
                if (entity.getDescription() != null) {
                    latestEntity.setDescription(entity.getDescription());
                }
                if (entity.getAvatar() != null) {
                    latestEntity.setAvatar(entity.getAvatar());
                }
                if (entity.getSchema() != null) {
                    latestEntity.setSchema(entity.getSchema());
                }
                if (StringUtils.hasText(entity.getType())) {
                    latestEntity.setType(entity.getType());
                }
                if (entity.getCurrentNodeId() != null) {
                    latestEntity.setCurrentNodeId(entity.getCurrentNodeId());
                }
                latestEntity.setSettings(entity.getSettings());
                if (entity.getCategoryUid() != null) {
                    latestEntity.setCategoryUid(entity.getCategoryUid());
                }
                
                // 直接使用repository保存，避免递归调用save()方法
                WorkflowEntity savedEntity = workflowRepository.save(latestEntity);
                log.info("乐观锁冲突处理成功，实体ID: {}, 新版本: {}", savedEntity.getId(), savedEntity.getVersion());
                return savedEntity;
            } else {
                log.error("处理乐观锁冲突时未找到实体，UID: {}", entity.getUid());
                throw new RuntimeException("处理乐观锁冲突时未找到实体: " + entity.getUid());
            }
        } catch (ObjectOptimisticLockingFailureException retryException) {
            log.error("处理乐观锁冲突时再次发生冲突，实体UID: {}", entity.getUid());
            throw new RuntimeException("多次乐观锁冲突，请稍后重试", retryException);
        } catch (Exception ex) {
            log.error("处理乐观锁冲突时发生异常，实体UID: {}, 错误: {}", entity.getUid(), ex.getMessage(), ex);
            throw new RuntimeException("处理乐观锁冲突失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public WorkflowResponse convertToResponse(WorkflowEntity entity) {
        WorkflowResponse response = modelMapper.map(entity, WorkflowResponse.class);
        response.setCurrentNode(entity.getCurrentNodeId());
        WorkflowSettingsEntity settings = entity.getSettings();
        if (settings != null) {
            response.setSettingsUid(settings.getUid());
            response.setSettingsName(settings.getName());
            response.setSettingsDescription(settings.getDescription());
        }
        return response;
    }

    public WorkflowExcel convertToExcel(WorkflowEntity entity) {
        WorkflowExcel excel = new WorkflowExcel();
        excel.setName(entity.getNickname());
        excel.setType(entity.getType());
        excel.setContent(entity.getSchema());
        return excel;
    }

    private void bindSettings(WorkflowEntity entity, String settingsUid, String orgUid, boolean useDefaultWhenMissing) {
        if (!StringUtils.hasText(settingsUid)) {
            if (useDefaultWhenMissing) {
                entity.setSettings(workflowSettingsRestService.getOrCreateDefault(orgUid));
            }
            return;
        }
        WorkflowSettingsEntity settings = workflowSettingsRestService.findByUid(settingsUid)
                .orElseThrow(() -> new RuntimeException("Workflow settings not found with UID: " + settingsUid));
        entity.setSettings(settings);
    }

    @Transactional
    public WorkflowResponse initDefaultWorkflow(String orgUid) {
        WorkflowResponse defaultWorkflow = initDefaultChatbotWorkflow(orgUid);
        initDefaultIvrWorkflow(orgUid);
        return defaultWorkflow;
    }

    @Transactional
    public WorkflowResponse initDefaultIvrWorkflow(String orgUid) {
        Assert.hasText(orgUid, "Organization UID must not be empty");
        WorkflowResponse defaultResponse = initDefaultIvrWorkflowDefinition(
                orgUid,
                resolveDefaultIvrWorkflowUid(orgUid),
                DEFAULT_IVR_NICKNAME,
                DEFAULT_IVR_DESCRIPTION,
                WorkflowInitData.buildDefaultHotlineIvrWorkflowSchemaJson(),
                WorkflowInitData.DEFAULT_IVR_START_NODE_ID);

        initDefaultIvrWorkflowDefinition(
                orgUid,
                resolveDefaultIvrSatisfactionWorkflowUid(orgUid),
                DEFAULT_IVR_SATISFACTION_NICKNAME,
                DEFAULT_IVR_SATISFACTION_DESCRIPTION,
                WorkflowInitData.buildDefaultSatisfactionIvrWorkflowSchemaJson(),
                WorkflowInitData.DEFAULT_IVR_SATISFACTION_START_NODE_ID);

        initDefaultIvrWorkflowDefinition(
                orgUid,
                resolveDefaultIvrPasswordVerificationWorkflowUid(orgUid),
                DEFAULT_IVR_PASSWORD_VERIFICATION_NICKNAME,
                DEFAULT_IVR_PASSWORD_VERIFICATION_DESCRIPTION,
                WorkflowInitData.buildDefaultPasswordVerificationIvrWorkflowSchemaJson(),
                WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID);

        initDefaultIvrWorkflowDefinition(
            orgUid,
            resolveDefaultIvrBotWorkflowUid(orgUid),
            DEFAULT_IVR_BOT_NICKNAME,
            DEFAULT_IVR_BOT_DESCRIPTION,
            WorkflowInitData.buildDefaultBotIvrWorkflowSchemaJson(),
            WorkflowInitData.DEFAULT_IVR_BOT_START_NODE_ID);

        return defaultResponse;
    }

    private WorkflowResponse initDefaultIvrWorkflowDefinition(String orgUid, String workflowUid,
            String nickname, String description, String schema, String startNodeId) {
        Optional<WorkflowEntity> existing = workflowRepository.findByUid(workflowUid);
        if (existing.isPresent() && !existing.get().isDeleted()) {
            WorkflowEntity existingEntity = existing.get();
            if (!requiresDefaultIvrWorkflowRefresh(existingEntity, orgUid, nickname, description, schema,
                    startNodeId)) {
                log.debug("Default IVR workflow already initialized, org: {}, workflowUid: {}", orgUid, workflowUid);
                return convertToResponse(existingEntity);
            }

            applyDefaultIvrWorkflowDefinition(existingEntity, orgUid, nickname, description, schema, startNodeId);

            WorkflowEntity saved = save(existingEntity);
            if (saved == null) {
                throw new RuntimeException("Refresh default IVR workflow failed: " + workflowUid);
            }
            refreshWorkflowCache(saved);
            log.info("Refreshed default IVR workflow for org: {}, workflowUid: {}", orgUid, workflowUid);
            return convertToResponse(saved);
        }

        WorkflowEntity entity = existing.orElseGet(() -> WorkflowEntity.builder()
                .uid(workflowUid)
                .orgUid(orgUid)
                .build());
        applyDefaultIvrWorkflowDefinition(entity, orgUid, nickname, description, schema, startNodeId);

        WorkflowEntity saved = save(entity);
        if (saved == null) {
            throw new RuntimeException("Initialize default IVR workflow failed: " + workflowUid);
        }
        refreshWorkflowCache(saved);
        log.info("Initialized default IVR workflow for org: {}, workflowUid: {}", orgUid, workflowUid);
        return convertToResponse(saved);
    }

    private WorkflowEntity refreshManagedDefaultIvrWorkflowIfNeeded(WorkflowEntity entity) {
        DefaultIvrWorkflowDefinition definition = resolveManagedDefaultIvrDefinition(entity);
        if (definition == null || !requiresDefaultIvrWorkflowRefresh(entity,
                definition.orgUid(),
                definition.nickname(),
                definition.description(),
                definition.schema(),
                definition.startNodeId())) {
            return entity;
        }

        applyDefaultIvrWorkflowDefinition(entity,
                definition.orgUid(),
                definition.nickname(),
                definition.description(),
                definition.schema(),
                definition.startNodeId());

        WorkflowEntity saved = save(entity);
        if (saved == null) {
            throw new RuntimeException("Refresh managed default IVR workflow failed: " + entity.getUid());
        }
        refreshWorkflowCache(saved);
        log.info("Auto-refreshed managed default IVR workflow on read, org: {}, workflowUid: {}",
                definition.orgUid(),
                entity.getUid());
        return saved;
    }

    private WorkflowEntity refreshManagedDefaultWorkflowIfNeeded(WorkflowEntity entity) {
        WorkflowEntity refreshedEntity = refreshManagedDefaultChatbotWorkflowIfNeeded(entity);
        return refreshManagedDefaultIvrWorkflowIfNeeded(refreshedEntity);
    }

    private WorkflowEntity refreshManagedDefaultChatbotWorkflowIfNeeded(WorkflowEntity entity) {
        DefaultChatbotWorkflowDefinition definition = resolveManagedDefaultChatbotDefinition(entity);
        if (definition == null || !requiresDefaultChatbotWorkflowRefresh(entity,
                definition.orgUid(),
                definition.nickname(),
                definition.description(),
                definition.schema(),
                definition.startNodeId())) {
            return entity;
        }

        applyDefaultChatbotWorkflowDefinition(entity,
                definition.orgUid(),
                definition.nickname(),
                definition.description(),
                definition.schema(),
                definition.startNodeId());

        WorkflowEntity saved = save(entity);
        if (saved == null) {
            throw new RuntimeException("Refresh managed default chatbot workflow failed: " + entity.getUid());
        }
        refreshWorkflowCache(saved);
        log.info("Auto-refreshed managed default chatbot workflow on read, org: {}, workflowUid: {}",
                definition.orgUid(),
                entity.getUid());
        return saved;
    }

    private void applyDefaultIvrWorkflowDefinition(WorkflowEntity entity, String orgUid, String nickname,
            String description, String schema, String startNodeId) {
        entity.setDeleted(false);
        entity.setNickname(nickname);
        entity.setDescription(description);
        entity.setSchema(schema);
        entity.setCurrentNodeId(startNodeId);
        entity.setType(WorkflowTypeEnum.IVR.name());
        entity.setAvatar(AvatarConsts.getDefaultWorkflowAvatar());
        entity.setOrgUid(orgUid);
        entity.setSettings(workflowSettingsRestService.getOrCreateDefault(orgUid));
    }

    private boolean requiresDefaultIvrWorkflowRefresh(WorkflowEntity entity, String orgUid, String nickname,
            String description, String schema, String startNodeId) {
        return !StringUtils.pathEquals(orgUid, entity.getOrgUid())
                || !StringUtils.pathEquals(nickname, entity.getNickname())
                || !StringUtils.pathEquals(description, entity.getDescription())
                || !StringUtils.pathEquals(schema, entity.getSchema())
                || !StringUtils.pathEquals(startNodeId, entity.getCurrentNodeId())
                || !WorkflowTypeEnum.IVR.name().equals(entity.getType())
                || !StringUtils.pathEquals(AvatarConsts.getDefaultWorkflowAvatar(), entity.getAvatar())
                || entity.getSettings() == null;
    }

    private DefaultIvrWorkflowDefinition resolveManagedDefaultIvrDefinition(WorkflowEntity entity) {
        if (entity == null || !StringUtils.hasText(entity.getUid()) || !StringUtils.hasText(entity.getOrgUid())) {
            return null;
        }

        String uid = entity.getUid();
        String orgUid = entity.getOrgUid();

        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_WORKFLOW_UID_SUFFIX)) {
            return new DefaultIvrWorkflowDefinition(
                    orgUid,
                    DEFAULT_IVR_NICKNAME,
                    DEFAULT_IVR_DESCRIPTION,
                    WorkflowInitData.buildDefaultIvrWorkflowSchemaJson(),
                    WorkflowInitData.DEFAULT_IVR_START_NODE_ID);
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID_SUFFIX)) {
            return new DefaultIvrWorkflowDefinition(
                    orgUid,
                    DEFAULT_IVR_SATISFACTION_NICKNAME,
                    DEFAULT_IVR_SATISFACTION_DESCRIPTION,
                    WorkflowInitData.buildDefaultSatisfactionIvrWorkflowSchemaJson(),
                    WorkflowInitData.DEFAULT_IVR_SATISFACTION_START_NODE_ID);
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID_SUFFIX)) {
            return new DefaultIvrWorkflowDefinition(
                    orgUid,
                    DEFAULT_IVR_PASSWORD_VERIFICATION_NICKNAME,
                    DEFAULT_IVR_PASSWORD_VERIFICATION_DESCRIPTION,
                    WorkflowInitData.buildDefaultPasswordVerificationIvrWorkflowSchemaJson(),
                    WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID);
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_UID_SUFFIX)) {
            return new DefaultIvrWorkflowDefinition(
                    orgUid,
                    DEFAULT_IVR_BOT_NICKNAME,
                    DEFAULT_IVR_BOT_DESCRIPTION,
                    WorkflowInitData.buildDefaultBotIvrWorkflowSchemaJson(),
                    WorkflowInitData.DEFAULT_IVR_BOT_START_NODE_ID);
        }
        return null;
    }

    private record DefaultIvrWorkflowDefinition(String orgUid, String nickname, String description, String schema,
            String startNodeId) {
    }

    private record DefaultChatbotWorkflowDefinition(String orgUid, String nickname, String description, String schema,
            String startNodeId) {
    }

    private WorkflowResponse initDefaultChatbotWorkflow(String orgUid) {
        Assert.hasText(orgUid, "Organization UID must not be empty");
        String workflowUid = Utils.formatUid(orgUid, WorkflowInitData.DEFAULT_WORKFLOW_UID_SUFFIX);
        String schema = WorkflowInitData.buildDefaultLeadCollectionWorkflowSchemaJson();
        String startNodeId = WorkflowInitData.DEFAULT_START_NODE_ID;

        Optional<WorkflowEntity> existing = workflowRepository.findByUid(workflowUid);
        if (existing.isPresent() && !existing.get().isDeleted()) {
            WorkflowEntity existingEntity = existing.get();
            if (!requiresDefaultChatbotWorkflowRefresh(existingEntity, orgUid, DEFAULT_NICKNAME, DEFAULT_DESCRIPTION,
                    schema, startNodeId)) {
                log.debug("Default workflow already initialized for org: {}", orgUid);
                return convertToResponse(existingEntity);
            }

            applyDefaultChatbotWorkflowDefinition(existingEntity, orgUid, DEFAULT_NICKNAME, DEFAULT_DESCRIPTION,
                    schema, startNodeId);
            WorkflowEntity saved = save(existingEntity);
            if (saved == null) {
                throw new RuntimeException("Refresh default workflow failed");
            }
            refreshWorkflowCache(saved);
            log.info("Refreshed default workflow for org: {}", orgUid);
            return convertToResponse(saved);
        }

        WorkflowSchema workflowSchema = WorkflowSchema.fromJson(schema);
        if (!WorkflowUtils.validateWorkflowDocument(workflowSchema)) {
            throw new IllegalStateException("Default workflow schema validation failed");
        }

        WorkflowEntity entity = existing.orElseGet(() -> WorkflowEntity.builder()
                .uid(workflowUid)
                .orgUid(orgUid)
                .build());
        applyDefaultChatbotWorkflowDefinition(entity, orgUid, DEFAULT_NICKNAME, DEFAULT_DESCRIPTION, schema,
                startNodeId);

        WorkflowEntity saved = save(entity);
        if (saved == null) {
            throw new RuntimeException("Initialize default workflow failed");
        }
        refreshWorkflowCache(saved);
        log.info("Initialized default workflow for org: {}", orgUid);
        return convertToResponse(saved);
    }

    private void applyDefaultChatbotWorkflowDefinition(WorkflowEntity entity, String orgUid, String nickname,
            String description, String schema, String startNodeId) {
        entity.setDeleted(false);
        entity.setNickname(nickname);
        entity.setDescription(description);
        entity.setSchema(schema);
        entity.setCurrentNodeId(startNodeId);
        entity.setType(WorkflowTypeEnum.CHATBOT.name());
        entity.setAvatar(AvatarConsts.getDefaultWorkflowAvatar());
        entity.setOrgUid(orgUid);
        entity.setSettings(workflowSettingsRestService.getOrCreateDefault(orgUid));
    }

    private boolean requiresDefaultChatbotWorkflowRefresh(WorkflowEntity entity, String orgUid, String nickname,
            String description, String schema, String startNodeId) {
        return !StringUtils.pathEquals(orgUid, entity.getOrgUid())
                || !StringUtils.pathEquals(nickname, entity.getNickname())
                || !StringUtils.pathEquals(description, entity.getDescription())
                || !StringUtils.pathEquals(schema, entity.getSchema())
                || !StringUtils.pathEquals(startNodeId, entity.getCurrentNodeId())
                || !WorkflowTypeEnum.CHATBOT.name().equals(entity.getType())
                || !StringUtils.pathEquals(AvatarConsts.getDefaultWorkflowAvatar(), entity.getAvatar())
                || entity.getSettings() == null;
    }

    private DefaultChatbotWorkflowDefinition resolveManagedDefaultChatbotDefinition(WorkflowEntity entity) {
        if (entity == null || !StringUtils.hasText(entity.getUid()) || !StringUtils.hasText(entity.getOrgUid())) {
            return null;
        }

        String workflowUid = Utils.formatUid(entity.getOrgUid(), WorkflowInitData.DEFAULT_WORKFLOW_UID_SUFFIX);
        if (!workflowUid.equals(entity.getUid())) {
            return null;
        }

        return new DefaultChatbotWorkflowDefinition(
                entity.getOrgUid(),
                DEFAULT_NICKNAME,
                DEFAULT_DESCRIPTION,
                WorkflowInitData.buildDefaultLeadCollectionWorkflowSchemaJson(),
                WorkflowInitData.DEFAULT_START_NODE_ID);
    }

    private String resolveDefaultIvrWorkflowUid(String orgUid) {
        if (BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(orgUid)) {
            return BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID;
        }
        return Utils.formatUid(orgUid, WorkflowInitData.DEFAULT_IVR_WORKFLOW_UID_SUFFIX);
    }

    private String resolveDefaultIvrSatisfactionWorkflowUid(String orgUid) {
        if (BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(orgUid)) {
            return BytedeskConsts.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID;
        }
        return Utils.formatUid(orgUid, WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID_SUFFIX);
    }

    private String resolveDefaultIvrPasswordVerificationWorkflowUid(String orgUid) {
        if (BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(orgUid)) {
            return BytedeskConsts.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID;
        }
        return Utils.formatUid(orgUid, WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID_SUFFIX);
    }

    private String resolveDefaultIvrBotWorkflowUid(String orgUid) {
        if (BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(orgUid)) {
            return BytedeskConsts.DEFAULT_IVR_BOT_WORKFLOW_UID;
        }
        return Utils.formatUid(orgUid, WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_UID_SUFFIX);
    }

    private String resolveResetSchema(WorkflowEntity entity) {
        String uid = entity.getUid();
        String orgUid = entity.getOrgUid();

        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.buildDefaultLeadCollectionWorkflowSchemaJson();
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.buildDefaultHotlineIvrWorkflowSchemaJson();
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.buildDefaultSatisfactionIvrWorkflowSchemaJson();
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.buildDefaultPasswordVerificationIvrWorkflowSchemaJson();
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.buildDefaultBotIvrWorkflowSchemaJson();
        }
        throw new IllegalArgumentException("Workflow does not support demo reset: " + uid);
    }

    private String resolveResetStartNodeId(WorkflowEntity entity) {
        String uid = entity.getUid();
        String orgUid = entity.getOrgUid();

        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.DEFAULT_START_NODE_ID;
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.DEFAULT_IVR_START_NODE_ID;
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.DEFAULT_IVR_SATISFACTION_START_NODE_ID;
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID;
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_UID_SUFFIX)) {
            return WorkflowInitData.DEFAULT_IVR_BOT_START_NODE_ID;
        }
        throw new IllegalArgumentException("Workflow does not support demo reset: " + uid);
    }

    private WorkflowTypeEnum resolveResetWorkflowType(WorkflowEntity entity) {
        String uid = entity.getUid();
        String orgUid = entity.getOrgUid();

        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_WORKFLOW_UID_SUFFIX)) {
            return WorkflowTypeEnum.CHATBOT;
        }
        if (matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_WORKFLOW_UID_SUFFIX)
                || matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID_SUFFIX)
                || matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID_SUFFIX)
                || matchesResetUid(uid, orgUid, WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_UID_SUFFIX)) {
            return WorkflowTypeEnum.IVR;
        }
        throw new IllegalArgumentException("Workflow does not support demo reset: " + uid);
    }

    private boolean matchesResetUid(String workflowUid, String orgUid, String suffix) {
        if (!StringUtils.hasText(workflowUid)) {
            return false;
        }
        if (BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID.equals(workflowUid)) {
            return WorkflowInitData.DEFAULT_IVR_WORKFLOW_UID_SUFFIX.equals(suffix);
        }
        if (BytedeskConsts.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID.equals(workflowUid)) {
            return WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID_SUFFIX.equals(suffix);
        }
        if (BytedeskConsts.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID.equals(workflowUid)) {
            return WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID_SUFFIX.equals(suffix);
        }
        if (BytedeskConsts.DEFAULT_IVR_BOT_WORKFLOW_UID.equals(workflowUid)) {
            return WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_UID_SUFFIX.equals(suffix);
        }
        if (!StringUtils.hasText(orgUid)) {
            return false;
        }
        return workflowUid.equals(Utils.formatUid(orgUid, suffix));
    }

    private void refreshWorkflowCache(WorkflowEntity entity) {
        // if (entity == null || !StringUtils.hasText(entity.getUid()) || cacheManager == null) {
        //     return;
        // }
        // Cache cache = cacheManager.getCache(CACHE_WORKFLOW);
        // if (cache == null) {
        //     return;
        // }
        // cache.put(entity.getUid(), entity);
    }

    private WorkflowTemplateOptionResponse createTemplateOption(String value, String label, String description,
            String schema) {
        return WorkflowTemplateOptionResponse.builder()
                .value(value)
                .label(label)
                .description(description)
                .schema(schema)
                .build();
    }


    

}