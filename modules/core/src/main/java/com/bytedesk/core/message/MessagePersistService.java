/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:04:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-20 13:25:43
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

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessagePersistService {

    private final MessageRestService messageRestService;

    private final ThreadRestService threadRestService;

    private final ModelMapper modelMapper;

    public void persist(String messageJSON) {
        // log.info("persist: {}", messageJSON);
        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageJSON); 
        //JSON.parseObject(messageJSON, MessageProtobuf.class);
        //
        MessageTypeEnum type = messageProtobuf.getType();
        String threadUid = messageProtobuf.getThread().getUid();
        // String threadTopic = messageProtobuf.getThread().getTopic();
        // log.info("orgUid: {}", orgUid);

        // 返回true表示该消息是系统通知，不应该保存到数据库
        if (dealWithMessageNotification(type, messageProtobuf)) {
            return;
        }
        //
        String uid = messageProtobuf.getUid();
        if (messageRestService.existsByUid(uid)) {
            // 流式消息单独处理下
            if (MessageTypeEnum.STREAM.equals(type)) {
                // 更新消息内容
                Optional<MessageEntity> message = messageRestService.findByUid(uid);
                if (message.isPresent()) {
                    MessageEntity m = message.get();
                    m.setContent(m.getContent() + messageProtobuf.getContent());
                    messageRestService.save(m);
                }
                return;
            }
            log.info("message already exists, uid: {}， type: {}, content: {}", uid, type, messageProtobuf.getContent());
            //
            return;
        }
        //
        MessageEntity message = modelMapper.map(messageProtobuf, MessageEntity.class);
        if (MessageStatusEnum.SENDING.equals(messageProtobuf.getStatus())) {
            message.setStatus(MessageStatusEnum.SUCCESS.name());
        }
        Optional<ThreadEntity> threadOpt = threadRestService.findByUid(threadUid);
        if (threadOpt.isPresent()) {
            ThreadEntity thread = threadOpt.get();
            message.setThread(thread);
        } else {
            log.info("thread not found, uid: {}", threadUid);
            return;
        }
        // message.setThreadUid(threadUid);
        // message.setTopic(threadTopic);
        message.setUser(messageProtobuf.getUser().toJson());
        message.setUserUid(messageProtobuf.getUser().getUid());
        // 
        MessageExtra extraObject = MessageExtra.fromJson(messageProtobuf.getExtra()); 
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            message.setOrgUid(orgUid);
        }
        //
        messageRestService.save(message);
    }

    // 处理消息通知，已处理的消息返回true，未处理的消息返回false
    private Boolean dealWithMessageNotification(@NonNull MessageTypeEnum type, MessageProtobuf messageProtobuf) {
        // String content = messageProtobuf.getContent();
        // log.info("dealWithMessageNotification: {}, {}", type, content);

        // 正在输入/消息预知 - 不保存
        if (MessageTypeEnum.TYPING.equals(type)
                || MessageTypeEnum.PROCESSING.equals(type)
                || MessageTypeEnum.PREVIEW.equals(type)) {
            return true;
        }

        // 继续会话 - 不保存
        if (MessageTypeEnum.CONTINUE.equals(type)) {
            return true;
        }

        // 消息撤回 - 从数据库中删除
        if (MessageTypeEnum.RECALL.equals(type)) {
            dealWithMessageRecall(messageProtobuf);
            return true;
        }

        // 消息送达回执 - 处理
        if (MessageTypeEnum.DELIVERED.equals(type)) {
            dealWithMessageReceipt(type, messageProtobuf);
            return true;
        }

        // 消息已读回执 - 处理
        if (MessageTypeEnum.READ.equals(type)) {
            dealWithMessageReceipt(type, messageProtobuf);
            return true;
        }

        //
        // if (MessageTypeEnum.RATE_SUBMIT.equals(type)
        //         || MessageTypeEnum.RATE_CANCEL.equals(type)) {
        //     // 如果是客服邀请评价/主动评价，则content为邀请/主动评价消息的uid
        //     if (StringUtils.hasText(messageProtobuf.getContent())) {
        //         dealWithRateMessage(type, messageProtobuf);
        //         return true;
        //     }
        // }

        //
        // if (MessageTypeEnum.LEAVE_MSG_SUBMIT.equals(type)) {
        //     // content为留言提示消息的uid
        //     if (StringUtils.hasText(messageProtobuf.getContent())) {
        //         dealWithMessageLeave(type, messageProtobuf);
        //         return true;
        //     }
        // }

        // FAQ

        //
        // if (MessageTypeEnum.FAQ_UP.equals(type)
        //         || MessageTypeEnum.FAQ_DOWN.equals(type)) {
        //     // content为被评价的faq消息的uid
        //     if (StringUtils.hasText(messageProtobuf.getContent())) {
        //         dealWithFaqRateMessage(type, messageProtobuf);
        //         return true;
        //     }
        // }

        // QA
        // if (MessageTypeEnum.QA.equals(type)) {
        //     dealWithQaMessage(messageProtobuf);
        //     return true;
        // }

        //
        // if (MessageTypeEnum.ROBOT_UP.equals(type)
        //         || MessageTypeEnum.ROBOT_DOWN.equals(type)) {
        //     // content为邀请评价消息的uid
        //     if (StringUtils.hasText(messageProtobuf.getContent())) {
        //         dealWithRobotRateMessage(type, messageProtobuf);
        //         return true;
        //     }
        // }

        //
        if (MessageTypeEnum.TRANSFER_ACCEPT.equals(type)
                || MessageTypeEnum.TRANSFER_REJECT.equals(type)) {
            // content为转接消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithTransferMessage(type, messageProtobuf);
                return true;
            }
        }

        if (MessageTypeEnum.INVITE_ACCEPT.equals(type)
                || MessageTypeEnum.INVITE_REJECT.equals(type)) {
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
        Optional<MessageEntity> messageOpt = messageRestService.findByUid(message.getContent());
        if (messageOpt.isPresent() && messageOpt.get().getStatus() != MessageStatusEnum.READ.name()) {
            MessageEntity messageEntity = messageOpt.get();
            if (MessageTypeEnum.READ.equals(type)) {
                messageEntity.setStatus(MessageStatusEnum.READ.name());
            } else if (MessageTypeEnum.DELIVERED.equals(type)) {
                messageEntity.setStatus(MessageStatusEnum.DELIVERED.name());
            }
            messageRestService.save(messageEntity);
        }
    }

    // 消息撤回，从数据库中删除消息
    private void dealWithMessageRecall(MessageProtobuf message) {
        // log.info("dealWithMessageRecall");
        // content为撤回消息的uid
        messageRestService.deleteByUid(message.getContent());
    }

    // private void dealWithRateMessage(MessageTypeEnum type, MessageProtobuf message) {
    //     // log.info("dealWithMessageRateSubmit");
    //     // 如果是客服邀请评价，则content为邀请评价消息的uid，否则为空
    //     Optional<MessageEntity> messageOpt = messageRestService.findByUid(message.getContent());
    //     if (messageOpt.isPresent()) {
    //         MessageEntity messageEntity = messageOpt.get();
    //         if (MessageTypeEnum.RATE_SUBMIT.equals(type)) {
    //             messageEntity.setStatus(MessageStatusEnum.RATE_SUBMIT.name());
    //             messageEntity.setContent(message.getExtra());
    //         } else if (MessageTypeEnum.RATE_CANCEL.equals(type)) {
    //             messageEntity.setStatus(MessageStatusEnum.RATE_CANCEL.name());
    //         }
    //         messageRestService.save(messageEntity);
    //     }
    // }

    // private void dealWithMessageLeave(MessageTypeEnum type, MessageProtobuf message) {
    //     log.info("dealWithMessageLeave");
    //     Optional<MessageEntity> messageOpt = messageRestService.findByUid(message.getContent());
    //     if (messageOpt.isPresent()) {
    //         MessageEntity messageEntity = messageOpt.get();
    //         if (MessageTypeEnum.LEAVE_MSG_SUBMIT.equals(type)) {
    //             messageEntity.setStatus(MessageStatusEnum.LEAVE_MSG_SUBMIT.name());
    //             messageEntity.setContent(message.getExtra());
    //             messageRestService.save(messageEntity);
    //         }
    //     }
    // }

    // private void dealWithFaqRateMessage(MessageTypeEnum type, MessageProtobuf message) {
    //     // log.info("dealWithFaqRateMessage");
    //     Optional<MessageEntity> messageOpt = messageRestService.findByUid(message.getContent());
    //     if (messageOpt.isPresent()) {
    //         MessageEntity messageEntity = messageOpt.get();
    //         if (MessageTypeEnum.FAQ_UP.equals(type)) {
    //             messageEntity.setStatus(MessageStatusEnum.RATE_UP.name());
    //         } else if (MessageTypeEnum.FAQ_DOWN.equals(type)) {
    //             messageEntity.setStatus(MessageStatusEnum.RATE_DOWN.name());
    //         }
    //         messageRestService.save(messageEntity);
    //     }
    // }

    // private void dealWithRobotRateMessage(MessageTypeEnum type, MessageProtobuf message) {
    //     // log.info("dealWithRobotRateMessage");
    //     Optional<MessageEntity> messageOpt = messageRestService.findByUid(message.getContent());
    //     if (messageOpt.isPresent()) {
    //         MessageEntity messageEntity = messageOpt.get();
    //         if (MessageTypeEnum.ROBOT_UP.equals(type)) {
    //             messageEntity.setStatus(MessageStatusEnum.RATE_UP.name());
    //         } else if (MessageTypeEnum.ROBOT_DOWN.equals(type)) {
    //             messageEntity.setStatus(MessageStatusEnum.RATE_DOWN.name());
    //         }
    //         messageRestService.save(messageEntity);
    //     }
    // }

    // 处理转接消息
    private void dealWithTransferMessage(MessageTypeEnum type, MessageProtobuf message) {
    }

    // 处理邀请消息
    private void dealWithInviteMessage(MessageTypeEnum type, MessageProtobuf message) {
    }


}
