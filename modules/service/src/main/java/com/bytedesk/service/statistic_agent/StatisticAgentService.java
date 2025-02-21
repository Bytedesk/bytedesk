/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 09:17:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:27:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_agent;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StatisticAgentService extends BaseRestService<StatisticAgentEntity, StatisticAgentRequest, StatisticAgentResponse> {

    private final StatisticAgentRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<StatisticAgentResponse> queryByOrg(StatisticAgentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<StatisticAgentResponse> queryByUser(StatisticAgentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<StatisticAgentEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Cacheable(value = "StatisticAgent", key = "#agentUid", unless="#result == null")
    public Optional<StatisticAgentEntity> findByAgentUid(String agentUid) {
        String todayDate = BdDateUtils.formatToday();
        return repository.findByAgentUidAndDate(agentUid, todayDate);
    }

    @Override
    public StatisticAgentResponse create(StatisticAgentRequest request) {
        StatisticAgentEntity statisticAgent = modelMapper.map(request, StatisticAgentEntity.class);
        statisticAgent.setUid(uidUtils.getUid());
        // 
        StatisticAgentEntity savedStatisticAgent = save(statisticAgent);
        if (savedStatisticAgent == null) {
            throw new RuntimeException("Create StatisticAgent failed");
        }
        return convertToResponse(savedStatisticAgent);
    }

    @Override
    public StatisticAgentResponse update(StatisticAgentRequest request) {
        Optional<StatisticAgentEntity> statisticOptional = findByAgentUid(request.getAgentUid());
        if (statisticOptional.isPresent()) {
            StatisticAgentEntity statistic = statisticOptional.get();
            // statistic.setAgentUid(request.getAgentUid());

            // statistic.setOnline(request.getOnline());
            return convertToResponse(save(statistic));
        } else {
            return create(request);
        }
    }

    @Override
    public StatisticAgentEntity save(StatisticAgentEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(StatisticAgentRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            StatisticAgentEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public StatisticAgentResponse convertToResponse(StatisticAgentEntity entity) {
        return modelMapper.map(entity, StatisticAgentResponse.class);
    }

    
    
}
