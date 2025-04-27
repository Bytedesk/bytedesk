/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 14:42:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageLeaveRestService extends
        BaseRestServiceWithExcel<MessageLeaveEntity, MessageLeaveRequest, MessageLeaveResponse, MessageLeaveExcel> {

    private final MessageLeaveRepository messageLeaveRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    private final MessageRestService messageRestService;

    private final QueueMemberRestService queueMemberRestService;

    @Override
    public Page<MessageLeaveEntity> queryByOrgEntity(MessageLeaveRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageLeaveEntity> spec = MessageLeaveSpecification.search(request);
        return messageLeaveRepository.findAll(spec, pageable);
    }

    @Override
    public Page<MessageLeaveResponse> queryByOrg(MessageLeaveRequest request) {
        Page<MessageLeaveEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MessageLeaveResponse> queryByUser(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Override
    public MessageLeaveResponse queryByUid(MessageLeaveRequest request) {
        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            return convertToResponse(messageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }

    @Cacheable(value = "messageLeave", key = "#uid", unless = "#result == null")
    @Override
    public Optional<MessageLeaveEntity> findByUid(String uid) {
        return messageLeaveRepository.findByUid(uid);
    }

    @Override
    public MessageLeaveResponse create(MessageLeaveRequest request) {
        log.info("request {}", request);

        MessageLeaveEntity messageLeave = modelMapper.map(request, MessageLeaveEntity.class);
        messageLeave.setUid(uidUtils.getUid());
        messageLeave.setStatus(MessageLeaveStatusEnum.PENDING.name());
        //
        // 保存留言
        MessageLeaveEntity savedMessageLeave = save(messageLeave);
        if (savedMessageLeave == null) {
            throw new RuntimeException("MessageLeave not saved");
        }

        // 更新留言提示消息状态
        Optional<MessageEntity> messageOptional = messageRestService.findByUid(savedMessageLeave.getMessageUid());
        if (messageOptional.isPresent()) {
            MessageEntity message = messageOptional.get();
            message.setStatus(MessageStatusEnum.LEAVE_MSG_SUBMIT.name());
            //
            MessageLeaveExtra messageLeaveExtra = MessageLeaveExtra.builder()
                    .content(request.getContent())
                    .contact(request.getContact())
                    .images(request.getImages())
                    .status(messageLeave.getStatus())
                    .build();
            message.setExtra(messageLeaveExtra.toJson());
            messageRestService.save(message);
        }

        // 更新队列留言状态
        Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService
                .findByThreadUid(savedMessageLeave.getThreadUid());
        if (queueMemberOptional.isPresent()) {
            QueueMemberEntity queueMember = queueMemberOptional.get();
            queueMember.setLeaveMsg(true);
            queueMember.setLeaveMsgAt(savedMessageLeave.getCreatedAt());
            queueMemberRestService.save(queueMember);
        }

        return convertToResponse(savedMessageLeave);
    }

    @Override
    public MessageLeaveResponse update(MessageLeaveRequest request) {

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(request.getStatus());

            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not updated");
            }
            return convertToResponse(updateMessageLeave);
        }

        throw new RuntimeException("MessageLeave not found");
    }

    // reply
    public MessageLeaveResponse reply(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setReply(request.getReply());
            messageLeave.setReplyImages(request.getReplyImages());
            messageLeave.setRepliedAt(request.getRepliedAt());
            messageLeave.setReplyUser(user.toProtobuf().toJson());
            messageLeave.setStatus(MessageLeaveStatusEnum.REPLIED.name());
            // 
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not updated");
            }
            // 更新留言提示消息状态
            Optional<MessageEntity> messageOptional = messageRestService.findByUid(messageLeave.getMessageUid());
            if (messageOptional.isPresent()) {
                MessageEntity message = messageOptional.get();
                message.setStatus(MessageStatusEnum.LEAVE_MSG_REPLIED.name());
                // 
                MessageLeaveExtra messageLeaveExtra = MessageLeaveExtra.fromJson(message.getExtra());
                messageLeaveExtra.setReply(request.getReply());
                messageLeaveExtra.setReplyImages(request.getReplyImages());
                messageLeaveExtra.setStatus(messageLeave.getStatus());
                message.setExtra(messageLeaveExtra.toJson());
                messageRestService.save(message);
            }
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }

    @Override
    protected MessageLeaveEntity doSave(MessageLeaveEntity entity) {
        return messageLeaveRepository.save(entity);
    }

    @Override
    public MessageLeaveEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageLeaveEntity entity) {
        try {
            Optional<MessageLeaveEntity> latest = messageLeaveRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MessageLeaveEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return messageLeaveRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Failed to handle optimistic locking exception: {}", ex.getMessage());
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(uid);
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setDeleted(true);
            save(messageLeave);
        }
    }

    @Override
    public void delete(MessageLeaveRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public MessageLeaveResponse convertToResponse(MessageLeaveEntity entity) {
        return ServiceConvertUtils.convertToMessageLeaveResponse(entity);
    }

    @Override
    public MessageLeaveExcel convertToExcel(MessageLeaveEntity entity) {
        MessageLeaveExcel excel = modelMapper.map(entity, MessageLeaveExcel.class);
        excel.setImages(Utils.convertListToString(entity.getImages()));
        excel.setStatus(MessageLeaveStatusEnum.fromString(entity.getStatus()).getDescription());
        // 
        UserProtobuf user = UserProtobuf.fromJson(entity.getUser());
        if (user != null) {
            excel.setUser(user.getNickname());
        } else {
            excel.setUser("未知");
        }
        return excel;
    }

}
