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
package com.bytedesk.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Core模块健康检查
 * 监控核心服务依赖：数据库、Redis、应用版本等
 */
@Slf4j
@Component
public class CoreHealthIndicator implements HealthIndicator {

    @Value("${application.version:unknown}")
    private String appVersion;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired(required = false)
    private DatabaseTypeChecker databaseTypeChecker;

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();
            
            // 应用基本信息
            builder.withDetail("app-version", appVersion)
                   .withDetail("active-profile", activeProfile);

            // 检查数据库连接
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    String dbType = databaseTypeChecker != null ? 
                            databaseTypeChecker.getDatabaseType() : "unknown";
                    builder.withDetail("database-status", "Connected")
                           .withDetail("database-type", dbType)
                           .withDetail("database-url", connection.getMetaData().getURL());
                } catch (Exception e) {
                    log.error("Database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            } else {
                builder.withDetail("database-status", "Not Configured");
            }

            // 检查Redis连接
            if (redisConnectionFactory != null) {
                try {
                    RedisConnection connection = redisConnectionFactory.getConnection();
                    String pong = connection.ping();
                    connection.close();
                    builder.withDetail("redis-status", "Connected")
                           .withDetail("redis-ping", pong);
                } catch (Exception e) {
                    log.error("Redis health check failed", e);
                    builder.down()
                           .withDetail("redis-status", "Connection Failed")
                           .withDetail("redis-error", e.getMessage());
                }
            } else {
                builder.withDetail("redis-status", "Not Configured");
            }

            return builder.build();
            
        } catch (Exception e) {
            log.error("Core health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("app-version", appVersion)
                    .build();
        }
    }
}
