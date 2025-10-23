package com.bytedesk.service.workgroup_settings;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;

@Service
public class WorkgroupSettingsRestService extends BaseRestService<WorkgroupSettingsEntity, WorkgroupSettingsRequest, WorkgroupSettingsResponse> {

    @Autowired
    private WorkgroupSettingsService workgroupSettingsService;

    @Autowired
    private WorkgroupSettingsRepository workgroupSettingsRepository;

    @Override
    public Optional<WorkgroupSettingsEntity> findByUid(String uid) {
        return workgroupSettingsService.findByUid(uid);
    }

    @Override
    public WorkgroupSettingsResponse create(WorkgroupSettingsRequest request) {
        WorkgroupSettingsEntity entity = new WorkgroupSettingsEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
        entity.setIsDefault(request.getIsDefault());
        entity.setOrgUid(request.getOrgUid());
        entity.setServiceSettings(request.getServiceSettings());
        entity.setMessageLeaveSettings(request.getMessageLeaveSettings());
        entity.setRobotSettings(request.getRobotSettings());
        entity.setQueueSettings(request.getQueueSettings());

        WorkgroupSettingsEntity saved = workgroupSettingsService.create(entity);
        return convertToResponse(saved);
    }

    @Override
    public WorkgroupSettingsResponse update(WorkgroupSettingsRequest request) {
        Optional<WorkgroupSettingsEntity> optional = workgroupSettingsService.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkgroupSettingsEntity entity = optional.get();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setEnabled(request.getEnabled());
            entity.setServiceSettings(request.getServiceSettings());
            entity.setMessageLeaveSettings(request.getMessageLeaveSettings());
            entity.setRobotSettings(request.getRobotSettings());
            entity.setQueueSettings(request.getQueueSettings());
            WorkgroupSettingsEntity updated = workgroupSettingsService.update(entity);
            return convertToResponse(updated);
        }
        throw new RuntimeException("WorkgroupSettings not found: " + request.getUid());
    }

    @Override
    public void deleteByUid(String uid) {
        workgroupSettingsService.findByUid(uid).ifPresent(entity -> workgroupSettingsService.delete(entity.getUid()));
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

    @Override
    protected WorkgroupSettingsEntity doSave(WorkgroupSettingsEntity entity) {
        return workgroupSettingsService.create(entity);
    }

    @Override
    public WorkgroupSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkgroupSettingsEntity entity) {
        throw new RuntimeException("Optimistic locking failure for WorkgroupSettings: " + entity.getUid(), e);
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
