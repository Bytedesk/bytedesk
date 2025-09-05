/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 16:33:32
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
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageLeaveRestService extends
        BaseRestServiceWithExport<MessageLeaveEntity, MessageLeaveRequest, MessageLeaveResponse, MessageLeaveExcel> {

    private final MessageLeaveRepository messageLeaveRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    private final MessageRestService messageRestService;

    private final QueueMemberRestService queueMemberRestService;

    private final ThreadRestService threadRestService;

    @Override
    protected Specification<MessageLeaveEntity> createSpecification(MessageLeaveRequest request) {
        return MessageLeaveSpecification.search(request, authService);
    }

    @Override
    protected Page<MessageLeaveEntity> executePageQuery(Specification<MessageLeaveEntity> spec, Pageable pageable) {
        return messageLeaveRepository.findAll(spec, pageable);
    }

    public Page<MessageLeaveResponse> queryByVisitor(MessageLeaveRequest request) {
        // 检查uid和orgUid是否为空
        if (!StringUtils.hasText(request.getUid()) || !StringUtils.hasText(request.getOrgUid())) {
            throw new RuntimeException("uid and orgUid are required");
        }
        return queryByOrg(request);
    }

    // 查询当前组织未处理的留言数量
    public long countPendingByOrg(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            UserEntity user = authService.getUser();
            if (user != null && StringUtils.hasText(user.getOrgUid())) {
                orgUid = user.getOrgUid();
            } else {
                throw new RuntimeException("orgUid is required");
            }
        }
        return messageLeaveRepository.countByOrgUidAndStatusAndDeletedFalse(orgUid, MessageLeaveStatusEnum.PENDING.name());
    }

    @Override
    public MessageLeaveResponse queryByUid(MessageLeaveRequest request) {
        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());
        if (messageLeaveOptional.isPresent()) {
            return convertToResponse(messageLeaveOptional.get());
        }
        throw new RuntimeException("MessageLeave not found");
    }

    // Query threads by leave message
    public Page<ThreadResponse> queryThreadsByLeaveMessage(MessageLeaveRequest request) {
        Page<MessageLeaveEntity> messageLeaveEntities = queryByOrgEntity(request);
        
        // Extract thread UIDs from message leave entities
        List<String> threadUids = messageLeaveEntities.getContent().stream()
                .map(MessageLeaveEntity::getThreadUid)
                .filter(threadUid -> threadUid != null && !threadUid.isEmpty())
                .collect(Collectors.toList());
        
        // Query threads by UIDs
        List<ThreadResponse> threadResponses = threadUids.stream()
                .map(threadUid -> threadRestService.findByUid(threadUid))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(threadRestService::convertToResponse)
                .collect(Collectors.toList());
        
        // Group by topic and keep only the latest one by createdAt
        List<ThreadResponse> mergedThreadResponses = threadResponses.stream()
                .collect(Collectors.groupingBy(ThreadResponse::getTopic))
                .values()
                .stream()
                .map(threadList -> threadList.stream()
                        .max((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                        .orElse(null))
                .filter(thread -> thread != null)
                .collect(Collectors.toList());
        
        // Create a Page object
        Pageable pageable = messageLeaveEntities.getPageable();
        return new PageImpl<>(mergedThreadResponses, pageable, messageLeaveEntities.getTotalElements());
    }

    @Cacheable(value = "messageLeave", key = "#uid", unless = "#result == null")
    @Override
    public Optional<MessageLeaveEntity> findByUid(String uid) {
        return messageLeaveRepository.findByUid(uid);
    }

    @Override
    public MessageLeaveResponse create(MessageLeaveRequest request) {
        // log.info("request {}", request);
        MessageLeaveEntity messageLeave = modelMapper.map(request, MessageLeaveEntity.class);
        messageLeave.setUid(uidUtils.getUid());
        messageLeave.setStatus(MessageLeaveStatusEnum.PENDING.name());
        
        // 确保设置正确的orgUid
        if (!StringUtils.hasText(messageLeave.getOrgUid())) {
            UserEntity user = authService.getUser();
            if (user != null && StringUtils.hasText(user.getOrgUid())) {
                messageLeave.setOrgUid(user.getOrgUid());
            } else if (StringUtils.hasText(request.getOrgUid())) {
                messageLeave.setOrgUid(request.getOrgUid());
            }
        }
        
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
                    .uid(savedMessageLeave.getUid())
                    .nickname(request.getNickname())
                    .type(request.getType())
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
            queueMember.setMessageLeave(true);
            queueMember.setMessageLeaveAt(savedMessageLeave.getCreatedAt());
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
            // 
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
            messageLeave.setReplyContent(request.getReplyContent());
            messageLeave.setReplyImages(request.getReplyImages());
            messageLeave.setRepliedAt(BdDateUtils.now());
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
                messageLeaveExtra.setReplyContent(request.getReplyContent());
                messageLeaveExtra.setReplyImages(request.getReplyImages());
                messageLeaveExtra.setStatus(messageLeave.getStatus());
                message.setExtra(messageLeaveExtra.toJson());
                messageRestService.save(message);
            }
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 更新留言状态
    public MessageLeaveResponse updateStatus(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(request.getStatus());
            
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave status not updated");
            }
            
            // 更新留言提示消息状态
            updateMessageStatus(messageLeave, MessageStatusEnum.valueOf(request.getStatus()));
            
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 标记为已读
    public MessageLeaveResponse markAsRead(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(MessageLeaveStatusEnum.READ.name());
            messageLeave.setReadAt(BdDateUtils.now());
            messageLeave.setReadUser(user.toProtobuf().toJson());
            
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not marked as read");
            }
            
            // 更新留言提示消息状态
            updateMessageStatus(messageLeave, MessageStatusEnum.LEAVE_MSG_READ);
            
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 转接留言
    public MessageLeaveResponse transfer(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }
        
        if (request.getTargetAgentUid() == null || request.getTargetAgentUid().isEmpty()) {
            throw new RuntimeException("Target agent UID is required");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(MessageLeaveStatusEnum.TRANSFERRED.name());
            messageLeave.setTransferredAt(BdDateUtils.now());
            messageLeave.setTransferUser(user.toProtobuf().toJson());
            messageLeave.setTargetAgentUid(request.getTargetAgentUid());
            
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not transferred");
            }
            
            // 更新留言提示消息状态
            updateMessageStatus(messageLeave, MessageStatusEnum.LEAVE_MSG_TRANSFERRED);
            
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 关闭留言
    public MessageLeaveResponse close(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(MessageLeaveStatusEnum.CLOSED.name());
            messageLeave.setClosedAt(BdDateUtils.now());
            messageLeave.setCloseUser(user.toProtobuf().toJson());
            
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not closed");
            }
            
            // 更新留言提示消息状态
            updateMessageStatus(messageLeave, MessageStatusEnum.LEAVE_MSG_CLOSED);
            
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 标记为垃圾留言
    public MessageLeaveResponse markAsSpam(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(MessageLeaveStatusEnum.SPAM.name());
            messageLeave.setSpamAt(BdDateUtils.now());
            messageLeave.setSpamUser(user.toProtobuf().toJson());
            
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not marked as spam");
            }
            
            // 更新留言提示消息状态
            updateMessageStatus(messageLeave, MessageStatusEnum.LEAVE_MSG_SPAM);
            
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 确认留言
    public MessageLeaveResponse confirm(MessageLeaveRequest request) {
        // 检查uid和orgUid是否为空
        if (!StringUtils.hasText(request.getUid()) || !StringUtils.hasText(request.getOrgUid())) {
            throw new RuntimeException("uid and orgUid are required");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(MessageLeaveStatusEnum.CONFIRMED.name());
            messageLeave.setConfirmedAt(BdDateUtils.now());
            // 设置确认用户信息（访客确认，使用访客信息）
            if (StringUtils.hasText(request.getUser())) {
                messageLeave.setConfirmUser(request.getUser());
            }
            
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not confirmed");
            }
            
            // 更新留言提示消息状态
            updateMessageStatus(messageLeave, MessageStatusEnum.LEAVE_MSG_CONFIRMED);
            
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 拒绝留言
    public MessageLeaveResponse reject(MessageLeaveRequest request) {
        // 检查uid和orgUid是否为空
        if (!StringUtils.hasText(request.getUid()) || !StringUtils.hasText(request.getOrgUid())) {
            throw new RuntimeException("uid and orgUid are required");
        }

        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());    
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(MessageLeaveStatusEnum.REJECTED.name());
            messageLeave.setRejectedAt(BdDateUtils.now());
            // 设置拒绝用户信息（访客拒绝，使用访客信息）
            if (StringUtils.hasText(request.getUser())) {
                messageLeave.setRejectUser(request.getUser());
            }
            
            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not rejected");
            }
            
            // 更新留言提示消息状态
            updateMessageStatus(messageLeave, MessageStatusEnum.LEAVE_MSG_REJECTED);
            
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }
    
    // 更新消息状态的辅助方法
    private void updateMessageStatus(MessageLeaveEntity messageLeave, MessageStatusEnum status) {
        Optional<MessageEntity> messageOptional = messageRestService.findByUid(messageLeave.getMessageUid());
        if (messageOptional.isPresent()) {
            MessageEntity message = messageOptional.get();
            message.setStatus(status.name());
            
            // 更新消息扩展信息
            MessageLeaveExtra messageLeaveExtra = MessageLeaveExtra.fromJson(message.getExtra());
            if (messageLeaveExtra != null) {
                messageLeaveExtra.setStatus(messageLeave.getStatus());
                message.setExtra(messageLeaveExtra.toJson());
            }
            
            messageRestService.save(message);
        }
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
        excel.setType(MessageLeaveTypeEnum.fromCode(entity.getType()) != null ? 
                     MessageLeaveTypeEnum.fromCode(entity.getType()).getName() : "未知");
        excel.setStatus(MessageLeaveStatusEnum.fromString(entity.getStatus()).getDescription());
        // 
        UserProtobuf user = UserProtobuf.fromJson(entity.getUser());
        if (user != null) {
            excel.setUser(user.getNickname());
        } else {
            excel.setUser("未知");
        }
        // 回复人
        UserProtobuf replyUser = UserProtobuf.fromJson(entity.getReplyUser());
        if (replyUser != null) {
            excel.setReplyUser(replyUser.getNickname());
        } else {
            excel.setReplyUser("未知");
        }
        excel.setCreatedAt(entity.getCreatedAtString());
        excel.setRepliedAt(entity.getRepliedAtString());
        return excel;
    }

}
