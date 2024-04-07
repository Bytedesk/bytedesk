/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-13 22:50:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import com.bytedesk.socket.mqtt.config.MqttProperties;
import com.bytedesk.socket.mqtt.initializer.MqttWebSocketServerInitializer;
import com.bytedesk.socket.mqtt.protocol.ProtocolProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * encrypt/security through nginx proxy
 * 
 * @author bytedesk.com on 2019-07-23
 */
@Slf4j
@Component
public class MqttWebSocketServer {

    @Autowired
    private MqttProperties mqttProperties;

    @Autowired
    private ProtocolProcess protocolProcess;

    private Channel serverChannel;

    private EventLoopGroup parentEventLoopGroup;

    private EventLoopGroup childEventLoopGroup;

    @PostConstruct
    public void init() throws Exception {

        ResourceLeakDetector
                .setLevel(ResourceLeakDetector.Level.valueOf(mqttProperties.getLeakDetectorLevel().toUpperCase()));

        parentEventLoopGroup = new NioEventLoopGroup(mqttProperties.getParentEventLoopGroupThreadCount());

        childEventLoopGroup = new NioEventLoopGroup(mqttProperties.getChildEventLoopGroupThreadCount());

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(parentEventLoopGroup, childEventLoopGroup)
                //
                .channel(NioServerSocketChannel.class)
                // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
                // 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // 当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次,因此在数据包不足的时候会等待其他数据的到了，
                // 组装成大的数据包进行发送，虽然该方式有效提高网络的有效
                // 参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 打印log，TODO: 生产环境删除
                // .handler(new LoggingHandler(LogLevel.WARN))
                //
                .childHandler(
                        new MqttWebSocketServerInitializer(null, protocolProcess, mqttProperties.getMaxPayloadSize()));

        serverChannel = serverBootstrap.bind(mqttProperties.getHost(), mqttProperties.getWebsocketPort()).sync()
                .channel();

        log.debug("Mqtt websocket transport started! {}:{}", mqttProperties.getHost(),
                mqttProperties.getWebsocketPort());
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        try {
            serverChannel.close().sync();
        } finally {
            childEventLoopGroup.shutdownGracefully();
            parentEventLoopGroup.shutdownGracefully();
        }
    }

}
