package com.bytedesk.ai.robot_settings;

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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RobotSettingsRestService
        extends BaseRestService<RobotSettingsEntity, RobotSettingsRequest, RobotSettingsResponse> {

    private final RobotSettingsRepository robotSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

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

        // 发布与草稿：差评配置（统一使用 fromRequest，内部已处理 null）
        RatedownSettingsEntity r = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
        r.setUid(uidUtils.getUid());
        entity.setRateDownSettings(r);
        RatedownSettingsEntity rd = RatedownSettingsEntity.fromRequest(request.getRateDownSettings(), modelMapper);
        rd.setUid(uidUtils.getUid());
        entity.setDraftRateDownSettings(rd);

        // LLM: 如果请求包含 llm，则创建并关联（同时初始化 draft）
        if (request.getLlm() != null) {
            RobotLlmEntity llm = modelMapper.map(request.getLlm(), RobotLlmEntity.class);
            llm.setUid(uidUtils.getUid());
            entity.setLlm(llm);
            RobotLlmEntity draftLlm = modelMapper.map(request.getLlm(), RobotLlmEntity.class);
            draftLlm.setUid(uidUtils.getUid());
            entity.setDraftLlm(draftLlm);
        }

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
        modelMapper.map(request, entity);

        // 使用静态工厂方法更新嵌套设置,只在非 null 时更新
        if (request.getServiceSettings() != null) {
            // 复用并更新现有草稿
            ServiceSettingsEntity draft = entity.getDraftServiceSettings();
            // 保存原有uid(如果存在)
            String originalUid = (draft != null) ? draft.getUid() : null;
            
            if (draft == null) {
                draft = new ServiceSettingsEntity();
            }
            modelMapper.map(request.getServiceSettings(), draft);
            
            // 根据原有uid决定是保留还是创建新uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            
            entity.setDraftServiceSettings(draft);
            entity.setHasUnpublishedChanges(true);
        }
        // 更新草稿:邀请/意图
        if (request.getInviteSettings() != null) {
            InviteSettingsEntity draft = entity.getDraftInviteSettings();
            // 保存原有uid(如果存在)
            String originalUid = (draft != null) ? draft.getUid() : null;
            
            if (draft == null) {
                draft = new InviteSettingsEntity();
            }
            modelMapper.map(request.getInviteSettings(), draft);
            
            // 根据原有uid决定是保留还是创建新uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            
            entity.setDraftInviteSettings(draft);
            entity.setHasUnpublishedChanges(true);
        }
        if (request.getIntentionSettings() != null) {
            IntentionSettingsEntity draft = entity.getDraftIntentionSettings();
            // 保存原有uid(如果存在)
            String originalUid = (draft != null) ? draft.getUid() : null;
            
            if (draft == null) {
                draft = new IntentionSettingsEntity();
            }
            modelMapper.map(request.getIntentionSettings(), draft);
            
            // 根据原有uid决定是保留还是创建新uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            
            entity.setDraftIntentionSettings(draft);
            entity.setHasUnpublishedChanges(true);
        }
        if (request.getRateDownSettings() != null) {
            RatedownSettingsEntity draft = entity.getDraftRateDownSettings();
            // 保存原有uid(如果存在)
            String originalUid = (draft != null) ? draft.getUid() : null;
            
            if (draft == null) {
                draft = new RatedownSettingsEntity();
            }
            modelMapper.map(request.getRateDownSettings(), draft);
            
            // 根据原有uid决定是保留还是创建新uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            
            entity.setDraftRateDownSettings(draft);
            entity.setHasUnpublishedChanges(true);
        }

        // 更新 LLM(仅更新草稿;发布时再覆盖线上)
        if (request.getLlm() != null) {
            RobotLlmEntity draft = entity.getDraftLlm();
            // 保存原有uid(如果存在)
            String originalUid = (draft != null) ? draft.getUid() : null;
            
            if (draft == null) {
                draft = new RobotLlmEntity();
            }
            modelMapper.map(request.getLlm(), draft);
            
            // 根据原有uid决定是保留还是创建新uid
            if (originalUid != null) {
                draft.setUid(originalUid);
            } else {
                draft.setUid(uidUtils.getUid());
            }
            
            entity.setDraftLlm(draft);
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

        // Create default settings
        RobotSettingsEntity settings = RobotSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .name("默认机器人配置")
                .description("系统默认机器人配置")
                .isDefault(true)
                .enabled(true)
                .orgUid(orgUid)
                .build();
        ServiceSettingsEntity published = ServiceSettingsEntity.builder().build();
        published.setUid(uidUtils.getUid());
        ServiceSettingsEntity draft = ServiceSettingsEntity.builder().build();
        draft.setUid(uidUtils.getUid());
        settings.setServiceSettings(published);
        settings.setDraftServiceSettings(draft);

        // 默认：关闭知识库，空 LLM 配置
        settings.setKbEnabled(false);
        settings.setKbUid(null);

        // 刚创建的即为默认，确保同 org 唯一
        ensureSingleDefault(orgUid, settings);
        return save(settings);
    }

    /**
     * Publish draft settings to online for robot
     */
    @CachePut(value = "robotSettings", key = "#uid", unless = "#result == null")
    public RobotSettingsResponse publish(String uid) {
        Optional<RobotSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("RobotSettings not found: " + uid);
        }
        RobotSettingsEntity entity = optional.get();
        
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
        
        if (entity.getDraftLlm() != null) {
            RobotLlmEntity published = entity.getLlm();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            RobotLlmEntity newPublished = new RobotLlmEntity();
            modelMapper.map(entity.getDraftLlm(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setLlm(newPublished);
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
        
        entity.setHasUnpublishedChanges(false);
        entity.setPublishedAt(java.time.ZonedDateTime.now());
        RobotSettingsEntity updated = save(entity);
        return convertToResponse(updated);
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
