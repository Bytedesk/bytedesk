/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:37:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 14:31:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.redis.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息未读事件监听器
 */
@Slf4j
@Component
@AllArgsConstructor
public class MessageUnreadEventListener {

    private final MessageUnreadRestService messageUnreadRestService;
    
    private final RedisService redisService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        try {
            MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(event.getJson());
            MessageTypeEnum messageType = messageProtobuf.getType();
            // 仅当收到文本、图片、文件、音频、视频类型的消息时才处理
            if (!MessageTypeEnum.TEXT.equals(messageType) 
                && !MessageTypeEnum.IMAGE.equals(messageType)
                && !MessageTypeEnum.FILE.equals(messageType)
                && !MessageTypeEnum.AUDIO.equals(messageType)
                && !MessageTypeEnum.VIDEO.equals(messageType)
                && !MessageTypeEnum.READ.equals(messageType)
                ) {
                log.info("message unread only for TEXT/IMAGE/FILE/AUDIO/VIDEO message, current: {}", messageType);
                return;
            }
            if (ChannelEnum.SYSTEM.equals(messageProtobuf.getChannel())) {
                log.info("message unread system message {} 过滤掉", messageProtobuf.getChannel());
                return;
            }
            // 删除已读消息
            if (MessageTypeEnum.READ.equals(messageProtobuf.getType())) {
                log.info("message unread update event: {} {} {}", messageProtobuf.getUid(), messageProtobuf.getType(),
                        messageProtobuf.getContent());
                // message.getContent() 代表 已读消息的uid
                String readMessageUid = messageProtobuf.getContent();
                if (readMessageUid != null && !readMessageUid.trim().isEmpty()) {
                    try {
                        // 先清理 Redis 缓存
                        redisService.removeMessageExists(readMessageUid);
                        // 再删除数据库记录
                        messageUnreadRestService.deleteByUid(readMessageUid);
                    } catch (Exception e) {
                        log.error("Error processing READ message for uid {}: {}", readMessageUid, e.getMessage());
                        // 不重新抛出异常，避免影响其他事件处理
                    }
                } else {
                    log.warn("READ message content is null or empty: {}", messageProtobuf.getUid());
                }
            } else {
                // 缓存未读消息，create方法内部已包含重复检查逻辑
                try {
                    messageUnreadRestService.create(messageProtobuf);
                } catch (Exception e) {
                    log.error("Error creating unread message for uid {}: {}", messageProtobuf.getUid(), e.getMessage());
                    // 不重新抛出异常，避免影响其他事件处理
                }
            }
        } catch (Exception e) {
            log.error("Error processing message json event for message {}: {}", 
                    event.getJson(), e.getMessage(), e);
            // 不重新抛出异常，避免影响其他事件处理
        }
    }

    // @EventListener
    // public void onMqttConnectEvent(MqttConnectedEvent event) {
    //     // 用户clientId格式: uid/client/deviceUid
    //     // String clientId = event.getClientId();
    //     // log.info("message unread mqtt connect event: {}", clientId);
    //     // String[] clientIdArray = clientId.split("/");
    //     // if (clientIdArray.length != 3) {
    //     // return;
    //     // }
    //     // String userUid = clientIdArray[0];
    //     // TODO: 将缓存消息推送给相应客服端
    // }

    // @EventListener
    // public void onStompSessionConnectedEvent(StompConnectedEvent event) {
    //     // TODO: 将缓存消息推送给相应访客端
    //     // log.info("message unread stomp session connect event: {}",
    //     // event.getClientId());
    // }

}
