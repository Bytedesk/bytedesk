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

import com.bytedesk.call.esl.client.inbound.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Call默认配置
 * 当Call未启用时，提供一个空的Client实现
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "false", matchIfMissing = true)
public class CallDefaultConfig {

    /**
     * 提供一个默认的ESL客户端，仅用于满足依赖注入需求
     */
    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client defaultEslClient() {
        log.info("创建Call默认Client实例（未连接）");
        return new Client();
    }
}
