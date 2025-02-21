/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 11:15:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 14:42:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgentStatusLogService extends BaseRestService<AgentStatusLogEntity, AgentStatusLogRequest, AgentStatusLogResponse> {
    
    private final AgentStatusLogRepository agentStatusRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<AgentStatusLogResponse> queryByOrg(AgentStatusLogRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<AgentStatusLogResponse> queryByUser(AgentStatusLogRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<AgentStatusLogEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public AgentStatusLogResponse create(AgentStatusLogRequest request) {

        AgentStatusLogEntity agentStatus = modelMapper.map(request, AgentStatusLogEntity.class);
        agentStatus.setUid(uidUtils.getUid());

        AgentStatusLogEntity savedAgentStatusLog = save(agentStatus);
        if (savedAgentStatusLog == null) {
            throw new RuntimeException("AgentStatusLog create failed");
        }

        return convertToResponse(savedAgentStatusLog);
    }

    @Override
    public AgentStatusLogResponse update(AgentStatusLogRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public AgentStatusLogEntity save(AgentStatusLogEntity entity) {
        try {
            return agentStatusRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(AgentStatusLogRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AgentStatusLogEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public AgentStatusLogResponse convertToResponse(AgentStatusLogEntity entity) {
        return modelMapper.map(entity, AgentStatusLogResponse.class);
    }
    
    
}
