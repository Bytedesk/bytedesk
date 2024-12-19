/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 12:09:25
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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class LeaveMsgService extends BaseRestService<LeaveMsgEntity, LeaveMsgRequest, LeaveMsgResponse> {

    private final LeaveMsgRepository LeaveMsgRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    @Override
    public Page<LeaveMsgResponse> queryByOrg(LeaveMsgRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<LeaveMsgEntity> spec = LeaveMsgSpecification.search(request);

        Page<LeaveMsgEntity> page = LeaveMsgRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<LeaveMsgResponse> queryByUser(LeaveMsgRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<LeaveMsgEntity> spec = LeaveMsgSpecification.search(request);

        Page<LeaveMsgEntity> page = LeaveMsgRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Optional<LeaveMsgEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public LeaveMsgResponse create(LeaveMsgRequest request) {
        log.info("request {}", request);

        LeaveMsgEntity leaveMsg = modelMapper.map(request, LeaveMsgEntity.class);
        leaveMsg.setUid(uidUtils.getCacheSerialUid());
        leaveMsg.setStatus(LeaveMsgStatusEnum.UNREAD.name());
        
        //

        // 保存留言
        LeaveMsgEntity savedLeaveMsg = save(leaveMsg);
        if (savedLeaveMsg == null) {
            throw new RuntimeException("LeaveMsg not saved");
        }

        return convertToResponse(savedLeaveMsg);
    }

    @Override
    public LeaveMsgResponse update(LeaveMsgRequest request) {
        
        Optional<LeaveMsgEntity> leaveMsgOptional = findByUid(request.getUid());
        if (leaveMsgOptional.isPresent()) {
            LeaveMsgEntity leaveMsg = leaveMsgOptional.get();
            leaveMsg.setStatus(request.getStatus());

            LeaveMsgEntity updateLeaveMsg = save(leaveMsg);
            if (updateLeaveMsg == null) {
                throw new RuntimeException("LeaveMsg not updated");
            }
            return convertToResponse(updateLeaveMsg);
        }
        throw new RuntimeException("LeaveMsg not found");
    }

    @Override
    public LeaveMsgEntity save(LeaveMsgEntity entity) {
        try {
            return LeaveMsgRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<LeaveMsgEntity> leaveMsgOptional = findByUid(uid);
        if (leaveMsgOptional.isPresent()) {
            LeaveMsgEntity leaveMsg = leaveMsgOptional.get();
            leaveMsg.setDeleted(true);
            save(leaveMsg);
        }
    }

    @Override
    public void delete(LeaveMsgRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            LeaveMsgEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public LeaveMsgResponse convertToResponse(LeaveMsgEntity entity) {
        return modelMapper.map(entity, LeaveMsgResponse.class);
    }

}
