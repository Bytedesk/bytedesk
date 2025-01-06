/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 09:24:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 11:04:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QueueMemberRestService extends BaseRestService<QueueMemberEntity, QueueMemberRequest, QueueMemberResponse> {

    private final QueueMemberRepository queueMemberRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<QueueMemberResponse> queryByOrg(QueueMemberRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<QueueMemberEntity> specification = QueueMemberSpecification.search(request);
        Page<QueueMemberEntity> page = queueMemberRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QueueMemberResponse> queryByUser(QueueMemberRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user is null");
        }
        // set user uid

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<QueueMemberEntity> specification = QueueMemberSpecification.search(request);
        Page<QueueMemberEntity> page = queueMemberRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Cacheable(value = "counter", key = "#uid")
    @Override
    public Optional<QueueMemberEntity> findByUid(String uid) {
        return queueMemberRepository.findByUid(uid);
    }

    @Cacheable(value = "queue_member", key = "#queueTopic#queueDay#threadUid#status")
    public Optional<QueueMemberEntity> findByQueueTopicAndQueueDayAndThreadUidAndStatus(String queueTopic, String queueDay, String threadUid, String status) {
        return queueMemberRepository.findByQueueTopicAndQueueDayAndThreadUidAndStatus(queueTopic, queueDay, threadUid, status);
    }

    @Override
    public QueueMemberResponse create(QueueMemberRequest request) {
        QueueMemberEntity counter = modelMapper.map(request, QueueMemberEntity.class);
        counter.setUid(uidUtils.getUid());
        //
        QueueMemberEntity savedQueueMember = save(counter);
        if (savedQueueMember == null) {
            throw new RuntimeException("save counter failed");
        }
        return convertToResponse(savedQueueMember);
    }

    @Override
    public QueueMemberResponse update(QueueMemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public QueueMemberEntity save(QueueMemberEntity entity) {
        try {
            return queueMemberRepository.save(entity);
        } catch (Exception e) {
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
    public void delete(QueueMemberRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public void deleteAll() {
        queueMemberRepository.deleteAll();
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            QueueMemberEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public QueueMemberResponse convertToResponse(QueueMemberEntity entity) {
        return modelMapper.map(entity, QueueMemberResponse.class);
    }

}
