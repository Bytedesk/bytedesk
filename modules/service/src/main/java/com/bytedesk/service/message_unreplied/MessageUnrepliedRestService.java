/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:03:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unreplied;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageUnrepliedRestService extends BaseRestService<MessageUnrepliedEntity, MessageUnrepliedRequest, MessageUnrepliedResponse> {

    private final MessageUnrepliedRepository message_unrepliedRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Cacheable(value = "message_unreplied", key = "#uid", unless="#result==null")
    @Override
    public Optional<MessageUnrepliedEntity> findByUid(String uid) {
        return message_unrepliedRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return message_unrepliedRepository.existsByUid(uid);
    }

    @Override
    public MessageUnrepliedResponse create(MessageUnrepliedRequest request) {
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
        MessageUnrepliedEntity entity = modelMapper.map(request, MessageUnrepliedEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MessageUnrepliedEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create message_unreplied failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public MessageUnrepliedResponse update(MessageUnrepliedRequest request) {
        Optional<MessageUnrepliedEntity> optional = message_unrepliedRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MessageUnrepliedEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MessageUnrepliedEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update message_unreplied failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("MessageUnreplied not found");
        }
    }

    @Override
    protected MessageUnrepliedEntity doSave(MessageUnrepliedEntity entity) {
        // log.info("Attempting to save message_unreplied: {}", entity.getName());
        return message_unrepliedRepository.save(entity);
    }

    @Override
    public MessageUnrepliedEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MessageUnrepliedEntity entity) {
        try {
            Optional<MessageUnrepliedEntity> latest = message_unrepliedRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MessageUnrepliedEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return message_unrepliedRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Failed to handle optimistic locking exception: {}", ex.getMessage());
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<MessageUnrepliedEntity> optional = message_unrepliedRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // message_unrepliedRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("MessageUnreplied not found");
        }
    }

    @Override
    public void delete(MessageUnrepliedRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MessageUnrepliedResponse convertToResponse(MessageUnrepliedEntity entity) {
        return modelMapper.map(entity, MessageUnrepliedResponse.class);
    }

    @Override
    public MessageUnrepliedResponse queryByUid(MessageUnrepliedRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    protected Specification<MessageUnrepliedEntity> createSpecification(MessageUnrepliedRequest request) {
        return MessageUnrepliedSpecification.search(request, authService);
    }

    @Override
    protected Page<MessageUnrepliedEntity> executePageQuery(Specification<MessageUnrepliedEntity> spec, Pageable pageable) {
        return message_unrepliedRepository.findAll(spec, pageable);
    }
    
}
