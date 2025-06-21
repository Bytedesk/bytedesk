/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-21 18:02:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageUnreadRestService extends BaseRestService<MessageUnreadEntity, MessageUnreadRequest, MessageUnreadResponse> {

    private final MessageUnreadRepository messageUnreadRepository;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    @Override
    public Page<MessageUnreadResponse> queryByOrg(MessageUnreadRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageUnreadEntity> specs = MessageUnreadSpecification.search(request);
        Page<MessageUnreadEntity> page = messageUnreadRepository.findAll(specs, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MessageUnreadResponse> queryByUser(MessageUnreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Override
    public Optional<MessageUnreadEntity> findByUid(String uid) {
        return messageUnreadRepository.findByUid(uid);
    }

    @Override
    public MessageUnreadResponse create(MessageUnreadRequest request) {
        MessageUnreadEntity messageUnread = modelMapper.map(request, MessageUnreadEntity.class);
        MessageUnreadEntity savedMessageUnread = save(messageUnread);
        return convertToResponse(savedMessageUnread);
    }

    @Override
    public MessageUnreadResponse update(MessageUnreadRequest request) {
        MessageUnreadEntity messageUnread = modelMapper.map(request, MessageUnreadEntity.class);
        MessageUnreadEntity savedMessageUnread = save(messageUnread);
        return convertToResponse(savedMessageUnread);
    }

    // 拉取的同时从数据库中删除，所以不需要缓存
    @Transactional
    @Cacheable(value = "message_unread", key = "#userUid", unless = "#result == null")
    public List<MessageResponse> getMessages(String userUid) {
        List<MessageUnreadEntity> messageUnreadList = messageUnreadRepository.findByUserUid(userUid);
        delete(userUid);
        return messageUnreadList.stream().map(this::convertToMessageResponse).toList();
    }

    // @Caching(put = {@CachePut(value = "message_unread", key = "#userUid"),})
    @Transactional
    public void create(MessageEntity message) {
        // 检查是否已经存在相同的未读消息记录
        Optional<MessageUnreadEntity> existing = messageUnreadRepository.findByUid(message.getUid());
        if (existing.isPresent()) {
            log.debug("Message unread already exists for message: {}", message.getUid());
            return;
        }
        
        MessageUnreadEntity messageUnread = modelMapper.map(message, MessageUnreadEntity.class);
        // messageUnread.setUserUid(userUid);
        MessageUnreadEntity savedMessageUnread = save(messageUnread);
        log.info("create message unread: {}", savedMessageUnread);
    }

    @Transactional
    public void delete(String userUid) {
        try {
            messageUnreadRepository.deleteByUserUid(userUid);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 重试机制
            retryDelete(userUid);
        }
    }

    private void retryDelete(String userUid) {
        messageUnreadRepository.deleteByUserUid(userUid);
    }

    // @Cacheable(value = "message_unread_count", key = "#userUid", unless = "#result == null")
    public int getUnreadCount(String userUid) {
        return messageUnreadRepository.countByUserUid(userUid);
    }

    public void clearUnreadCount(String userUid) {
        messageUnreadRepository.deleteByUserUid(userUid);
    }

    @Override
    protected MessageUnreadEntity doSave(MessageUnreadEntity entity) {
        return messageUnreadRepository.save(entity);
    }

    @Override
    public MessageUnreadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageUnreadEntity entity) {
        try {
            log.warn("Optimistic locking failure for MessageUnreadEntity: {}, retrying...", entity.getUid());
            
            // 检查是否已经存在相同的记录
            Optional<MessageUnreadEntity> existing = messageUnreadRepository.findByUid(entity.getUid());
            if (existing.isPresent()) {
                log.info("MessageUnreadEntity already exists, skipping creation: {}", entity.getUid());
                return existing.get();
            }
            
            // 如果不存在，重新尝试保存
            log.info("Retrying to save MessageUnreadEntity: {}", entity.getUid());
            return messageUnreadRepository.save(entity);
            
        } catch (Exception ex) {
            log.error("Failed to handle optimistic locking exception for MessageUnreadEntity {}: {}", 
                     entity.getUid(), ex.getMessage(), ex);
            // 对于未读消息，如果处理失败，我们选择忽略而不是抛出异常
            // 因为未读消息的丢失不会影响核心业务逻辑
            return null;
        }
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public MessageUnreadResponse convertToResponse(MessageUnreadEntity entity) {
        return modelMapper.map(entity, MessageUnreadResponse.class);
    }

    public MessageResponse convertToMessageResponse(MessageUnreadEntity message) {
        MessageResponse messageResponse = modelMapper.map(message, MessageResponse.class);

        UserProtobuf user = JSON.parseObject(message.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }


}
