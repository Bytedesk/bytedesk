/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-23 10:39:58
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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.visitor.VisitorRequest;

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
    public List<MessageResponse> getMessages(VisitorRequest request) {
        // List<MessageUnreadEntity> messageUnreadList = messageUnreadRepository.findByUserUid(userUid);
        // delete(userUid);
        // return messageUnreadList.stream().map(ServiceConvertUtils::convertToMessageResponse).toList();
        return null;
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

        // 创建新的 MessageUnreadEntity，避免复制 version 字段
        MessageUnreadEntity messageUnread = MessageUnreadEntity.builder()
                .uid(message.getUid())
                .type(message.getType())
                .status(message.getStatus())
                .content(message.getContent())
                .extra(message.getExtra())
                .client(message.getClient())
                .user(message.getUser())
                .userUid(message.getUserUid())
                .orgUid(message.getOrgUid())
                .level(message.getLevel())
                .platform(message.getPlatform())
                .build();

        MessageUnreadEntity savedMessageUnread = save(messageUnread);
        log.info("create message unread: {}", savedMessageUnread.getContent());
    }

    // @Transactional
    // public void delete(String userUid) {
    //     try {
    //         // messageUnreadRepository.deleteByUserUid(userUid);
    //     } catch (ObjectOptimisticLockingFailureException e) {
    //         log.warn("Optimistic locking failure when deleting message unread for user: {}, retrying...", userUid);
    //         // 重试机制
    //         retryDelete(userUid);
    //     } catch (Exception e) {
    //         log.error("Error deleting message unread for user: {}", userUid, e);
    //         // 对于删除失败，我们选择记录错误而不是抛出异常
    //         // 因为未读消息的删除失败不会影响核心业务逻辑
    //     }
    // }

    // private void retryDelete(String userUid) {
    //     try {
    //         // messageUnreadRepository.deleteByUserUid(userUid);
    //     } catch (Exception e) {
    //         log.error("Retry delete failed for user: {}", userUid, e);
    //     }
    // }

    // @Cacheable(value = "message_unread_count", key = "#userUid", unless = "#result == null")
    public int getUnreadCount(VisitorRequest request) {
        // return messageUnreadRepository.countByUserUid(userUid);

        return 0;
    }

    public void clearUnreadCount(String userUid) {
        try {
            // messageUnreadRepository.deleteByUserUid(userUid);
        } catch (Exception e) {
            log.error("Error clearing unread count for user: {}", userUid, e);
            // 对于清除未读消息失败，我们选择记录错误而不是抛出异常
            // 因为未读消息的清除失败不会影响核心业务逻辑
        }
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
                log.info("MessageUnreadEntity already exists, returning existing entity: {}", entity.getUid());
                return existing.get();
            }

            // 如果不存在，重新尝试保存，但使用新的实体避免版本冲突
            log.info("Retrying to save MessageUnreadEntity: {}", entity.getUid());
            MessageUnreadEntity newEntity = MessageUnreadEntity.builder()
                    .uid(entity.getUid())
                    .type(entity.getType())
                    .status(entity.getStatus())
                    .content(entity.getContent())
                    .extra(entity.getExtra())
                    .client(entity.getClient())
                    .user(entity.getUser())
                    .userUid(entity.getUserUid())
                    .orgUid(entity.getOrgUid())
                    .level(entity.getLevel())
                    .platform(entity.getPlatform())
                    .build();

            return messageUnreadRepository.save(newEntity);

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
        Optional<MessageUnreadEntity> messageUnreadEntityOptional = findByUid(uid);
        if (messageUnreadEntityOptional.isPresent()) {
            messageUnreadRepository.delete(messageUnreadEntityOptional.get());
        }
    }

    @Override
    public void delete(MessageUnreadRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MessageUnreadResponse convertToResponse(MessageUnreadEntity entity) {
        return modelMapper.map(entity, MessageUnreadResponse.class);
    }

    

}
