/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-15 16:30:27
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
import io.netty.handler.codec.mqtt.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class PubRec {

        // private final MqttDupPublishMessageStoreService
        // mqttDupPublishMessageStoreService;

        // private final MqttDupPubRelMessageStoreService
        // mqttDupPubRelMessageStoreService;

        public void processPubRec(Channel channel, MqttMessageIdVariableHeader variableHeader) {
                //
                int messageId = variableHeader.messageId();
                String clientId = MqttChannelUtils.getClientId(channel);
                log.debug("PUBREC - clientId: {}, messageId: {}", clientId, messageId);
                
                // 验证messageId的有效性
                if (messageId <= 0 || messageId > 65535) {
                    log.warn("Invalid message ID in PUBREC: {}, ignoring", messageId);
                    return;
                }
                
                //
                // mqttDupPublishMessageStoreService.remove(clientId, messageId);
                //
                // MqttDupPubRelMessage dupPubRelMessageStore = new
                // MqttDupPubRelMessage().setClientId(clientId).setMessageId(messageId);
                // mqttDupPubRelMessageStoreService.put(clientId, dupPubRelMessageStore);
                //
                MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                                new MqttFixedHeader(MqttMessageType.PUBREL,
                                                false,
                                                MqttQoS.AT_MOST_ONCE,
                                                false,
                                                0),
                                MqttMessageIdVariableHeader.from(
                                                messageId),
                                null);
                channel.writeAndFlush(pubRelMessage);
        }
}
