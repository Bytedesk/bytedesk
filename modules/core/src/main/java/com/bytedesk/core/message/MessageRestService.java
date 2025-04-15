/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 12:28:53
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.MessageTypeConverter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageRestService extends BaseRestServiceWithExcel<NoticeEntity, MessageRequest, MessageResponse, MessageExcel> {

    private final MessageRepository messageRepository;

    private final AuthService authService;

    @Override
    public Page<NoticeEntity> queryByOrgEntity(MessageRequest request) {
        Pageable pageable = request.getPageable();
        Specification<NoticeEntity> specs = MessageSpecification.search(request);
        return messageRepository.findAll(specs, pageable);
    }

    public Page<MessageResponse> queryByOrg(MessageRequest request) {
        Page<NoticeEntity> messagePage = queryByOrgEntity(request);
        return messagePage.map(this::convertToResponse);
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

    @Override
    public MessageResponse queryByUid(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }


    @Cacheable(value = "message", key = "#uid", unless = "#result == null")
    public Optional<NoticeEntity> findByUid(String uid) {
        return messageRepository.findByUid(uid);
    }

    @Cacheable(value = "message", key = "#threadUid", unless = "#result == null")
    public Optional<NoticeEntity> findLatestByThreadUid(String threadUid) {
        return messageRepository.findFirstByThread_UidOrderByCreatedAtDesc(threadUid);
    }

    @Cacheable(value = "message", key = "#threadUid + #type + #userUid", unless = "#result == null")
    public Optional<NoticeEntity> findByThreadUidAndTypeAndUserContains(String threadUid, String type,
            String userUid) {
        return messageRepository.findFirstByThread_UidAndTypeAndUserContainsOrderByCreatedAtDesc(threadUid, type,
                userUid);
    }

    // rate message extra helpful
    public MessageResponse rateUp(String uid) {
        Optional<NoticeEntity> messageOptional = messageRepository.findByUid(uid);
        if (messageOptional.isPresent()) {
            NoticeEntity message = messageOptional.get();
            // message.setHelpful(true);
            MessageExtra messageExtra = JSON.parseObject(message.getExtra(), MessageExtra.class);
            messageExtra.setHelpful(MessageHelpfulEnum.HELPFUL.name());
            message.setExtra(JSON.toJSONString(messageExtra));
            //
            NoticeEntity savedMessage = save(message);
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
        Optional<NoticeEntity> optionalMessage = findByUid(uid);
        if (optionalMessage.isPresent()) {
            NoticeEntity message = optionalMessage.get();
            // message.setHelpful(false);
            MessageExtra messageExtra = JSON.parseObject(message.getExtra(), MessageExtra.class);
            messageExtra.setHelpful(MessageHelpfulEnum.UNHELPFUL.name());
            message.setExtra(JSON.toJSONString(messageExtra));
            //
            NoticeEntity savedMessage = save(message);
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

    @Override
    protected NoticeEntity doSave(NoticeEntity entity) {
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
        Optional<NoticeEntity> messageOptional = findByUid(uid);
        messageOptional.ifPresent(message -> {
            message.setDeleted(true);
            save(message);
        });
    }

    public boolean existsByUid(String uid) {
        return messageRepository.existsByUid(uid);
    }

    @Override
    public MessageResponse convertToResponse(NoticeEntity entity) {
        return ConvertUtils.convertToMessageResponse(entity);
    }

    @Override
    public NoticeEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            NoticeEntity entity) {
        try {
            Optional<NoticeEntity> latest = messageRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                NoticeEntity latestEntity = latest.get();
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
    public MessageExcel convertToExcel(NoticeEntity entity) {
        MessageExcel messageExcel = new MessageExcel();
        messageExcel.setType(MessageTypeConverter.convertToChineseType(entity.getType()));
        messageExcel.setContent(entity.getContent());
        messageExcel.setSender(entity.getUserProtobuf().getNickname());
        messageExcel.setCreatedAt(BdDateUtils.formatDatetimeToString(entity.getCreatedAt()));
        return messageExcel;
    }

}
