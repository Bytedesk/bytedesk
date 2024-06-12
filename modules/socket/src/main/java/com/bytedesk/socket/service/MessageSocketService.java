/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 10:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-31 18:34:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.service;

import java.util.Set;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.MqConsts;
// import com.bytedesk.core.constant.ThreadTypeConsts;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.Topic;
import com.bytedesk.core.topic.TopicService;
import com.bytedesk.socket.mqtt.model.MqttSession;
// import com.bytedesk.socket.mqtt.model.MqttSubscribe;
// import com.bytedesk.socket.mqtt.service.MqttClientIdService;
import com.bytedesk.socket.mqtt.service.MqttMessageIdService;
import com.bytedesk.socket.mqtt.service.MqttSessionService;
// import com.bytedesk.socket.mqtt.service.MqttSubscribeService;
import com.bytedesk.socket.protobuf.model.MessageProto;
import com.bytedesk.socket.protobuf.model.ThreadProto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MqttMessageIdService mqttMessageIdService;

    private final MqttSessionService mqttSessionService;

    private final TopicService topicService;

    public void sendJsonMessage(String messageJSON) {
        log.debug("send json message {}", messageJSON);

        if (!StringUtils.hasText(messageJSON)) {
            return;
        }
        //
        MessageResponse messageObject = JSON.parseObject(messageJSON, MessageResponse.class);
        String topic = MqConsts.TOPIC_PREFIX + messageObject.getThread().getTopic().replace("/", ".");
        log.debug("stomp topic {}", topic);
        // 发送给Stomp客户端
        simpMessagingTemplate.convertAndSend(topic, messageJSON);
    }

    public void sendProtoMessage(MessageProto.Message messageProto) {
        log.debug("send proto message");
        //
        // 广播给消息发送者的多个客户端，如：pc客户端发送消息，手机客户端可以同步收到自己发送的消息, 群组会话除外
        byte[] messageBytes = messageProto.toByteArray();
        ThreadTypeEnum threadType = ThreadTypeEnum.valueOf(messageProto.getThread().getType());

        // 发送消息给订阅者
        // 只有contact会话需要替换tid/topic/nickname/avatar
        // 广播给消息接收者，一对一会话的tid互为翻转
        if (threadType.equals(ThreadTypeEnum.MEMBER)) {
            doSendToSenderClients(messageProto);
            // 广播给消息发送者的多个客户端，如：pc客户端发送消息，手机客户端可以同步收到自己发送的消息
            String tid = messageProto.getThread().getUid();
            String reverseTid = new StringBuffer(tid).reverse().toString();
            //
            String userTopic = messageProto.getUser().getUid();
            ThreadProto.Thread thread = messageProto.getThread().toBuilder()
                    .setUid(reverseTid)
                    .setTopic(userTopic)
                    .build();
            MessageProto.Message message = messageProto.toBuilder()
                    .setThread(thread)
                    .build();
            messageBytes = message.toByteArray();
        }
        //
        String topic = messageProto.getThread().getTopic();
        log.debug("mqtt send topic {}", topic);
        doSendToSubscribers(topic, messageBytes);
    }

    private void doSendToSubscribers(String topic, byte[] messageBytes) {
        // String sid = topic.split("/")[0];
        log.debug("doSendToSubscribers {}", topic);
        //
        Set<Topic> topicSet = topicService.findByTopic(topic);
        log.info("topicList size {}", topicSet.size());
        topicSet.forEach(topicElement -> {
            Set<String> clientIdSet = topicElement.getClientIds();
            clientIdSet.forEach(clientId -> {
                doSendMessage(topic, messageBytes, clientId);
            });
        });
    }

    private void doSendToSenderClients(MessageProto.Message messageProto) {
        log.debug("doSendToSenderClients");
        // String uid, String topic, byte[] messageBytes
        // String uid = messageProto.getUser().getUid();
        String topic = messageProto.getThread().getTopic();
        byte[] messageBytes = messageProto.toByteArray();
        //
        // String sid = topic.split("/")[0];
        // List<Topic> topicList = topicService.findByTopic(sid);
        Set<Topic> topicSet = topicService.findByTopic(topic);
        topicSet.forEach(topicElement -> {
            Set<String> clientIdList = topicElement.getClientIds();
            clientIdList.forEach(clientId -> {
                doSendMessage(topic, messageBytes, clientId);
            });
        });
        //
        if (topicSet.size() == 0) {
            log.debug("doSendToSenderClients: no topic");
            // TODO: 数据库中为空，尝试匹配内存
            // doSendMessage(topic, messageBytes, topic);
        }
    }

    private void doSendMessage(String topic, byte[] messageBytes, String clientId) {
        log.debug("doSendMessage: {}, {}", topic, clientId);
        //
        MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
        boolean dup = false;
        boolean retain = false;
        // 当前活跃长连接信息
        if (mqttSessionService.containsKey(clientId)) {
            log.debug("hasSession: topic {} clientId {}", topic, clientId);
            // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
            int messageId = mqttMessageIdService.getNextMessageId();
            //
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
