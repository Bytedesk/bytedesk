package com.bytedesk.service.workgroup_settings;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ai.robot.settings.RobotRoutingSettingsEntity;
import com.bytedesk.ai.robot.settings.RobotRoutingSettingsRequest;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRepository;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsHelper;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsEntity;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsHelper;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.robot_to_agent_settings.RobotToAgentSettingsEntity;
import com.bytedesk.service.worktime_settings.WorktimeSettingEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkgroupSettingsRestService
        extends BaseRestService<WorkgroupSettingsEntity, WorkgroupSettingsRequest, WorkgroupSettingsResponse> {

    private final WorkgroupSettingsRepository workgroupSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ServiceSettingsHelper serviceSettingsHelper;

    private final MessageLeaveSettingsHelper messageLeaveSettingsHelper;

    private final RobotRepository robotRepository;

    @Cacheable(value = "workgroupSettings", key = "#uid", unless = "#result == null")
    @Override
    public Optional<WorkgroupSettingsEntity> findByUid(String uid) {
        return workgroupSettingsRepository.findByUid(uid);
    }

    @Override
    @Transactional
    public WorkgroupSettingsResponse create(WorkgroupSettingsRequest request) {
        WorkgroupSettingsEntity entity = modelMapper.map(request, WorkgroupSettingsEntity.class);
        entity.setUid(uidUtils.getUid());

        ServiceSettingsEntity service = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
        // 创建时统一强制生成 uid
        service.setUid(uidUtils.getUid());
        entity.setServiceSettings(service);
        ServiceSettingsEntity draft = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
        draft.setUid(uidUtils.getUid());
        entity.setDraftServiceSettings(draft);

        // 发布与草稿：邀请配置（统一使用 fromRequest，外部不再判空）
        InviteSettingsEntity inv = InviteSettingsEntity.fromRequest(request.getInviteSettings(), modelMapper);
        inv.setUid(uidUtils.getUid());
        entity.setInviteSettings(inv);
        InviteSettingsEntity invDraft = InviteSettingsEntity.fromRequest(request.getInviteSettings(), modelMapper);
        invDraft.setUid(uidUtils.getUid());
        entity.setDraftInviteSettings(invDraft);

        // 发布与草稿：意图配置（统一使用 fromRequest，外部不再判空）
        IntentionSettingsEntity inte = IntentionSettingsEntity.fromRequest(request.getIntentionSettings(), modelMapper);
        inte.setUid(uidUtils.getUid());
        entity.setIntentionSettings(inte);
        IntentionSettingsEntity inteDraft = IntentionSettingsEntity.fromRequest(request.getIntentionSettings(),
                modelMapper);
        inteDraft.setUid(uidUtils.getUid());
        entity.setDraftIntentionSettings(inteDraft);

        MessageLeaveSettingsEntity mls = MessageLeaveSettingsEntity.fromRequest(request.getMessageLeaveSettings(),
                modelMapper);
        mls.setUid(uidUtils.getUid());
        entity.setMessageLeaveSettings(mls);
        MessageLeaveSettingsEntity mlsDraft = MessageLeaveSettingsEntity.fromRequest(request.getMessageLeaveSettings(),
                modelMapper);
        mlsDraft.setUid(uidUtils.getUid());
        entity.setDraftMessageLeaveSettings(mlsDraft);

        WorktimeSettingEntity worktimePublished = WorktimeSettingEntity.fromRequest(request.getWorktimeSettings(),
            modelMapper);
        worktimePublished.setUid(uidUtils.getUid());
        entity.setWorktimeSettings(worktimePublished);
        WorktimeSettingEntity worktimeDraft = WorktimeSettingEntity.fromRequest(request.getWorktimeSettings(),
            modelMapper);
        worktimeDraft.setUid(uidUtils.getUid());
        entity.setDraftWorktimeSettings(worktimeDraft);

        // create 场景：不将 robotUid 解析成 RobotEntity，统一使用 Entity.fromRequest 风格
        RobotRoutingSettingsEntity rrs = RobotRoutingSettingsEntity.fromRequest(request.getRobotRoutingSettings());
        rrs.setUid(uidUtils.getUid());
        entity.setRobotSettings(rrs);
        RobotRoutingSettingsEntity rrsDraft = RobotRoutingSettingsEntity.fromRequest(
                request.getRobotRoutingSettings());
        rrsDraft.setUid(uidUtils.getUid());
        entity.setDraftRobotSettings(rrsDraft);

        QueueSettingsEntity qs = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
        qs.setUid(uidUtils.getUid());
        entity.setQueueSettings(qs);
        QueueSettingsEntity qsDraft = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
        qsDraft.setUid(uidUtils.getUid());
        entity.setDraftQueueSettings(qsDraft);

        RobotToAgentSettingsEntity rtas = RobotToAgentSettingsEntity.fromRequest(request.getRobotToAgentSettings(),
            modelMapper);
        rtas.setUid(uidUtils.getUid());
        entity.setRobotToAgentSettings(rtas);
        RobotToAgentSettingsEntity rtasDraft = RobotToAgentSettingsEntity.fromRequest(request.getRobotToAgentSettings(),
            modelMapper);
        rtasDraft.setUid(uidUtils.getUid());
        entity.setDraftRobotToAgentSettings(rtasDraft);

        // 如果请求或实体标记为默认，则保证同 org 仅一个默认
        if (Boolean.TRUE.equals(request.getIsDefault()) || Boolean.TRUE.equals(entity.getIsDefault())) {
            ensureSingleDefault(entity.getOrgUid(), entity);
        }

        WorkgroupSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public WorkgroupSettingsResponse update(WorkgroupSettingsRequest request) {
        Optional<WorkgroupSettingsEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("WorkgroupSettings not found: " + request.getUid());
        }

        WorkgroupSettingsEntity entity = optional.get();
        // 使用 ModelMapper 批量更新基础字段
        // modelMapper.map(request, entity);
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setIsDefault(request.getIsDefault());
        entity.setEnabled(request.getEnabled());
        entity.setRoutingMode(request.getRoutingMode());

        // 使用静态工厂方法更新嵌套设置,只在非 null 时更新
        if (request.getServiceSettings() != null) {
            ServiceSettingsEntity draft = entity.getDraftServiceSettings();
            if (draft == null) {
                draft = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
                draft.setUid(uidUtils.getUid());
                entity.setDraftServiceSettings(draft);
                // 
                ServiceSettingsEntity settings = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setServiceSettings(settings);
            } else {
                // 保留草稿唯一标识，避免被请求体覆盖
                String originalUid = draft.getUid();
                modelMapper.map(request.getServiceSettings(), draft);
                draft.setUid(originalUid);
            }
            // 处理 ServiceSettings 关联（FAQ 列表等）
            serviceSettingsHelper.updateFaqAssociationsIfPresent(draft, request.getServiceSettings());
            entity.setHasUnpublishedChanges(true);
        }

        // 更新草稿：邀请/意图
        if (request.getInviteSettings() != null) {
            InviteSettingsEntity draft = entity.getDraftInviteSettings();
            if (draft == null) {
                draft = InviteSettingsEntity.fromRequest(request.getInviteSettings(), modelMapper);
                draft.setUid(uidUtils.getUid());
                entity.setDraftInviteSettings(draft);
                // 
                InviteSettingsEntity settings = InviteSettingsEntity.fromRequest(request.getInviteSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setInviteSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getInviteSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getIntentionSettings() != null) {
            IntentionSettingsEntity draft = entity.getDraftIntentionSettings();
            if (draft == null) {
                draft = IntentionSettingsEntity.fromRequest(request.getIntentionSettings(), modelMapper);
                draft.setUid(uidUtils.getUid());
                entity.setDraftIntentionSettings(draft);
                // 
                IntentionSettingsEntity settings = IntentionSettingsEntity.fromRequest(request.getIntentionSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setIntentionSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getIntentionSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getMessageLeaveSettings() != null) {
            MessageLeaveSettingsEntity draft = entity.getDraftMessageLeaveSettings();
            if (draft == null) {
                draft = MessageLeaveSettingsEntity.fromRequest(request.getMessageLeaveSettings(), modelMapper);
                draft.setUid(uidUtils.getUid());
                entity.setDraftMessageLeaveSettings(draft);
                // 
                MessageLeaveSettingsEntity settings = MessageLeaveSettingsEntity.fromRequest(request.getMessageLeaveSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setMessageLeaveSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getMessageLeaveSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getWorktimeSettings() != null) {
            WorktimeSettingEntity draft = entity.getDraftWorktimeSettings();
            if (draft == null) {
                draft = WorktimeSettingEntity.fromRequest(request.getWorktimeSettings(), modelMapper);
                draft.setUid(uidUtils.getUid());
                entity.setDraftWorktimeSettings(draft);
                // 
                WorktimeSettingEntity settings = WorktimeSettingEntity.fromRequest(request.getWorktimeSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setWorktimeSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getWorktimeSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getRobotRoutingSettings() != null) {
            RobotRoutingSettingsEntity draft = entity.getDraftRobotSettings();
            if (draft == null) {
                draft = RobotRoutingSettingsEntity.fromRequest(request.getRobotRoutingSettings());
                draft.setUid(uidUtils.getUid());
                entity.setDraftRobotSettings(draft);
                // 
            } else {
                String originalUid = draft.getUid();
                // 先用 mapper 覆盖简单字段
                modelMapper.map(request.getRobotRoutingSettings(), draft);
                // 再根据 robotUid 解析为 RobotEntity 进行关联
                applyRobotUidToEntity(request.getRobotRoutingSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getQueueSettings() != null) {
            QueueSettingsEntity draft = entity.getDraftQueueSettings();
            if (draft == null) {
                draft = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
                draft.setUid(uidUtils.getUid());
                entity.setDraftQueueSettings(draft);
                // 
                QueueSettingsEntity settings = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setQueueSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getQueueSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getRobotToAgentSettings() != null) {
            RobotToAgentSettingsEntity draft = entity.getDraftRobotToAgentSettings();
            if (draft == null) {
                draft = RobotToAgentSettingsEntity.fromRequest(request.getRobotToAgentSettings(), modelMapper);
                draft.setUid(uidUtils.getUid());
                entity.setDraftRobotToAgentSettings(draft);
                // 
                RobotToAgentSettingsEntity settings = RobotToAgentSettingsEntity
                        .fromRequest(request.getRobotToAgentSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setRobotToAgentSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getRobotToAgentSettings(), draft);
                RobotToAgentSettingsEntity.ensureDefaults(draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        // 若本次更新将其设为默认，需取消同 org 其他默认
        if (Boolean.TRUE.equals(request.getIsDefault()) || Boolean.TRUE.equals(entity.getIsDefault())) {
            ensureSingleDefault(entity.getOrgUid(), entity);
        }

        WorkgroupSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    @CacheEvict(value = "workgroupSettings", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<WorkgroupSettingsEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(WorkgroupSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    protected Specification<WorkgroupSettingsEntity> createSpecification(WorkgroupSettingsRequest request) {
        return WorkgroupSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkgroupSettingsEntity> executePageQuery(Specification<WorkgroupSettingsEntity> spec,
            Pageable pageable) {
        return workgroupSettingsRepository.findAll(spec, pageable);
    }

    @CachePut(value = "workgroupSettings", key = "#entity.uid", unless = "#result == null")
    @Override
    protected WorkgroupSettingsEntity doSave(WorkgroupSettingsEntity entity) {
        return workgroupSettingsRepository.save(entity);
    }

    @Override
    public WorkgroupSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            WorkgroupSettingsEntity entity) {
        try {
            Optional<WorkgroupSettingsEntity> latest = workgroupSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkgroupSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return workgroupSettingsRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Get or create default settings for organization
     */
    @Transactional
    public WorkgroupSettingsEntity getOrCreateDefault(String orgUid) {
        // 加锁读取，防止并发创建多个默认
        Optional<WorkgroupSettingsEntity> defaultSettings = workgroupSettingsRepository
                .findDefaultForUpdate(orgUid);
        if (defaultSettings.isPresent()) {
            return defaultSettings.get();
        }

        // Create default settings
        WorkgroupSettingsEntity settings = WorkgroupSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .name("默认工作组配置")
                .description("系统默认工作组配置")
                .isDefault(true)
                .enabled(true)
                .orgUid(orgUid)
                .build();
        // 参考 create()：为各嵌套配置初始化“发布 + 草稿”并分配独立 UID
        // Service settings（发布 + 草稿）
        ServiceSettingsEntity published = ServiceSettingsEntity.fromRequest(null, modelMapper);
        published.setUid(uidUtils.getUid());
        ServiceSettingsEntity draft = ServiceSettingsEntity.fromRequest(null, modelMapper);
        draft.setUid(uidUtils.getUid());
        settings.setServiceSettings(published);
        settings.setDraftServiceSettings(draft);

        // 邀请配置（发布 + 草稿）
        InviteSettingsEntity inv = InviteSettingsEntity.fromRequest(null, modelMapper);
        inv.setUid(uidUtils.getUid());
        InviteSettingsEntity invDraft = InviteSettingsEntity.fromRequest(null, modelMapper);
        invDraft.setUid(uidUtils.getUid());
        settings.setInviteSettings(inv);
        settings.setDraftInviteSettings(invDraft);

        // 意图配置（发布 + 草稿）
        IntentionSettingsEntity inte = IntentionSettingsEntity.fromRequest(null, modelMapper);
        inte.setUid(uidUtils.getUid());
        IntentionSettingsEntity inteDraft = IntentionSettingsEntity.fromRequest(null, modelMapper);
        inteDraft.setUid(uidUtils.getUid());
        settings.setIntentionSettings(inte);
        settings.setDraftIntentionSettings(inteDraft);

        // 留言设置（发布 + 草稿）
        MessageLeaveSettingsEntity mls = MessageLeaveSettingsEntity.fromRequest(null, modelMapper);
        mls.setUid(uidUtils.getUid());
        MessageLeaveSettingsEntity mlsDraft = MessageLeaveSettingsEntity.fromRequest(null, modelMapper);
        mlsDraft.setUid(uidUtils.getUid());
        settings.setMessageLeaveSettings(mls);
        settings.setDraftMessageLeaveSettings(mlsDraft);

        // 工作时间设置（发布 + 草稿）
        WorktimeSettingEntity worktimePublished = WorktimeSettingEntity.fromRequest(null, modelMapper);
        worktimePublished.setUid(uidUtils.getUid());
        WorktimeSettingEntity worktimeDraft = WorktimeSettingEntity.fromRequest(null, modelMapper);
        worktimeDraft.setUid(uidUtils.getUid());
        settings.setWorktimeSettings(worktimePublished);
        settings.setDraftWorktimeSettings(worktimeDraft);

        // 机器人路由设置（发布 + 草稿）
        RobotRoutingSettingsEntity rrs = RobotRoutingSettingsEntity.fromRequest(null);
        rrs.setUid(uidUtils.getUid());
        RobotRoutingSettingsEntity rrsDraft = RobotRoutingSettingsEntity.fromRequest(null);
        rrsDraft.setUid(uidUtils.getUid());
        settings.setRobotSettings(rrs);
        settings.setDraftRobotSettings(rrsDraft);

        // 排队设置（发布 + 草稿）
        QueueSettingsEntity qs = QueueSettingsEntity.fromRequest(null, modelMapper);
        qs.setUid(uidUtils.getUid());
        QueueSettingsEntity qsDraft = QueueSettingsEntity.fromRequest(null, modelMapper);
        qsDraft.setUid(uidUtils.getUid());
        settings.setQueueSettings(qs);
        settings.setDraftQueueSettings(qsDraft);

        RobotToAgentSettingsEntity rtas = RobotToAgentSettingsEntity.fromRequest(null, modelMapper);
        rtas.setUid(uidUtils.getUid());
        RobotToAgentSettingsEntity rtasDraft = RobotToAgentSettingsEntity.fromRequest(null, modelMapper);
        rtasDraft.setUid(uidUtils.getUid());
        settings.setRobotToAgentSettings(rtas);
        settings.setDraftRobotToAgentSettings(rtasDraft);

        // 刚创建的即为默认，确保同 org 唯一
        ensureSingleDefault(orgUid, settings);
        return save(settings);
    }

    /**
     * Publish draft settings to online for workgroup
     */
    @Transactional
    @CachePut(value = "workgroupSettings", key = "#uid", unless = "#result == null")
    public WorkgroupSettingsResponse publish(String uid) {
        Optional<WorkgroupSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("WorkgroupSettings not found: " + uid);
        }
        WorkgroupSettingsEntity entity = optional.get();

        // 复制草稿到发布版本
        if (entity.getDraftServiceSettings() != null) {
            ServiceSettingsEntity published = entity.getServiceSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftServiceSettings(), published);
            } else {
                ServiceSettingsEntity newPublished = new ServiceSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftServiceSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setServiceSettings(newPublished);
            }
        }

        if (entity.getDraftMessageLeaveSettings() != null) {
            MessageLeaveSettingsEntity published = entity.getMessageLeaveSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftMessageLeaveSettings(), published);
            } else {
                MessageLeaveSettingsEntity newPublished = new MessageLeaveSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftMessageLeaveSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setMessageLeaveSettings(newPublished);
            }
        }

        if (entity.getDraftWorktimeSettings() != null) {
            WorktimeSettingEntity published = entity.getWorktimeSettings();
            if (published != null) {
                copyWorktimeSettings(entity.getDraftWorktimeSettings(), published);
            } else {
                WorktimeSettingEntity newPublished = new WorktimeSettingEntity();
                copyWorktimeSettings(entity.getDraftWorktimeSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setWorktimeSettings(newPublished);
            }
        }

        if (entity.getDraftRobotSettings() != null) {
            RobotRoutingSettingsEntity published = entity.getRobotSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftRobotSettings(), published);
            } else {
                RobotRoutingSettingsEntity newPublished = new RobotRoutingSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftRobotSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setRobotSettings(newPublished);
            }
        }

        if (entity.getDraftQueueSettings() != null) {
            QueueSettingsEntity published = entity.getQueueSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftQueueSettings(), published);
            } else {
                QueueSettingsEntity newPublished = new QueueSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftQueueSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setQueueSettings(newPublished);
            }
        }

        if (entity.getDraftRobotToAgentSettings() != null) {
            RobotToAgentSettingsEntity published = entity.getRobotToAgentSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftRobotToAgentSettings(), published);
            } else {
                RobotToAgentSettingsEntity newPublished = new RobotToAgentSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftRobotToAgentSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setRobotToAgentSettings(newPublished);
            }
        }

        if (entity.getDraftInviteSettings() != null) {
            InviteSettingsEntity published = entity.getInviteSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftInviteSettings(), published);
            } else {
                InviteSettingsEntity newPublished = new InviteSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftInviteSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setInviteSettings(newPublished);
            }
        }

        if (entity.getDraftIntentionSettings() != null) {
            IntentionSettingsEntity published = entity.getIntentionSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftIntentionSettings(), published);
            } else {
                IntentionSettingsEntity newPublished = new IntentionSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftIntentionSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setIntentionSettings(newPublished);
            }
        }

        entity.setHasUnpublishedChanges(false);
        entity.setPublishedAt(java.time.ZonedDateTime.now());
        WorkgroupSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    // 仅复制业务字段,忽略 id/uid/version 与时间字段
    // 使用 Helper 类处理懒加载集合的正确复制
    private void copyPropertiesExcludingIds(Object source, Object target) {
        if (source instanceof ServiceSettingsEntity && target instanceof ServiceSettingsEntity) {
            serviceSettingsHelper.copyServiceSettingsProperties((ServiceSettingsEntity) source,
                    (ServiceSettingsEntity) target);
        } else if (source instanceof MessageLeaveSettingsEntity && target instanceof MessageLeaveSettingsEntity) {
            messageLeaveSettingsHelper.copyMessageLeaveSettingsProperties((MessageLeaveSettingsEntity) source,
                    (MessageLeaveSettingsEntity) target);
        } else {
            messageLeaveSettingsHelper.copyPropertiesExcludingIds(source, target);
        }
    }

    private void copyWorktimeSettings(WorktimeSettingEntity source, WorktimeSettingEntity target) {
        messageLeaveSettingsHelper.copyPropertiesExcludingIds(source, target);
    }

    /**
     * 根据请求中的 robotUid 解析并设置到目标实体上；若为空则清空关联。
     */
    private void applyRobotUidToEntity(RobotRoutingSettingsRequest request, RobotRoutingSettingsEntity target) {
        if (request == null || target == null) {
            return;
        }
        String robotUid = request.getRobotUid();
        if (robotUid == null || robotUid.isBlank()) {
            // 允许通过传空来移除关联机器人
            target.setRobot(null);
            return;
        }
        RobotEntity robot = robotRepository.findByUid(robotUid)
                .orElseThrow(() -> new RuntimeException("Robot not found by uid: " + robotUid));
        target.setRobot(robot);
    }

    @Override
    public WorkgroupSettingsResponse convertToResponse(WorkgroupSettingsEntity entity) {
        // 注意：Entity 与 Response 的字段命名不一致：
        // - Entity: robotSettings / draftRobotSettings
        // - Response: robotRoutingSettings / draftRobotRoutingSettings
        // 直接使用 ModelMapper 会导致这两个字段为 null，这里手动补齐映射。
        WorkgroupSettingsResponse resp = modelMapper.map(entity, WorkgroupSettingsResponse.class);

        // 机器人路由（发布）
        if (entity.getRobotSettings() != null) {
            resp.setRobotRoutingSettings(modelMapper.map(entity.getRobotSettings(),
                    com.bytedesk.ai.robot.settings.RobotRoutingSettingsResponse.class));
        } else {
            resp.setRobotRoutingSettings(modelMapper.map(RobotRoutingSettingsEntity.builder().uid(uidUtils.getUid()).build(), 
                com.bytedesk.ai.robot.settings.RobotRoutingSettingsResponse.class));
        }

        // 机器人路由（草稿）
        if (entity.getDraftRobotSettings() != null) {
            resp.setDraftRobotRoutingSettings(modelMapper.map(entity.getDraftRobotSettings(),
                    com.bytedesk.ai.robot.settings.RobotRoutingSettingsResponse.class));
        } else {
            resp.setDraftRobotRoutingSettings(modelMapper.map(RobotRoutingSettingsEntity.builder().uid(uidUtils.getUid()).build(), 
                com.bytedesk.ai.robot.settings.RobotRoutingSettingsResponse.class));
        }

        if (entity.getRobotToAgentSettings() != null) {
            resp.setRobotToAgentSettings(modelMapper.map(entity.getRobotToAgentSettings(),
                    com.bytedesk.service.robot_to_agent_settings.RobotToAgentSettingsResponse.class));
        } else {
            resp.setRobotToAgentSettings(modelMapper.map(RobotToAgentSettingsEntity.builder().uid(uidUtils.getUid()).build(), 
                com.bytedesk.service.robot_to_agent_settings.RobotToAgentSettingsResponse.class));
        }

        if (entity.getDraftRobotToAgentSettings() != null) {
            resp.setDraftRobotToAgentSettings(modelMapper.map(entity.getDraftRobotToAgentSettings(),
                    com.bytedesk.service.robot_to_agent_settings.RobotToAgentSettingsResponse.class));
        } else {
            resp.setDraftRobotToAgentSettings(modelMapper.map(RobotToAgentSettingsEntity.builder().uid(uidUtils.getUid()).build(), 
                com.bytedesk.service.robot_to_agent_settings.RobotToAgentSettingsResponse.class));
        }

        return resp;
    }

    /**
     * 保证同一个 orgUid 下仅有一个 isDefault=true。
     * 在事务内使用，借助悲观锁串行化并发修改。
     */
    private void ensureSingleDefault(String orgUid, WorkgroupSettingsEntity target) {
        if (orgUid == null) {
            return;
        }
        Optional<WorkgroupSettingsEntity> existingOpt = workgroupSettingsRepository.findDefaultForUpdate(orgUid);
        if (existingOpt.isPresent()) {
            WorkgroupSettingsEntity existing = existingOpt.get();
            if (!existing.getUid().equals(target.getUid())) {
                existing.setIsDefault(false);
                save(existing);
            }
        }
        target.setIsDefault(true);
    }

}
