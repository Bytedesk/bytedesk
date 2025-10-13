/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-21 08:10:28
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
import io.netty.handler.codec.mqtt.MqttMessage;

import com.bytedesk.core.socket.mqtt.MqttChannelUtils;
import com.bytedesk.core.socket.mqtt.event.MqttEventPublisher;
import com.bytedesk.core.socket.mqtt.service.MqttSessionService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DisConnect {

    private final MqttSessionService mqttSessionStoreService;

    private final MqttEventPublisher mqService;

    public void processDisConnect(final Channel channel, final MqttMessage mqttMessage) {
        // log.debug("processDisConnect {}", mqttMessage.toString());
        String clientId = MqttChannelUtils.getClientId(channel);
        log.info("processDisConnect {}", clientId);
        mqttSessionStoreService.remove(clientId);
        // 更新离线状态
        mqService.publishMqttDisconnectedEvent(clientId);
        // 关闭连接
        channel.close();
    }


}
