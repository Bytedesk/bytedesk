/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 14:42:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-09 16:26:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.core.action.Action;
import com.bytedesk.core.action.ActionCreateEvent;
import com.bytedesk.core.event.GenericApplicationEvent;
// import com.bytedesk.core.cache.CaffeineCacheGroupEvent;
import com.bytedesk.core.message.MessageProtoEvent;
import com.bytedesk.core.message.MessageUpdateEvent;
import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageCreateEvent;
import com.bytedesk.core.message.MessageJsonEvent;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserCreateEvent;
import com.bytedesk.core.rbac.user.UserUpdateEvent;
import com.bytedesk.core.socket.mqtt.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.MqttDisconnectedEvent;
import com.bytedesk.core.socket.mqtt.MqttSubscribeEvent;
import com.bytedesk.core.socket.mqtt.MqttUnsubscribeEvent;
import com.bytedesk.core.socket.stomp.StompConnectedEvent;
import com.bytedesk.core.socket.stomp.StompDisconnectedEvent;
import com.bytedesk.core.socket.stomp.StompSubscribeEvent;
import com.bytedesk.core.socket.stomp.StompUnsubscribeEvent;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadCreateEvent;
import com.bytedesk.core.thread.ThreadUpdateEvent;
import com.bytedesk.core.topic.TopicCreateEvent;
import com.bytedesk.core.topic.TopicUpdateEvent;

import lombok.AllArgsConstructor;

@Async
@Component
@AllArgsConstructor
public class BytedeskEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishGenericApplicationEvent(GenericApplicationEvent<?> event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void publishTopicCreateEvent(String topic, String userUid) {
        applicationEventPublisher.publishEvent(new TopicCreateEvent(this, topic, userUid));
    }

    public void publishTopicUpdateEvent(String topic, String userUid) {
        applicationEventPublisher.publishEvent(new TopicUpdateEvent(this, topic, userUid));
    }

    public void publishUserCreateEvent(User user) {
        applicationEventPublisher.publishEvent(new UserCreateEvent(user));
    }

    public void publishUserUpdateEvent(User user) {
        applicationEventPublisher.publishEvent(new UserUpdateEvent(user));
    }

    public void publishMessageProtoEvent(byte[] messageBytes) {
        applicationEventPublisher.publishEvent(new MessageProtoEvent(this, messageBytes));
    }

    public void publishMessageJsonEvent(String json) {
        applicationEventPublisher.publishEvent(new MessageJsonEvent(this, json));
    }

    public void publishMessageCreateEvent(Message message) {
        applicationEventPublisher.publishEvent(new MessageCreateEvent(this, message));
    }

    public void publishMessageUpdateEvent(Message message) {
        applicationEventPublisher.publishEvent(new MessageUpdateEvent(this, message));
    }

    public void publishQuartzFiveSecondEvent() {
        applicationEventPublisher.publishEvent(new QuartzFiveSecondEvent(this));
    }

    public void publishQuartzFiveMinEvent() {
        applicationEventPublisher.publishEvent(new QuartzFiveMinEvent(this));
    }

    public void publishQuartzOneMinEvent() {
        applicationEventPublisher.publishEvent(new QuartzOneMinEvent(this));
    }

    public void publishMqttConnectedEvent(String clientId) {
        applicationEventPublisher.publishEvent(new MqttConnectedEvent(this, clientId));
    }

    public void publishMqttDisconnectedEvent(String clientId) {
        applicationEventPublisher.publishEvent(new MqttDisconnectedEvent(this, clientId));
    }

    public void publishMqttSubscribeEvent(String topic, String clientId) {
        applicationEventPublisher.publishEvent(new MqttSubscribeEvent(this, topic, clientId));
    }

    public void publishMqttUnsubscribeEvent(String topic, String clientId) {
        applicationEventPublisher.publishEvent(new MqttUnsubscribeEvent(this, topic, clientId));
    }

    public void publishStompConnectedEvent(String clientId) {
        applicationEventPublisher.publishEvent(new StompConnectedEvent(this, clientId));
    }

    public void publishStompDisconnectedEvent(String clientId) {
        applicationEventPublisher.publishEvent(new StompDisconnectedEvent(this, clientId));
    }

    public void publishStompSubscribeEvent(String topic, String clientId) {
        applicationEventPublisher.publishEvent(new StompSubscribeEvent(this, topic, clientId));
    }

    public void publishStompUnsubscribeEvent(String topic, String clientId) {
        applicationEventPublisher.publishEvent(new StompUnsubscribeEvent(this, topic, clientId));
    }

    public void publishThreadCreateEvent(Thread thread) {
        applicationEventPublisher.publishEvent(new ThreadCreateEvent(this, thread));
    }

    public void publishThreadUpdateEvent(Thread thread) {
        applicationEventPublisher.publishEvent(new ThreadUpdateEvent(this, thread));
    }

    public void publishActionCreateEvent(Action action) {
        applicationEventPublisher.publishEvent(new ActionCreateEvent(this, action));
    }

    public void publishOrganizationCreateEvent(Organization organization) {
        applicationEventPublisher.publishEvent(new OrganizationCreateEvent(organization));
    }

    // public void publishEmailAlreadyExistsEvent(String email) {
    // applicationEventPublisher.publishEvent(new EmailAlreadyExistsEvent(this,
    // email));
    // }

    // public void publishCaffeineCacheGroupEvent(String groupUid, String
    // messageJson) {
    // applicationEventPublisher.publishEvent(new CaffeineCacheGroupEvent(this,
    // groupUid, messageJson));
    // }

}
