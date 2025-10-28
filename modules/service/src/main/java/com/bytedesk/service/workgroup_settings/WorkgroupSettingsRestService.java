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

        WorkgroupSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
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
    public WorkgroupSettingsEntity getOrCreateDefault(String orgUid) {
        Optional<WorkgroupSettingsEntity> defaultSettings = workgroupSettingsRepository
                .findByOrgUidAndIsDefaultTrue(orgUid);
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
        entity.setServiceSettings(entity.getDraftServiceSettings());
        entity.setMessageLeaveSettings(entity.getDraftMessageLeaveSettings());
        entity.setRobotSettings(entity.getDraftRobotSettings());
        entity.setQueueSettings(entity.getDraftQueueSettings());
        if (entity.getDraftInviteSettings() != null) {
            entity.setInviteSettings(entity.getDraftInviteSettings());
        }
        if (entity.getDraftIntentionSettings() != null) {
            entity.setIntentionSettings(entity.getDraftIntentionSettings());
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

}
