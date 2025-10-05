/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-05 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-05 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.social.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Social模块健康检查
 * 监控社交功能相关服务：数据库、Redis缓存、用户关系图谱等
 */
@Slf4j
@Component
public class SocialHealthIndicator implements HealthIndicator {

    @Value("${bytedesk.social.max-connections:5000}")
    private int maxConnections;

    @Value("${bytedesk.social.cache.enabled:true}")
    private boolean cacheEnabled;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();

            // 检查数据库连接
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    builder.withDetail("database-status", "Connected")
                           .withDetail("database-catalog", connection.getCatalog());
                } catch (Exception e) {
                    log.error("Social database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            }

            // 检查Redis缓存（用于社交关系缓存）
            if (cacheEnabled && redisTemplate != null) {
                try {
                    // 检查Redis连接
                    redisTemplate.opsForValue().get("health:check");
                    
                    // 获取社交关系缓存统计（示例）
                    Long followCount = redisTemplate.opsForHash().size("social:follows");
                    Long friendCount = redisTemplate.opsForHash().size("social:friends");
                    
                    builder.withDetail("redis-status", "Connected")
                           .withDetail("cache-enabled", true)
                           .withDetail("cached-follows", followCount != null ? followCount : 0)
                           .withDetail("cached-friends", friendCount != null ? friendCount : 0);
                } catch (Exception e) {
                    log.error("Social Redis health check failed", e);
                    builder.down()
                           .withDetail("redis-status", "Connection Failed")
                           .withDetail("redis-error", e.getMessage());
                }
            } else {
                builder.withDetail("cache-status", cacheEnabled ? "Enabled (Redis not available)" : "Disabled");
            }

            // 社交功能配置信息
            builder.withDetail("max-connections", maxConnections)
                   .withDetail("social-features", "Active")
                   .withDetail("supported-relations", "Follow, Friend, Block");

            return builder.build();
            
        } catch (Exception e) {
            log.error("Social health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
