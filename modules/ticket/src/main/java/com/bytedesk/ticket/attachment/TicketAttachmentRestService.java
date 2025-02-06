/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-06 15:06:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-06 15:14:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.attachment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketAttachmentRestService extends BaseRestService<TicketAttachmentEntity, TicketAttachmentRequest, TicketAttachmentResponse> {

    // private final TicketAttachmentRepository ticketAttachmentRepository;

    // private final ModelMapper modelMapper;

    // private final UidUtils uidUtils;

    @Override
    public Page<TicketAttachmentResponse> queryByOrg(TicketAttachmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<TicketAttachmentResponse> queryByUser(TicketAttachmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public TicketAttachmentResponse create(TicketAttachmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public TicketAttachmentResponse update(TicketAttachmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(TicketAttachmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public Optional<TicketAttachmentEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TicketAttachmentEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    

    @Override
    public TicketAttachmentEntity save(TicketAttachmentEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    

    @Override
    public TicketAttachmentResponse convertToResponse(TicketAttachmentEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }
    
}
