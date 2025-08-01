/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 18:18:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.MessageTypeConverter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageRestService extends BaseRestServiceWithExcel<MessageEntity, MessageRequest, MessageResponse, MessageExcel> {

    private final MessageRepository messageRepository;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    @Override
    public Page<MessageEntity> queryByOrgEntity(MessageRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageEntity> specs = MessageSpecification.search(request, authService);
        return messageRepository.findAll(specs, pageable);
    }

    public Page<MessageResponse> queryByOrg(MessageRequest request) {
        Page<MessageEntity> messagePage = queryByOrgEntity(request);
        return messagePage.map(this::convertToResponse);
    }

    @Override
    public Page<MessageResponse> queryByUser(MessageRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Override
    public MessageResponse queryByUid(MessageRequest request) {
        Optional<MessageEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new NotFoundException("Message not found");
        }
        return convertToResponse(optional.get());
    }

    @Cacheable(value = "message", key = "#uid", unless = "#result == null")
    public Optional<MessageEntity> findByUid(String uid) {
        return messageRepository.findByUid(uid);
    }

    @Cacheable(value = "message", key = "# type + #messageUid", unless = "#result == null")
    public Optional<MessageEntity> findTransferMessage(String type, String messageUid) {
        return messageRepository.findTransferMessage(type, messageUid);
    }

    @Cacheable(value = "message", key = "#threadUid", unless = "#result == null")
    public Optional<MessageEntity> findLatestByThreadUid(String threadUid) {
        return messageRepository.findFirstByThread_UidOrderByCreatedAtDesc(threadUid);
    }

    @Cacheable(value = "message", key = "#threadUid", unless = "#result == null")
    public List<MessageEntity> findByThreadUid(String threadUid) {
        return messageRepository.findByThread_UidOrderByCreatedAtAsc(threadUid);
    }
    
    @Cacheable(value = "message", key = "#threadUid + #type + #userUid", unless = "#result == null")
    public Optional<MessageEntity> findByThreadUidAndTypeAndUserContains(String threadUid, String type,
            String userUid) {
        return messageRepository.findFirstByThread_UidAndTypeAndUserContainsOrderByCreatedAtDesc(threadUid, type,
                userUid);
    }

    @Cacheable(value = "message", key = "#threadTopic + #limit", unless = "#result == null")
    public List<MessageEntity> getRecentMessages(String threadTopic, int limit) {
        // 只返回前5条记录
        PageRequest pageRequest = PageRequest.of(0, limit);
        return messageRepository.findLatestByThreadTopicOrderByCreatedAtDesc(threadTopic, pageRequest);
        // return messageEntities.stream().map(MessageProtobuf::convertToProtobuf).collect(Collectors.toList());
    }

    @Cacheable(value = "message", key = "#uid", unless = "#result == null")
    public Boolean isMessageExists(String uid) {
        return messageRepository.existsByUid(uid);
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
    @CachePut(value = "message", key = "#message.uid")
    protected MessageEntity doSave(MessageEntity entity) {
        return messageRepository.save(entity);
    }

    @Caching(evict = {
            @CacheEvict(value = "message", key = "#message.uid"),
    })
    public void delete(@NonNull MessageRequest message) {
        deleteByUid(message.getUid());
    }

    @Caching(evict = {
            @CacheEvict(value = "message", key = "#uid"),
    })
    public void deleteByUid(String uid) {
        // messageRepository.deleteByUid(uid);
        Optional<MessageEntity> messageOptional = findByUid(uid);
        messageOptional.ifPresent(message -> {
            message.setDeleted(true);
            save(message);
        });
    }

    public Boolean existsByUid(String uid) {
        return messageRepository.existsByUid(uid);
    }

    @Override
    public MessageResponse convertToResponse(MessageEntity entity) {
        return ConvertUtils.convertToMessageResponse(entity);
    }

    @Override
    public MessageEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageEntity entity) {
        try {
            Optional<MessageEntity> latest = messageRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MessageEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return messageRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Failed to handle optimistic locking exception: {}", ex.getMessage());
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }
    
    @Override
    public MessageExcel convertToExcel(MessageEntity entity) {
        MessageExcel messageExcel = modelMapper.map(entity, MessageExcel.class);
        messageExcel.setType(MessageTypeConverter.convertToChineseType(entity.getType()));
        messageExcel.setContent(entity.getContent());
        messageExcel.setSender(entity.getUserProtobuf().getNickname());
        return messageExcel;
    }

}
