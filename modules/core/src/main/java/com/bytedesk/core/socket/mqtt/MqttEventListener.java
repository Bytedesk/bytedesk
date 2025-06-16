/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-04 10:44:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-16 07:45:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.event.MqttDisconnectedEvent;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MqttEventListener {

    private final TopicService topicService;

    private final TopicCacheService topicCacheService;

    private final MqttConnectionService mqttConnectionService;

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("topic onMqttConnectedEvent uid {}, clientId {}", uid, clientId);
        //
        topicCacheService.pushClientId(clientId);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("topic onMqttDisconnectedEvent uid {}, clientId {}", uid, clientId);
        //
        // 暂不删除
        // topicCacheService.removeClientId(clientId);
    }

    @EventListener
    public void onMqttSubscribeEvent(MqttSubscribeEvent event) {
        log.info("topic onMqttSubscribeEvent {}", event);
        //
        topicService.subscribe(event.getTopic(), event.getClientId());
    }

    @EventListener
    public void onMqttUnsubscribeEvent(MqttUnsubscribeEvent event) {
        log.info("topic onMqttUnsubscribeEvent {}", event);
        //
        // topicService.unsubscribe(event.getTopic(), event.getClientId());
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("mqtt QuartzOneMinEvent");
        mqttConnectionService.cleanExpiredClients();
    }

}
