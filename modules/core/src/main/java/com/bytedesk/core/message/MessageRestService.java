/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-18 12:43:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class MessageRestService extends BaseRestService<MessageEntity, MessageRequest, MessageResponse> {

    private final MessageRepository messageRepository;

    private final AuthService authService;

    public Page<MessageResponse> queryByOrg(MessageRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageEntity> specs = MessageSpecification.search(request);
        Page<MessageEntity> messagePage = messageRepository.findAll(specs, pageable);
        return messagePage.map(ConvertUtils::convertToMessageResponse);
    }

    @Override
    public Page<MessageResponse> queryByUser(MessageRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    // @Cacheable(value = "message", key = "#request.topic", unless = "#result == null")
    // public Page<MessageResponse> queryByTopic(MessageRequest request) {
    //     Pageable pageable = request.getPageable();
    //     Specification<MessageEntity> specs = MessageSpecification.search(request);
    //     Page<MessageEntity> messagePage = messageRepository.findAll(specs, pageable);
    //     return messagePage.map(ConvertUtils::convertToMessageResponse);
    // }

    @Cacheable(value = "message", key = "#uid", unless = "#result == null")
    public Optional<MessageEntity> findByUid(String uid) {
        return messageRepository.findByUid(uid);
    }

    // rate message extra helpful
    public MessageResponse rateUp(String uid) {
        Optional<MessageEntity> messageOptional = messageRepository.findByUid(uid);
        if (messageOptional.isPresent()) {
            MessageEntity message = messageOptional.get();
            // message.setHelpful(true);
            MessageExtra messageExtra = JSON.parseObject(message.getExtra(), MessageExtra.class);
            messageExtra.setHelpful(MessageHelpfulEnum.HELPFUL.name());
            message.setExtra(JSON.toJSONString(messageExtra));
            // 
            MessageEntity savedMessage = save(message);
            if (savedMessage == null) {
                throw new RuntimeException("Message not saved");
            }
            // 
            return ConvertUtils.convertToMessageResponse(message);
        }
        return null;
    }

    // rate message extra unhelpful
    public MessageResponse rateDown(String uid) {
        Optional<MessageEntity> optionalMessage = findByUid(uid);
        if (optionalMessage.isPresent()) {
            MessageEntity message = optionalMessage.get();
            // message.setHelpful(false);
            MessageExtra messageExtra = JSON.parseObject(message.getExtra(), MessageExtra.class);
            messageExtra.setHelpful(MessageHelpfulEnum.UNHELPFUL.name());
            message.setExtra(JSON.toJSONString(messageExtra));
            // 
            MessageEntity savedMessage = save(message);
            if (savedMessage == null) {
                throw new RuntimeException("Message not saved");
            }
            //
            return ConvertUtils.convertToMessageResponse(message);
        }
        return null;
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

    @Caching(put = {
            @CachePut(value = "message", key = "#message.uid"),
    })
    public MessageEntity save(@NonNull MessageEntity message) {
        try {
            return messageRepository.save(message);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, message);
        }
        return null;
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

    public boolean existsByUid(String uid) {
        return messageRepository.existsByUid(uid);
    }

    @Override
    public MessageResponse convertToResponse(MessageEntity entity) {
        return ConvertUtils.convertToMessageResponse(entity);
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageEntity message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public MessageResponse queryByUid(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

}
