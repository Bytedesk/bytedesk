/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 16:02:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-09 10:33:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.io.IOException;
import java.time.LocalDateTime;
// import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.black.BlackEntity;
import com.bytedesk.core.black.BlackRestService;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.utils.MessageConvertUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageEventListener {

    private final MessagePersistCache messagePersistCache;

    private final MessagePersistService messagePersistService;

    private final MessageSocketService messageSocketService;

    private final BlackRestService blackRestService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        log.info("MessageJsonEvent {}", event.getJson());
        
        try {
            String messageJson = event.getJson();
            MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
            if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
                messageProtobuf.setStatus(MessageStatusEnum.SUCCESS);
            }
            
            ThreadProtobuf thread = messageProtobuf.getThread();
            if (thread == null) {
                throw new RuntimeException("thread is null");
            }
            
            // Replace client timestamp
            messageProtobuf.setCreatedAt(LocalDateTime.now());

            // Check blacklist
            if (isBlackList(messageProtobuf)) {
                return;
            }

            // Filter sensitive words
            messageJson = filterTaboo(JSON.toJSONString(messageProtobuf));
            
            // Cache message for persistence
            messagePersistCache.pushForPersist(messageJson);
            
            // Send to Stomp clients
            messageSocketService.sendJsonMessage(messageJson);
            
            // Send to MQTT clients - with additional error handling
            try {
                MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
                MessageProto.Message message = MessageConvertUtils.toProtoBean(builder, messageJson);
                if (message != null) {
                    messageSocketService.sendProtoMessage(message);
                } else {
                    log.error("Failed to convert message to proto format");
                }
            } catch (IOException e) {
                log.error("Error sending proto message: ", e);
            }
        } catch (Exception e) {
            log.error("Error processing message event: ", e);
            // Consider whether to rethrow or handle the error differently
        }
    }

    // 检查黑名单
    private boolean isBlackList(MessageProtobuf messageProtobuf) {
        String uid = messageProtobuf.getUser().getUid();
        MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            Optional<BlackEntity> blackOpt = blackRestService.findByVisitorUidAndOrgUid(uid, orgUid);
            if (blackOpt.isPresent()) {
                BlackEntity black = blackOpt.get();
                if (black.getEndTime() == null || black.getEndTime().isAfter(LocalDateTime.now())) {
                    return true;
                }
            }
        }
        return false;
    }

    // 过滤敏感词
    private String filterTaboo(String messageJson) {
        // TODO: 过滤敏感词，将敏感词替换为*
        // String filterJson = TabooUtil.replaceSensitiveWord(json, '*');
        return messageJson;
    }

    @EventListener
    public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
        // log.info("message quartz five second event: " + event);
        List<String> messageJsonList = messagePersistCache.getListForPersist();
        if (messageJsonList == null || messageJsonList.isEmpty()) {
            return;
        }
        messageJsonList.forEach(item -> {
            messagePersistService.persist(item);
        });
    }

    // @Async
    // @EventListener
    // public void onMessageCreateEvent(MessageCreateEvent event) {
    //     List<String> messageJsonList = event.getMessageJsonList();
    //     Assert.notEmpty(messageJsonList, "Message JSON list must not be empty");
        
    //     // ... rest of the method
    // }

}
