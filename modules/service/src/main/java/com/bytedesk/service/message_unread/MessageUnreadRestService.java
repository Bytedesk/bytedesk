/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 12:39:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageUnreadRestService
        extends BaseRestService<MessageUnreadEntity, MessageUnreadRequest, MessageUnreadResponse> {

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
            log.info("visitor request, uid: {}", request.getUid());
            request.setUid(request.getUid());
            // request.setUserUid(request.getUid());
        } else {
            request.setUid(user.getUid());
        }
        //
        return queryByOrg(request);
    }

    @Override
    public Optional<MessageUnreadEntity> findByUid(String uid) {
        return messageUnreadRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        if (StringUtils.hasText(uid)) {
            return false;
        }
        return messageUnreadRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public MessageUnreadResponse create(MessageUnreadRequest request) {
        // 检查uid是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        MessageUnreadEntity messageUnread = modelMapper.map(request, MessageUnreadEntity.class);
        MessageUnreadEntity savedMessageUnread = save(messageUnread);
        if (savedMessageUnread == null) {
            throw new RuntimeException("Failed to save MessageUnreadEntity");
        }
        // 

        return convertToResponse(savedMessageUnread);
    }

    @Transactional
    @Override
    public MessageUnreadResponse update(MessageUnreadRequest request) {
        Optional<MessageUnreadEntity> messageUnreadEntityOptional = findByUid(request.getUid());
        if (messageUnreadEntityOptional.isPresent()) {
            MessageUnreadEntity messageUnread = messageUnreadEntityOptional.get();
            // messageUnread.setStatus(request.getStatus());
            // messageUnread.setExtra(request.getExtra());

            MessageUnreadEntity savedMessageUnread = save(messageUnread);
            if (savedMessageUnread == null) {
                throw new RuntimeException("Failed to save MessageUnreadEntity");
            }
            return convertToResponse(savedMessageUnread);
        } 
        return null;
    }

    @Transactional
    public void create(MessageProtobuf messageProtobuf) {
        //
        MessageTypeEnum type = messageProtobuf.getType();
        String threadUid = messageProtobuf.getThread().getUid();
        String threadTopic = messageProtobuf.getThread().getTopic();

        // 目前只记录文本、图片和文件类型的未读消息
        if (!MessageTypeEnum.TEXT.equals(type) &&
            !MessageTypeEnum.IMAGE.equals(type) &&
            !MessageTypeEnum.FILE.equals(type)) {
            return;
        }
        //
        String uid = messageProtobuf.getUid();
        if (existsByUid(uid)) {
            // 流式消息单独处理下
            if (MessageTypeEnum.STREAM.equals(type)) {
                return;
            }
            log.info("message already exists, uid: {}， type: {}, content: {}", uid, type, messageProtobuf.getContent());
            //
            return;
        }
        //
        MessageUnreadEntity messageUnread = modelMapper.map(messageProtobuf, MessageUnreadEntity.class);
        if (MessageStatusEnum.SENDING.equals(messageProtobuf.getStatus())) {
            messageUnread.setStatus(MessageStatusEnum.SUCCESS.name());
        }
        messageUnread.setThreadUid(threadUid);
        messageUnread.setThreadTopic(threadTopic);
        messageUnread.setUser(messageProtobuf.getUser().toJson());
        messageUnread.setUserUid(messageProtobuf.getUser().getUid());
        //
        MessageExtra extraObject = MessageExtra.fromJson(messageProtobuf.getExtra());
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            messageUnread.setOrgUid(orgUid);
        }
        MessageUnreadEntity savedMessageUnread = save(messageUnread);
        if (savedMessageUnread == null) {
            throw new RuntimeException("Failed to save MessageUnreadEntity");
        }
        log.info("create message unread: uid {}, content {}", savedMessageUnread.getContent());
    }

    public long getUnreadCount(MessageUnreadRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            return 0;
        }
        // 
        Page<MessageUnreadResponse> page = queryByOrg(request);
        return page.getTotalElements();
    }

    // 这里的uid是：系统自动生成访客uid
    @Transactional
    public void clearUnreadMessages(MessageUnreadRequest request) {
        try {
            String uid = request.getUid();
            log.info("Clearing unread messages for uid: {}", uid);
            
            // 删除符合条件的未读消息
            messageUnreadRepository.deleteByThreadTopicContainsAndUserNotContains(uid, uid);
            
            log.info("Successfully cleared unread messages for uid: {}", uid);
        } catch (Exception e) {
            log.error("Failed to clear unread messages for uid {}: {}", request.getUid(), e.getMessage(), e);
            throw new RuntimeException("Failed to clear unread messages", e);
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
        messageUnreadRepository.deleteByUid(uid);
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
