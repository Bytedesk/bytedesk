/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 09:51:18
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
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageUnreadService extends BaseRestService<MessageUnreadEntity, MessageUnreadRequest, MessageUnreadResponse> {

    private final MessageUnreadRepository messageUnreadRepository;

    private final ModelMapper modelMapper;

    @Override
    public Page<MessageUnreadResponse> queryByOrg(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<MessageUnreadResponse> queryByUser(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<MessageUnreadEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public MessageUnreadResponse create(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public MessageUnreadResponse update(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    // 拉取的同时从数据库中删除，所以不需要缓存
    @Transactional
    // @Cacheable(value = "message_unread", key = "#userUid", unless = "#result == null")
    public List<MessageResponse> getMessages(String userUid) {
        List<MessageUnreadEntity> messageUnreadList = messageUnreadRepository.findByUserUid(userUid);
        delete(userUid);
        return messageUnreadList.stream().map(this::convertToMessageResponse).toList();
    }

    // @Caching(put = {@CachePut(value = "message_unread", key = "#userUid"),})
    @Transactional
    public void create(MessageEntity message, String userUid) {
        MessageUnreadEntity messageUnread = modelMapper.map(message, MessageUnreadEntity.class);
        messageUnread.setUserUid(userUid);
        messageUnreadRepository.save(messageUnread);
    }

    // @Caching(evict = { @CacheEvict(value = "message_unread", key = "#userUid"),})
    @Transactional
    public void delete(String userUid) {
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

    // @Cacheable(value = "message_unread_count", key = "#userUid", unless = "#result == null")
    public int getUnreadCount(String userUid) {
        return messageUnreadRepository.countByUserUid(userUid);
    }

    @Override
    protected MessageUnreadEntity doSave(MessageUnreadEntity entity) {
        return messageUnreadRepository.save(entity);
    }

    @Override
    public MessageUnreadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageUnreadEntity entity) {
                try {
                    Optional<MessageUnreadEntity> latest = messageUnreadRepository.findByUid(entity.getUid());
                    if (latest.isPresent()) {
                        MessageUnreadEntity latestEntity = latest.get();
                        // 合并需要保留的数据
                        // 这里可以根据业务需求合并实体
                        return messageUnreadRepository.save(latestEntity);
                    }
                } catch (Exception ex) {
                    log.error("Failed to handle optimistic locking exception: {}", ex.getMessage());
                    throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
                }
                return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public MessageUnreadResponse convertToResponse(MessageUnreadEntity entity) {
        return modelMapper.map(entity, MessageUnreadResponse.class);
    }

    public MessageResponse convertToMessageResponse(MessageUnreadEntity message) {
        MessageResponse messageResponse = modelMapper.map(message, MessageResponse.class);

        UserProtobuf user = JSON.parseObject(message.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }


}
