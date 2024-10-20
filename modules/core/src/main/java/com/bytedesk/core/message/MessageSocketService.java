/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 10:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-15 18:25:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.Topic;
import com.bytedesk.core.topic.TopicService;
import com.bytedesk.core.topic.TopicUtils;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.socket.mqtt.MqttMessageIdService;
import com.bytedesk.core.socket.mqtt.MqttSession;
import com.bytedesk.core.socket.mqtt.MqttSessionService;
// import com.bytedesk.core.socket.mqtt.service.MqttSubscribeService;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.socket.protobuf.model.ThreadProto;
import com.bytedesk.core.socket.protobuf.model.UserProto;

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

    private final TopicService topicService;

    // 发送消息给stomp访客端
    public void sendJsonMessage(@NonNull String messageJson) {
        MessageProtobuf messageObject = JSON.parseObject(messageJson, MessageProtobuf.class);
        //
        if (messageObject == null) {
            throw new RuntimeException("messageObject is null");
        }
        if (messageObject.getThread() == null) {
            throw new RuntimeException("thread is null");
        }
        if (messageObject.getThread().getTopic() == null) {
            throw new RuntimeException("thread.topic is null");
        }
        // 注：将 /org/agent/default_agent_uid/1418711693000834 中 ‘/’ 替换为 ‘.’，然后拼接上 /topic/
        String topic = TopicUtils.TOPIC_PREFIX + messageObject.getThread().getTopic().replace("/", ".");
        log.debug("stomp topic {}, {}", topic, messageJson);
        // 发送给Stomp客户端
        simpMessagingTemplate.convertAndSend(topic, messageJson);
    }

    // 发送消息给mqtt客户端
    public void sendProtoMessage(@NonNull MessageProto.Message messageProto) {
        log.debug("send proto message");
        ThreadTypeEnum threadType = ThreadTypeEnum.valueOf(messageProto.getThread().getType());

        if (threadType.equals(ThreadTypeEnum.MEMBER)) {
            log.info("reverse member message");
            // 同事一对一
            String threadUid = messageProto.getThread().getUid();
            String reverseThreadUid = new StringBuffer(threadUid).reverse().toString();
            String threadTopic = messageProto.getThread().getTopic();
            String reverseThreadTopic = TopicUtils.getOrgMemberTopicReverse(threadTopic);
            //
            UserProto.User user = messageProto.getUser();
            ThreadProto.Thread thread = messageProto.getThread().toBuilder()
                    .setTopic(reverseThreadTopic)
                    .setUid(reverseThreadUid)
                    .setUser(user)
                    .build();
            //
            MessageProto.Message reverseMessage = messageProto.toBuilder().setThread(thread).build();
            doSendToSubscribers(reverseThreadTopic, reverseMessage);
            //
        } else if (threadType.equals(ThreadTypeEnum.GROUP)) {
            // 群聊
            log.info("send group message");
        }
        //
        String topic = messageProto.getThread().getTopic();        
        doSendToSubscribers(topic, messageProto);
    }

    private void doSendToSubscribers(String topic, @NonNull MessageProto.Message messageProto) {
        // log.debug("doSendToSubscribers: user={}, content={}, topic={}, type={}, clientId={}",
        //         messageProto.getUser().getNickname(), messageProto.getContent(), topic, messageProto.getType(),
        //         messageProto.getClient());
        Set<Topic> topicSet = topicService.findByTopic(topic);
        log.info("topicList size {}", topicSet.size());
        topicSet.forEach(topicElement -> {
            Set<String> clientIdSet = topicElement.getClientIds();
            clientIdSet.forEach(clientId -> {
                doSendMessage(topic, messageProto, clientId);
            });
        });
    }

    private void doSendMessage(String topic, @NonNull MessageProto.Message messageProto, String clientId) {
        // log.debug("doSendMessage: user={}, content={}, topic={}, type={}, clientId={}",
        //                  messageProto.getUser().getNickname(), messageProto.getContent(), topic, messageProto.getType(), clientId);
        MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
        boolean dup = false;
        boolean retain = false;
        byte[] messageBytes = messageProto.toByteArray();
        // 当前活跃长连接信息
        if (mqttSessionService.containsKey(clientId)) {
            log.debug("doSendMessage hasSession: topic {} clientId {}", topic, clientId);
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
