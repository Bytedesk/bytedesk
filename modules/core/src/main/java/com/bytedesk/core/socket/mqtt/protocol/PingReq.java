/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 15:40:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */

package com.bytedesk.core.socket.mqtt.protocol;

import com.bytedesk.core.socket.mqtt.MqttChannelUtils;
import com.bytedesk.core.socket.mqtt.service.MqttConnectionService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@AllArgsConstructor
public class PingReq {

    private final MqttConnectionService mqttConnectionService;

    // client send ping every 30 seconds
    public void processPingReq(Channel channel, MqttMessage message) {
        //
        String clientId = MqttChannelUtils.getClientId(channel);
        // log.info("PINGREQ - clientId: {}", clientId);
        mqttConnectionService.addConnected(clientId);

        // send ping response PINGRESP
        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                null,
                null);
        // PINGREQ - clientId: MQTT_FX_Client
        channel.writeAndFlush(pingRespMessage);
    }

}
