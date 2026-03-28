/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 11:15:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:52:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgentStatusRestService extends BaseRestServiceWithExport<AgentStatusEntity, AgentStatusRequest, AgentStatusResponse, AgentStatusExcel> {
    
    private final AgentStatusRepository agentStatusRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<AgentStatusEntity> createSpecification(AgentStatusRequest request) {
        return AgentStatusSpecification.search(request, authService);
    }

    @Override
    protected Page<AgentStatusEntity> executePageQuery(Specification<AgentStatusEntity> spec, Pageable pageable) {
        return agentStatusRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "agentStatus", key = "#uid", unless = "#result == null")
    @Override
    public Optional<AgentStatusEntity> findByUid(String uid) {
        return agentStatusRepository.findByUid(uid);
    }

    @Override
    public AgentStatusResponse create(AgentStatusRequest request) {

        AgentStatusEntity agentStatus = modelMapper.map(request, AgentStatusEntity.class);
        agentStatus.setUid(uidUtils.getUid());
        finalizePreviousStatusDuration(agentStatus);

        AgentStatusEntity savedAgentStatus = save(agentStatus);
        if (savedAgentStatus == null) {
            throw new RuntimeException("AgentStatus create failed");
        }

        return convertToResponse(savedAgentStatus);
    }

    @Override
    public AgentStatusResponse update(AgentStatusRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public AgentStatusEntity save(AgentStatusEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }
    
    @Override
    protected AgentStatusEntity doSave(AgentStatusEntity entity) {
        return agentStatusRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AgentStatusEntity> agentStatus = findByUid(uid);
        if (agentStatus.isPresent()) {
            AgentStatusEntity entity = agentStatus.get();
            entity.setDeleted(true);
            save(entity);
            // agentStatusRepository.delete(agentStatus.get());
        } else {
            throw new RuntimeException("AgentStatus not found");
        }
    }

    @Override
    public void delete(AgentStatusRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public AgentStatusEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AgentStatusEntity entity) {
        try {
            Optional<AgentStatusEntity> latest = agentStatusRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AgentStatusEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return agentStatusRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public AgentStatusResponse convertToResponse(AgentStatusEntity entity) {
        AgentStatusResponse response = modelMapper.map(entity, AgentStatusResponse.class);
        response.setAgent(entity.getAgent());
        return response;
    }

    @Override
    public AgentStatusExcel convertToExcel(AgentStatusEntity entity) {
        UserProtobuf agent = entity.getAgent();

        AgentStatusExcel excel = new AgentStatusExcel();
        excel.setNickname(agent != null ? agent.getNickname() : null);
        excel.setAgentUid(agent != null ? agent.getUid() : null);
        excel.setStatus(formatStatus(entity.getStatus()));
        excel.setRestReason(entity.getRestReason());
        excel.setDuration(formatDuration(entity.getDurationSeconds()));
        excel.setCreatedAt(entity.getCreatedAt());
        return excel;
    }

    private void finalizePreviousStatusDuration(AgentStatusEntity currentStatus) {
        if (currentStatus == null) {
            return;
        }

        currentStatus.setDurationSeconds(0L);
        if (!StringUtils.hasText(currentStatus.getOrgUid())) {
            return;
        }

        String agentPayload = currentStatus.getAgentString();
        if (!StringUtils.hasText(agentPayload)) {
            return;
        }

        UserProtobuf agentProto = currentStatus.getAgent();
        if (agentProto == null || !StringUtils.hasText(agentProto.getUid())) {
            return;
        }

        Optional<AgentStatusEntity> latestStatusOptional = agentStatusRepository
                .findFirstByAgentContainsAndOrgUidAndDeletedFalseOrderByCreatedAtDesc(agentProto.getUid(),
                        currentStatus.getOrgUid());

        if (latestStatusOptional.isEmpty()) {
            return;
        }

        AgentStatusEntity previousStatus = latestStatusOptional.get();
        if (previousStatus.getCreatedAt() == null) {
            return;
        }

        ZonedDateTime now = ZonedDateTime.now();
        long durationSeconds = ChronoUnit.SECONDS.between(previousStatus.getCreatedAt(), now);
        if (durationSeconds < 0) {
            durationSeconds = 0;
        }

        previousStatus.setDurationSeconds(durationSeconds);
        agentStatusRepository.save(previousStatus);
    }

    private String formatStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "-";
        }

        return switch (status) {
            case "AVAILABLE" -> "接待中";
            case "REST" -> "小休中";
            case "BUSY" -> "忙碌中";
            case "OFFLINE" -> "离线";
            default -> status;
        };
    }

    private String formatDuration(Long seconds) {
        if (seconds == null) {
            return "-";
        }

        long totalSeconds = Math.max(0L, seconds);
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long remainingSeconds = totalSeconds % 60;

        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days).append("天");
        }
        if (hours > 0 || days > 0) {
            builder.append(hours).append("小时");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            builder.append(minutes).append("分钟");
        }
        builder.append(remainingSeconds).append("秒");
        return builder.toString();
    }

    
    
    
}
