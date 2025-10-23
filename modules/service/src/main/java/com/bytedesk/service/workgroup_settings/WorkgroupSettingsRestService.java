package com.bytedesk.service.workgroup_settings;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WorkgroupSettingsRestService extends BaseRestService<WorkgroupSettingsEntity, WorkgroupSettingsRequest, WorkgroupSettingsResponse> {

    private final WorkgroupSettingsRepository workgroupSettingsRepository;

    private final UidUtils uidUtils;

    @Cacheable(value = "workgroupSettings", key = "#uid", unless = "#result == null")
    @Override
    public Optional<WorkgroupSettingsEntity> findByUid(String uid) {
        return workgroupSettingsRepository.findByUid(uid);
    }

    @Override
    public WorkgroupSettingsResponse create(WorkgroupSettingsRequest request) {
        WorkgroupSettingsEntity entity = new WorkgroupSettingsEntity();
        entity.setUid(uidUtils.getUid());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
        entity.setIsDefault(request.getIsDefault());
        entity.setOrgUid(request.getOrgUid());
        entity.setServiceSettings(request.getServiceSettings());
        entity.setMessageLeaveSettings(request.getMessageLeaveSettings());
        entity.setRobotSettings(request.getRobotSettings());
        entity.setQueueSettings(request.getQueueSettings());

        WorkgroupSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    public WorkgroupSettingsResponse update(WorkgroupSettingsRequest request) {
        Optional<WorkgroupSettingsEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkgroupSettingsEntity entity = optional.get();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setEnabled(request.getEnabled());
            entity.setServiceSettings(request.getServiceSettings());
            entity.setMessageLeaveSettings(request.getMessageLeaveSettings());
            entity.setRobotSettings(request.getRobotSettings());
            entity.setQueueSettings(request.getQueueSettings());
            WorkgroupSettingsEntity updated = save(entity);
            return convertToResponse(updated);
        }
        throw new RuntimeException("WorkgroupSettings not found: " + request.getUid());
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
        return (root, query, cb) -> cb.equal(root.get("orgUid"), request.getOrgUid());
    }

    @Override
    protected Page<WorkgroupSettingsEntity> executePageQuery(Specification<WorkgroupSettingsEntity> spec, Pageable pageable) {
        return workgroupSettingsRepository.findAll(spec, pageable);
    }

    @CachePut(value = "workgroupSettings", key = "#entity.uid", unless = "#result == null")
    @Override
    protected WorkgroupSettingsEntity doSave(WorkgroupSettingsEntity entity) {
        return workgroupSettingsRepository.save(entity);
    }

    @Override
    public WorkgroupSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkgroupSettingsEntity entity) {
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
        Optional<WorkgroupSettingsEntity> defaultSettings = workgroupSettingsRepository.findByOrgUidAndIsDefaultTrue(orgUid);
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
        
        return save(settings);
    }

    @Override
    public WorkgroupSettingsResponse convertToResponse(WorkgroupSettingsEntity entity) {
        return WorkgroupSettingsResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.getEnabled())
                .isDefault(entity.getIsDefault())
                .serviceSettings(entity.getServiceSettings())
                .messageLeaveSettings(entity.getMessageLeaveSettings())
                .robotSettings(entity.getRobotSettings())
                .queueSettings(entity.getQueueSettings())
                .build();
    }

}
