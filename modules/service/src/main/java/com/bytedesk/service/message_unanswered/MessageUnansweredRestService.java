/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 09:26:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unanswered;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageUnansweredRestService extends BaseRestService<MessageUnansweredEntity, MessageUnansweredRequest, MessageUnansweredResponse> {

    private final MessageUnansweredRepository message_unansweredRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<MessageUnansweredResponse> queryByOrg(MessageUnansweredRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageUnansweredEntity> spec = MessageUnansweredSpecification.search(request);
        Page<MessageUnansweredEntity> page = message_unansweredRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MessageUnansweredResponse> queryByUser(MessageUnansweredRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "message_unanswered", key = "#uid", unless="#result==null")
    @Override
    public Optional<MessageUnansweredEntity> findByUid(String uid) {
        return message_unansweredRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return message_unansweredRepository.existsByUid(uid);
    }

    @Override
    public MessageUnansweredResponse create(MessageUnansweredRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        MessageUnansweredEntity entity = modelMapper.map(request, MessageUnansweredEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MessageUnansweredEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create message_unanswered failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public MessageUnansweredResponse update(MessageUnansweredRequest request) {
        Optional<MessageUnansweredEntity> optional = message_unansweredRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MessageUnansweredEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MessageUnansweredEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update message_unanswered failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("MessageUnanswered not found");
        }
    }

    /**
     * 保存标签，失败时自动重试
     * maxAttempts: 最大重试次数（包括第一次尝试）
     * backoff: 重试延迟，multiplier是延迟倍数
     * recover: 当重试次数用完后的回调方法
     */
    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public MessageUnansweredEntity save(MessageUnansweredEntity entity) {
        log.info("Attempting to save message_unanswered: {}", entity.getName());
        return message_unansweredRepository.save(entity);
    }

    /**
     * 重试失败后的回调方法
     */
    @Recover
    public MessageUnansweredEntity recover(Exception e, MessageUnansweredEntity entity) {
        log.error("Failed to save message_unanswered after 3 attempts: {}", entity.getName(), e);
        // 可以在这里添加告警通知
        throw new RuntimeException("Failed to save message_unanswered after retries: " + e.getMessage());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<MessageUnansweredEntity> optional = message_unansweredRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // message_unansweredRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("MessageUnanswered not found");
        }
    }

    @Override
    public void delete(MessageUnansweredRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MessageUnansweredEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public MessageUnansweredResponse convertToResponse(MessageUnansweredEntity entity) {
        return modelMapper.map(entity, MessageUnansweredResponse.class);
    }

    @Override
    public MessageUnansweredResponse queryByUid(MessageUnansweredRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
