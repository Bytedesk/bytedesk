/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-05 12:05:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class MessageService extends BaseRestService<MessageEntity, MessageRequest, MessageResponse> {

    private final MessageRepository messageRepository;

    private final AuthService authService;

    public Page<MessageResponse> queryByOrg(MessageRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

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

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Specification<MessageEntity> specs = MessageSpecification.search(request);

        Page<MessageEntity> messagePage = messageRepository.findAll(specs, pageable);

        return messagePage.map(ConvertUtils::convertToMessageResponse);
    }

    @Cacheable(value = "message", key = "#request.threadTopic", unless = "#result == null")
    public Page<MessageResponse> queryByThreadTopic(MessageRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Specification<MessageEntity> specs = MessageSpecification.search(request);

        Page<MessageEntity> messagePage = messageRepository.findAll(specs, pageable);

        return messagePage.map(ConvertUtils::convertToMessageResponse);
    }

    @Cacheable(value = "message", key = "#uid", unless = "#result == null")
    public Optional<MessageEntity> findByUid(String uid) {
        return messageRepository.findByUid(uid);
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageEntity message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

}
