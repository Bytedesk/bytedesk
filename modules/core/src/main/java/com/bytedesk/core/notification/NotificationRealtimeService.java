/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-06-20 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-06-20 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

import java.io.IOException;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageSocketService;
import com.bytedesk.core.message.content.NoticeContent;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.utils.MessageConvertUtils;
import com.bytedesk.core.notification.event.NotificationCreateEvent;
import com.bytedesk.core.rbac.user.UserUtils;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRealtimeService {

    private static final String EVENT_CREATED = "CREATED";

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MessageSocketService messageSocketService;

    public void onNotificationCreateEvent(NotificationCreateEvent event) {
        NotificationEntity notification = event.getNotification();
        if (notification == null || !StringUtils.hasText(notification.getUserUid())) {
            return;
        }

        String topic = TopicUtils.TOPIC_PREFIX + TopicUtils.getNotificationTopic(notification.getUserUid()).replace("/", ".");
        JSONObject payload = new JSONObject();
        payload.put("event", EVENT_CREATED);
        payload.put("uid", notification.getUid());
        payload.put("userUid", notification.getUserUid());
        payload.put("orgUid", notification.getOrgUid());
        payload.put("title", notification.getTitle());
        payload.put("content", notification.getContent());
        payload.put("type", notification.getType());
        payload.put("status", notification.getStatus());
        payload.put("extra", notification.getExtra());

        try {
            simpMessagingTemplate.convertAndSend(topic, payload.toJSONString());
        } catch (Exception ex) {
            log.warn("send notification realtime event failed: topic={}, notificationUid={}, error={}",
                    topic, notification.getUid(), ex.getMessage());
        }

        sendMqttNotification(notification);
    }

    private void sendMqttNotification(NotificationEntity notification) {
        String userUid = notification.getUserUid();
        String mqttTopic = TopicUtils.getSystemTopic(userUid);
        log.info("[NOTICE-DIAG] sendMqttNotification: userUid={} topic={} title={}",
                userUid, mqttTopic, notification.getTitle());
        MessageProto.Message message = buildMqttNotice(notification, mqttTopic);
        if (message == null) {
            log.info("[NOTICE-DIAG] sendMqttNotification SKIPPED: buildMqttNotice returned null");
            return;
        }

        try {
            messageSocketService.sendMqttMessageToUser(userUid, mqttTopic, message);
            log.info("[NOTICE-DIAG] sendMqttNotification sent successfully to userUid={}", userUid);
        } catch (Exception ex) {
            log.warn("send notification mqtt event failed: topic={}, notificationUid={}, error={}",
                    mqttTopic, notification.getUid(), ex.getMessage());
        }
    }

    private MessageProto.Message buildMqttNotice(NotificationEntity notification, String mqttTopic) {
        NoticeContent noticeContent = NoticeContent.builder()
                .noticeUid(notification.getUid())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .status(notification.getStatus())
                .level("USER")
                .orgUid(notification.getOrgUid())
                .userUid(notification.getUserUid())
                .extra(notification.getExtra())
                .build();

        ThreadProtobuf thread = ThreadProtobuf.builder()
                .uid(mqttTopic)
                .topic(mqttTopic)
                .type(ThreadTypeEnum.CHANNEL)
                .status(ThreadProcessStatusEnum.CHATTING)
                .channel(ChannelEnum.SYSTEM)
                .user(UserUtils.getSystemUser())
                .build();

        MessageProtobuf message = MessageProtobuf.builder()
                .uid(notification.getUid())
                .type(MessageTypeEnum.NOTICE)
                .content(noticeContent.toJson())
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(BdDateUtils.now())
                .channel(ChannelEnum.SYSTEM)
                .thread(thread)
                .user(UserUtils.getSystemUser())
                .build();

        try {
            return MessageConvertUtils.toProtoBean(MessageProto.Message.newBuilder(), message.toJson());
        } catch (IOException ex) {
            log.warn("build notification mqtt message failed: notificationUid={}, error={}",
                    notification.getUid(), ex.getMessage());
            return null;
        }
    }
}