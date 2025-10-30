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
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsEntity;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WorkgroupSettingsRestService
        extends BaseRestService<WorkgroupSettingsEntity, WorkgroupSettingsRequest, WorkgroupSettingsResponse> {

    private final WorkgroupSettingsRepository workgroupSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

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

        RobotRoutingSettingsEntity rrs = convertRobotRoutingSettingsRequestToEntity(request.getRobotSettings());
        rrs.setUid(uidUtils.getUid());
        entity.setRobotSettings(rrs);
        RobotRoutingSettingsEntity rrsDraft = convertRobotRoutingSettingsRequestToEntity(request.getRobotSettings());
        rrsDraft.setUid(uidUtils.getUid());
        entity.setDraftRobotSettings(rrsDraft);

        QueueSettingsEntity qs = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
        qs.setUid(uidUtils.getUid());
        entity.setQueueSettings(qs);
        QueueSettingsEntity qsDraft = QueueSettingsEntity.fromRequest(request.getQueueSettings(), modelMapper);
        qsDraft.setUid(uidUtils.getUid());
        entity.setDraftQueueSettings(qsDraft);

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
        modelMapper.map(request, entity);

        // 使用静态工厂方法更新嵌套设置,只在非 null 时更新
        if (request.getServiceSettings() != null) {
            ServiceSettingsEntity draft = entity.getDraftServiceSettings();
            if (draft == null) {
                draft = new ServiceSettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getServiceSettings(), draft);
                entity.setDraftServiceSettings(draft);
            } else {
                // 保留草稿唯一标识，避免被请求体覆盖
                String originalUid = draft.getUid();
                modelMapper.map(request.getServiceSettings(), draft);
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
            entity.setHasUnpublishedChanges(true);
        }
        if (request.getRobotSettings() != null) {
            RobotRoutingSettingsEntity draft = entity.getDraftRobotSettings();
            if (draft == null) {
                draft = convertRobotRoutingSettingsRequestToEntity(request.getRobotSettings());
                if (draft != null && draft.getUid() == null) {
                    draft.setUid(uidUtils.getUid());
                }
                entity.setDraftRobotSettings(draft);
            } else {
                String originalUid = draft.getUid();
                modelMapper.map(request.getRobotSettings(), draft);
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
                .name("默认技能组配置")
                .description("系统默认技能组配置")
                .isDefault(true)
                .enabled(true)
                .orgUid(orgUid)
                .build();
        // 初始化 service 的发布与草稿
        ServiceSettingsEntity published = ServiceSettingsEntity.builder().build();
        published.setUid(uidUtils.getUid());
        ServiceSettingsEntity draft = ServiceSettingsEntity.builder().build();
        draft.setUid(uidUtils.getUid());
        settings.setServiceSettings(published);
        settings.setDraftServiceSettings(draft);

        // 刚创建的即为默认，确保同 org 唯一
        ensureSingleDefault(orgUid, settings);
        return save(settings);
    }

    /**
     * Publish draft settings to online for workgroup
     */
    @CachePut(value = "workgroupSettings", key = "#uid", unless = "#result == null")
    public WorkgroupSettingsResponse publish(String uid) {
        Optional<WorkgroupSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("WorkgroupSettings not found: " + uid);
        }
        WorkgroupSettingsEntity entity = optional.get();
        
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
        
        if (entity.getDraftRobotSettings() != null) {
            RobotRoutingSettingsEntity published = entity.getRobotSettings();
            String publishedUid = (published != null) ? published.getUid() : null;
            
            RobotRoutingSettingsEntity newPublished = new RobotRoutingSettingsEntity();
            modelMapper.map(entity.getDraftRobotSettings(), newPublished);
            
            if (publishedUid != null) {
                newPublished.setUid(publishedUid);
            } else {
                newPublished.setUid(uidUtils.getUid());
            }
            entity.setRobotSettings(newPublished);
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
        WorkgroupSettingsEntity updated = save(entity);
        return convertToResponse(updated);
    }

    // RobotRoutingSettings 转换方法 (TODO: 将来可以迁移到静态工厂方法)
    private RobotRoutingSettingsEntity convertRobotRoutingSettingsRequestToEntity(RobotRoutingSettingsRequest request) {
        if (request == null) {
            return RobotRoutingSettingsEntity.builder().build();
        }
        // 使用 ModelMapper 简化转换
        return modelMapper.map(request, RobotRoutingSettingsEntity.class);
    }

    @Override
    public WorkgroupSettingsResponse convertToResponse(WorkgroupSettingsEntity entity) {
        return modelMapper.map(entity, WorkgroupSettingsResponse.class);
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
