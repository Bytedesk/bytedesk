package com.bytedesk.ai.robot_settings;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;

@Service
public class RobotSettingsRestService extends BaseRestService<RobotSettingsEntity, RobotSettingsRequest, RobotSettingsResponse> {

    @Autowired
    private RobotSettingsService robotSettingsService;

    @Autowired
    private RobotSettingsRepository robotSettingsRepository;

    @Override
    public Optional<RobotSettingsEntity> findByUid(String uid) {
        return robotSettingsService.findByUid(uid);
    }

    @Override
    public RobotSettingsResponse create(RobotSettingsRequest request) {
        RobotSettingsEntity entity = new RobotSettingsEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
        entity.setIsDefault(request.getIsDefault());
        entity.setOrgUid(request.getOrgUid());
        entity.setServiceSettings(request.getServiceSettings());
        entity.setRateDownSettings(request.getRateDownSettings());

        RobotSettingsEntity saved = robotSettingsService.create(entity);
        return convertToResponse(saved);
    }

    @Override
    public RobotSettingsResponse update(RobotSettingsRequest request) {
        Optional<RobotSettingsEntity> optional = robotSettingsService.findByUid(request.getUid());
        if (optional.isPresent()) {
            RobotSettingsEntity entity = optional.get();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setEnabled(request.getEnabled());
            entity.setServiceSettings(request.getServiceSettings());
            entity.setRateDownSettings(request.getRateDownSettings());
            RobotSettingsEntity updated = robotSettingsService.update(entity);
            return convertToResponse(updated);
        }
        throw new RuntimeException("RobotSettings not found: " + request.getUid());
    }

    @Override
    public void deleteByUid(String uid) {
        robotSettingsService.findByUid(uid).ifPresent(entity -> robotSettingsService.delete(entity.getUid()));
    }

    @Override
    public void delete(RobotSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    protected Specification<RobotSettingsEntity> createSpecification(RobotSettingsRequest request) {
        return (root, query, cb) -> cb.equal(root.get("orgUid"), request.getOrgUid());
    }

    @Override
    protected Page<RobotSettingsEntity> executePageQuery(Specification<RobotSettingsEntity> spec, Pageable pageable) {
        return robotSettingsRepository.findAll(spec, pageable);
    }

    @Override
    protected RobotSettingsEntity doSave(RobotSettingsEntity entity) {
        return robotSettingsService.create(entity);
    }

    @Override
    public RobotSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RobotSettingsEntity entity) {
        throw new RuntimeException("Optimistic locking failure for RobotSettings: " + entity.getUid(), e);
    }

    @Override
    public RobotSettingsResponse convertToResponse(RobotSettingsEntity entity) {
        return RobotSettingsResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.getEnabled())
                .isDefault(entity.getIsDefault())
                .serviceSettings(entity.getServiceSettings())
                .rateDownSettings(entity.getRateDownSettings())
                .build();
    }

}
