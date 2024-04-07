/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-29 13:03:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 检测空闲连接和超时是为了及时释放资源: 在规定时间内没有收到来自客户端的任何packet，包括ping
 *
 * 常用的方法：
 * 1. 发送消息用于测试一个不活跃的连接（俗称'心跳'）到远程断点用来确定其是否还活着。
 * 2. 另一种比较粗暴的方法是直接断开那些指定时间间隔内不活跃的连接
 */
// @Slf4j
public class MqttIdleStateHandler extends IdleStateHandler {

    public MqttIdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        super.channelIdle(ctx, evt);
        // String clientId = (String)
        // ctx.channel().attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
        // log.debug("client {} is idle, close now {}", clientId);
        // 直接关掉空闲连接
        ctx.channel().close();
    }
}
