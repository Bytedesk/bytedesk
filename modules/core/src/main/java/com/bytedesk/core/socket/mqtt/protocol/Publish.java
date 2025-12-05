/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 15:54:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.utils.MessageConvertUtils;
import com.bytedesk.core.socket.connection.ConnectionRestService;
import com.bytedesk.core.socket.mqtt.MqttChannelUtils;
import com.bytedesk.core.socket.protobuf.model.MessageProto;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@AllArgsConstructor
public class Publish {

    private final IMessageSendService messageSendService;
    private final ConnectionRestService connectionRestService;

    //
    public void processPublish(Channel channel, MqttPublishMessage mqttPublishMessage) {
        // log.debug("processPublish {}", mqttPublishMessage.toString());
        // TODO: 发送：消息发送成功回执
        // String clientId = (String)
        // channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
        // 心跳增强：每次客户端有实际数据发布，刷新该连接的心跳，避免仅依赖PINGREQ
        String clientId = MqttChannelUtils.getClientId(channel);
        if (clientId != null) {
            try {
                connectionRestService.heartbeat(clientId);
            } catch (Exception ignored) {
                // 保守处理，不影响主消息流
            }
        }
        byte[] messageBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        this.sendMqMessage(mqttPublishMessage, messageBytes);
        //
        // QoS=0
        if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            // QoS=0 消息不需要回执，无需发送PUBACK
            // this.sendMqMessage(clientId, mqttPublishMessage, messageBytes);
        }
        // QoS=1
        else if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            int packetId = mqttPublishMessage.variableHeader().packetId();
            if (packetId > 0 && packetId <= 65535) {
                MqttChannelUtils.sendPubAckMessage(channel, packetId);
            } else {
                // log.warn("Invalid packet ID for QoS=1 message: {}", packetId);
            }
        }
        // QoS=2
        else if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            int packetId = mqttPublishMessage.variableHeader().packetId();
            if (packetId > 0 && packetId <= 65535) {
                MqttChannelUtils.sendPubRecMessage(channel, packetId);
            } else {
                // log.warn("Invalid packet ID for QoS=2 message: {}", packetId);
            }
        }
        // retain=1, 保留消息
        if (mqttPublishMessage.fixedHeader().isRetain()) {
            mqttPublishMessage.payload().getBytes(mqttPublishMessage.payload().readerIndex(), messageBytes);
        }
    }

    // 下列过滤不能从直接数据库中读取，否则会增加数据库压力，影响消息发送速度，务必从内存或redis中读取
    private void sendMqMessage(MqttPublishMessage publishMessage, byte[] messageBytes) {
        // TODO: 发送回执
        // 注意：不能去掉，否则无法解析protobuf
        publishMessage.payload().getBytes(publishMessage.payload().readerIndex(), messageBytes);
        // publish message event, developers can listener to new message
        try {
            if (messageBytes.length == 0) {
                // log.warn("Empty message payload, skipping protobuf parsing");
                return;
            }
            MessageProto.Message messageProto = MessageProto.Message.parseFrom(messageBytes);
            String messageJson = MessageConvertUtils.toJson(messageProto);
            // 
            messageSendService.sendJsonMessage(messageJson);
        } catch (Exception e) {
            // log.warn("Failed to parse protobuf message: {}, message length: {}", e.getMessage(), messageBytes.length);
            // 对于无法解析的消息，我们可以记录但不抛出异常，以免影响整个系统
            // e.printStackTrace();
        }
    }

}
