/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 14:42:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-23 08:55:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.Thread;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BytedeskEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Async
    public void publishMessageBytesEvent(byte[] messageBytes) {
        applicationEventPublisher.publishEvent(new MessageBytesEvent(this, messageBytes));
    }

    @Async
    public void publishMessageJsonEvent(String json) {
        applicationEventPublisher.publishEvent(new MessageJsonEvent(this, json));
    }

    @Async
    public void publishQuartzFiveSecondEvent() {
        applicationEventPublisher.publishEvent(new QuartzFiveSecondEvent(this));
    }

    @Async
    public void publishMqttConnectedEvent(String client) {
        applicationEventPublisher.publishEvent(new MqttConnectedEvent(this, client));
    }

    @Async
    public void publishMqttDisconnectedEvent(String client) {
        applicationEventPublisher.publishEvent(new MqttDisconnectedEvent(this, client));
    }

    // @Async
    // public void publishMqttSubscribeEvent(String uid, String topic) {
    //     applicationEventPublisher.publishEvent(new MqttSubscribeEvent(this, uid, topic));
    // }

    // @Async
    // public void publishMqttUnsubscribeEvent(String uid, String topic) {
    //     applicationEventPublisher.publishEvent(new MqttUnsubscribeEvent(this, uid, topic));
    // }

    @Async
    public void publishThreadCreateEvent(Thread thread) {
        applicationEventPublisher.publishEvent(new ThreadCreateEvent(this, thread));
    }

    @Async
    public void publishThreadUpdateEvent(Thread thread) {
        applicationEventPublisher.publishEvent(new ThreadUpdateEvent(this, thread));
    }


}
