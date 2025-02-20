/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 12:50:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 13:18:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketStatisticRestService extends BaseRestService<TicketStatisticEntity, TicketStatisticRequest, TicketStatisticResponse> {
    
    private final TicketStatisticRepository ticketStatisticRepository;


    @Override
    public Page<TicketStatisticResponse> queryByOrg(TicketStatisticRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<TicketStatisticResponse> queryByUser(TicketStatisticRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public TicketStatisticResponse update(TicketStatisticRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
    
    public TicketStatisticResponse convertToResponse(TicketStatisticEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

    @Override
    public TicketStatisticResponse create(TicketStatisticRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void delete(TicketStatisticRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TicketStatisticEntity> entity = findByUid(uid);
        if (entity.isPresent()) {
            entity.get().setDeleted(true);
            save(entity.get());
        }
    }

    @Override
    public Optional<TicketStatisticEntity> findByUid(String uid) {
        return ticketStatisticRepository.findByUid(uid);
    }

    @Override
    public TicketStatisticEntity save(TicketStatisticEntity entity) {
        try {
            return ticketStatisticRepository.save(entity);
        } catch (Exception e) {
            log.error("save ticket statistic entity error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TicketStatisticEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

} 