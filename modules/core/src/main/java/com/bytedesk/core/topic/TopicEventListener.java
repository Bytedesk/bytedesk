/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 15:11:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-06 12:45:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.event.MqttConnectedEvent;
import com.bytedesk.core.event.MqttDisconnectedEvent;
import com.bytedesk.core.event.MqttSubscribeEvent;
import com.bytedesk.core.event.MqttUnsubscribeEvent;
import com.bytedesk.core.thread.ThreadCreateEvent;
import com.bytedesk.core.thread.ThreadTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TopicEventListener {

    private final TopicService topicService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        log.info("onThreadCreateEvent: {}", event.getThread().getUid());

        // 机器人会话不需要订阅topic
        if (event.getThread().getType().equals(ThreadTypeEnum.ROBOT)) {
            return;
        }

        topicService.create(event.getThread().getTopic(), event.getThread().getOwner().getUid());
    }

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("topic onMqttConnectedEvent uid {}, clientId {}", uid, clientId);
        //
        topicService.addClientId(clientId);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("topic onMqttDisconnectedEvent uid {}, clientId {}", uid, clientId);
        //
        topicService.removeClientId(clientId);
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
        topicService.unsubscribe(event.getTopic(), event.getClientId());
    }
    
}
