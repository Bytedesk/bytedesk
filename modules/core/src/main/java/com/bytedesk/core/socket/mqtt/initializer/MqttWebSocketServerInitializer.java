/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-19 16:16:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

import com.bytedesk.core.socket.mqtt.MqttConsts;
import com.bytedesk.core.socket.mqtt.handler.MqttTransportHandler;
import com.bytedesk.core.socket.mqtt.initializer.websocket.BinaryWebSocketFrameHandler;
import com.bytedesk.core.socket.mqtt.initializer.websocket.ByteBufToWebSocketFrameEncoder;
import com.bytedesk.core.socket.mqtt.initializer.websocket.ContinuationWebSocketFrameHandler;
import com.bytedesk.core.socket.mqtt.initializer.websocket.TextWebSocketFrameHandler;
import com.bytedesk.core.socket.mqtt.protocol.ProtocolProcess;

/**
 * @author bytedesk.com on 2019-07-05
 */
public class MqttWebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext mSslCtx;

    private final int mMaxPayloadSize;

    private final ProtocolProcess mProtocolProcess;

    public MqttWebSocketServerInitializer(SslContext sslCtx, ProtocolProcess protocolProcess, int maxPayloadSize) {
        this.mSslCtx = sslCtx;
        this.mProtocolProcess = protocolProcess;
        this.mMaxPayloadSize = maxPayloadSize;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (mSslCtx != null) {
            pipeline.addLast(mSslCtx.newHandler(socketChannel.alloc()));
        }
        // HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码
        pipeline.addLast(new HttpServerCodec());
        // 将HttpMessage和HttpContents聚合到一个完成的
        // FullHttpRequest或FullHttpResponse中,具体是FullHttpRequest对象还是FullHttpResponse对象取决于是请求还是响应
        // 需要放到HttpServerCodec这个处理器后面
        pipeline.addLast(new HttpObjectAggregator(65536));
        // 分块向客户端写数据，防止发送大文件时导致内存溢出
        pipeline.addLast(new ChunkedWriteHandler());
        // 自定义http handler，加载.html文件
        // pipeline.addLast(new HttpRequestHandler());
        // webSocket 数据压缩扩展，当添加这个的时候WebSocketServerProtocolHandler的第三个参数需要设置成true
        // maxAllocation 参数为解压缩缓冲区的最大大小，0 表示不限制
        pipeline.addLast(new WebSocketServerCompressionHandler(0));
        // 聚合 websocket 的数据帧，因为客户端可能分段向服务器端发送数据
        // https://github.com/netty/netty/issues/1112
        // https://github.com/netty/netty/pull/1207
        pipeline.addLast(new WebSocketFrameAggregator(10 * 1024 * 1024));
        // 处理close/ping/pong协议
        // 服务器端向外暴露的 web socket 端点，当客户端传递比较大的对象时，maxFrameSize参数的值需要调大
        pipeline.addLast(
                new WebSocketServerProtocolHandler(MqttConsts.WEBSOCKET_PATH, "mqtt", true, Integer.MAX_VALUE));
        // 自定义处理binary协议
        pipeline.addLast(new BinaryWebSocketFrameHandler());
        //
        pipeline.addLast(new ContinuationWebSocketFrameHandler());
        // 自定义处理text协议
        pipeline.addLast(new TextWebSocketFrameHandler());
        //
        pipeline.addLast(new ByteBufToWebSocketFrameEncoder());
        //
        pipeline.addLast(new MqttDecoder(mMaxPayloadSize));
        pipeline.addLast(MqttEncoder.INSTANCE);
        //
        pipeline.addLast(new MqttTransportHandler(mProtocolProcess));
    }

}
