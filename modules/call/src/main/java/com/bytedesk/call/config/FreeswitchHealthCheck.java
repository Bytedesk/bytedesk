package com.bytedesk.call.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.Socket;

@Component
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeswitchHealthCheck implements HealthIndicator {

    @Value("${bytedesk.freeswitch.server}")
    private String freeswitchServer;

    @Value("${bytedesk.freeswitch.esl-port}")
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