/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-06 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-06 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import lombok.extern.slf4j.Slf4j;

/**
 * Bytedesk插件抽象基类
 * 提供插件的通用实现
 */
@Slf4j
public abstract class AbstractBytedeskPlugin implements BytedeskPlugin {
    
    private final Instant registerTime = Instant.now();
    
    /**
     * 获取插件作者
     * 默认返回Bytedesk团队
     */
    @Override
    public String getAuthor() {
        return "Bytedesk Team";
    }
    
    /**
     * 获取健康指示器
     * 子类可以重写此方法提供自定义的HealthIndicator
     */
    protected HealthIndicator getHealthIndicator() {
        return null;
    }
    
    /**
     * 获取插件健康状态
     * 默认从对应的HealthIndicator获取
     */
    @Override
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            HealthIndicator healthIndicator = getHealthIndicator();
            if (healthIndicator != null) {
                Health health = healthIndicator.health();
                status.put("status", health.getStatus().getCode());
                status.put("details", health.getDetails());
            } else {
                status.put("status", "UP");
                status.put("message", "No health indicator configured");
            }
        } catch (Exception e) {
            log.error("Failed to get health status for plugin: {}", getPluginId(), e);
            status.put("status", "DOWN");
            status.put("error", e.getMessage());
        }
        
        return status;
    }
    
    /**
     * 获取插件统计信息
     * 包含基本信息和运行时长
     */
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pluginId", getPluginId());
        stats.put("pluginName", getPluginName());
        stats.put("version", getVersion());
        stats.put("enabled", isEnabled());
        stats.put("priority", getPriority());
        stats.put("dependencies", getDependencies());
        
        // 计算运行时长
        Duration uptime = Duration.between(registerTime, Instant.now());
        stats.put("uptime-seconds", uptime.getSeconds());
        stats.put("uptime-readable", formatDuration(uptime));
        stats.put("registerTime", registerTime.toString());
        
        return stats;
    }
    
    /**
     * 格式化时长
     */
    protected String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        return String.format("%dd %dh %dm", days, hours, minutes);
    }
    
    /**
     * 插件初始化
     */
    @Override
    public void initialize() {
        log.info("Initializing plugin: {} ({})", getPluginName(), getPluginId());
    }
    
    /**
     * 插件销毁
     */
    @Override
    public void destroy() {
        log.info("Destroying plugin: {} ({})", getPluginName(), getPluginId());
    }
}
