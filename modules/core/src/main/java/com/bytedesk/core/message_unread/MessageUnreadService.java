/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 16:16:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.utils.ConvertUtils;

// import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MessageUnreadService {

    private final MessageUnreadRepository messageUnreadRepository;

    private final ModelMapper modelMapper;

    // 拉取的同时从数据库中删除，所以不需要缓存
    @Transactional
    // @Cacheable(value = "message_unread", key = "#userUid", unless = "#result ==
    // null")
    public List<MessageResponse> getMessages(String userUid) {
        List<MessageUnread> messageUnreadList = messageUnreadRepository.findByUserUid(userUid);
        delete(userUid);
        return messageUnreadList.stream().map(ConvertUtils::convertToMessageResponse).toList();
    }

    // @Caching(put = {
    // @CachePut(value = "message_unread", key = "#userUid"),
    // })
    @Transactional
    public void create(MessageEntity message, String userUid) {
        MessageUnread messageUnread = modelMapper.map(message, MessageUnread.class);
        messageUnread.setUserUid(userUid);
        messageUnreadRepository.save(messageUnread);
    }

    // @Caching(evict = {
    // @CacheEvict(value = "message_unread", key = "#userUid"),
    // })
    @Transactional
    public void delete(String userUid) {
        // org.springframework.orm.ObjectOptimisticLockingFailureException: Row was
        // updated or deleted by another transaction (or unsaved-value mapping was
        // incorrect) : [com.bytedesk.core.message_unread.MessageUnread#102]
        try {
            messageUnreadRepository.deleteByUserUid(userUid);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 重试机制
            retryDelete(userUid);
        }
    }

    private void retryDelete(String userUid) {
        // 重试逻辑
        messageUnreadRepository.deleteByUserUid(userUid);
    }

    // @Cacheable(value = "message_unread_count", key = "#userUid", unless =
    // "#result == null")
    public int getUnreadCount(String userUid) {
        return messageUnreadRepository.countByUserUid(userUid);
    }

}
