/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.bytedesk.core.socket.mqtt.initializer.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles handshakes and messages
 */
@Slf4j
public class BinaryWebSocketFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Override
    public void channelRead0(ChannelHandlerContext context, BinaryWebSocketFrame frame) throws IOException {
        // log.debug("binary message received");
        // 传递给下一个handler
        context.fireChannelRead(frame.content().retain());
        //
        // log.debug("binary message received length: [{}]",
        // frame.content().capacity());
        // ByteBuf byteBuf = Unpooled.directBuffer(frame.content().capacity());
        // byteBuf.writeBytes(frame.content());
        // String contentString = new String(ByteBufUtil.getBytes(byteBuf),
        // CharsetUtil.UTF_8);
        // // FIXME: 数据没有输出？
        // log.debug("decode binary content {}", contentString);

        // 转成byte
        // byte [] bytes = new byte[frame.content().capacity()];
        // byteBuf.readBytes(bytes);
        // log.debug("decode binary content {}", String.valueOf(bytes));

        // 原样返回给发送者
        // context.write(frame.retain());
    }

    // @Override
    // public void channelReadComplete(ChannelHandlerContext ctx) {
    // log.debug("binary flush");
    // ctx.flush();
    // }
    //
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // 添加
        // log.debug("binary channelActive [ {} ]", ctx.channel().id().asLongText());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // 移除
        // log.debug("binary channelInactive [ {} ] ", ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("BinaryWebSocketFrameHandler exceptionCaught", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // log.debug("binary userEventTriggered");
        //
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // log.debug("binary web socket 握手成功。");
            //
            // WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete =
            // (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            // String requestUri = handshakeComplete.requestUri();
            // log.debug("binary requestUri:[{}]", requestUri);
            //
            // String subproTocol = handshakeComplete.selectedSubprotocol();
            // log.debug("binary subproTocol:[{}]", subproTocol);
            //
            // handshakeComplete.requestHeaders().forEach(entry -> log.debug("binary header
            // key:[{}] value:[{}]", entry.getKey(), entry.getValue()));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
