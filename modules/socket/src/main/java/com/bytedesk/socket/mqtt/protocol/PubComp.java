/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-29 10:58:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import com.bytedesk.socket.mqtt.service.MqttDupPubRelMessageStoreService;
import com.bytedesk.socket.mqtt.util.ChannelUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PUBCOMP连接处理
 *
 * @author jackning
 */
@Slf4j
@AllArgsConstructor
public class PubComp {

    private final MqttDupPubRelMessageStoreService mqttDupPubRelMessageStoreService;

    public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        String clientId = ChannelUtils.getClientId(channel);
        int messageId = variableHeader.messageId();
        log.debug("PUBCOMP - clientId: {}, messageId: {}", clientId, messageId);
        mqttDupPubRelMessageStoreService.remove(clientId, messageId);
    }
}
