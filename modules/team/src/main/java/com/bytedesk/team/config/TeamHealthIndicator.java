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
package com.bytedesk.team.config;

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
 * Team模块健康检查
 * 监控团队协作相关服务：数据库、Redis缓存、成员管理、权限系统等
 */
@Slf4j
@Component
public class TeamHealthIndicator implements HealthIndicator {

    @Value("${bytedesk.team.max-members-per-team:100}")
    private int maxMembersPerTeam;

    @Value("${bytedesk.team.invitation.expiry-hours:72}")
    private int invitationExpiryHours;

    @Value("${bytedesk.team.cache.enabled:true}")
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
                    log.error("Team database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            }

            // 检查Redis缓存（用于团队成员、权限缓存）
            if (cacheEnabled && redisTemplate != null) {
                try {
                    // 检查Redis连接
                    redisTemplate.opsForValue().get("health:check");
                    
                    // 获取团队相关统计
                    Long activeTeams = redisTemplate.opsForSet().size("team:active");
                    Long pendingInvitations = redisTemplate.opsForHash().size("team:invitations:pending");
                    
                    builder.withDetail("redis-status", "Connected")
                           .withDetail("cache-enabled", true)
                           .withDetail("active-teams-cached", activeTeams != null ? activeTeams : 0)
                           .withDetail("pending-invitations", pendingInvitations != null ? pendingInvitations : 0);
                } catch (Exception e) {
                    log.error("Team Redis health check failed", e);
                    builder.down()
                           .withDetail("redis-status", "Connection Failed")
                           .withDetail("redis-error", e.getMessage());
                }
            } else {
                builder.withDetail("cache-status", cacheEnabled ? "Enabled (Redis not available)" : "Disabled");
            }

            // 团队功能配置信息
            builder.withDetail("max-members-per-team", maxMembersPerTeam)
                   .withDetail("invitation-expiry-hours", invitationExpiryHours)
                   .withDetail("team-features", "Active")
                   .withDetail("supported-roles", "Owner, Admin, Member, Guest")
                   .withDetail("permission-system", "Enabled");

            return builder.build();
            
        } catch (Exception e) {
            log.error("Team health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
