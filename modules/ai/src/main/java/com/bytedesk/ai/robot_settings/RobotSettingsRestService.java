package com.bytedesk.ai.robot_settings;

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
public class RobotSettingsRestService extends BaseRestService<RobotSettingsEntity, RobotSettingsRequest, RobotSettingsResponse> {

    private final RobotSettingsRepository robotSettingsRepository;

    private final UidUtils uidUtils;

    @Cacheable(value = "robotSettings", key = "#uid", unless = "#result == null")
    @Override
    public Optional<RobotSettingsEntity> findByUid(String uid) {
        return robotSettingsRepository.findByUid(uid);
    }

    @Override
    public RobotSettingsResponse create(RobotSettingsRequest request) {
        RobotSettingsEntity entity = new RobotSettingsEntity();
        entity.setUid(uidUtils.getUid());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
        entity.setIsDefault(request.getIsDefault());
        entity.setOrgUid(request.getOrgUid());
        entity.setServiceSettings(request.getServiceSettings());
        entity.setRateDownSettings(request.getRateDownSettings());

        RobotSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    public RobotSettingsResponse update(RobotSettingsRequest request) {
        Optional<RobotSettingsEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            RobotSettingsEntity entity = optional.get();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setEnabled(request.getEnabled());
            entity.setServiceSettings(request.getServiceSettings());
            entity.setRateDownSettings(request.getRateDownSettings());
            RobotSettingsEntity updated = save(entity);
            return convertToResponse(updated);
        }
        throw new RuntimeException("RobotSettings not found: " + request.getUid());
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
        return (root, query, cb) -> cb.equal(root.get("orgUid"), request.getOrgUid());
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
    public RobotSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RobotSettingsEntity entity) {
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
    public RobotSettingsEntity getOrCreateDefault(String orgUid) {
        Optional<RobotSettingsEntity> defaultSettings = robotSettingsRepository.findByOrgUidAndIsDefaultTrue(orgUid);
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
        
        return save(settings);
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
