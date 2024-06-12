/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-24 15:45:13
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
import org.springframework.stereotype.Service;

import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Page<MessageResponse> queryAll(MessageRequest request) {

        // 优先加载最新聊天记录，也即：id越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Specification<Message> specs = MessageSpecs.search(request);
        Page<Message> messagePage = messageRepository.findAll(specs, pageable);
        // Page<Message> messagePage = messageRepository.findByOrgUidAndDeleted(request.getOrgUid(), false, pageable);

        return messagePage.map(ConvertUtils::convertToMessageResponse);
    }

    public Page<MessageResponse> query(MessageRequest request) {

        // 优先加载最新聊天记录，也即：id越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Page<Message> messagePage = messageRepository.findByThreadsUidIn(request.getThreads(), pageable);

        return messagePage.map(ConvertUtils::convertToMessageResponse);
    }

    @Cacheable(value = "message", key = "#uid", unless = "#result == null")
    public Optional<Message> findByUid(String uid) {
        return messageRepository.findByUid(uid);
    }

    /**
     * find the last message in the thread
     * 找到当前会话中最新一条聊天记录
     */
    @Cacheable(value = "message", key = "#threadTid", unless = "#result == null")
    public Optional<Message> findByThreadsUidInOrderByCreatedAtDesc(String threadTid) {
        return messageRepository.findFirstByThreadsUidInOrderByCreatedAtDesc(new String[] { threadTid });
    }

    @Caching(put = {
            @CachePut(value = "message", key = "#message.uid"),
    })
    public Message save(@NonNull Message message) {
        return messageRepository.save(message);
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

    // public MessageResponse convertToMessageResponse(Message message) {
    // return modelMapper.map(message, MessageResponse.class);
    // }

}
