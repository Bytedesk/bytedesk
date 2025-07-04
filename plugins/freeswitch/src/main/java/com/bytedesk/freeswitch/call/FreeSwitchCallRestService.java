/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-14 11:27:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 12:45:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.call;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import com.bytedesk.core.base.BaseRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch呼叫服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreeSwitchCallRestService extends BaseRestService<FreeSwitchCallEntity, FreeSwitchCallRequest, FreeSwitchCallResponse> {
    
    // private final FreeSwitchCallRepository callRepository;
    // private final FreeSwitchAgentService agentService;
    // private final FreeSwitchQueueService queueService;
    
    @Override
    public Page<FreeSwitchCallResponse> queryByOrg(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<FreeSwitchCallResponse> queryByUser(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<FreeSwitchCallEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public FreeSwitchCallResponse initVisitor(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public FreeSwitchCallResponse update(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    protected FreeSwitchCallEntity doSave(FreeSwitchCallEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doSave'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(FreeSwitchCallRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public FreeSwitchCallEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            FreeSwitchCallEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public FreeSwitchCallResponse convertToResponse(FreeSwitchCallEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }
}