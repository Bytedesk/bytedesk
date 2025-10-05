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
package com.bytedesk.forum.config;

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
 * Forum模块健康检查
 * 监控论坛系统相关服务：数据库、Redis缓存、搜索索引、审核队列等
 */
@Slf4j
@Component
public class ForumHealthIndicator implements HealthIndicator {

    @Value("${bytedesk.forum.post-moderation.enabled:false}")
    private boolean moderationEnabled;

    @Value("${bytedesk.forum.hot-topics.cache-ttl:3600}")
    private int hotTopicsCacheTtl;

    @Value("${bytedesk.forum.search.enabled:true}")
    private boolean searchEnabled;

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
                    log.error("Forum database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            }

            // 检查Redis缓存（用于热帖、统计等）
            if (redisTemplate != null) {
                try {
                    // 检查Redis连接
                    redisTemplate.opsForValue().get("health:check");
                    
                    // 获取论坛相关统计
                    Long hotTopicsCount = redisTemplate.opsForZSet().size("forum:hot:topics");
                    Long pendingModeration = redisTemplate.opsForList().size("forum:moderation:queue");
                    
                    builder.withDetail("redis-status", "Connected")
                           .withDetail("hot-topics-cached", hotTopicsCount != null ? hotTopicsCount : 0)
                           .withDetail("pending-moderation", moderationEnabled ? (pendingModeration != null ? pendingModeration : 0) : "N/A");
                } catch (Exception e) {
                    log.error("Forum Redis health check failed", e);
                    builder.down()
                           .withDetail("redis-status", "Connection Failed")
                           .withDetail("redis-error", e.getMessage());
                }
            } else {
                builder.withDetail("redis-status", "Not Configured");
            }

            // 论坛功能配置信息
            builder.withDetail("post-moderation", moderationEnabled ? "Enabled" : "Disabled")
                   .withDetail("search-enabled", searchEnabled)
                   .withDetail("hot-topics-cache-ttl", hotTopicsCacheTtl + "s")
                   .withDetail("forum-features", "Active")
                   .withDetail("supported-content", "Post, Thread, Comment, Tag");

            return builder.build();
            
        } catch (Exception e) {
            log.error("Forum health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
