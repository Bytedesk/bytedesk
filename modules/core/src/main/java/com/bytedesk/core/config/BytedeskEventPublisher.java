/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 14:42:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 16:39:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.core.quartz.event.QuartzDay8Event;
import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.quartz.event.QuartzHalfHourEvent;
import com.bytedesk.core.quartz.event.QuartzHourlyEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.event.UserCreateEvent;
import com.bytedesk.core.rbac.user.event.UserUpdateEvent;
import com.bytedesk.core.socket.mqtt.MqttSubscribeEvent;
import com.bytedesk.core.socket.mqtt.MqttUnsubscribeEvent;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.event.MqttDisconnectedEvent;
import com.bytedesk.core.socket.stomp.StompConnectedEvent;
import com.bytedesk.core.socket.stomp.StompDisconnectedEvent;
import com.bytedesk.core.socket.stomp.StompSubscribeEvent;
import com.bytedesk.core.socket.stomp.StompUnsubscribeEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.thread.event.ThreadUpdateEvent;
import com.bytedesk.core.topic.event.TopicCreateEvent;
import com.bytedesk.core.topic.event.TopicUpdateEvent;

import lombok.AllArgsConstructor;

@Async
@Component
@AllArgsConstructor
public class BytedeskEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishTopicCreateEvent(String topic, String userUid) {
        applicationEventPublisher.publishEvent(new TopicCreateEvent(this, topic, userUid));
    }

    public void publishTopicUpdateEvent(String topic, String userUid) {
        applicationEventPublisher.publishEvent(new TopicUpdateEvent(this, topic, userUid));
    }

    public void publishUserCreateEvent(UserEntity user) {
        applicationEventPublisher.publishEvent(new UserCreateEvent(user));
    }

    public void publishUserUpdateEvent(UserEntity user) {
        applicationEventPublisher.publishEvent(new UserUpdateEvent(user));
    }

    // public void publishMessageProtoEvent(byte[] messageBytes) {
    //     applicationEventPublisher.publishEvent(new MessageProtoEvent(this, messageBytes));
    // }

    public void publishMessageJsonEvent(String json) {
        applicationEventPublisher.publishEvent(new MessageJsonEvent(this, json));
    }

    public void publishMessageCreateEvent(MessageEntity message) {
        applicationEventPublisher.publishEvent(new MessageCreateEvent(this, message));
    }

    public void publishMessageUpdateEvent(MessageEntity message) {
        applicationEventPublisher.publishEvent(new MessageUpdateEvent(this, message));
    }

    public void publishQuartzDay8Event() {
        applicationEventPublisher.publishEvent(new QuartzDay8Event(this));
    }

    public void publishQuartzDay0Event() {
        applicationEventPublisher.publishEvent(new QuartzDay0Event(this));
    }

    public void publishQuartzHourlyEvent() {
        applicationEventPublisher.publishEvent(new QuartzHourlyEvent(this));
    }

    public void publishQuartzHalfHourEvent() {
        applicationEventPublisher.publishEvent(new QuartzHalfHourEvent(this));
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

    public void publishThreadCreateEvent(ThreadEntity thread) {
        applicationEventPublisher.publishEvent(new ThreadCreateEvent(this, thread));
    }

    public void publishThreadUpdateEvent(ThreadEntity thread) {
        applicationEventPublisher.publishEvent(new ThreadUpdateEvent(this, thread));
    }

    // public void publishThreadUpdateStatusEvent(Thread thread, String status) {
    //     applicationEventPublisher.publishEvent(new ThreadUpdateStatusEvent(this, thread, status));
    // }

    public void publishOrganizationCreateEvent(OrganizationEntity organization) {
        applicationEventPublisher.publishEvent(new OrganizationCreateEvent(organization));
    }

    public void publishEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

}
