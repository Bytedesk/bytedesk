/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 10:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-16 08:58:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.bytedesk.core.topic.TopicEntity;
import com.bytedesk.core.topic.TopicRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.socket.mqtt.MqttSession;
import com.bytedesk.core.socket.mqtt.service.MqttMessageIdService;
import com.bytedesk.core.socket.mqtt.service.MqttSessionService;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.socket.protobuf.model.ThreadProto;
import com.bytedesk.core.socket.protobuf.model.UserProto;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MqttMessageIdService mqttMessageIdService;

    private final MqttSessionService mqttSessionService;

    private final TopicRestService topicRestService;

    // 发送消息给stomp访客端
    public void sendStompMessage(@NonNull String messageJson) {
        Assert.notNull(messageJson, "messageJson is null");
        //
        MessageProtobuf messageObject = JSON.parseObject(messageJson, MessageProtobuf.class);
        if (messageObject.getThread() == null) {
            throw new IllegalArgumentException("The thread field in message is null.");
        }
        // 
        String topicStr = messageObject.getThread().getTopic();
        if (topicStr == null) {
            throw new IllegalArgumentException("The topic field in thread is null.");
        }
        // 将topic中的'/'替换为'.'，以符合Stomp客户端的topic格式要求
        // 例如，将 /org/agent/default_agent_uid/1418711693000834 转换为
        // /topic/org.agent.default_agent_uid.1418711693000834
        String topic = TopicUtils.TOPIC_PREFIX + topicStr.replace("/", ".");
        // log.debug("stomp topic {}, {}", topic, messageJson);
        // 发送给Stomp客户端
        simpMessagingTemplate.convertAndSend(topic, messageJson);
    }

    // 发送消息给mqtt客户端
    public void sendMqttMessage(@NonNull MessageProto.Message messageProto) {
        // log.debug("send proto message");
        ThreadProto.Thread thread = messageProto.getThread(); // 提取线程信息到临时变量
        ThreadTypeEnum threadType = ThreadTypeEnum.valueOf(thread.getType());
        String topic = thread.getTopic(); // 提取主题信息到临时变量
        // 
        if (threadType.equals(ThreadTypeEnum.MEMBER)) {
            log.info("Reversing member message for topic: {}", topic);
            // 同事一对一消息反转处理
            String threadUid = thread.getUid();
            String reverseThreadUid = new StringBuffer(threadUid).reverse().toString();
            String reverseThreadTopic = TopicUtils.getOrgMemberTopicReverse(topic);
            //
            UserProto.User user = messageProto.getUser();
            ThreadProto.Thread reverseThread = thread.toBuilder()
                    .setTopic(reverseThreadTopic)
                    .setUid(reverseThreadUid)
                    .setUser(user)
                    .build();
            MessageProto.Message reverseMessage = messageProto.toBuilder().setThread(reverseThread).build();
            doSendToSubscribers(reverseThreadTopic, reverseMessage);
        } else if (threadType.equals(ThreadTypeEnum.GROUP)) {
            // 群聊消息处理（当前无特定处理，预留扩展）
            log.info("Sending group message for topic: {}", topic);
            // 可以在此处添加未来群聊消息的处理逻辑
        }
        // 发送原始消息给订阅者
        doSendToSubscribers(topic, messageProto);
    }

    private void doSendToSubscribers(String topic, @NonNull MessageProto.Message messageProto) {
        // log.debug("doSendToSubscribers: topic={}", topic);
        Set<TopicEntity> topicSet = topicRestService.findByTopic(topic);
        log.info("topicList size {}", topicSet.size());
        topicSet.forEach(topicEntity -> {
            Set<String> clientIdSet = topicEntity.getClientIds();
            clientIdSet.forEach(clientId -> {
                doSendMessage(topic, messageProto, clientId);
            });
        });
    }

    private void doSendMessage(String topic, @NonNull MessageProto.Message messageProto, String clientId) {
        log.debug("doSendMessage: topic {} clientId {}", topic, clientId);
        MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
        boolean dup = false;
        boolean retain = false;
        byte[] messageBytes = messageProto.toByteArray();
        // 当前活跃长连接信息
        if (mqttSessionService.containsKey(clientId)) {
            // log.debug("doSendMessage hasSession: topic {} clientId {}", topic, clientId);
            // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
            int messageId = mqttMessageIdService.getNextMessageId();
            MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, dup, mqttQoS, retain, 0),
                    new MqttPublishVariableHeader(topic, messageId),
                    Unpooled.buffer().writeBytes(messageBytes));
            //
            final MqttSession mqttSession = mqttSessionService.get(clientId);
            mqttSession.getChannel().writeAndFlush(publishMessage);
        }
    }

}

