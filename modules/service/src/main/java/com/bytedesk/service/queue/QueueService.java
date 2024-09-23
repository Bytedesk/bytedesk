/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-19 20:34:45
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
// import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QueueService extends BaseService<Queue, QueueRequest, QueueResponse> {

    private final QueueRepository queueRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    // @Transactional
    // public void enqueue(QueueRequest request) {
    // // QueueMessage message = new QueueMessage();
    // // message.setContent(content);
    // // // 设置其他属性
    // // queueMessageRepository.save(message);
    // }

    // @Transactional
    // public Optional<Queue> dequeue() {
    // // 假设我们按ID升序处理消息，因此这里使用findFirst()
    // // return queueMessageRepository.findFirst();

    // return null;
    // }

    @Override
    public Page<QueueResponse> queryByOrg(QueueRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<Queue> specification = QueueSpecification.search(request);
        Page<Queue> page = queueRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QueueResponse> queryByUser(QueueRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "queue", key = "#uid", unless = "#result==null")
    @Override
    public Optional<Queue> findByUid(String uid) {
        return queueRepository.findByUid(uid);
    }

    @Cacheable(value = "queue", key = "#queueUid", unless = "#result==null")
    public Optional<Queue> findByQueueUid(String queueUid) {
        return queueRepository.findByQueueUid(queueUid);
    }

    @Override
    public QueueResponse create(QueueRequest request) {
        Queue entity = modelMapper.map(request, Queue.class);
        entity.setUid(uidUtils.getUid());
        //
        Queue savedQueue = save(entity);
        if (savedQueue == null) {
            throw new RuntimeException("Create queue failed");
        }
        return convertToResponse(savedQueue);
    }

    @Override
    public QueueResponse update(QueueRequest request) {

        Optional<Queue> queueOptional = findByQueueUid(request.getQueueUid());
        if (queueOptional.isPresent()) {
            Queue entity = queueOptional.get();
            // modelMapper.map(request, entity);
            entity.setQueueUid(request.getQueueUid());
            entity.setThreadTopics(request.getThreadTopics());
            //
            return convertToResponse(save(entity));
        }
        //
        return create(request);
    }

    @Override
    public Queue save(Queue entity) {
        try {
            return queueRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Queue> optional = findByUid(uid);
        if (optional.isPresent()) {
            delete(optional.get());
        }
    }

    @Override
    public void delete(Queue entity) {
        entity.setDeleted(false);
        save(entity);
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Queue entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public QueueResponse convertToResponse(Queue entity) {
        return modelMapper.map(entity, QueueResponse.class);
    }

}
