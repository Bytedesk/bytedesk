/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-05 20:19:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 18:26:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import link.thingscloud.freeswitch.esl.IEslEventListener;
// import link.thingscloud.freeswitch.esl.InboundClient;
// import link.thingscloud.freeswitch.esl.ServerConnectionListener;
// import link.thingscloud.freeswitch.esl.inbound.option.InboundClientOption;
// import link.thingscloud.freeswitch.esl.inbound.option.ServerOption;
// import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * 使用 thingscloud 库的 Call ESL 客户端配置
//  * 
//  * 参考链接：https://github.com/zhouhailin/freeswitch-externals
//  * 
//  * @author jackning
//  * @version 1.0.0
//  */
// @Slf4j
// @Configuration
// @RequiredArgsConstructor
// @ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
// public class CallEslInboundConfig {

//     private final CallFreeswitchProperties callFreeswitchProperties;

//     /**
//      * 配置基于 thingscloud 的 Call ESL 客户端
//      */
//     @Bean
//     public InboundClient thingsCloudEslClient() {
//         InboundClientOption option = new InboundClientOption();

//         log.info("配置 freeswitch ESL 客户端: {}:{}", 
//                 callFreeswitchProperties.getServer(), 
//                 callFreeswitchProperties.getEslPort());

//         // 配置服务器连接信息
//         option.defaultPassword(callFreeswitchProperties.getEslPassword())
//                 .addServerOption(new ServerOption(
//                     callFreeswitchProperties.getServer(), 
//                     callFreeswitchProperties.getEslPort()
//                 ));
        
//         // 订阅所有事件
//         option.addEvents("all");

//         // 添加事件监听器
//         option.addListener(new IEslEventListener() {
//             @Override
//             public void eventReceived(String addr, EslEvent event) {
//                 log.debug("freeswitch ESL 收到事件来自 {}: {}", addr, event.getEventName());
//                 // 在这里可以添加具体的事件处理逻辑
//                 handleEvent(event);
//             }

//             @Override
//             public void backgroundJobResultReceived(String addr, EslEvent event) {
//                 log.debug("freeswitch ESL 收到后台任务结果来自 {}: {}", addr, event);
//             }
//         });

//         // 添加服务器连接监听器
//         option.serverConnectionListener(new ServerConnectionListener() {
//             @Override
//             public void onOpened(ServerOption serverOption) {
//                 log.info("freeswitch ESL 连接已打开: {}:{}", 
//                         serverOption.host(), serverOption.port());
//             }

//             @Override
//             public void onClosed(ServerOption serverOption) {
//                 log.warn("freeswitch ESL 连接已关闭: {}:{}", 
//                         serverOption.host(), serverOption.port());
//             }
//         });

//         // 创建并启动客户端
//         InboundClient inboundClient = InboundClient.newInstance(option);
        
//         try {
//             log.info("启动 freeswitch ESL 客户端...");
//             inboundClient.start();
//             log.info("freeswitch ESL 客户端启动成功");
//         } catch (Exception e) {
//             log.error("freeswitch ESL 客户端启动失败: {}", e.getMessage());
            
//             // 分析错误类型并提供具体的解决建议
//             if (e.getMessage() != null) {
//                 if (e.getMessage().contains("Connection refused")) {
//                     log.error("连接被拒绝 - 可能的解决方案:");
//                     log.error("1. 确认Call服务正在运行");
//                     log.error("2. 检查端口{}是否正确并且可访问", callFreeswitchProperties.getEslPort());
//                     log.error("3. 确认防火墙设置允许连接到该端口");
//                 } else if (e.getMessage().contains("Authentication") || e.getMessage().contains("password")) {
//                     log.error("认证失败 - 可能的解决方案:");
//                     log.error("1. 检查ESL密码是否正确: {}", callFreeswitchProperties.getEslPassword());
//                     log.error("2. 确认Call的event_socket.conf.xml配置正确");
//                 } else if (e.getMessage().contains("timeout")) {
//                     log.error("连接超时 - 可能的解决方案:");
//                     log.error("1. 检查网络连接");
//                     log.error("2. 增加连接超时时间");
//                     log.error("3. 确认服务器地址正确: {}", callFreeswitchProperties.getServer());
//                 }
//             }
            
//             // 记录当前配置信息用于调试
//             log.error("freeswitch ESL 当前ESL配置 - 服务器: {}:{}, 密码: {}", 
//                     callFreeswitchProperties.getServer(), 
//                     callFreeswitchProperties.getEslPort(),
//                     callFreeswitchProperties.getEslPassword());
//         }

//         return inboundClient;
//     }

//     /**
//      * 处理接收到的事件
//      */
//     private void handleEvent(EslEvent event) {
//         String eventName = event.getEventName();
//         log.trace("freeswitch ESL 处理事件: {} / {}", eventName, event.getEventHeaders());
        
//         switch(eventName) {
//             case "CHANNEL_CREATE":
//                 handleChannelCreate(event);
//                 break;
                
//             case "CHANNEL_ANSWER":
//                 handleChannelAnswer(event);
//                 break;
                
//             case "CHANNEL_HANGUP":
//                 handleChannelHangup(event);
//                 break;
                
//             case "DTMF":
//                 handleDtmf(event);
//                 break;
                
//             default:
//                 log.trace("未处理的事件类型: {}", eventName);
//                 break;
//         }
//     }

//     /**
//      * 处理通道创建事件
//      */
//     private void handleChannelCreate(EslEvent event) {
//         String uuid = event.getEventHeaders().get("Unique-ID");
//         String callerId = event.getEventHeaders().get("Caller-Caller-ID-Number");
//         String destination = event.getEventHeaders().get("Caller-Destination-Number");
        
//         log.info("freeswitch ESL - 通道创建: UUID {} 主叫 {} 被叫 {}", uuid, callerId, destination);
//     }

//     /**
//      * 处理通道应答事件
//      */
//     private void handleChannelAnswer(EslEvent event) {
//         String uuid = event.getEventHeaders().get("Unique-ID");
        
//         log.info("freeswitch ESL - 通道应答: UUID {}", uuid);
//     }

//     /**
//      * 处理通道挂断事件
//      */
//     private void handleChannelHangup(EslEvent event) {
//         String uuid = event.getEventHeaders().get("Unique-ID");
//         String hangupCause = event.getEventHeaders().get("Hangup-Cause");
        
//         log.info("freeswitch ESL - 通道挂断: UUID {} 原因 {}", uuid, hangupCause);
//     }

//     /**
//      * 处理DTMF按键事件
//      */
//     private void handleDtmf(EslEvent event) {
//         String uuid = event.getEventHeaders().get("Unique-ID");
//         String digit = event.getEventHeaders().get("DTMF-Digit");
        
//         log.info("freeswitch ESL - DTMF按键: UUID {} 键值 {}", uuid, digit);
//     }
// }
