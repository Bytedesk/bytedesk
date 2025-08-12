/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 07:24:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 08:58:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.Socket;

@Component
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CallHealthCheck implements HealthIndicator {

    @Value("${bytedesk.call.freeswitch.server}")
    private String freeswitchServer;

    @Value("${bytedesk.call.freeswitch.esl-port}")
    private int eslPort;

    @Override
    public Health health() {
        try {
            // 检查 ESL 端口连接
            try (Socket socket = new Socket(freeswitchServer, eslPort)) {
                socket.setSoTimeout(5000);
                return Health.up()
                    .withDetail("freeswitch-server", freeswitchServer)
                    .withDetail("esl-port", eslPort)
                    .withDetail("status", "Connected")
                    .build();
            }
        } catch (IOException e) {
            return Health.down()
                .withDetail("freeswitch-server", freeswitchServer)
                .withDetail("esl-port", eslPort)
                .withDetail("error", e.getMessage())
                .withDetail("status", "Connection Failed")
                .build();
        }
    }
}