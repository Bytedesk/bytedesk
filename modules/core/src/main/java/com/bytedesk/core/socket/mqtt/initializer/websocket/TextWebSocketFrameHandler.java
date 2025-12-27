/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-24 13:05:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.initializer.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles handshakes and messages
 */
@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelRead0(ChannelHandlerContext context, TextWebSocketFrame frame) {
        // 针对mqtt, 收到text，认为非法数据，需要关闭连接
        // final Channel channel = context.channel();
        // channel.disconnect();
        // if (log.isDebugEnabled()) {
        // log.debug("Sending websocket text frames is illegal, only binary frames are
        // allowed for MQTT over websockets. ");
        // }
        // 针对普通websocket，接收并解析
        // log.debug("text message received length: [{}]", frame.content().capacity());
        // log.debug("decode text content {}", frame.text());
        // 传递给下一个handler
        // context.fireChannelRead(frame.content().retain());
        // 原样返回给发送者
        // context.write(frame.retain());
    }

    // @Override
    // public void channelReadComplete(ChannelHandlerContext ctx) {
    // log.debug("text flush");
    // ctx.flush();
    // }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // 添加
        // log.debug("text channelActive [ {} ]", ctx.channel().id().asLongText());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // 移除
        // log.debug("text channelInactive [ {} ] ", ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("TextWebSocketFrameHandler exceptionCaught", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // log.debug("text userEventTriggered");
        //
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // log.debug("text web socket 握手成功。");
            // //
            // WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete =
            // (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            // String requestUri = handshakeComplete.requestUri();
            // log.debug("text requestUri:[{}]", requestUri);
            // //
            // String subproTocol = handshakeComplete.selectedSubprotocol();
            // log.debug("text subproTocol:[{}]", subproTocol);
            // //
            // handshakeComplete.requestHeaders().forEach(entry -> log.debug("text header
            // key:[{}] value:[{}]", entry.getKey(), entry.getValue()));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
