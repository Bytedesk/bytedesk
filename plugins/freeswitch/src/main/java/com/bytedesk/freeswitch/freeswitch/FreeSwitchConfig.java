/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:14:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-07 07:13:25
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
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
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
            
            log.info("FreeSwitch ESL连接成功");
        } catch (InboundConnectionFailure e) {
            log.error("FreeSwitch ESL连接失败: {}", e.getMessage(), e);
        }
        
        return client;
    }
}
