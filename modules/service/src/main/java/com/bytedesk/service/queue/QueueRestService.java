/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 12:10:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QueueRestService extends BaseRestService<QueueEntity, QueueRequest, QueueResponse> {

    private final QueueRepository queueRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<QueueResponse> queryByOrg(QueueRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QueueEntity> specification = QueueSpecification.search(request);
        Page<QueueEntity> page = queueRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QueueResponse> queryByUser(QueueRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        // set user uid
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "queue", key = "#uid", unless = "#result==null")
    @Override
    public Optional<QueueEntity> findByUid(String uid) {
        return queueRepository.findByUid(uid);
    }

    @Cacheable(value = "queue", key = "#topic+#day", unless = "#result==null")
    public Optional<QueueEntity> findByTopicAndDay(String topic, String day) {
        return queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(topic, day);
    }

    public QueueEntity createQueue(QueueRequest request) {
        QueueEntity queue = modelMapper.map(request, QueueEntity.class);
        queue.setUid(uidUtils.getUid());
        try {
            return queueRepository.save(queue);
        } catch (DataIntegrityViolationException e) {
            // 如果在保存时发生唯一约束冲突，说明其他线程已创建
            // 重新查询获取已存在的记录
            return queueRepository.findByTopicAndDayAndDeletedFalse(request.getTopic(), request.getDay())
                    .orElseThrow(() -> new RuntimeException("Queue creation failed"));
        }
    }

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
    public QueueResponse update(QueueRequest request) {
        Optional<QueueEntity> queueOptional = findByUid(request.getUid());
        if (queueOptional.isPresent()) {
            QueueEntity entity = queueOptional.get();
            // modelMapper.map(request, entity);
            entity.setUid(request.getUid());
            //
            return convertToResponse(save(entity));
        }
        //
        return create(request);
    }

    @Override
    public QueueEntity save(QueueEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    protected QueueEntity doSave(QueueEntity entity) {
        return queueRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<QueueEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            // delete(optional.get());
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(QueueRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public QueueEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QueueEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<QueueEntity> latest = queueRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                QueueEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return queueRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public QueueResponse convertToResponse(QueueEntity entity) {
        // return modelMapper.map(entity, QueueResponse.class);
        return ServiceConvertUtils.convertToQueueResponse(entity);
    }

    public Optional<QueueEntity> findLatestByQueueTopicAndDay(String queueTopic, String day) {
        return queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(queueTopic, day);
    }

    public QueueExcel convertToExcel(QueueResponse response) {
        return modelMapper.map(response, QueueExcel.class);
    }

}
