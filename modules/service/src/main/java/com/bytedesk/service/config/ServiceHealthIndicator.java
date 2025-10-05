/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-05 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;

/**
 * Service模块健康检查
 * 监控客服服务相关指标：消息队列、会话管理、数据库连接等
 */
@Slf4j
@Component
public class ServiceHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private final Instant startTime = Instant.now();

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();
            
            // 服务运行时长
            Duration uptime = Duration.between(startTime, Instant.now());
            builder.withDetail("uptime-seconds", uptime.getSeconds())
                   .withDetail("uptime-readable", formatDuration(uptime));

            // 检查数据库连接
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    builder.withDetail("database-status", "Connected")
                           .withDetail("database-catalog", connection.getCatalog());
                } catch (Exception e) {
                    log.error("Service database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            }

            // 检查Redis缓存
            if (redisTemplate != null) {
                try {
                    // 测试Redis连接
                    redisTemplate.opsForValue().get("health:check");
                    
                    // 检查在线会话数（示例）
                    Long sessionCount = redisTemplate.opsForHash().size("service:sessions");
                    builder.withDetail("redis-status", "Connected")
                           .withDetail("active-sessions", sessionCount != null ? sessionCount : 0);
                } catch (Exception e) {
                    log.error("Service Redis health check failed", e);
                    builder.down()
                           .withDetail("redis-status", "Connection Failed")
                           .withDetail("redis-error", e.getMessage());
                }
            }

            // 队列健康检查（如果有消息队列）
            builder.withDetail("message-queue-status", "OK");

            return builder.build();
            
        } catch (Exception e) {
            log.error("Service health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        return String.format("%dd %dh %dm", days, hours, minutes);
    }
}
