package com.bytedesk.ai.robot_settings;

import java.util.ArrayList;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ai.robot_settings.tools.RobotToolsSettingsEntity;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings_emotion.EmotionSettingEntity;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;
import com.bytedesk.kbase.settings_service.ServiceSettingsEntity;
import com.bytedesk.kbase.settings_service.ServiceSettingsHelper;
import com.bytedesk.kbase.settings_summary.SummarySettingsEntity;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsEntity;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsHelper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RobotSettingsRestService
        extends BaseRestService<RobotSettingsEntity, RobotSettingsRequest, RobotSettingsResponse> {

    private final RobotSettingsRepository robotSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ServiceSettingsHelper serviceSettingsHelper;

    private final TriggerSettingsHelper triggerSettingsHelper;

    @Cacheable(value = "robotSettings", key = "#uid", unless = "#result == null")
    @Override
    public Optional<RobotSettingsEntity> findByUid(String uid) {
        return robotSettingsRepository.findByUid(uid);
    }

    @Override
    @Transactional
    public RobotSettingsResponse create(RobotSettingsRequest request) {
        RobotSettingsEntity entity = modelMapper.map(request, RobotSettingsEntity.class);
        entity.setUid(uidUtils.getUid());

        // 使用静态工厂方法处理嵌套的 ServiceSettings,传入 modelMapper,null 检查已内置
        ServiceSettingsEntity svc = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
        // 创建时统一强制生成 uid
        svc.setUid(uidUtils.getUid());
        entity.setServiceSettings(svc);
        ServiceSettingsEntity draft = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
        draft.setUid(uidUtils.getUid());
        entity.setDraftServiceSettings(draft);

        TriggerSettingsEntity trigger = TriggerSettingsEntity.fromRequest(request.getTriggerSettings(), modelMapper);
        trigger.setUid(uidUtils.getUid());
        if (request.getTriggerSettings() != null) {
            triggerSettingsHelper.updateTriggerAssociationsIfPresent(trigger, request.getTriggerSettings());
        }
        entity.setTriggerSettings(trigger);
        TriggerSettingsEntity triggerDraft = TriggerSettingsEntity.fromRequest(request.getTriggerSettings(), modelMapper);
        triggerDraft.setUid(uidUtils.getUid());
        if (request.getTriggerSettings() != null) {
            triggerSettingsHelper.updateTriggerAssociationsIfPresent(triggerDraft, request.getTriggerSettings());
        }
        entity.setDraftTriggerSettings(triggerDraft);

        // 发布与草稿：邀请配置（统一使用 fromRequest，内部已处理 null）
        InviteSettingsEntity inv = InviteSettingsEntity.fromRequest(request.getInviteSettings(), modelMapper);
        inv.setUid(uidUtils.getUid());
        entity.setInviteSettings(inv);
        InviteSettingsEntity invDraft = InviteSettingsEntity.fromRequest(request.getInviteSettings(), modelMapper);
        invDraft.setUid(uidUtils.getUid());
        entity.setDraftInviteSettings(invDraft);

        // 发布与草稿：意图配置（统一使用 fromRequest，内部已处理 null）
        IntentionSettingsEntity inte = IntentionSettingsEntity.fromRequest(request.getIntentionSettings(), modelMapper);
        inte.setUid(uidUtils.getUid());
        entity.setIntentionSettings(inte);
        IntentionSettingsEntity inteDraft = IntentionSettingsEntity.fromRequest(request.getIntentionSettings(),
                modelMapper);
        inteDraft.setUid(uidUtils.getUid());
        entity.setDraftIntentionSettings(inteDraft);

        // 发布与草稿：情绪配置（统一使用 fromRequest，内部已处理 null）
        EmotionSettingEntity emo = EmotionSettingEntity.fromRequest(request.getEmotionSettings(), modelMapper);
        emo.setUid(uidUtils.getUid());
        entity.setEmotionSettings(emo);
        EmotionSettingEntity emoDraft = EmotionSettingEntity.fromRequest(request.getEmotionSettings(), modelMapper);
        emoDraft.setUid(uidUtils.getUid());
        entity.setDraftEmotionSettings(emoDraft);

        // 发布与草稿：会话小结配置（统一使用 fromRequest，内部已处理 null）
        SummarySettingsEntity sum = SummarySettingsEntity.fromRequest(request.getSummarySettings(), modelMapper);
        sum.setUid(uidUtils.getUid());
        entity.setSummarySettings(sum);
        SummarySettingsEntity sumDraft = SummarySettingsEntity.fromRequest(request.getSummarySettings(), modelMapper);
        sumDraft.setUid(uidUtils.getUid());
        entity.setDraftSummarySettings(sumDraft);

        // 发布与草稿：差评配置（统一使用 fromRequest，内部已处理 null）
        RatedownSettingsEntity r = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
        r.setUid(uidUtils.getUid());
        entity.setRateDownSettings(r);
        RatedownSettingsEntity rd = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
        rd.setUid(uidUtils.getUid());
        entity.setDraftRateDownSettings(rd);

        // 发布与草稿：Spring AI 工具配置
        RobotToolsSettingsEntity tools = RobotToolsSettingsEntity.fromRequest(request.getToolsSettings(), modelMapper);
        tools.setUid(uidUtils.getUid());
        entity.setToolsSettings(tools);
        RobotToolsSettingsEntity toolsDraft = RobotToolsSettingsEntity.fromRequest(request.getToolsSettings(), modelMapper);
        toolsDraft.setUid(uidUtils.getUid());
        entity.setDraftToolsSettings(toolsDraft);

        // 如果请求或实体标记为默认，则保证同 org 仅一个默认
        if (Boolean.TRUE.equals(request.getIsDefault()) || Boolean.TRUE.equals(entity.getIsDefault())) {
            ensureSingleDefault(entity.getOrgUid(), entity);
        }

        RobotSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public RobotSettingsResponse update(RobotSettingsRequest request) {
        Optional<RobotSettingsEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("RobotSettings not found: " + request.getUid());
        }

        RobotSettingsEntity entity = optional.get();
        // 使用 ModelMapper 批量更新基础字段
        // modelMapper.map(request, entity);
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setIsDefault(request.getIsDefault());
        entity.setEnabled(request.getEnabled());

        // 使用静态工厂方法更新嵌套设置,只在非 null 时更新
        if (request.getServiceSettings() != null) {
            ServiceSettingsEntity draft = entity.getDraftServiceSettings();
            if (draft == null) {
                draft = new ServiceSettingsEntity();
                entity.setDraftServiceSettings(draft);
            }
            // 保存原有uid及实体id，避免被 modelMapper 覆盖
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            modelMapper.map(request.getServiceSettings(), draft);
            // 恢复或设置 uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            // 处理 ServiceSettings 关联（FAQ 列表等）
            serviceSettingsHelper.updateFaqAssociationsIfPresent(draft, request.getServiceSettings());
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getTriggerSettings() != null) {
            TriggerSettingsEntity draft = entity.getDraftTriggerSettings();
            if (draft == null) {
                draft = new TriggerSettingsEntity();
                entity.setDraftTriggerSettings(draft);
            }
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            triggerSettingsHelper.updateTriggerAssociationsIfPresent(draft, request.getTriggerSettings());
            entity.setHasUnpublishedChanges(true);
        }
        // 更新草稿:邀请/意图
        if (request.getInviteSettings() != null) {
            InviteSettingsEntity draft = entity.getDraftInviteSettings();
            if (draft == null) {
                draft = new InviteSettingsEntity();
                entity.setDraftInviteSettings(draft);
            }
            // 保存原有uid及实体id，避免被 modelMapper 覆盖
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            modelMapper.map(request.getInviteSettings(), draft);
            // 恢复或设置 uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getIntentionSettings() != null) {
            IntentionSettingsEntity draft = entity.getDraftIntentionSettings();
            if (draft == null) {
                draft = new IntentionSettingsEntity();
                entity.setDraftIntentionSettings(draft);
            }
            // 保存原有uid及实体id，避免被 modelMapper 覆盖
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            modelMapper.map(request.getIntentionSettings(), draft);
            // 恢复或设置 uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getEmotionSettings() != null) {
            EmotionSettingEntity draft = entity.getDraftEmotionSettings();
            if (draft == null) {
                draft = new EmotionSettingEntity();
                entity.setDraftEmotionSettings(draft);
            }
            // 保存原有uid及实体id，避免被 modelMapper 覆盖
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            modelMapper.map(request.getEmotionSettings(), draft);
            // 恢复或设置 uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getSummarySettings() != null) {
            SummarySettingsEntity draft = entity.getDraftSummarySettings();
            if (draft == null) {
                draft = new SummarySettingsEntity();
                entity.setDraftSummarySettings(draft);
            }
            // 保存原有uid及实体id，避免被 modelMapper 覆盖
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            modelMapper.map(request.getSummarySettings(), draft);
            // 恢复或设置 uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getRateDownSettings() != null) {
            RatedownSettingsEntity draft = entity.getDraftRateDownSettings();
            if (draft == null) {
                draft = new RatedownSettingsEntity();
                entity.setDraftRateDownSettings(draft);
            }
            // 保存原有uid及实体id，避免被 modelMapper 覆盖
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            modelMapper.map(request.getRateDownSettings(), draft);
            // 恢复或设置 uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getToolsSettings() != null) {
            RobotToolsSettingsEntity draft = entity.getDraftToolsSettings();
            if (draft == null) {
                draft = RobotToolsSettingsEntity.builder().build();
                entity.setDraftToolsSettings(draft);
            }
            String originalUid = draft.getUid();
            Long originalId = draft.getId();
            modelMapper.map(request.getToolsSettings(), draft);
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            if (originalId != null) {
                draft.setId(originalId);
            }
            if (draft.getToolConfigs() == null) {
                draft.setToolConfigs(new ArrayList<>());
            }
            entity.setHasUnpublishedChanges(true);
        }

        // 若本次更新将其设为默认，需取消同 org 其他默认
        if (Boolean.TRUE.equals(request.getIsDefault()) || Boolean.TRUE.equals(entity.getIsDefault())) {
            ensureSingleDefault(entity.getOrgUid(), entity);
        }

        RobotSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    @CacheEvict(value = "robotSettings", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<RobotSettingsEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(RobotSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    protected Specification<RobotSettingsEntity> createSpecification(RobotSettingsRequest request) {
        return RobotSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<RobotSettingsEntity> executePageQuery(Specification<RobotSettingsEntity> spec, Pageable pageable) {
        return robotSettingsRepository.findAll(spec, pageable);
    }

    @CachePut(value = "robotSettings", key = "#entity.uid", unless = "#result == null")
    @Override
    protected RobotSettingsEntity doSave(RobotSettingsEntity entity) {
        return robotSettingsRepository.save(entity);
    }

    @Override
    public RobotSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            RobotSettingsEntity entity) {
        try {
            Optional<RobotSettingsEntity> latest = robotSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RobotSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return robotSettingsRepository.save(latestEntity);
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
    public RobotSettingsEntity getOrCreateDefault(String orgUid) {
        // 加锁读取，防止并发创建多个默认
        Optional<RobotSettingsEntity> defaultSettings = robotSettingsRepository.findDefaultForUpdate(orgUid);
        if (defaultSettings.isPresent()) {
            return defaultSettings.get();
        }
        // 
        // Create default settings
        RobotSettingsEntity settings = RobotSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .name("默认机器人配置")
                .description("系统默认机器人配置")
                .isDefault(true)
                .enabled(true)
                .orgUid(orgUid)
                .build();

        // 参考 create() 的初始化逻辑：为各嵌套配置同时初始化“发布 + 草稿”两份，并分配独立 UID
        // Service settings
        ServiceSettingsEntity published = ServiceSettingsEntity.fromRequest(null, modelMapper);
        published.setUid(uidUtils.getUid());
        ServiceSettingsEntity draft = ServiceSettingsEntity.fromRequest(null, modelMapper);
        draft.setUid(uidUtils.getUid());
        settings.setServiceSettings(published);
        settings.setDraftServiceSettings(draft);

        // Trigger settings（发布 + 草稿）
        TriggerSettingsEntity triggerPublished = TriggerSettingsEntity.fromRequest(null, modelMapper);
        triggerPublished.setUid(uidUtils.getUid());
        TriggerSettingsEntity triggerDraft = TriggerSettingsEntity.fromRequest(null, modelMapper);
        triggerDraft.setUid(uidUtils.getUid());
        settings.setTriggerSettings(triggerPublished);
        settings.setDraftTriggerSettings(triggerDraft);

        // Invite settings（发布 + 草稿）
        InviteSettingsEntity inv = InviteSettingsEntity.fromRequest(null, modelMapper);
        inv.setUid(uidUtils.getUid());
        settings.setInviteSettings(inv);
        InviteSettingsEntity invDraft = InviteSettingsEntity.fromRequest(null, modelMapper);
        invDraft.setUid(uidUtils.getUid());
        settings.setDraftInviteSettings(invDraft);

        // Intention settings（发布 + 草稿）
        IntentionSettingsEntity inte = IntentionSettingsEntity.fromRequest(null, modelMapper);
        inte.setUid(uidUtils.getUid());
        settings.setIntentionSettings(inte);
        IntentionSettingsEntity inteDraft = IntentionSettingsEntity.fromRequest(null, modelMapper);
        inteDraft.setUid(uidUtils.getUid());
        settings.setDraftIntentionSettings(inteDraft);

        // Ratedown settings（发布 + 草稿）
        RatedownSettingsEntity r = RatedownSettingsEntity.fromRequest(null, modelMapper);
        r.setUid(uidUtils.getUid());
        settings.setRateDownSettings(r);
        RatedownSettingsEntity rd = RatedownSettingsEntity.fromRequest(null, modelMapper);
        rd.setUid(uidUtils.getUid());
        settings.setDraftRateDownSettings(rd);

        // Spring AI 工具（发布 + 草稿）
        RobotToolsSettingsEntity tools = RobotToolsSettingsEntity.fromRequest(null, modelMapper);
        tools.setUid(uidUtils.getUid());
        settings.setToolsSettings(tools);
        RobotToolsSettingsEntity toolsDraft = RobotToolsSettingsEntity.fromRequest(null, modelMapper);
        toolsDraft.setUid(uidUtils.getUid());
        settings.setDraftToolsSettings(toolsDraft);

        // LLM settings（发布 + 草稿）
        // RobotLlmEntity llm = RobotLlmEntity.fromRequest(null, modelMapper);
        // llm.setUid(uidUtils.getUid());
        // settings.setLlm(llm);
        // RobotLlmEntity draftLlm = RobotLlmEntity.fromRequest(null, modelMapper);
        // draftLlm.setUid(uidUtils.getUid());
        // settings.setDraftLlm(draftLlm);

        // // 默认：关闭知识库，空 LLM 配置
        // settings.setKbEnabled(false);
        // settings.setKbUid(null);

        // 刚创建的即为默认，确保同 org 唯一
        ensureSingleDefault(orgUid, settings);
        return save(settings);
    }

    /**
     * Publish draft settings to online for robot
     */
    @Transactional
    @CachePut(value = "robotSettings", key = "#uid", unless = "#result == null")
    public RobotSettingsResponse publish(String uid) {
        Optional<RobotSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("RobotSettings not found: " + uid);
        }
        RobotSettingsEntity entity = optional.get();
        
        // 复制草稿到发布版本
        if (entity.getDraftServiceSettings() != null) {
            ServiceSettingsEntity published = entity.getServiceSettings();
            if (published != null) {
                log.info("welcomeTip {}, draftWelcomeTip {}", entity.getServiceSettings().getWelcomeTip(), 
                entity.getDraftServiceSettings().getWelcomeTip());
                // 
                copyPropertiesExcludingIds(entity.getDraftServiceSettings(), published);
            } else {
                ServiceSettingsEntity newPublished = new ServiceSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftServiceSettings(), newPublished);
                log.info("new welcomeTip {}", newPublished.getWelcomeTip());
                newPublished.setUid(uidUtils.getUid());
                entity.setServiceSettings(newPublished);
            }
        }

        if (entity.getDraftTriggerSettings() != null) {
            TriggerSettingsEntity published = entity.getTriggerSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftTriggerSettings(), published);
            } else {
                TriggerSettingsEntity newPublished = new TriggerSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftTriggerSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setTriggerSettings(newPublished);
            }
        }

        if (entity.getDraftRateDownSettings() != null) {
            RatedownSettingsEntity published = entity.getRateDownSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftRateDownSettings(), published);
            } else {
                RatedownSettingsEntity newPublished = new RatedownSettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftRateDownSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setRateDownSettings(newPublished);
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

        if (entity.getDraftEmotionSettings() != null) {
            EmotionSettingEntity published = entity.getEmotionSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftEmotionSettings(), published);
            } else {
                EmotionSettingEntity newPublished = new EmotionSettingEntity();
                copyPropertiesExcludingIds(entity.getDraftEmotionSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setEmotionSettings(newPublished);
            }
        }

        if (entity.getDraftSummarySettings() != null) {
            SummarySettingsEntity published = entity.getSummarySettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftSummarySettings(), published);
            } else {
                SummarySettingsEntity newPublished = new SummarySettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftSummarySettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setSummarySettings(newPublished);
            }
        }

        if (entity.getDraftToolsSettings() != null) {
            RobotToolsSettingsEntity published = entity.getToolsSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftToolsSettings(), published);
            } else {
                RobotToolsSettingsEntity newPublished = RobotToolsSettingsEntity.builder().build();
                copyPropertiesExcludingIds(entity.getDraftToolsSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setToolsSettings(newPublished);
            }
        }
        
        entity.setHasUnpublishedChanges(false);
        entity.setPublishedAt(java.time.ZonedDateTime.now());
        RobotSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    // 仅复制业务字段,忽略 id/uid/version 与时间字段
    // 使用 ServiceSettingsHelper 处理懒加载集合的正确复制
    private void copyPropertiesExcludingIds(Object source, Object target) {
        if (source instanceof ServiceSettingsEntity && target instanceof ServiceSettingsEntity) {
            serviceSettingsHelper.copyServiceSettingsProperties((ServiceSettingsEntity) source, (ServiceSettingsEntity) target);
        } else if (source instanceof TriggerSettingsEntity && target instanceof TriggerSettingsEntity) {
            // triggerSettingsHelper.copyTriggerSettingsProperties((TriggerSettingsEntity) source, (TriggerSettingsEntity) target);
        } else {
            BeanUtils.copyProperties(source, target, "id", "uid", "version", "createdAt", "updatedAt");
        }
    }

    @Override
    public RobotSettingsResponse convertToResponse(RobotSettingsEntity entity) {
        return modelMapper.map(entity, RobotSettingsResponse.class);
    }

    /**
     * 保证同一个 orgUid 下仅有一个 isDefault=true。
     * 在事务内使用，借助悲观锁串行化并发修改。
     */
    private void ensureSingleDefault(String orgUid, RobotSettingsEntity target) {
        if (orgUid == null) {
            return;
        }
        Optional<RobotSettingsEntity> existingOpt = robotSettingsRepository.findDefaultForUpdate(orgUid);
        if (existingOpt.isPresent()) {
            RobotSettingsEntity existing = existingOpt.get();
            if (!existing.getUid().equals(target.getUid())) {
                existing.setIsDefault(false);
                save(existing);
            }
        }
        target.setIsDefault(true);
    }

}
