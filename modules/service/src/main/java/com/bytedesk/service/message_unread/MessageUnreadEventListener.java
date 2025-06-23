/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:37:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-23 10:25:55
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

import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.message.event.MessageUpdateEvent;
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

    private final MessageUnreadRestService messageUnreadService;

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        try {
            MessageEntity message = event.getMessage();
            if (MessageTypeEnum.STREAM.name().equalsIgnoreCase(message.getType()) || 
                MessageTypeEnum.NOTICE.name().equalsIgnoreCase(message.getType()) ||
                MessageTypeEnum.SYSTEM.name().equalsIgnoreCase(message.getType())) {
                return;
            }
            if (ClientEnum.SYSTEM.name().equalsIgnoreCase(message.getClient())) {
                return;
            }
            log.info("message unread create event: {} {} {}", message.getUid(), message.getType(), message.getContent());
            // 缓存未读消息
            // String threadTopic = message.getThread().getTopic();
            // String userString = message.getUser();
            // UserProtobuf user = UserProtobuf.fromJson(userString);
            // String userUid = user.getUid();
            // log.info("userUid {} threadTopic {}", userUid, threadTopic);
            // 
            messageUnreadService.create(message);
            //
        } catch (Exception e) {
            log.error("Error processing message create event for message {}: {}", 
                     event.getMessage() != null ? event.getMessage().getUid() : "unknown", e.getMessage(), e);
            // 不重新抛出异常，避免影响其他事件处理
            // 对于未读消息处理失败，我们选择记录错误而不是中断消息流程
        }
    }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        try {
            MessageEntity message = event.getMessage();
            log.info("message unread update event: {} {} {}", message.getUid(), message.getType(), message.getContent());
            //
            // 删除已读消息
            if (MessageTypeEnum.READ.name().equalsIgnoreCase(message.getType())) {
                // message.getContent() 代表 已读消息的uid
                messageUnreadService.deleteByUid(message.getContent());
            }
            
        } catch (Exception e) {
            log.error("Error processing message update event for message {}: {}", 
                     event.getMessage() != null ? event.getMessage().getUid() : "unknown", e.getMessage(), e);
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
        // List<MessageUnread> messageList = messageUnreadService.getMessages(userUid);
        // if (messageList == null || messageList.isEmpty()) {
        // return;
        // }
    }

    @EventListener
    public void onStompSessionConnectedEvent(StompConnectedEvent event) {
        // TODO: 将缓存消息推送给相应访客端
        // log.info("message unread stomp session connect event: {}", event.getClientId());
    }

}
