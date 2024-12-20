/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 07:48:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

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
public class QueueRestService extends BaseRestService <QueueEntity, QueueRequest, QueueResponse> {

    private final QueueRepository queueRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page <QueueResponse> queryByOrg (QueueRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification <QueueEntity> specification = QueueSpecification.search(request);
        Page <QueueEntity> page = queueRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page <QueueResponse> queryByUser (QueueRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        // set user uid

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification <QueueEntity> specification = QueueSpecification.search(request);
        Page <QueueEntity> page = queueRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Cacheable(value = "queue", key = "#uid", unless = "#result==null")
    @Override
    public Optional<QueueEntity> findByUid(String uid) {
        return queueRepository.findByUid(uid);
    }

    @Cacheable(value = "queue", key = "#threadTopic+#day", unless = "#result==null")
    public Optional<QueueEntity> findByThreadTopicAndDay(String threadTopic, String day) {
        return queueRepository.findByThreadTopicAndDayAndDeletedFalse(threadTopic, day);
    }

    // public QueueResponse getOrCreateQueue(String threadTopic, String day, String orgUid) {
    //     Optional<QueueEntity> queueOptional = findByThreadTopicAndDay(threadTopic, day);
    //     if (queueOptional.isPresent()) {
    //         return convertToResponse(queueOptional.get());
    //     } else {
    //         QueueRequest request = QueueRequest.builder()
    //             .threadTopic(threadTopic)
    //             .day(day)
    //             .build();
    //         request.setOrgUid(orgUid);
    //         return create(request);
    //     }
    // }

    @Override
    public QueueResponse create(QueueRequest request) {
        QueueEntity entity = modelMapper.map(request, QueueEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        QueueEntity savedQueue = save(entity);
        if (savedQueue == null) {
            throw new RuntimeException("Create queue failed");
        }
        return convertToResponse(savedQueue);
    }

    @Override
    public QueueResponse update (QueueRequest request) {

        Optional <QueueEntity> queueOptional = findByUid(request.getUid());
        if (queueOptional.isPresent()) {
            QueueEntity entity = queueOptional.get();
            // modelMapper.map(request, entity);
            entity.setUid(request.getUid());
            // entity.setThreadTopics(request.getThreadTopics());
            //
            return convertToResponse(save(entity));
        }
        //
        return create(request);
    }

    @Override
    public QueueEntity save (QueueEntity entity) {
        try {
            return queueRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional <QueueEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            // delete(optional.get());
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete (QueueRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QueueEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public QueueResponse convertToResponse (QueueEntity entity) {
        return modelMapper.map(entity, QueueResponse.class);
    }

}
