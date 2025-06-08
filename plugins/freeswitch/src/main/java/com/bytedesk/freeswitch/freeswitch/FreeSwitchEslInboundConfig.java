/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-05 20:19:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 13:10:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.freeswitch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import link.thingscloud.freeswitch.esl.IEslEventListener;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.ServerConnectionListener;
import link.thingscloud.freeswitch.esl.inbound.option.InboundClientOption;
import link.thingscloud.freeswitch.esl.inbound.option.ServerOption;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用 thingscloud 库的 FreeSwitch ESL 客户端配置
 * 
 * 参考链接：https://github.com/zhouhailin/freeswitch-externals
 * 
 * @author jackning
 * @version 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ComponentScan("com.bytedesk.freeswitch")
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchEslInboundConfig {

    private final FreeSwitchProperties freeSwitchProperties;

    /**
     * 配置基于 thingscloud 的 FreeSwitch ESL 客户端
     */
    @Bean
    public InboundClient thingsCloudEslClient() {
        InboundClientOption option = new InboundClientOption();

        log.info("配置 thingscloud ESL 客户端: {}:{}", 
                freeSwitchProperties.getServer(), 
                freeSwitchProperties.getEslPort());

        // 配置服务器连接信息
        option.defaultPassword(freeSwitchProperties.getEslPassword())
                .addServerOption(new ServerOption(
                    freeSwitchProperties.getServer(), 
                    freeSwitchProperties.getEslPort()
                ));
        
        // 订阅所有事件
        option.addEvents("all");

        // 添加事件监听器
        option.addListener(new IEslEventListener() {
            @Override
            public void eventReceived(String addr, EslEvent event) {
                log.debug("收到事件来自 {}: {}", addr, event.getEventName());
                // 在这里可以添加具体的事件处理逻辑
                handleEvent(event);
            }

            @Override
            public void backgroundJobResultReceived(String addr, EslEvent event) {
                log.debug("收到后台任务结果来自 {}: {}", addr, event);
            }
        });

        // 添加服务器连接监听器
        option.serverConnectionListener(new ServerConnectionListener() {
            @Override
            public void onOpened(ServerOption serverOption) {
                log.info("thingscloud ESL 连接已打开: {}:{}", 
                        serverOption.host(), serverOption.port());
            }

            @Override
            public void onClosed(ServerOption serverOption) {
                log.warn("thingscloud ESL 连接已关闭: {}:{}", 
                        serverOption.host(), serverOption.port());
            }
        });

        // 创建并启动客户端
        InboundClient inboundClient = InboundClient.newInstance(option);
        
        try {
            inboundClient.start();
            log.info("thingscloud ESL 客户端启动成功");
        } catch (Exception e) {
            log.error("thingscloud ESL 客户端启动失败: {}", e.getMessage(), e);
        }

        return inboundClient;
    }

    /**
     * 处理接收到的事件
     */
    private void handleEvent(EslEvent event) {
        String eventName = event.getEventName();
        
        switch(eventName) {
            case "CHANNEL_CREATE":
                handleChannelCreate(event);
                break;
                
            case "CHANNEL_ANSWER":
                handleChannelAnswer(event);
                break;
                
            case "CHANNEL_HANGUP":
                handleChannelHangup(event);
                break;
                
            case "DTMF":
                handleDtmf(event);
                break;
                
            default:
                log.trace("未处理的事件类型: {}", eventName);
                break;
        }
    }

    /**
     * 处理通道创建事件
     */
    private void handleChannelCreate(EslEvent event) {
        String uuid = event.getEventHeaders().get("Unique-ID");
        String callerId = event.getEventHeaders().get("Caller-Caller-ID-Number");
        String destination = event.getEventHeaders().get("Caller-Destination-Number");
        
        log.info("thingscloud - 通道创建: UUID {} 主叫 {} 被叫 {}", uuid, callerId, destination);
    }

    /**
     * 处理通道应答事件
     */
    private void handleChannelAnswer(EslEvent event) {
        String uuid = event.getEventHeaders().get("Unique-ID");
        
        log.info("thingscloud - 通道应答: UUID {}", uuid);
    }

    /**
     * 处理通道挂断事件
     */
    private void handleChannelHangup(EslEvent event) {
        String uuid = event.getEventHeaders().get("Unique-ID");
        String hangupCause = event.getEventHeaders().get("Hangup-Cause");
        
        log.info("thingscloud - 通道挂断: UUID {} 原因 {}", uuid, hangupCause);
    }

    /**
     * 处理DTMF按键事件
     */
    private void handleDtmf(EslEvent event) {
        String uuid = event.getEventHeaders().get("Unique-ID");
        String digit = event.getEventHeaders().get("DTMF-Digit");
        
        log.info("thingscloud - DTMF按键: UUID {} 键值 {}", uuid, digit);
    }
}
