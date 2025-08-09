/*
 * @Author: GitHub Copilot
 * @Date: 2025-06-03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import org.freeswitch.esl.client.inbound.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch默认配置
 * 当FreeSwitch未启用时，提供一个空的Client实现
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "false", matchIfMissing = true)
public class FreeSwitchDefaultConfig {

    /**
     * 提供一个默认的ESL客户端，仅用于满足依赖注入需求
     */
    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client defaultEslClient() {
        log.info("创建FreeSwitch默认Client实例（未连接）");
        return new Client();
    }
}
