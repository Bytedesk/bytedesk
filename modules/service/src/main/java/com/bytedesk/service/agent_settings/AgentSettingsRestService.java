package com.bytedesk.service.agent_settings;

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
public class AgentSettingsRestService extends BaseRestService<AgentSettingsEntity, AgentSettingsRequest, AgentSettingsResponse> {

    private final AgentSettingsRepository agentSettingsRepository;

    private final UidUtils uidUtils;

    @Cacheable(value = "agentSettings", key = "#uid", unless = "#result == null")
    @Override
    public Optional<AgentSettingsEntity> findByUid(String uid) {
        return agentSettingsRepository.findByUid(uid);
    }

    @Override
    public AgentSettingsResponse create(AgentSettingsRequest request) {
        AgentSettingsEntity entity = new AgentSettingsEntity();
        entity.setUid(uidUtils.getUid());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
        entity.setIsDefault(request.getIsDefault());
        entity.setOrgUid(request.getOrgUid());
        entity.setServiceSettings(request.getServiceSettings());
        entity.setMessageLeaveSettings(request.getMessageLeaveSettings());
        entity.setAutoReplySettings(request.getAutoReplySettings());
        entity.setQueueSettings(request.getQueueSettings());
        entity.setRateDownSettings(request.getRateDownSettings());

        AgentSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    public AgentSettingsResponse update(AgentSettingsRequest request) {
        Optional<AgentSettingsEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            AgentSettingsEntity entity = optional.get();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setEnabled(request.getEnabled());
            entity.setServiceSettings(request.getServiceSettings());
            entity.setMessageLeaveSettings(request.getMessageLeaveSettings());
            entity.setAutoReplySettings(request.getAutoReplySettings());
            entity.setQueueSettings(request.getQueueSettings());
            entity.setRateDownSettings(request.getRateDownSettings());
            AgentSettingsEntity updated = save(entity);
            return convertToResponse(updated);
        }
        throw new RuntimeException("AgentSettings not found: " + request.getUid());
    }

    @CacheEvict(value = "agentSettings", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
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
        return (root, query, cb) -> cb.equal(root.get("orgUid"), request.getOrgUid());
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
    public AgentSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AgentSettingsEntity entity) {
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
    public AgentSettingsEntity getOrCreateDefault(String orgUid) {
        Optional<AgentSettingsEntity> defaultSettings = agentSettingsRepository.findByOrgUidAndIsDefaultTrue(orgUid);
        if (defaultSettings.isPresent()) {
            return defaultSettings.get();
        }
        
        // Create default settings
        AgentSettingsEntity settings = AgentSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .name("默认客服配置")
                .description("系统默认客服配置")
                .isDefault(true)
                .enabled(true)
                .orgUid(orgUid)
                .build();
        
        return save(settings);
    }

    @Override
    public AgentSettingsResponse convertToResponse(AgentSettingsEntity entity) {
        return AgentSettingsResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.getEnabled())
                .isDefault(entity.getIsDefault())
                .serviceSettings(entity.getServiceSettings())
                .messageLeaveSettings(entity.getMessageLeaveSettings())
                .autoReplySettings(entity.getAutoReplySettings())
                .queueSettings(entity.getQueueSettings())
                .rateDownSettings(entity.getRateDownSettings())
                .build();
    }

    // no protected convertToResponse(Entity) here to avoid duplicate signature

}
