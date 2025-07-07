/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:04:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 12:20:16
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
        // 手动设置 createdAt，确保时间字段正确映射
        if (messageProtobuf.getCreatedAtDateTime() != null) {
            message.setCreatedAt(messageProtobuf.getCreatedAtDateTime());
        }
        // message content: 4, createdAt: 2025-07-04T12:21:50+08:00[Asia/Shanghai], messageProtobuf createdAt: 2025-07-04 12:21:50
        log.info("message content: {}, createdAt: {}, messageProtobuf createdAt: {}", message.getContent(), message.getCreatedAt(), messageProtobuf.getCreatedAt());
        Optional<ThreadEntity> threadOpt = threadRestService.findByUid(threadUid);
        if (threadOpt.isPresent()) {
            ThreadEntity thread = threadOpt.get();
            message.setThread(thread);
        } else {
            log.info("thread not found, uid: {}", threadUid);
            return;
        }
        message.setUser(messageProtobuf.getUser().toJson());
        message.setUserUid(messageProtobuf.getUser().getUid());
        // 
        MessageExtra extraObject = MessageExtra.fromJson(messageProtobuf.getExtra()); 
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            message.setOrgUid(orgUid);
        }
        messageRestService.save(message);
    }

    // 处理消息通知，已处理的消息返回true，未处理的消息返回false
    public Boolean dealWithMessageNotification(@NonNull MessageTypeEnum type, MessageProtobuf messageProtobuf) {
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
        // content为撤回消息的uid
        messageRestService.deleteByUid(message.getContent());
    }


}
