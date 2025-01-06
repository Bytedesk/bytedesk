/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 16:11:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.handler.packet;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bytedesk.com on 2019-07-05
 */
@Slf4j
@ChannelHandler.Sharable
public class MqttUnsubscribeHandler extends SimpleChannelInboundHandler<MqttUnsubscribeMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
            MqttUnsubscribeMessage mqttUnsubscribeMessage) throws Exception {
        log.info("mqttUnsubscribeMessage: ", mqttUnsubscribeMessage.toString());
    }

}
