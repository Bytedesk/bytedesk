/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 12:26:50
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
import com.bytedesk.core.socket.mqtt.event.MqttEventPublisher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class UnSubscribe {

    private final MqttEventPublisher mqttEventPublisher;

    public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage mqttUnsubscribeMessage) {
        // log.debug("processUnSubscribe {}", mqttUnsubscribeMessage.toString());
        //
        List<String> topicFilters = mqttUnsubscribeMessage.payload().topics();
        String clientId = MqttChannelUtils.getClientId(channel);
        log.info("unsubscribe clientId {}", clientId);
        // 用户clientId格式: uid/client
        // final String uid = clientId.split("/")[0];
        topicFilters.forEach(topicFilter -> {
            //
            // mqttSubscribeStoreService.remove(topicFilter, clientId);
            // topicService.unsubscribe(topicFilter, clientId);
            mqttEventPublisher.publishMqttUnsubscribeEvent(topicFilter, clientId);
            // 移除缓存
            // redisUserService.removeTopic(uid, topicFilter);
            log.debug("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clientId, topicFilter);
        });
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(mqttUnsubscribeMessage.variableHeader().messageId()),
                null);
        channel.writeAndFlush(unsubAckMessage);
    }
}
