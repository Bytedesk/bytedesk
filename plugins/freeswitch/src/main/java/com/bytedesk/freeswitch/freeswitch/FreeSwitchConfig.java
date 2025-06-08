/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:14:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 13:42:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.freeswitch;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@RequiredArgsConstructor
@ComponentScan("com.bytedesk.freeswitch")
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchConfig {
        
    private final FreeSwitchProperties freeSwitchProperties;
    
    private final FreeSwitchEventListener freeSwitchEventListener;

    /**
     * 配置FreeSwitch ESL客户端
     */
    @Bean
    public Client eslClient() {
        Client client = new Client();
        
        try {
            log.info("连接FreeSwitch ESL: {}:{}", 
                    freeSwitchProperties.getServer(), 
                    freeSwitchProperties.getEslPort());
                    
            client.connect(
                freeSwitchProperties.getServer(), 
                freeSwitchProperties.getEslPort(), 
                freeSwitchProperties.getEslPassword(),
                2);
                
            // 注册事件监听器
            client.addEventListener(freeSwitchEventListener);
            
            // 订阅所有事件
            client.setEventSubscriptions("plain", "all");

            //这里必须检查，防止网络抖动时，连接断开
            if (client.canSend()) {
                log.info("连接成功，准备发起呼叫...");
                //（异步）向1000用户发起呼叫，用户接通后，播放音乐/tmp/demo1.wav
                String callResult = client.sendAsyncApiCommand("originate", "user/1000 &playback(/tmp/demo.wav)");
                log.info("api uuid:" + callResult);
            }
            
            log.info("FreeSwitch ESL连接成功");
        } catch (InboundConnectionFailure e) {
            // 处理连接失败的情况
            e.printStackTrace();
            log.error("FreeSwitch ESL连接失败: {}, {}", e.getMessage(), e);
        }
        
        return client;
    }
}
