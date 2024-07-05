/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-02 18:06:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class LeaveMessageService extends BaseService<LeaveMessage, LeaveMessageRequest, LeaveMessageResponse> {

    private final LeaveMessageRepository leaveMessageRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final ThreadService threadService;

    @Override
    public Page<LeaveMessageResponse> queryByOrg(LeaveMessageRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<LeaveMessage> spec = LeaveMessageSpecification.search(request);

        Page<LeaveMessage> page = leaveMessageRepository.findAll(spec, pageable);

        return page.map(this:: convertToResponse);
    }

    @Override
    public Page<LeaveMessageResponse> queryByUser(LeaveMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<LeaveMessage> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public LeaveMessageResponse create(LeaveMessageRequest request) {
        log.info("request {}", request);
        
        LeaveMessage leaveMessage = modelMapper.map(request, LeaveMessage.class);
        leaveMessage.setUid(uidUtils.getCacheSerialUid());
        // 
        Optional<Thread> thread = threadService.findByUid(request.getThreadUid());
        if (thread.isPresent()) {
            leaveMessage.setThread(thread.get());
            leaveMessage.setOrgUid(thread.get().getOrgUid());
        } else {
            throw new RuntimeException("Thread not found");
        }
        LeaveMessage savedLeaveMessage = save(leaveMessage);
        if (savedLeaveMessage == null) {
            throw new RuntimeException("LeaveMessage not saved");
        }

        return convertToResponse(savedLeaveMessage);
    }

    @Override
    public LeaveMessageResponse update(LeaveMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public LeaveMessage save(LeaveMessage entity) {
        try {
           return leaveMessageRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(LeaveMessage entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            LeaveMessage entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public LeaveMessageResponse convertToResponse(LeaveMessage entity) {
        return modelMapper.map(entity, LeaveMessageResponse.class);
    }

}
