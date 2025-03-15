/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:04:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 12:19:54
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

import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.notice.extra.NoticeExtraInvite;
import com.bytedesk.core.notice.extra.NoticeExtraTransfer;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessagePersistService {

    private final MessageRestService messageService;

    private final ModelMapper modelMapper;

    public void persist(String messageJSON) {
        // log.info("persist: {}", messageJSON);
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJSON, MessageProtobuf.class);
        //
        MessageTypeEnum type = messageProtobuf.getType();
        String threadTopic = messageProtobuf.getThread().getTopic();
        // log.info("orgUid: {}", orgUid);

        // 返回true表示该消息是系统通知，不应该保存到数据库
        if (dealWithMessageNotification(type, messageProtobuf)) {
            // log.info("message should not be saved uid {}, type {}", messageProtobuf.getUid(), type);
            return;
        }
        //
        String uid = messageProtobuf.getUid();
        if (messageService.existsByUid(uid)) {
            // 流式消息单独处理下
            if (type.equals(MessageTypeEnum.STREAM)) {
                // 更新消息内容
                Optional<MessageEntity> message = messageService.findByUid(uid);
                if (message.isPresent()) {
                    MessageEntity m = message.get();
                    m.setContent(m.getContent() + messageProtobuf.getContent());
                    messageService.save(m);
                }
                return;
            }
            log.info("message already exists, uid: {}", uid);
            //
            return;
        }
        //
        MessageEntity message = modelMapper.map(messageProtobuf, MessageEntity.class);
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            message.setStatus(MessageStatusEnum.SUCCESS.name());
        }
        message.setTopic(threadTopic);
        message.setUser(JSON.toJSONString(messageProtobuf.getUser()));
        // 
        MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            message.setOrgUid(orgUid);
        }
        //
        messageService.save(message);
    }

    // 处理消息通知，已处理的消息返回true，未处理的消息返回false
    private boolean dealWithMessageNotification(@NonNull MessageTypeEnum type, MessageProtobuf messageProtobuf) {
        // String content = messageProtobuf.getContent();
        // log.info("dealWithMessageNotification: {}, {}", type, content);

        // 正在输入/消息预知 - 不保存
        if (type.equals(MessageTypeEnum.TYPING)
                || type.equals(MessageTypeEnum.PROCESSING)
                || type.equals(MessageTypeEnum.PREVIEW)) {
            return true;
        }

        // 继续会话 - 不保存
        if (type.equals(MessageTypeEnum.CONTINUE)) {
            return true;
        }

        // 消息撤回 - 从数据库中删除
        if (type.equals(MessageTypeEnum.RECALL)) {
            dealWithMessageRecall(messageProtobuf);
            return true;
        }

        // 消息送达回执 - 处理
        if (type.equals(MessageTypeEnum.DELIVERED)) {
            dealWithMessageReceipt(type, messageProtobuf);
            return true;
        }

        // 消息已读回执 - 处理
        if (type.equals(MessageTypeEnum.READ)) {
            dealWithMessageReceipt(type, messageProtobuf);
            return true;
        }

        //
        if (type.equals(MessageTypeEnum.RATE_SUBMIT)
                || type.equals(MessageTypeEnum.RATE_CANCEL)) {
            // 如果是客服邀请评价/主动评价，则content为邀请/主动评价消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithRateMessage(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.LEAVE_MSG_SUBMIT)) {
            // content为留言提示消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithLeaveMsg(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.FAQ_UP)
                || type.equals(MessageTypeEnum.FAQ_DOWN)) {
            // content为被评价的faq消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithFaqRateMessage(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.ROBOT_UP)
                || type.equals(MessageTypeEnum.ROBOT_DOWN)) {
            // content为邀请评价消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithRobotRateMessage(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.TRANSFER_ACCEPT)
                || type.equals(MessageTypeEnum.TRANSFER_REJECT)) {
            // content为转接消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithTransferMessage(type, messageProtobuf);
                return true;
            }
        }

        // INVITE_ACCEPT, INVITE_REJECT, INVITE_CANCEL, INVITE_VISITOR, INVITE_VISITOR_REJECT, INVITE_VISITOR_ACCEPT, INVITE_VISITOR_CANCEL
        if (type.equals(MessageTypeEnum.INVITE_ACCEPT)
                || type.equals(MessageTypeEnum.INVITE_REJECT)) {
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithInviteMessage(type, messageProtobuf);
                return true;
            }
        }

        return false;
    }

    // 处理消息回执
    private void dealWithMessageReceipt(MessageTypeEnum type, @Nonnull MessageProtobuf message) {
        log.info("dealWithMessageReceipt: {}", type);
        // 回执消息内容存储被回执消息的uid
        // 当status已经为read时，不处理。防止delivered在后面更新read消息
        Optional<MessageEntity> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent() && messageOpt.get().getStatus() != MessageStatusEnum.READ.name()) {
            MessageEntity messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.READ)) {
                messageEntity.setStatus(MessageStatusEnum.READ.name());
            } else if (type.equals(MessageTypeEnum.DELIVERED)) {
                messageEntity.setStatus(MessageStatusEnum.DELIVERED.name());
            }
            messageService.save(messageEntity);
        }
    }

    // 消息撤回，从数据库中删除消息
    private void dealWithMessageRecall(MessageProtobuf message) {
        // log.info("dealWithMessageRecall");
        // content为撤回消息的uid
        messageService.deleteByUid(message.getContent());
    }

    private void dealWithRateMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithMessageRateSubmit");
        // 如果是客服邀请评价，则content为邀请评价消息的uid，否则为空
        Optional<MessageEntity> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            MessageEntity messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.RATE_SUBMIT)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_SUBMIT.name());
                messageEntity.setContent(message.getExtra());
            } else if (type.equals(MessageTypeEnum.RATE_CANCEL)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_CANCEL.name());
            }
            messageService.save(messageEntity);
        }
    }

    private void dealWithLeaveMsg(MessageTypeEnum type, MessageProtobuf message) {
        log.info("dealWithLeaveMsg");
        Optional<MessageEntity> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            MessageEntity messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.LEAVE_MSG_SUBMIT)) {
                messageEntity.setStatus(MessageStatusEnum.LEAVE_MSG_SUBMIT.name());
                messageEntity.setContent(message.getExtra());
                messageService.save(messageEntity);
            }
        }
    }

    private void dealWithFaqRateMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithFaqRateMessage");
        Optional<MessageEntity> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            MessageEntity messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.FAQ_UP)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_UP.name());
            } else if (type.equals(MessageTypeEnum.FAQ_DOWN)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_DOWN.name());
            }
            messageService.save(messageEntity);
        }
    }

    private void dealWithRobotRateMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithRobotRateMessage");
        Optional<MessageEntity> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            MessageEntity messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.ROBOT_UP)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_UP.name());
            } else if (type.equals(MessageTypeEnum.ROBOT_DOWN)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_DOWN.name());
            }
            messageService.save(messageEntity);
        }
    }

    // 处理转接消息
    private void dealWithTransferMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithTransferMessage");
        NoticeExtraTransfer transferContentObject = JSONObject.parseObject(message.getContent(),
                NoticeExtraTransfer.class);
        //
        Optional<MessageEntity> messageOpt = messageService.findByUid(transferContentObject.getMessageUid());
        if (messageOpt.isPresent()) {
            MessageEntity messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.TRANSFER_ACCEPT)) {
                messageEntity.setStatus(MessageStatusEnum.TRANSFER_ACCEPTED.name());
            } else if (type.equals(MessageTypeEnum.TRANSFER_REJECT)) {
                messageEntity.setStatus(MessageStatusEnum.TRANSFER_REJECTED.name());
            }
            messageService.save(messageEntity);
        }
    }

    // 处理邀请消息
    private void dealWithInviteMessage(MessageTypeEnum type, MessageProtobuf message) {
        NoticeExtraInvite inviteContentObject = JSONObject.parseObject(message.getContent(),
                NoticeExtraInvite.class);
        Optional<MessageEntity> messageOpt = messageService.findByUid(inviteContentObject.getMessageUid());
        if (messageOpt.isPresent()) {
            MessageEntity messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.INVITE_ACCEPT)) {
                messageEntity.setStatus(MessageStatusEnum.INVITE_ACCEPTED.name());
            } else if (type.equals(MessageTypeEnum.INVITE_REJECT)) {
                messageEntity.setStatus(MessageStatusEnum.INVITE_REJECTED.name());
            } else if (type.equals(MessageTypeEnum.INVITE_CANCEL)) {
                // messageEntity.setStatus(MessageStatusEnum.INVITE_CANCEL.name());
            }
            messageService.save(messageEntity);
        }
    }


}
