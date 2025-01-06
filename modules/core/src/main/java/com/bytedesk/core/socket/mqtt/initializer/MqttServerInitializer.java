/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-29 12:25:27
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
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.socket.mqtt.handler.MqttTransportHandler;
import com.bytedesk.core.socket.mqtt.protocol.ProtocolProcess;

/**
 * @author bytedesk.com on 2019-07-05
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class MqttServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext mSslCtx;

    private final ProtocolProcess mProtocolProcess;

    private final int mMaxPayloadSize;

    // public MqttServerInitializer(SslContext sslContext, ProtocolProcess
    // protocolProcess, int maxPayloadSize) {
    // this.mSslCtx = sslContext;
    // this.mProtocolProcess = protocolProcess;
    // this.mMaxPayloadSize = maxPayloadSize;
    // }

    /**
     * TODO: ChannelInitializer中应该使用 NioSocketChannel or SocketChannel ？优缺点？
     *
     * @param socketChannel channel
     * @throws Exception expt
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        log.debug("MqttServerInitializer initChannel");

        ChannelPipeline pipeline = socketChannel.pipeline();

        // // Add SSL handler first to encrypt and decrypt everything.
        // SelfSignedCertificate cert = new SelfSignedCertificate();
        // SslContext context = SslContext.newServerContext(cert.certificate(),
        // cert.privateKey());
        // SSLEngine engine = context.newEngine(socketChannel.alloc());
        // //将 SslHandler 添加到ChannelPipeline 中以使用 HTTPS
        // pipeline.addFirst("ssl", new SslHandler(engine));

        if (mSslCtx != null) {
            pipeline.addLast(mSslCtx.newHandler(socketChannel.alloc()));
        }

        //
        pipeline.addLast(new MqttDecoder(mMaxPayloadSize));
        pipeline.addLast(MqttEncoder.INSTANCE);
        //
        pipeline.addLast(new MqttTransportHandler(mProtocolProcess));
    }

}
