/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-24 08:22:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.server;

// import io.netty.bootstrap.ServerBootstrap;
// import io.netty.channel.Channel;
// import io.netty.channel.ChannelOption;
// import io.netty.channel.EventLoopGroup;
// import io.netty.channel.nio.NioEventLoopGroup;
// import io.netty.channel.socket.nio.NioServerSocketChannel;
// import io.netty.util.ResourceLeakDetector;
// import jakarta.annotation.PostConstruct;
// import jakarta.annotation.PreDestroy;

// import com.bytedesk.core.socket.mqtt.MqttProperties;
// import com.bytedesk.core.socket.mqtt.initializer.MqttServerInitializer;
// import com.bytedesk.core.socket.mqtt.protocol.ProtocolProcess;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// /**
//  * current version based on 3.1.1
//  * 当前版本基于 mqtt3.1.1
//  * 
//  * @see https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html
//  * @see https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html
//  * 
//  *      encrypt/security through nginx proxy
//  * 
//  * @author jackning
//  */
// @Slf4j
// @Component
// public class MqttServer {

//     @Autowired
//     private MqttProperties mqttProperties;

//     @Autowired
//     private ProtocolProcess protocolProcess;

//     private Channel serverChannel;

//     private EventLoopGroup parentEventLoopGroup;

//     private EventLoopGroup childEventLoopGroup;

//     @PostConstruct
//     public void init() throws Exception {

//         ResourceLeakDetector
//                 .setLevel(ResourceLeakDetector.Level.valueOf(mqttProperties.getLeakDetectorLevel().toUpperCase()));

//         parentEventLoopGroup = new NioEventLoopGroup(mqttProperties.getParentEventLoopGroupThreadCount());

//         childEventLoopGroup = new NioEventLoopGroup(mqttProperties.getChildEventLoopGroupThreadCount());

//         ServerBootstrap serverBootstrap = new ServerBootstrap();

//         serverBootstrap.group(parentEventLoopGroup, childEventLoopGroup)
//                 //
//                 .channel(NioServerSocketChannel.class)
//                 // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
//                 // 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
//                 .option(ChannelOption.SO_BACKLOG, 1024)
//                 // .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                 // 当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
//                 .childOption(ChannelOption.SO_KEEPALIVE, true)
//                 // Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次,因此在数据包不足的时候会等待其他数据的到了，
//                 // 组装成大的数据包进行发送，虽然该方式有效提高网络的有效
//                 // 参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
//                 .childOption(ChannelOption.TCP_NODELAY, true)
//                 // .childOption(ChannelOption.SO_REUSEADDR, true)
//                 // .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                 // 打印log, TODO: 生产环境删除
//                 // .handler(new LoggingHandler(LogLevel.WARN))
//                 .childHandler(new MqttServerInitializer(null, protocolProcess, mqttProperties.getMaxPayloadSize()));

//         serverChannel = serverBootstrap.bind(mqttProperties.getHost(), mqttProperties.getPort()).sync().channel();

//         log.debug("Mqtt transport started! {}:{}", mqttProperties.getHost(), mqttProperties.getPort());
//     }

//     @PreDestroy
//     public void shutdown() throws InterruptedException {
//         try {
//             serverChannel.close().sync();
//         } finally {
//             childEventLoopGroup.shutdownGracefully();
//             parentEventLoopGroup.shutdownGracefully();
//         }
//     }
// }
