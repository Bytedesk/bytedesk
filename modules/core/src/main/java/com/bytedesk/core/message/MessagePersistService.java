/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:04:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-23 16:28:10
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
import com.bytedesk.core.message.content.StreamContent;

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
        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageJSON); 
        
        MessageTypeEnum type = messageProtobuf.getType();
        String threadUid = messageProtobuf.getThread().getUid();

        // 返回true表示该消息是系统通知，不应该保存到数据库
        if (dealWithMessageNotification(type, messageProtobuf)) {
            return;
        }
        
        String uid = messageProtobuf.getUid();
        if (messageRestService.existsByUid(uid)) {
            // 流式消息单独处理下
            if (MessageTypeEnum.ROBOT_STREAM.equals(type)) {
                // 更新消息内容
                Optional<MessageEntity> messageOpt = messageRestService.findByUid(uid);
                if (messageOpt.isPresent()) {
                    MessageEntity message = messageOpt.get();
                    try {
                        // 解析已存与本次分片为 RobotStreamContent，按字段拼接
                        String existingJson = message.getContent();
                        String incomingJson = messageProtobuf.getContent();

                        StreamContent existing = null;
                        StreamContent incoming = null;
                        try {
                            if (existingJson != null && !existingJson.isEmpty()) {
                                existing = StreamContent.fromJson(existingJson, StreamContent.class);
                            }
                        } catch (Exception ignore) {
                            // 旧数据或非JSON，忽略
                        }
                        try {
                            if (incomingJson != null && !incomingJson.isEmpty()) {
                                incoming = StreamContent.fromJson(incomingJson, StreamContent.class);
                            }
                        } catch (Exception ignore) {
                        }

                        if (existing == null || incoming == null) {
                            // 兜底：任一无法解析时，仍旧进行字符串拼接以不丢数据
                            message.setContent((existingJson == null ? "" : existingJson)
                                    + (incomingJson == null ? "" : incomingJson));
                        } else {
                            String mergedAnswer = concatSafe(existing.getAnswer(), incoming.getAnswer());
                            String mergedReason = concatSafe(existing.getReasonContent(), incoming.getReasonContent());

                            // 沿用已有的其它字段（question、sources、kbUid、robotUid、regenerationContext）
                            StreamContent merged = StreamContent.builder()
                                    .question(existing.getQuestion() != null ? existing.getQuestion()
                                            : incoming.getQuestion())
                                    .answer(mergedAnswer)
                                    .reasonContent(mergedReason)
                                    .sources(existing.getSources() != null ? existing.getSources()
                                            : incoming.getSources())
                                    .regenerationContext(existing.getRegenerationContext() != null
                                            ? existing.getRegenerationContext()
                                            : incoming.getRegenerationContext())
                                    .kbUid(existing.getKbUid() != null ? existing.getKbUid() : incoming.getKbUid())
                                    .robotUid(existing.getRobotUid() != null ? existing.getRobotUid()
                                            : incoming.getRobotUid())
                                    .build();
                            message.setContent(merged.toJson());
                        }
                    } catch (Exception ex) {
                        log.warn("Failed to merge ROBOT_STREAM content using JSON, fallback to raw append: {}",
                                ex.getMessage());
                        message.setContent(message.getContent() + messageProtobuf.getContent());
                    }
                    messageRestService.save(message);
                }
                return;
            }
            log.info("message already exists, uid: {}， type: {}, content: {}", uid, type, messageProtobuf.getContent());
            return;
        }
        
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
        
        MessageExtra extraObject = MessageExtra.fromJson(messageProtobuf.getExtra()); 
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            message.setOrgUid(orgUid);
        }
        messageRestService.save(message);
    }

    private String concatSafe(String a, String b) {
        if (a == null || a.isEmpty()) return b == null ? "" : b;
        if (b == null || b.isEmpty()) return a;
        return a + b;
    }

    // 处理消息通知，已处理的消息返回true，未处理的消息返回false
    public Boolean dealWithMessageNotification(@NonNull MessageTypeEnum type, MessageProtobuf messageProtobuf) {
        // String content = messageProtobuf.getContent();
        // log.info("dealWithMessageNotification: {}, {}", type, content);

        // 不需要保存的消息类型
        if (MessageTypeEnum.TYPING.equals(type)
                || MessageTypeEnum.PROCESSING.equals(type)
                || MessageTypeEnum.PREVIEW.equals(type)
                || MessageTypeEnum.CONTINUE.equals(type)) {
            return true;
        }

        // 消息撤回 - 从数据库中删除
        if (MessageTypeEnum.RECALL.equals(type)) {
            dealWithMessageRecall(messageProtobuf);
            return true;
        }

        // 消息回执处理（送达/已读）
        if (MessageTypeEnum.DELIVERED.equals(type) || MessageTypeEnum.READ.equals(type)) {
            dealWithMessageReceipt(type, messageProtobuf);
            return true;
        }

        return false;
    }

    // 处理消息回执
    private void dealWithMessageReceipt(MessageTypeEnum type, @Nonnull MessageProtobuf message) {
        log.info("dealWithMessageReceipt: {}, content: {}", type, message.getContent());
        // 回执消息内容存储被回执消息的uid
        // 当status已经为read时，不处理。防止delivered在后面更新read消息
        Optional<MessageEntity> messageOpt = messageRestService.findByUid(message.getContent());
        if (messageOpt.isPresent() && !MessageStatusEnum.READ.name().equals(messageOpt.get().getStatus())) {
            MessageEntity messageEntity = messageOpt.get();
            // 直接设置状态，避免重复判断
            messageEntity.setStatus(type.name());
            messageRestService.save(messageEntity);
        }
    }

    // 消息撤回，从数据库中删除消息
    private void dealWithMessageRecall(MessageProtobuf message) {
        // content为撤回消息的uid
        messageRestService.deleteByUid(message.getContent());
    }


}
