package com.bytedesk.service.agent_settings;

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

import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsHelper;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.agent_status.settings.AgentStatusSettingEntity;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsEntity;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsHelper;
import com.bytedesk.service.worktime_settings.WorktimeSettingEntity;
import com.bytedesk.service.agent.AgentRepository;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AgentSettingsRestService
        extends BaseRestService<AgentSettingsEntity, AgentSettingsRequest, AgentSettingsResponse> {

    private final AgentSettingsRepository agentSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final AgentRepository agentRepository;

    private final ServiceSettingsHelper serviceSettingsHelper;

    private final MessageLeaveSettingsHelper messageLeaveSettingsHelper;

    @Cacheable(value = "agentSettings", key = "#uid", unless = "#result == null")
    @Override
    public Optional<AgentSettingsEntity> findByUid(String uid) {
        return agentSettingsRepository.findByUid(uid);
    }

    @Override
    public AgentSettingsResponse create(AgentSettingsRequest request) {
        AgentSettingsEntity entity = modelMapper.map(request, AgentSettingsEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        UserEntity user = authService.getUser();
        if (user != null) {
            entity.setUserUid(user.getUid());
        }
        // 使用静态工厂方法处理嵌套设置,传入 modelMapper,null 检查已内置
        ServiceSettingsEntity service = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
        // 创建时，不信任外部 uid，统一强制生成
        service.setUid(uidUtils.getUid());
        // 处理 ServiceSettings 关联（FAQ 列表等）
        if (request.getServiceSettings() != null) {
            serviceSettingsHelper.updateFaqAssociationsIfPresent(service, request.getServiceSettings());
        }
        entity.setServiceSettings(service);

        // 初始化草稿为发布版本的拷贝，确保具有独立 uid
        ServiceSettingsEntity draft = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
        // 保证草稿与发布是不同实体，强制新的 uid
        draft.setUid(uidUtils.getUid());
        if (request.getServiceSettings() != null) {
            serviceSettingsHelper.updateFaqAssociationsIfPresent(draft, request.getServiceSettings());
        }
        entity.setDraftServiceSettings(draft);

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
        IntentionSettingsEntity inteDraft = IntentionSettingsEntity.fromRequest(request.getIntentionSettings(), modelMapper);
        inteDraft.setUid(uidUtils.getUid());
        entity.setDraftIntentionSettings(inteDraft);

        // 发布与草稿：留言、自动回复、排队
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

        AutoReplySettingsEntity ars = AutoReplySettingsEntity.fromRequest(request.getAutoReplySettings(), modelMapper);
        ars.setUid(uidUtils.getUid());
        entity.setAutoReplySettings(ars);
        AutoReplySettingsEntity arsDraft = AutoReplySettingsEntity.fromRequest(request.getAutoReplySettings(),
                modelMapper);
        arsDraft.setUid(uidUtils.getUid());
        entity.setDraftAutoReplySettings(arsDraft);

        QueueSettingsEntity qs = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
        qs.setUid(uidUtils.getUid());
        entity.setQueueSettings(qs);
        QueueSettingsEntity qsDraft = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
        qsDraft.setUid(uidUtils.getUid());
        entity.setDraftQueueSettings(qsDraft);

        // 评分设置（统一使用 fromRequest，内部已处理 null）
        RatedownSettingsEntity r = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
        r.setUid(uidUtils.getUid());
        entity.setRateDownSettings(r);
        RatedownSettingsEntity rd = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
        rd.setUid(uidUtils.getUid());
        entity.setDraftRateDownSettings(rd);

        // 发布与草稿：客服状态设置（统一使用 fromRequest，内部已处理 null）
        AgentStatusSettingEntity st = AgentStatusSettingEntity.fromRequest(request.getAgentStatusSettings(), modelMapper);
        st.setUid(uidUtils.getUid());
        entity.setAgentStatusSettings(st);
        AgentStatusSettingEntity stDraft = AgentStatusSettingEntity.fromRequest(request.getAgentStatusSettings(), modelMapper);
        stDraft.setUid(uidUtils.getUid());
        entity.setDraftAgentStatusSettings(stDraft);

        AgentSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    public AgentSettingsResponse update(AgentSettingsRequest request) {
        Optional<AgentSettingsEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("AgentSettings not found: " + request.getUid());
        }

        AgentSettingsEntity entity = optional.get();
        // 使用 ModelMapper 批量更新基础字段（非设置的通用元信息）
        modelMapper.map(request, entity);

        // 使用静态工厂方法更新嵌套设置,只在非 null 时更新
        if (request.getServiceSettings() != null) {
            // 复用并更新现有草稿，避免新建导致孤儿记录
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
                // 保留草稿自身的唯一标识，避免被请求体中的 uid/uuid 覆盖
                String originalUid = draft.getUid();
                modelMapper.map(request.getServiceSettings(), draft);
                draft.setUid(originalUid);
            }
            // 根据 request 中的 Faq uids 映射关联
            serviceSettingsHelper.updateFaqAssociationsIfPresent(draft, request.getServiceSettings());
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getMessageLeaveSettings() != null) {
            MessageLeaveSettingsEntity draft = entity.getDraftMessageLeaveSettings();
            if (draft == null) {
                draft = MessageLeaveSettingsEntity.fromRequest(request.getMessageLeaveSettings(), modelMapper);
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
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
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
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

        if (request.getAutoReplySettings() != null) {
            AutoReplySettingsEntity draft = entity.getDraftAutoReplySettings();
            if (draft == null) {
                draft = AutoReplySettingsEntity.fromRequest(request.getAutoReplySettings(), modelMapper);
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
                entity.setDraftAutoReplySettings(draft);
                // 
                AutoReplySettingsEntity settings = AutoReplySettingsEntity.fromRequest(request.getAutoReplySettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setAutoReplySettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getAutoReplySettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        // 更新草稿：邀请/意图
        if (request.getInviteSettings() != null) {
            InviteSettingsEntity draft = entity.getDraftInviteSettings();
            if (draft == null) {
                draft = InviteSettingsEntity.fromRequest(request.getInviteSettings(), modelMapper);
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
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
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
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

        if (request.getQueueSettings() != null) {
            QueueSettingsEntity draft = entity.getDraftQueueSettings();
            if (draft == null) {
                draft = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
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

        if (request.getRateDownSettings() != null) {
            RatedownSettingsEntity draft = entity.getDraftRateDownSettings();
            if (draft == null) {
                draft = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
                entity.setDraftRateDownSettings(draft);
                // 
                RatedownSettingsEntity settings = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setRateDownSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getRateDownSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        if (request.getAgentStatusSettings() != null) {
            AgentStatusSettingEntity draft = entity.getDraftAgentStatusSettings();
            if (draft == null) {
                draft = AgentStatusSettingEntity.fromRequest(request.getAgentStatusSettings(), modelMapper);
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
                entity.setDraftAgentStatusSettings(draft);
                // 
                AgentStatusSettingEntity settings = AgentStatusSettingEntity.fromRequest(request.getAgentStatusSettings(), modelMapper);
                settings.setUid(uidUtils.getUid());
                entity.setAgentStatusSettings(settings);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getAgentStatusSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        AgentSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    @CacheEvict(value = "agentSettings", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        // 删除前校验：是否有客服仍引用该配置
        if (agentRepository.existsBySettings_UidAndDeletedFalse(uid)) {
            throw new RuntimeException("无法删除：仍有客服引用该配置，请先在客服资料中解除引用后再删除");
        }
        Optional<AgentSettingsEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(AgentSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    protected Specification<AgentSettingsEntity> createSpecification(AgentSettingsRequest request) {
        return AgentSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<AgentSettingsEntity> executePageQuery(Specification<AgentSettingsEntity> spec, Pageable pageable) {
        return agentSettingsRepository.findAll(spec, pageable);
    }

    @CachePut(value = "agentSettings", key = "#entity.uid", unless = "#result == null")
    @Override
    protected AgentSettingsEntity doSave(AgentSettingsEntity entity) {
        return agentSettingsRepository.save(entity);
    }

    @Override
    public AgentSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AgentSettingsEntity entity) {
        try {
            Optional<AgentSettingsEntity> latest = agentSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AgentSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return agentSettingsRepository.save(latestEntity);
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
    public AgentSettingsEntity getOrCreateDefault(String orgUid) {
        // 先尝试加锁读取，避免并发下重复创建
        Optional<AgentSettingsEntity> locked = agentSettingsRepository.findDefaultForUpdate(orgUid);
        if (locked.isPresent()) {
            return locked.get();
        }

        // 双检：未找到则创建；若并发创建被约束/锁冲突，再查一次返回
        try {
            AgentSettingsEntity settings = AgentSettingsEntity.builder()
                    .uid(uidUtils.getUid())
                    .name("默认客服配置")
                    .description("系统默认客服配置")
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

            // 自动回复设置（发布 + 草稿）
            AutoReplySettingsEntity ars = AutoReplySettingsEntity.fromRequest(null, modelMapper);
            ars.setUid(uidUtils.getUid());
            AutoReplySettingsEntity arsDraft = AutoReplySettingsEntity.fromRequest(null, modelMapper);
            arsDraft.setUid(uidUtils.getUid());
            settings.setAutoReplySettings(ars);
            settings.setDraftAutoReplySettings(arsDraft);

            // 排队设置（发布 + 草稿）
            QueueSettingsEntity qs = QueueSettingsEntity.fromRequest(null, modelMapper);
            qs.setUid(uidUtils.getUid());
            QueueSettingsEntity qsDraft = QueueSettingsEntity.fromRequest(null, modelMapper);
            qsDraft.setUid(uidUtils.getUid());
            settings.setQueueSettings(qs);
            settings.setDraftQueueSettings(qsDraft);

            // 差评设置（发布 + 草稿）
            RatedownSettingsEntity r = RatedownSettingsEntity.fromRequest(null, modelMapper);
            r.setUid(uidUtils.getUid());
            RatedownSettingsEntity rd = RatedownSettingsEntity.fromRequest(null, modelMapper);
            rd.setUid(uidUtils.getUid());
            settings.setRateDownSettings(r);
            settings.setDraftRateDownSettings(rd);

            // 客服状态设置（发布 + 草稿）
            AgentStatusSettingEntity st = AgentStatusSettingEntity.fromRequest(null, modelMapper);
            st.setUid(uidUtils.getUid());
            AgentStatusSettingEntity stDraft = AgentStatusSettingEntity.fromRequest(null, modelMapper);
            stDraft.setUid(uidUtils.getUid());
            settings.setAgentStatusSettings(st);
            settings.setDraftAgentStatusSettings(stDraft);

            return save(settings);
        } catch (Exception ex) {
            // 并发下若出现冲突，退避后返回现有默认配置
            return agentSettingsRepository.findByOrgUidAndIsDefaultTrue(orgUid)
                    .orElseThrow(() -> new RuntimeException("获取或创建默认客服配置失败"));
        }
    }

    /**
     * Enable agent settings
     */
    @CachePut(value = "agentSettings", key = "#uid", unless = "#result == null")
    public AgentSettingsResponse enable(String uid) {
        Optional<AgentSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("AgentSettings not found: " + uid);
        }

        AgentSettingsEntity entity = optional.get();
        entity.setEnabled(true);
        AgentSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    /**
     * Disable agent settings
     */
    @CachePut(value = "agentSettings", key = "#uid", unless = "#result == null")
    public AgentSettingsResponse disable(String uid) {
        Optional<AgentSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("AgentSettings not found: " + uid);
        }

        AgentSettingsEntity entity = optional.get();
        entity.setEnabled(false);
        AgentSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    /**
     * Publish draft settings to online
     */
    @Transactional
    @CachePut(value = "agentSettings", key = "#uid", unless = "#result == null")
    public AgentSettingsResponse publish(String uid) {
        Optional<AgentSettingsEntity> optional = agentSettingsRepository.findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("AgentSettings not found: " + uid);
        }

        AgentSettingsEntity entity = optional.get();
        
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
        
        if (entity.getDraftAutoReplySettings() != null) {
            AutoReplySettingsEntity published = entity.getAutoReplySettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftAutoReplySettings(), published);
            } else {
                AutoReplySettingsEntity newPublished = new AutoReplySettingsEntity();
                copyPropertiesExcludingIds(entity.getDraftAutoReplySettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setAutoReplySettings(newPublished);
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
        
        if (entity.getDraftAgentStatusSettings() != null) {
            AgentStatusSettingEntity published = entity.getAgentStatusSettings();
            if (published != null) {
                copyPropertiesExcludingIds(entity.getDraftAgentStatusSettings(), published);
            } else {
                AgentStatusSettingEntity newPublished = new AgentStatusSettingEntity();
                copyPropertiesExcludingIds(entity.getDraftAgentStatusSettings(), newPublished);
                newPublished.setUid(uidUtils.getUid());
                entity.setAgentStatusSettings(newPublished);
            }
        }
        
        entity.setHasUnpublishedChanges(false);
        entity.setPublishedAt(java.time.ZonedDateTime.now());
        
        AgentSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    @Override
    public AgentSettingsResponse convertToResponse(AgentSettingsEntity entity) {
        return modelMapper.map(entity, AgentSettingsResponse.class);
    }

    // 仅复制业务字段,忽略 id/uid/version 与时间字段,避免发布时两边主键被覆盖或引用被替换
    // 使用 Helper 类处理懒加载集合的正确复制
    private void copyPropertiesExcludingIds(Object source, Object target) {
        if (source instanceof ServiceSettingsEntity && target instanceof ServiceSettingsEntity) {
            serviceSettingsHelper.copyServiceSettingsProperties((ServiceSettingsEntity) source, (ServiceSettingsEntity) target);
        } else if (source instanceof MessageLeaveSettingsEntity && target instanceof MessageLeaveSettingsEntity) {
            messageLeaveSettingsHelper.copyMessageLeaveSettingsProperties((MessageLeaveSettingsEntity) source, (MessageLeaveSettingsEntity) target);
        } else {
            messageLeaveSettingsHelper.copyPropertiesExcludingIds(source, target);
        }
    }

    private void copyWorktimeSettings(WorktimeSettingEntity source, WorktimeSettingEntity target) {
        messageLeaveSettingsHelper.copyPropertiesExcludingIds(source, target);
    }
}
