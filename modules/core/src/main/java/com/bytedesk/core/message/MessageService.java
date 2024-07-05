/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 15:42:11
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
// import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class MessageService extends BaseService<Message, MessageRequest, MessageResponse> {

    // private AuthService authService;

    private final MessageRepository messageRepository;

    // private final ExceptionHandlerUtils exceptionHandlerUtils;

    public Page<MessageResponse> queryByOrg(MessageRequest request) {

        // 优先加载最新聊天记录，也即：id越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Specification<Message> specs = MessageSpecification.search(request);

        Page<Message> messagePage = messageRepository.findAll(specs, pageable);

        return messagePage.map(ConvertUtils::convertToMessageResponse);
    }

    @Cacheable(value = "message", key = "#request.threadTopic", unless = "#result == null")
    public Page<MessageResponse> queryByThreadTopic(MessageRequest request) {

        // 优先加载最新聊天记录，也即：id越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Page<Message> messagePage = messageRepository.findByThreadTopic(request.getThreadTopic(), pageable);

        return messagePage.map(ConvertUtils::convertToMessageResponse);
    }

    public Page<MessageResponse> queryUnread(MessageRequest request) {

        // 优先加载最新聊天记录，也即：id越大越新
        // Pageable pageable = PageRequest.of(request.getPageNumber(),
        // request.getPageSize(), Sort.Direction.DESC,"createdAt");
        // Specification<Message> specs = MessageSpecification.unread(request);
        // Page<Message> messagePage = messageRepository.findAll(specs, pageable);
        // return messagePage.map(ConvertUtils::convertToMessageResponse);

        return null;
    }

    @Cacheable(value = "message", key = "#uid", unless = "#result == null")
    public Optional<Message> findByUid(String uid) {
        return messageRepository.findByUid(uid);
    }

    /**
     * find the last message in the thread
     * 找到当前会话中最新一条聊天记录
     */
    // @Cacheable(value = "message", key = "#threadUid", unless = "#result == null")
    // public Optional<Message> findByThreadsUidInOrderByCreatedAtDesc(String
    // threadUid) {
    // return messageRepository.findFirstByThreadsUidInOrderByCreatedAtDesc(new
    // String[] { threadUid });
    // }

    @Caching(put = {
            @CachePut(value = "message", key = "#message.uid"),
    })
    public Message save(@NonNull Message message) {
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
    public void delete(@NonNull Message message) {
        deleteByUid(message.getUid());
    }

    @Caching(evict = {
            @CacheEvict(value = "message", key = "#uid"),
    })
    public void deleteByUid(String uid) {
        // messageRepository.deleteByUid(uid);
        Optional<Message> messageOptional = findByUid(uid);
        messageOptional.ifPresent(message -> {
            message.setDeleted(true);
            save(message);
        });
    }

    public boolean existsByUid(String uid) {
        return messageRepository.existsByUid(uid);
    }

    // public int ping() {

    // User user = authService.getCurrentUser();

    // int count = messageCacheService.getUnreadCount(user.getUid());

    // return count;
    // }

    @Override
    public Page<MessageResponse> queryByUser(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
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
    public MessageResponse convertToResponse(Message entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Message message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

}
