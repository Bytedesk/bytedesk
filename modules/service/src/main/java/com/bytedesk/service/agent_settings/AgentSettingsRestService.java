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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettingsEntity;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRepository;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsRequest;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsRequest;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.agent_status.settings.AgentStatusSettingEntity;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.worktime.WorktimeEntity;
import com.bytedesk.service.worktime.WorktimeRepository;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AgentSettingsRestService
        extends BaseRestService<AgentSettingsEntity, AgentSettingsRequest, AgentSettingsResponse> {

    private final AgentSettingsRepository agentSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final AgentRepository agentRepository;

    private final FaqRepository faqRepository;

    private final WorktimeRepository worktimeRepository;

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
            updateFaqAssociationsIfPresent(service, request.getServiceSettings());
        }
        entity.setServiceSettings(service);

        // 初始化草稿为发布版本的拷贝，确保具有独立 uid
        ServiceSettingsEntity draft = ServiceSettingsEntity.fromRequest(request.getServiceSettings(), modelMapper);
        // 保证草稿与发布是不同实体，强制新的 uid
        draft.setUid(uidUtils.getUid());
        if (request.getServiceSettings() != null) {
            updateFaqAssociationsIfPresent(draft, request.getServiceSettings());
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
    // 处理 MessageLeaveSettings 关联（工作时间段）
    if (request.getMessageLeaveSettings() != null) {
        updateWorktimesIfPresent(mls, request.getMessageLeaveSettings());
    }
        entity.setMessageLeaveSettings(mls);
        MessageLeaveSettingsEntity mlsDraft = MessageLeaveSettingsEntity.fromRequest(request.getMessageLeaveSettings(),
                modelMapper);
        mlsDraft.setUid(uidUtils.getUid());
    if (request.getMessageLeaveSettings() != null) {
        updateWorktimesIfPresent(mlsDraft, request.getMessageLeaveSettings());
    }
        entity.setDraftMessageLeaveSettings(mlsDraft);

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

        // Agent status settings (published and draft)
        if (request.getAgentStatusSettings() != null) {
            AgentStatusSettingEntity st = modelMapper.map(request.getAgentStatusSettings(), AgentStatusSettingEntity.class);
            st.setUid(uidUtils.getUid());
            entity.setAgentStatusSettings(st);

            AgentStatusSettingEntity stDraft = modelMapper.map(request.getAgentStatusSettings(), AgentStatusSettingEntity.class);
            stDraft.setUid(uidUtils.getUid());
            entity.setDraftAgentStatusSettings(stDraft);
        }

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
                draft = new ServiceSettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getServiceSettings(), draft);
                entity.setDraftServiceSettings(draft);
            } else {
                // 保留草稿自身的唯一标识，避免被请求体中的 uid/uuid 覆盖
                String originalUid = draft.getUid();
                modelMapper.map(request.getServiceSettings(), draft);
                draft.setUid(originalUid);
            }
            // 根据 request 中的 Faq uids 映射关联
            updateFaqAssociationsIfPresent(draft, request.getServiceSettings());
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
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getMessageLeaveSettings(), draft);
                draft.setUid(originalUid);
            }
            // 根据 request 中的 worktime uids 映射关联
            updateWorktimesIfPresent(draft, request.getMessageLeaveSettings());
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
                draft = modelMapper.map(request.getRateDownSettings(), RatedownSettingsEntity.class);
                if (draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
                entity.setDraftRateDownSettings(draft);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getRateDownSettings(), draft);
                draft.setUid(originalUid);
            }
            entity.setHasUnpublishedChanges(true);
        }

        // Update draft: agent status settings
        if (request.getAgentStatusSettings() != null) {
            AgentStatusSettingEntity draft = entity.getDraftAgentStatusSettings();
            if (draft == null) {
                draft = modelMapper.map(request.getAgentStatusSettings(), AgentStatusSettingEntity.class);
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
                entity.setDraftAgentStatusSettings(draft);
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
            // 初始化发布与草稿版本
            ServiceSettingsEntity published = ServiceSettingsEntity.builder().build();
            published.setUid(uidUtils.getUid());
            ServiceSettingsEntity draft = ServiceSettingsEntity.builder().build();
            draft.setUid(uidUtils.getUid());
            settings.setServiceSettings(published);
            settings.setDraftServiceSettings(draft);

            // 初始化邀请/意图的发布与草稿
            InviteSettingsEntity inv = InviteSettingsEntity.builder().build();
            inv.setUid(uidUtils.getUid());
            InviteSettingsEntity invDraft = InviteSettingsEntity.builder().build();
            invDraft.setUid(uidUtils.getUid());
            settings.setInviteSettings(inv);
            settings.setDraftInviteSettings(invDraft);

            IntentionSettingsEntity inte = IntentionSettingsEntity.builder().build();
            inte.setUid(uidUtils.getUid());
            IntentionSettingsEntity inteDraft = IntentionSettingsEntity.builder().build();
            inteDraft.setUid(uidUtils.getUid());
            settings.setIntentionSettings(inte);
            settings.setDraftIntentionSettings(inteDraft);

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
    @CachePut(value = "agentSettings", key = "#uid", unless = "#result == null")
    public AgentSettingsResponse publish(String uid) {
        Optional<AgentSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("AgentSettings not found: " + uid);
        }

        AgentSettingsEntity entity = optional.get();
        
        // 复制草稿到发布版本，保留发布版本的原 uid
        if (entity.getDraftServiceSettings() != null) {
            ServiceSettingsEntity published = entity.getServiceSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            ServiceSettingsEntity newPublished = new ServiceSettingsEntity();
            modelMapper.map(entity.getDraftServiceSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setServiceSettings(newPublished);
        }
        
        if (entity.getDraftMessageLeaveSettings() != null) {
            MessageLeaveSettingsEntity published = entity.getMessageLeaveSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            MessageLeaveSettingsEntity newPublished = new MessageLeaveSettingsEntity();
            modelMapper.map(entity.getDraftMessageLeaveSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setMessageLeaveSettings(newPublished);
        }
        
        if (entity.getDraftAutoReplySettings() != null) {
            AutoReplySettingsEntity published = entity.getAutoReplySettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            AutoReplySettingsEntity newPublished = new AutoReplySettingsEntity();
            modelMapper.map(entity.getDraftAutoReplySettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setAutoReplySettings(newPublished);
        }
        
        if (entity.getDraftQueueSettings() != null) {
            QueueSettingsEntity published = entity.getQueueSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            QueueSettingsEntity newPublished = new QueueSettingsEntity();
            modelMapper.map(entity.getDraftQueueSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setQueueSettings(newPublished);
        }
        
        if (entity.getDraftRateDownSettings() != null) {
            RatedownSettingsEntity published = entity.getRateDownSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            RatedownSettingsEntity newPublished = new RatedownSettingsEntity();
            modelMapper.map(entity.getDraftRateDownSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setRateDownSettings(newPublished);
        }
        
        if (entity.getDraftInviteSettings() != null) {
            InviteSettingsEntity published = entity.getInviteSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            InviteSettingsEntity newPublished = new InviteSettingsEntity();
            modelMapper.map(entity.getDraftInviteSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setInviteSettings(newPublished);
        }
        
        if (entity.getDraftIntentionSettings() != null) {
            IntentionSettingsEntity published = entity.getIntentionSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            IntentionSettingsEntity newPublished = new IntentionSettingsEntity();
            modelMapper.map(entity.getDraftIntentionSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setIntentionSettings(newPublished);
        }
        
        if (entity.getDraftAgentStatusSettings() != null) {
            AgentStatusSettingEntity published = entity.getAgentStatusSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            AgentStatusSettingEntity newPublished = new AgentStatusSettingEntity();
            modelMapper.map(entity.getDraftAgentStatusSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setAgentStatusSettings(newPublished);
        }
        
        entity.setHasUnpublishedChanges(false);
        entity.setPublishedAt(java.time.ZonedDateTime.now());
        AgentSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    /**
     * 根据请求中的 FAQ UID 列表，解析并设置到目标 ServiceSettings 草稿(或发布)实体。
     * 仅当对应 UID 列表不为 null 才会更新；为 [] 时表示清空；为 null 时保持不变。
     */
    private void updateFaqAssociationsIfPresent(ServiceSettingsEntity target, ServiceSettingsRequest req) {
        if (req.getWelcomeFaqUids() != null) {
            target.setWelcomeFaqs(resolveFaqs(req.getWelcomeFaqUids()));
        }
        if (req.getFaqUids() != null) {
            target.setFaqs(resolveFaqs(req.getFaqUids()));
        }
        if (req.getQuickFaqUids() != null) {
            target.setQuickFaqs(resolveFaqs(req.getQuickFaqUids()));
        }
        if (req.getGuessFaqUids() != null) {
            target.setGuessFaqs(resolveFaqs(req.getGuessFaqUids()));
        }
        if (req.getHotFaqUids() != null) {
            target.setHotFaqs(resolveFaqs(req.getHotFaqUids()));
        }
        if (req.getShortcutFaqUids() != null) {
            target.setShortcutFaqs(resolveFaqs(req.getShortcutFaqUids()));
        }
        if (req.getProactiveFaqUids() != null) {
            target.setProactiveFaqs(resolveFaqs(req.getProactiveFaqUids()));
        }
    }

    /**
     * 将 uid 列表解析为 FaqEntity 列表。忽略无效 uid。
     */
    private List<FaqEntity> resolveFaqs(List<String> uids) {
        if (uids == null) {
            return null;
        }
        if (uids.isEmpty()) {
            return new ArrayList<>();
        }
        List<FaqEntity> result = new ArrayList<>(uids.size());
        for (String uid : uids) {
            if (uid == null || uid.isEmpty()) {
                continue;
            }
            faqRepository.findByUid(uid).ifPresent(result::add);
        }
        return result;
    }

    /**
     * 根据请求中的 worktime UID 列表，解析并设置到目标 MessageLeaveSettings 草稿(或发布)实体。
     * 仅当 UID 列表不为 null 才会更新；为空列表表示清空；为 null 时保持不变。
     */
    private void updateWorktimesIfPresent(MessageLeaveSettingsEntity target, MessageLeaveSettingsRequest req) {
        if (req.getWorktimeUids() == null) {
            return;
        }
        target.setWorktimes(resolveWorktimes(req.getWorktimeUids()));
    }

    /**
     * 将 uid 列表解析为 WorktimeEntity 列表。忽略无效 uid。
     */
    private List<WorktimeEntity> resolveWorktimes(List<String> uids) {
        if (uids == null) {
            return null;
        }
        if (uids.isEmpty()) {
            return new ArrayList<>();
        }
        List<WorktimeEntity> result = new ArrayList<>(uids.size());
        for (String uid : uids) {
            if (uid == null || uid.isEmpty()) {
                continue;
            }
            worktimeRepository.findByUid(uid).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public AgentSettingsResponse convertToResponse(AgentSettingsEntity entity) {
        return modelMapper.map(entity, AgentSettingsResponse.class);
    }

}
