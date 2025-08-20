/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 15:48:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

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
import com.bytedesk.core.redis.RedisService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.utils.ConvertUtils;

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

    private final ThreadRestService threadRestService;

    private final RedisService redisService;

    // Redis 缓存过期时间：24小时
    private static final long MESSAGE_CACHE_TTL = 24 * 60 * 60;

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
            request.setUid(request.getUid());
            // request.setUserUid(request.getUid());
        } else {
            request.setUid(user.getUid());
        }
        //
        Page<MessageUnreadResponse> page = queryByOrg(request);
        // 如果需要清空未读消息，则清空
        if (request.getClearUnread() != null && request.getClearUnread()) {
            clearUnreadMessages(request);
        }
        return page;
    }

    @Override
    public Optional<MessageUnreadEntity> findByUid(String uid) {
        return messageUnreadRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        if (!StringUtils.hasText(uid)) {
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
        // String threadTopic = messageProtobuf.getThread().getTopic();

        // 目前只记录文本、图片和文件类型的未读消息
        if (!MessageTypeEnum.TEXT.equals(type) &&
            !MessageTypeEnum.IMAGE.equals(type) &&
            !MessageTypeEnum.FILE.equals(type)) {
            return;
        }
        //
        String uid = messageProtobuf.getUid();
        
        // 使用 Redis 进行去重判断，避免数据库查询和并发问题
        if (redisService.isMessageExists(uid)) {
            log.info("message already exists in redis cache, uid: {}， type: {}, content: {}", uid, type, messageProtobuf.getContent());
            return;
        }
        
        // 双重检查：Redis 不存在，再检查数据库
        if (existsByUid(uid)) {
            log.info("message already exists in database, uid: {}， type: {}, content: {}", uid, type, messageProtobuf.getContent());
            // 同步到 Redis 缓存
            redisService.setMessageExists(uid, MESSAGE_CACHE_TTL);
            return;
        }
        
        // 在 Redis 中设置标记，防止并发创建
        redisService.setMessageExists(uid, MESSAGE_CACHE_TTL);
        
        //
        MessageUnreadEntity messageUnread = modelMapper.map(messageProtobuf, MessageUnreadEntity.class);
        if (MessageStatusEnum.SENDING.equals(messageProtobuf.getStatus())) {
            messageUnread.setStatus(MessageStatusEnum.SUCCESS.name());
        }
        // messageUnread.setThreadUid(threadUid);
        // messageUnread.setThreadTopic(threadTopic);
        Optional<ThreadEntity> threadEntityOptional = threadRestService.findByUid(threadUid);
        if (threadEntityOptional.isPresent()) {
            messageUnread.setThread(threadEntityOptional.get());
        }
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
            // 如果保存失败，删除 Redis 标记
            redisService.removeMessageExists(uid);
            throw new RuntimeException("Failed to save MessageUnreadEntity");
        }
        log.info("create message unread: uid {}, content {}", savedMessageUnread.getUid(), savedMessageUnread.getContent());
        
        // 同步更新会话未读消息数
        try {
            // ThreadRequest threadRequest = ThreadRequest.builder()
            //         .uid(threadUid)
            //         .build();
            // threadRestService.increaseUnreadCount(threadRequest);
            log.info("thread unread count increased for thread: {}", threadUid);
        } catch (Exception e) {
            log.error("Failed to increase thread unread count for thread {}: {}", threadUid, e.getMessage(), e);
            // 不抛出异常，避免影响未读消息的保存
        }
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
            
            redisService.removeMessageExists(uid);
            
            // 删除符合条件的未读消息
            messageUnreadRepository.deleteByThreadTopicContainsAndUserNotContains(uid, uid);
            
            // 同步更新相关会话的未读消息数为0
            // 对于访客uid，通常会话的topic会包含这个uid，我们需要找到所有相关的会话并清零未读数
            try {
                log.info("Attempting to clear unread count for visitor uid: {}", uid);
                // 这里暂时跳过会话未读数的更新，因为需要更多的上下文信息来准确找到相关会话
                // TODO: 如果需要精确更新，可能需要添加更多的查询方法或者通过其他方式获取会话信息
                // 例如：可以通过 ThreadRestService 查询包含该uid的topic的会话，然后更新它们的未读数为0
                
            } catch (Exception e) {
                log.error("Failed to clear thread unread count for uid {}: {}", uid, e.getMessage(), e);
                // 不抛出异常，避免影响整个清理流程
            }
            
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
                    .channel(entity.getChannel())
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
        if (!StringUtils.hasText(uid)) {
            return;
        }
        
        try {
            // 先删除 Redis 缓存
            redisService.removeMessageExists(uid);
            
            // 检查记录是否存在，如果不存在则直接返回
            if (!existsByUid(uid)) {
                log.debug("MessageUnreadEntity with uid {} does not exist, skipping deletion", uid);
                return;
            }
            
            // 使用软删除而不是硬删除，避免乐观锁冲突
            Optional<MessageUnreadEntity> entityOptional = findByUid(uid);
            if (entityOptional.isPresent()) {
                MessageUnreadEntity entity = entityOptional.get();
                entity.setDeleted(true);
                try {
                    save(entity);
                    log.debug("Successfully soft deleted MessageUnreadEntity with uid: {}", uid);
                } catch (ObjectOptimisticLockingFailureException e) {
                    // 乐观锁冲突，说明记录已经被其他线程修改
                    log.warn("Optimistic locking failure when soft deleting MessageUnreadEntity with uid {}: {}", uid, e.getMessage());
                    // 不需要重新抛出异常，因为删除操作已经完成（被其他线程删除）
                } catch (Exception e) {
                    log.error("Failed to soft delete MessageUnreadEntity with uid {}: {}", uid, e.getMessage(), e);
                    // 对于其他异常，可以选择重新抛出或者记录日志
                    throw new RuntimeException("Failed to soft delete MessageUnreadEntity", e);
                }
            }
        } catch (Exception e) {
            log.error("Error in deleteByUid for uid {}: {}", uid, e.getMessage(), e);
            // 不重新抛出异常，避免影响其他操作
        }
    }

    @Override
    public void delete(MessageUnreadRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MessageUnreadResponse convertToResponse(MessageUnreadEntity entity) {
        return ConvertUtils.convertToMessageUnreadResponse(entity);
    }

    @Override
    protected Specification<MessageUnreadEntity> createSpecification(MessageUnreadRequest request) {
        return MessageUnreadSpecification.search(request);
    }

    @Override
    protected Page<MessageUnreadEntity> executePageQuery(Specification<MessageUnreadEntity> spec, Pageable pageable) {
        return messageUnreadRepository.findAll(spec, pageable);
    }

}
