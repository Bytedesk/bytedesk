/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 12:25:42
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

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class PubAck {

    public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        //
        String clientId = MqttChannelUtils.getClientId(channel);
        int messageId = variableHeader.messageId();
        
        // 验证messageId的有效性
        if (messageId <= 0 || messageId > 65535) {
            log.warn("Invalid message ID in PUBACK: {}, ignoring", messageId);
            return;
        }
        
        log.debug("PUBACK - clientId: {}, messageId: {}", clientId, messageId);
        //
        // mqttDupPublishMessageStoreService.remove(clientId, messageId);
    }
}
