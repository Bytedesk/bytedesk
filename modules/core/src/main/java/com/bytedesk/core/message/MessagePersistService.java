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

import java.util.Objects;
import java.util.Optional;

import org.springframework.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.redis.RedisService;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessagePersistService {

    private static final long RECEIPT_DEDUP_TTL_SECONDS = 300L;

    /**
     * 回执最大重试次数（12次 × 5秒 = 60秒）。
     * 超过此次数后目标消息仍未入库，视为孤立回执，丢弃不再重试。
     */
    private static final int MAX_RECEIPT_RETRIES = 12;

    /** 回执重试信息存储在 extra 字段中的 JSON key */
    private static final String RECEIPT_RETRY_KEY = "_receiptRetries";

    private final MessageRestService messageRestService;

    private final ThreadRestService threadRestService;

    private final ModelMapper modelMapper;

    private final RedisService redisService;

    private final MessagePersistCache messagePersistCache;

    public void persist(String messageJSON) {
        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageJSON); 
        if (messageProtobuf == null) {
            log.warn("skip persist for invalid message payload: {}", messageJSON);
            return;
        }
        if (messageProtobuf.getThread() == null || !StringUtils.hasText(messageProtobuf.getThread().getUid())) {
            log.warn("skip persist for message without thread uid, messageUid: {}", messageProtobuf.getUid());
            return;
        }
        
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

                        RobotContent existing = null;
                        RobotContent incoming = null;
                        try {
                            if (existingJson != null && !existingJson.isEmpty()) {
                                existing = RobotContent.fromJson(existingJson, RobotContent.class);
                            }
                        } catch (Exception ignore) {
                            // 旧数据或非JSON，忽略
                        }
                        try {
                            if (incomingJson != null && !incomingJson.isEmpty()) {
                                incoming = RobotContent.fromJson(incomingJson, RobotContent.class);
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
                            RobotContent merged = RobotContent.builder()
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
            log.info("message already exists, uid: {}， type: {}", uid, type);
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
            thread = updateThreadContent(thread, type, messageProtobuf);
            message.setThread(thread);
        } else {
            log.warn("skip persist because thread not found, uid: {}, messageUid: {}", threadUid, uid);
            return;
        }
        message.setUser(messageProtobuf.getUser().toJson());
        message.setUserUid(messageProtobuf.getUser().getUid());
        
        MessageExtra extraObject = MessageExtra.fromJson(messageProtobuf.getExtra()); 
        String orgUid = null;
        if (extraObject != null) {
            orgUid = extraObject.getOrgUid();
            message.setOrgUid(orgUid);
        }
        messageRestService.save(message);
    }

    private ThreadEntity updateThreadContent(ThreadEntity thread, MessageTypeEnum type, MessageProtobuf messageProtobuf) {
        ThreadContent next = ThreadContent.fromMessage(type, messageProtobuf);
        String nextJson = next != null ? next.toJson() : null;

        // 避免频繁写库：仅当摘要/类型/payload 任一发生变化时才更新
        ThreadContent current = ThreadContent.fromStored(thread.getContent());
        String currentPreview = current != null ? current.getPreview() : thread.getContent();
        String nextPreview = next != null ? next.getPreview() : null;

        String currentMsgType = current != null ? current.getMsgType() : null;
        String nextMsgType = next != null ? next.getMsgType() : null;

        String currentPayload = current != null ? current.getPayload() : null;
        String nextPayload = next != null ? next.getPayload() : null;

        if (!Objects.equals(currentPreview, nextPreview)
                || !Objects.equals(currentMsgType, nextMsgType)
                || !Objects.equals(currentPayload, nextPayload)) {
            thread.setContent(nextJson);
            return threadRestService.save(thread);
        }
        return thread;
    }

    private String concatSafe(String a, String b) {
        if (a == null || a.isEmpty()) return b == null ? "" : b;
        if (b == null || b.isEmpty()) return a;
        return a + b;
    }

    // 处理消息通知，已处理的消息返回true，未处理的消息返回false
    public Boolean dealWithMessageNotification(MessageTypeEnum type, MessageProtobuf messageProtobuf) {
        // String content = messageProtobuf.getContent();
        // log.info("dealWithMessageNotification: {}, {}", type, content);

        // 不需要保存的消息类型
        if (MessageTypeEnum.TYPING.equals(type)
                || MessageTypeEnum.PROCESSING.equals(type)
                || MessageTypeEnum.PREVIEW.equals(type)
                || MessageTypeEnum.CONTINUE.equals(type)
                // 仅用于更新RATE_INVITE消息和通知前端刷新 UI，不作为独立聊天记录入库
                || MessageTypeEnum.RATE_SUBMIT.equals(type)
                // REACTION 仅用于通知前端刷新 UI，不作为独立聊天记录入库
                || MessageTypeEnum.REACTION.equals(type)
                // PLAYBACK 仅用于通知前端刷新 UI，不作为独立聊天记录入库
                || MessageTypeEnum.PLAYBACK.equals(type)
                // GOODS_UPDATE / ORDER_UPDATE 仅用于通知前端刷新 UI，不作为独立聊天记录入库
                || MessageTypeEnum.GOODS_UPDATE.equals(type)
                || MessageTypeEnum.ORDER_UPDATE.equals(type)) {
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
        String receiptContent = message.getContent();
        String receiptDedupKey = type.name() + ":" + receiptContent;
        // 回执消息内容存储被回执消息的uid
        // 当status已经为read时，不处理。防止delivered在后面更新read消息
        Optional<MessageEntity> messageOpt = messageRestService.findByUid(receiptContent);
        if (messageOpt.isEmpty()) {
            // 原消息尚未持久化（可能在同一批次中排在后面，或跨批次延迟），
            // 将回执重新推入缓存队列，在下一个5秒批次中重试
            int retryCount = parseReceiptRetryCount(message.getExtra());
            if (retryCount >= MAX_RECEIPT_RETRIES) {
                log.error("receipt retry limit exceeded ({}), dropping orphan receipt: type {}, targetUid {}, receiptUid {}",
                        retryCount, type, receiptContent, message.getUid());
                return;
            }
            log.warn("receipt target message not found yet, retry {} of {}: type {}, targetUid {}",
                    retryCount + 1, MAX_RECEIPT_RETRIES, type, receiptContent);
            message.setExtra(buildReceiptRetryExtra(retryCount + 1));
            messagePersistCache.pushForPersist(message.toJson());
            return;
        }

        MessageEntity messageEntity = messageOpt.get();
        if (MessageStatusEnum.READ.name().equals(messageEntity.getStatus())) {
            redisService.tryMarkMessageReceiptProcessed(receiptDedupKey, RECEIPT_DEDUP_TTL_SECONDS);
            log.debug("skip receipt update: message {} already READ", receiptContent);
            return;
        }

        try {
            // 直接设置状态，避免重复判断
            String previousStatus = messageEntity.getStatus();
            messageEntity.setStatus(type.name());
            MessageEntity savedMessage = messageRestService.save(messageEntity);
            redisService.tryMarkMessageReceiptProcessed(receiptDedupKey, RECEIPT_DEDUP_TTL_SECONDS);
            log.info("receipt status persisted: targetMessageUid {}, receiptUid {}, previousStatus {}, persistedStatus {}",
                    receiptContent, message.getUid(), previousStatus, savedMessage.getStatus());
        } catch (ObjectOptimisticLockingFailureException e) {
            int retryCount = parseReceiptRetryCount(message.getExtra());
            if (retryCount >= MAX_RECEIPT_RETRIES) {
                log.error("receipt retry limit exceeded after optimistic lock conflict ({}), dropping: type {}, targetUid {}, receiptUid {}",
                        retryCount, type, receiptContent, message.getUid());
                return;
            }
            log.warn("receipt update conflicted, retry {} of {}: type {}, targetUid {}, receiptUid {}",
                    retryCount + 1, MAX_RECEIPT_RETRIES, type, receiptContent, message.getUid());
            message.setExtra(buildReceiptRetryExtra(retryCount + 1));
            messagePersistCache.pushForPersist(message.toJson());
        }
    }

    // 消息撤回，从数据库中删除消息
    private void dealWithMessageRecall(MessageProtobuf message) {
        // content为撤回消息的uid
        messageRestService.deleteByUid(message.getContent());
    }

    /**
     * 从 extra 字段解析回执重试次数。
     * extra 为 null 或无法解析时返回 0。
     */
    private int parseReceiptRetryCount(String extra) {
        if (extra == null || extra.isEmpty()) {
            return 0;
        }
        try {
            com.alibaba.fastjson2.JSONObject obj = com.alibaba.fastjson2.JSON.parseObject(extra);
            Integer count = obj.getInteger(RECEIPT_RETRY_KEY);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 构建包含回执重试次数的 extra JSON 字符串。
     * 保留原有 extra 字段中的其他数据。
     */
    private String buildReceiptRetryExtra(int retryCount) {
        com.alibaba.fastjson2.JSONObject obj = new com.alibaba.fastjson2.JSONObject();
        obj.put(RECEIPT_RETRY_KEY, retryCount);
        return obj.toJSONString();
    }


}
