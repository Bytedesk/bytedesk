/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:37:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 11:09:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.stomp.StompConnectedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息未读事件监听器
 * 
 * 1. 当消息创建时，缓存未读消息
 * 2. 当消息更新时，删除未读消息
 * 3. 当用户连接时，推送未读消息
 * 4. 当用户断开连接时，删除未读消息
 * 5. 当用户断开连接时，删除未读消息
 * TODO: 添加过期时间，过期之后，自动删除
 */
@Slf4j
@Component
@AllArgsConstructor
public class MessageUnreadEventListener {

    private final MessageUnreadRestService messageUnreadRestService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        try {
            MessageProtobuf message = MessageProtobuf.fromJson(event.getJson());
            if (MessageTypeEnum.STREAM.equals(message.getType()) ||
                    MessageTypeEnum.NOTICE.equals(message.getType()) ||
                    MessageTypeEnum.SYSTEM.equals(message.getType())) {
                return;
            }
            if (ChannelEnum.SYSTEM.equals(message.getChannel())) {
                return;
            }
            // 删除已读消息
            if (MessageTypeEnum.READ.equals(message.getType())) {
                log.info("message unread update event: {} {} {}", message.getUid(), message.getType(),
                        message.getContent());
                // message.getContent() 代表 已读消息的uid
                messageUnreadRestService.deleteByUid(message.getContent());
            } else {
                // 缓存未读消息，create方法内部已包含重复检查逻辑
                messageUnreadRestService.create(message);
            }
        } catch (Exception e) {
            log.error("Error processing message json event for message {}: {}", 
                    event.getJson(), e.getMessage(), e);
            // 不重新抛出异常，避免影响其他事件处理
        }
    }

    @EventListener
    public void onMqttConnectEvent(MqttConnectedEvent event) {
        // 用户clientId格式: uid/client/deviceUid
        // String clientId = event.getClientId();
        // log.info("message unread mqtt connect event: {}", clientId);
        // String[] clientIdArray = clientId.split("/");
        // if (clientIdArray.length != 3) {
        // return;
        // }
        // String userUid = clientIdArray[0];
        // TODO: 将缓存消息推送给相应客服端
    }

    @EventListener
    public void onStompSessionConnectedEvent(StompConnectedEvent event) {
        // TODO: 将缓存消息推送给相应访客端
        // log.info("message unread stomp session connect event: {}",
        // event.getClientId());
    }

}
