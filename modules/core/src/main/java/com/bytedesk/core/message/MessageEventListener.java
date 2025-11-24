/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 16:02:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:03:17
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
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.utils.MessageConvertUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageEventListener {

    private final MessageService messageService;

    private final MessagePersistCache messagePersistCache;

    private final MessagePersistService messagePersistService;

    private final MessageSocketService messageSocketService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        try {
            String messageJson =  messageService.processMessageJson(event.getJson(), false);
            if (messageJson == null) {
                return;
            }
            // Send to Stomp clients
            messageSocketService.sendStompMessage(messageJson);
            
            // Send to MQTT clients - with additional error handling
            try {
                MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
                MessageProto.Message message = MessageConvertUtils.toProtoBean(builder, messageJson);
                if (message != null) {
                    messageSocketService.sendMqttMessage(message);
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


}
