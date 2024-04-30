/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-22 21:11:23
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bytedesk.core.utils.BdConvertUtils;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Page<MessageResponse> query(MessageRequest request) {

        // 优先加载最新聊天记录，也即：id越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Message> messagePage = messageRepository.findByThreadsTidIn(request.getThreads(), pageable);

        return messagePage.map(BdConvertUtils::convertToMessageResponse);
    }

    @Cacheable(value = "message", key = "#mid", unless="#result == null")
    public Optional<Message> findByMid(String mid) {
        return messageRepository.findByMid(mid);
    }

    /** 
     *  find the last message in the thread
     *  找到当前会话中最新一条聊天记录 
     */
    @Cacheable(value = "message", key = "#threadTid", unless="#result == null")
    public Optional<Message> findByThreadsTidInOrderByCreatedAtDesc(String threadTid) {
        return messageRepository.findFirstByThreadsTidInOrderByCreatedAtDesc(new String[]{threadTid});
    }

    @Caching(put = {
        @CachePut(value = "message", key = "#message.mid"),
    })
    public Message save(@NonNull Message message) {
        return messageRepository.save(message);
    }

    @Caching(evict = {
        @CacheEvict(value = "message", key = "#message.mid"),
    })
    public void delete(@NonNull Message message) {
        messageRepository.delete(message);
    }

    @Caching(evict = {
        @CacheEvict(value = "message", key = "#mid"),
    })
    public void deleteByMid(String mid) {
        messageRepository.deleteByMid(mid);
    }

    public boolean existsByMid(String mid) {
        return messageRepository.existsByMid(mid);
    }

    // public MessageResponse convertToMessageResponse(Message message) {
    //     return modelMapper.map(message, MessageResponse.class);
    // }

}
