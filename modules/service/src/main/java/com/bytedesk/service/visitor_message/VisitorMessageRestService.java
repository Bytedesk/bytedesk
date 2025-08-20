/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-20 13:21:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:08:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_message;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.message.MessageRequest;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageSpecification;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VisitorMessageRestService extends BaseRestService<MessageEntity, MessageRequest, MessageResponse> {

    private final MessageRepository messageRepository;

    @Override
    public Page<MessageResponse> queryByUser(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<MessageEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public MessageResponse create(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public MessageResponse update(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    protected MessageEntity doSave(MessageEntity entity) {
        return messageRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public MessageEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageEntity entity) {
        try {
            Optional<MessageEntity> latest = messageRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MessageEntity latestEntity = latest.get();
                // 消息实体通常不应该修改
                // 这里仅保留状态相关字段的更新
                latestEntity.setStatus(entity.getStatus());
                return messageRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public MessageResponse convertToResponse(MessageEntity entity) {
        return ConvertUtils.convertToMessageResponse(entity);
    }

    @Override
    protected Specification<MessageEntity> createSpecification(MessageRequest request) {
        return MessageSpecification.search(request, authService);
    }

    @Override
    protected Page<MessageEntity> executePageQuery(Specification<MessageEntity> spec, Pageable pageable) {
        return messageRepository.findAll(spec, pageable);
    }
    
}
