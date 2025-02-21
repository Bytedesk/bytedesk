/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 09:22:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:27:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_thread;

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
public class StatisticThreadService
        extends BaseRestService<StatisticThreadEntity, StatisticThreadRequest, StatisticThreadResponse> {
    
    private final StatisticThreadRepository statisticThreadRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<StatisticThreadResponse> queryByOrg(StatisticThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<StatisticThreadResponse> queryByUser(StatisticThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<StatisticThreadEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public StatisticThreadResponse create(StatisticThreadRequest request) {
    
        StatisticThreadEntity statisticThread = modelMapper.map(request, StatisticThreadEntity.class);
        statisticThread.setUid(uidUtils.getUid());

        StatisticThreadEntity savedThread = save(statisticThread);
        if (savedThread == null) {
            throw new RuntimeException("save statistic_thread failed");
        }
        return convertToResponse(savedThread);
    }

    @Override
    public StatisticThreadResponse update(StatisticThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public StatisticThreadEntity save(StatisticThreadEntity entity) {
        try {
            return statisticThreadRepository.save(entity);
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
    public void delete(StatisticThreadRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            StatisticThreadEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public StatisticThreadResponse convertToResponse(StatisticThreadEntity entity) {
        return modelMapper.map(entity, StatisticThreadResponse.class);
    }

    
    
}
