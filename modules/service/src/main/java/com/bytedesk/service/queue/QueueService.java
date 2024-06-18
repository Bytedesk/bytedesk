/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-17 14:53:31
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.base.BaseService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QueueService extends BaseService<Queue, QueueRequest, QueueResponse> {
    
    private final QueueRepository queueRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public void enqueue(QueueRequest request) {
        // QueueMessage message = new QueueMessage();
        // message.setContent(content);
        // // 设置其他属性
        // queueMessageRepository.save(message);
    }

    @Transactional
    public Optional<Queue> dequeue() {
        // 假设我们按ID升序处理消息，因此这里使用findFirst()
        // return queueMessageRepository.findFirst();

        return null;
    }
    
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

    @Override
    public Optional<Queue> findByUid(String uid) {
        return queueRepository.findByUid(uid);
    }

    @Override
    public QueueResponse create(QueueRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public QueueResponse update(QueueRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
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
