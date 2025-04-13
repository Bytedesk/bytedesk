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
package com.bytedesk.service.message_correction;

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
public class MessageCorrectionRestService extends BaseRestService<MessageCorrectionEntity, MessageCorrectionRequest, MessageCorrectionResponse> {

    private final MessageCorrectionRepository message_correctionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<MessageCorrectionResponse> queryByOrg(MessageCorrectionRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageCorrectionEntity> spec = MessageCorrectionSpecification.search(request);
        Page<MessageCorrectionEntity> page = message_correctionRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MessageCorrectionResponse> queryByUser(MessageCorrectionRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "message_correction", key = "#uid", unless="#result==null")
    @Override
    public Optional<MessageCorrectionEntity> findByUid(String uid) {
        return message_correctionRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return message_correctionRepository.existsByUid(uid);
    }

    @Override
    public MessageCorrectionResponse create(MessageCorrectionRequest request) {
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
        MessageCorrectionEntity entity = modelMapper.map(request, MessageCorrectionEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MessageCorrectionEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create message_correction failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public MessageCorrectionResponse update(MessageCorrectionRequest request) {
        Optional<MessageCorrectionEntity> optional = message_correctionRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MessageCorrectionEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MessageCorrectionEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update message_correction failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("MessageCorrection not found");
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
    public MessageCorrectionEntity save(MessageCorrectionEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected MessageCorrectionEntity doSave(MessageCorrectionEntity entity) {
        log.info("Attempting to save message_correction: {}", entity.getName());
        return message_correctionRepository.save(entity);
    }

    @Override
    public MessageCorrectionEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MessageCorrectionEntity entity) {
        try {
            Optional<MessageCorrectionEntity> latest = message_correctionRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MessageCorrectionEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return message_correctionRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Failed to handle optimistic locking exception: {}", ex.getMessage());
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 重试失败后的回调方法
     */
    @Recover
    public MessageCorrectionEntity recover(Exception e, MessageCorrectionEntity entity) {
        log.error("Failed to save message_correction after 3 attempts: {}", entity.getName(), e);
        // 可以在这里添加告警通知
        throw new RuntimeException("Failed to save message_correction after retries: " + e.getMessage());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<MessageCorrectionEntity> optional = message_correctionRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // message_correctionRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("MessageCorrection not found");
        }
    }

    @Override
    public void delete(MessageCorrectionRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MessageCorrectionResponse convertToResponse(MessageCorrectionEntity entity) {
        return modelMapper.map(entity, MessageCorrectionResponse.class);
    }

    @Override
    public MessageCorrectionResponse queryByUid(MessageCorrectionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
