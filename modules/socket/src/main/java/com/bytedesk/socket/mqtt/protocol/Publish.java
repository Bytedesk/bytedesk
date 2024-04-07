package com.bytedesk.socket.mqtt.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

import com.bytedesk.core.event.BytedeskEventPublisher;
// import com.bytedesk.core.constant.StatusConsts;
// import com.bytedesk.core.constant.TypeConsts;
// import com.bytedesk.core.redis.RedisBlockService;
// import com.bytedesk.core.redis.RedisSettingService;
// import com.bytedesk.core.service.MessageService;
// import com.bytedesk.core.util.BdDateUtils;
import com.bytedesk.socket.mqtt.model.MqttRetainMessage;
import com.bytedesk.socket.mqtt.service.*;
import com.bytedesk.socket.mqtt.util.ChannelUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@AllArgsConstructor
public class Publish {

    // private final MqttMqService mqttMqService;

    // private final RedisBlockService redisBlockService;

    private final MqttRetainMessageStoreService mqttRetainMessageStoreService;

    private final BytedeskEventPublisher bytedeskEventPublisher;;

    // private final MessageService messageService;

    // private final MqttMessageIdService mqttMessageIdService;

    // private final MqttClientIdStoreService mqttClientIdStoreService;

    // private final MqttSessionStoreService mqttSessionStoreService;

    // private final RedisSettingService redisSettingService;

    // private final RedisUserService redisUserService;

    // private final RedisThreadService redisThreadService;

    //
    public void processPublish(Channel channel, MqttPublishMessage mqttPublishMessage) {
        // log.debug("processPublish {}", mqttPublishMessage.toString());
        //
        // TODO: 发送：消息发送成功回执
        // String clientId = (String)
        // channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
        byte[] messageBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        this.sendMqMessage(mqttPublishMessage, messageBytes);
        // QoS=0
        if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            // this.sendMqMessage(clientId, mqttPublishMessage, messageBytes);
        }
        // QoS=1
        else if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            // this.sendMqMessage(clientId, mqttPublishMessage, messageBytes);
            ChannelUtils.sendPubAckMessage(channel, mqttPublishMessage.variableHeader().packetId());
        }
        // QoS=2
        else if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            // this.sendMqMessage(clientId, mqttPublishMessage, messageBytes);
            ChannelUtils.sendPubRecMessage(channel, mqttPublishMessage.variableHeader().packetId());
        }
        // retain=1, 保留消息
        if (mqttPublishMessage.fixedHeader().isRetain()) {
            //
            mqttPublishMessage.payload().getBytes(mqttPublishMessage.payload().readerIndex(), messageBytes);
            //
            if (messageBytes.length == 0) {
                mqttRetainMessageStoreService.remove(mqttPublishMessage.variableHeader().topicName());
            } else {
                MqttRetainMessage retainMessageStore = new MqttRetainMessage()
                        .setTopic(mqttPublishMessage.variableHeader().topicName())
                        .setMqttQoS(mqttPublishMessage.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes);
                mqttRetainMessageStoreService.put(mqttPublishMessage.variableHeader().topicName(), retainMessageStore);
            }
        }
    }

    // 下列过滤不能从直接数据库中读取，否则会增加数据库压力，影响消息发送速度，务必从内存或redis中读取
    private void sendMqMessage(MqttPublishMessage publishMessage, byte[] messageBytes) {
        // 注意：不能去掉，否则无法解析protobuf
        publishMessage.payload().getBytes(publishMessage.payload().readerIndex(), messageBytes);
        // TODO: 发送-发送成功回执
        // publish messsage event, developers can listener to new message
        bytedeskEventPublisher.publishMessageBytesEvent(messageBytes);
    }

    // private void sendPubAckMessage(Channel channel, int messageId) {
    //     //
    //     MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
    //             new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
    //             MqttMessageIdVariableHeader.from(messageId),
    //             null);
    //     channel.writeAndFlush(pubAckMessage);
    // }

    // private void sendPubRecMessage(Channel channel, int messageId) {
    //     //
    //     MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
    //             new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
    //             MqttMessageIdVariableHeader.from(messageId), null);
    //     channel.writeAndFlush(pubRecMessage);
    // }

    // private void doSendReceiptToSenderClients(String senderUid, MessageProto.Message messageProto) {
    //     String topic = messageProto.getThread().getTopic();
    //     byte[] messageByteArray = messageProto.toByteArray();
    //     //
    //     List<String> clientIdList = mqttClientIdStoreService.get(senderUid);
    //     // FIXME: java.util.ConcurrentModificationException: null at
    //     // java.util.ArrayList.forEach(ArrayList.java:1260) ~[na:1.8.0_192]
    //     clientIdList.forEach(clientId -> {
    //         if (StringUtils.hasLength(clientId) && (mqttSessionStoreService != null)) {
    //             // log.info("doSendReceiptToSenderClients clientId {}", clientId);
    //             int messageId = mqttMessageIdService.getNextMessageId();
    //             MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
    //                     new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, false, 0),
    //                     new MqttPublishVariableHeader(topic, messageId),
    //                     Unpooled.buffer().writeBytes(messageByteArray));
    //             // 只会发送给当前在同一台服务器上的登录客户端
    //             MqttSession mqttSession = mqttSessionStoreService.get(clientId);
    //             if (mqttSession != null) {
    //                 Channel channel = mqttSession.getChannel();
    //                 if (channel != null) {
    //                     channel.writeAndFlush(publishMessage);
    //                 } else {
    //                     log.error("FIXME: channel is null");
    //                     // 发送给mq
    //                     // messageService.sendProtobufBytesToMq(messageByteArray);
    //                 }
    //             } else {
    //                 log.error("FIXME: mqttSession is null");
    //                 // 发送给mq
    //                 // messageService.sendProtobufBytesToMq(messageByteArray);
    //             }
    //             // mqttSessionStoreService.get(clientId).getChannel().writeAndFlush(publishMessage);
    //         } else if (!StringUtils.hasLength(clientId)) {
    //             log.error("FIXME: doSendReceiptToSenderClients clientId {}", clientId);
    //             // 发送给mq
    //             // messageService.sendProtobufBytesToMq(messageByteArray);
    //         } else {
    //             log.error("FIXME: mqttSessionStoreService is null");
    //             // 发送给mq
    //             // messageService.sendProtobufBytesToMq(messageByteArray);
    //         }
    //     });
    // }

}
