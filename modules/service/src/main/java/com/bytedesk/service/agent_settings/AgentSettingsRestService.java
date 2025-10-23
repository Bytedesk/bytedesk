package com.bytedesk.service.agent_settings;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;

@Service
public class AgentSettingsRestService extends BaseRestService<AgentSettingsEntity, AgentSettingsRequest, AgentSettingsResponse> {

    @Autowired
    private AgentSettingsService agentSettingsService;
    
    @Autowired
    private AgentSettingsRepository agentSettingsRepository;

    @Override
    public Optional<AgentSettingsEntity> findByUid(String uid) {
        return agentSettingsService.findByUid(uid);
    }

    @Override
    public AgentSettingsResponse create(AgentSettingsRequest request) {
        AgentSettingsEntity entity = new AgentSettingsEntity();
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

        AgentSettingsEntity saved = agentSettingsService.create(entity);
        return convertToResponse(saved);
    }

    @Override
    public AgentSettingsResponse update(AgentSettingsRequest request) {
        Optional<AgentSettingsEntity> optional = agentSettingsService.findByUid(request.getUid());
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
            AgentSettingsEntity updated = agentSettingsService.update(entity);
            return convertToResponse(updated);
        }
        throw new RuntimeException("AgentSettings not found: " + request.getUid());
    }

    @Override
    public void deleteByUid(String uid) {
        agentSettingsService.findByUid(uid).ifPresent(entity -> agentSettingsService.delete(entity.getUid()));
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

    @Override
    protected AgentSettingsEntity doSave(AgentSettingsEntity entity) {
        return agentSettingsService.create(entity);
    }

    @Override
    public AgentSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AgentSettingsEntity entity) {
        throw new RuntimeException("Optimistic locking failure for AgentSettings: " + entity.getUid(), e);
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
