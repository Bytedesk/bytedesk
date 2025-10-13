/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 12:26:26
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

import com.bytedesk.core.socket.mqtt.MqttChannelUtils;
import com.bytedesk.core.socket.mqtt.MqttUtils;
import com.bytedesk.core.socket.mqtt.service.MqttEventPublisher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class Subscribe {

    private final MqttEventPublisher mqService;

    public void processSubscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage) {
        // log.debug("processSubscribe {}", mqttSubscribeMessage.toString());
        //
        List<MqttTopicSubscription> topicSubscriptions = mqttSubscribeMessage.payload().topicSubscriptions();
        if (MqttUtils.validTopicFilter(topicSubscriptions)) {

            // TODO: 增加权限验证，某些用户不允许订阅某些topic，如：用户唯一uid主题topic，只有用户本人可以订阅
            // The new spec changes that and added a new error (0x80) in the MQTT SUBACK
            // message, so clients can react on forbidden subscriptions.
            //
            String clientId = MqttChannelUtils.getClientId(channel);
            log.info("subscribe clientId {}", clientId);
            // 用户clientId格式: uid/client
            // final String uid = clientId.split("/")[0];
            //
            List<Integer> mqttQoSList = new ArrayList<>();
            topicSubscriptions.forEach(topicSubscription -> {

                String topicFilter = topicSubscription.topicFilter(); // topicSubscription.topicName();
                MqttQoS mqttQoS = topicSubscription.qualityOfService();
                // MqttSubscribe subscribeStore = new MqttSubscribe(clientId, topicFilter, mqttQoS.value());
                //
                // mqttSubscribeStoreService.put(topicFilter, subscribeStore);
                // topicService.subscribe(topicFilter, clientId);
                mqService.publishMqttSubscribeEvent(topicFilter, clientId);
                //
                mqttQoSList.add(mqttQoS.value());
                // 添加缓存
                // redisUserService.addTopic(uid, topicFilter);
                log.debug("SUBSCRIBE - clientId: {}, topFilter: {}, QoS: {}", clientId, topicFilter, mqttQoS.value());
            });

            // 回复 SUBACK 消息给当前客户端
            MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(mqttSubscribeMessage.variableHeader().messageId()),
                    new MqttSubAckPayload(mqttQoSList));
            channel.writeAndFlush(subAckMessage);

            // 发布当前topic的retain消息给当前客户端
            // topicSubscriptions.forEach(topicSubscription -> {
            // String topicFilter = topicSubscription.topicName();
            // MqttQoS mqttQoS = topicSubscription.qualityOfService();
            // this.sendRetainMessage(channel, topicFilter, mqttQoS);
            // });

        } else {
            channel.close();
        }
    }


}
