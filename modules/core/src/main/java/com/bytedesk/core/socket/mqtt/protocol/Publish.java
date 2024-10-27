/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 23:02:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

import com.bytedesk.core.message.IMessageSendService;
// import com.bytedesk.core.socket.MqService;
import com.bytedesk.core.socket.mqtt.util.ChannelUtils;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.utils.MessageConvertUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@AllArgsConstructor
public class Publish {

    // private final MqService mqService;
    private final IMessageSendService messageSendService;

    //
    public void processPublish(Channel channel, MqttPublishMessage mqttPublishMessage) {
        // log.debug("processPublish {}", mqttPublishMessage.toString());
        // TODO: 发送：消息发送成功回执
        // String clientId = (String)
        // channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
        byte[] messageBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        this.sendMqMessage(mqttPublishMessage, messageBytes);
        //
        // QoS=0
        if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            // this.sendMqMessage(clientId, mqttPublishMessage, messageBytes);
        }
        // QoS=1
        else if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            ChannelUtils.sendPubAckMessage(channel, mqttPublishMessage.variableHeader().packetId());
        }
        // QoS=2
        else if (mqttPublishMessage.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            ChannelUtils.sendPubRecMessage(channel, mqttPublishMessage.variableHeader().packetId());
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
        // mqService.sendProtoMessageToMq(messageBytes);
        try {
            MessageProto.Message messageProto = MessageProto.Message.parseFrom(messageBytes);
            String messageJson = MessageConvertUtils.toJson(messageProto);
            // mqService.sendJsonMessageToMq(messageJson);
            messageSendService.sendJsonMessage(messageJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
