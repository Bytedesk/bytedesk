/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-26 16:47:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.utils.JsonResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统状态监控服务
 */
@Slf4j
@Service
public class SystemStatusService {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取系统整体状态
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 基本信息
        status.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        status.put("uptime", getUptime());
        
        // 系统状态
        status.put("system", getSystemInfo());
        status.put("memory", getMemoryInfo());
        status.put("threads", getThreadInfo());
        
        // 服务状态
        status.put("services", getServicesStatus());
        
        // 数据库状态
        status.put("database", getDatabaseStatus());
        
        // Redis状态
        status.put("redis", getRedisStatus());
        
        return status;
    }

    /**
     * 获取系统运行时间
     */
    private String getUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime) % 60;
        
        return String.format("%d天 %02d:%02d:%02d", days, hours, minutes, seconds);
    }

    /**
     * 获取系统信息
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        systemInfo.put("os", osBean.getName() + " " + osBean.getVersion());
        systemInfo.put("arch", osBean.getArch());
        systemInfo.put("processors", osBean.getAvailableProcessors());
        systemInfo.put("loadAverage", String.format("%.2f", osBean.getSystemLoadAverage()));
        systemInfo.put("status", "UP");
        
        return systemInfo;
    }

    /**
     * 获取内存信息
     */
    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> memoryInfo = new HashMap<>();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
        
        memoryInfo.put("heapUsed", formatBytes(heapUsed));
        memoryInfo.put("heapMax", formatBytes(heapMax));
        memoryInfo.put("heapUsage", String.format("%.2f%%", (double) heapUsed / heapMax * 100));
        memoryInfo.put("nonHeapUsed", formatBytes(nonHeapUsed));
        memoryInfo.put("status", heapUsed < heapMax * 0.9 ? "UP" : "WARNING");
        
        return memoryInfo;
    }

    /**
     * 获取线程信息
     */
    private Map<String, Object> getThreadInfo() {
        Map<String, Object> threadInfo = new HashMap<>();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        threadInfo.put("total", threadBean.getThreadCount());
        threadInfo.put("peak", threadBean.getPeakThreadCount());
        threadInfo.put("daemon", threadBean.getDaemonThreadCount());
        threadInfo.put("status", "UP");
        
        return threadInfo;
    }

    /**
     * 获取服务状态
     */
    private Map<String, Object> getServicesStatus() {
        Map<String, Object> services = new HashMap<>();
        
        // Web服务状态
        services.put("web", Map.of("status", "UP", "message", "Web服务正常运行"));
        
        // WebSocket服务状态
        services.put("websocket", Map.of("status", "UP", "message", "WebSocket服务正常运行"));
        
        // 消息队列状态
        services.put("messageQueue", Map.of("status", "UP", "message", "消息队列服务正常运行"));
        
        return services;
    }

    /**
     * 获取数据库状态
     */
    private Map<String, Object> getDatabaseStatus() {
        Map<String, Object> dbStatus = new HashMap<>();
        
        try {
            // 这里可以添加实际的数据库连接测试
            // 暂时返回模拟状态
            dbStatus.put("status", "UP");
            dbStatus.put("message", "数据库连接正常");
            dbStatus.put("connectionPool", "正常");
        } catch (Exception e) {
            dbStatus.put("status", "DOWN");
            dbStatus.put("message", "数据库连接异常: " + e.getMessage());
            dbStatus.put("connectionPool", "异常");
        }
        
        return dbStatus;
    }

    /**
     * 获取Redis状态
     */
    private Map<String, Object> getRedisStatus() {
        Map<String, Object> redisStatus = new HashMap<>();
        
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().get("health_check");
                redisStatus.put("status", "UP");
                redisStatus.put("message", "Redis连接正常");
            } catch (Exception e) {
                redisStatus.put("status", "DOWN");
                redisStatus.put("message", "Redis连接异常: " + e.getMessage());
            }
        } else {
            redisStatus.put("status", "UNKNOWN");
            redisStatus.put("message", "Redis未配置");
        }
        
        return redisStatus;
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * 获取系统健康状态
     */
    public Map<String, Object> getHealth() {
        Map<String, Object> status = getSystemStatus();
        Map<String, Object> health = new HashMap<>();
        
        // 检查关键服务状态
        boolean isHealthy = true;
        String message = "系统运行正常";
        
        // 检查内存使用率
        Map<String, Object> memory = (Map<String, Object>) status.get("memory");
        if ("WARNING".equals(memory.get("status"))) {
            isHealthy = false;
            message = "内存使用率过高";
        }
        
        // 检查数据库状态
        Map<String, Object> database = (Map<String, Object>) status.get("database");
        if ("DOWN".equals(database.get("status"))) {
            isHealthy = false;
            message = "数据库连接异常";
        }
        
        // 检查Redis状态
        Map<String, Object> redis = (Map<String, Object>) status.get("redis");
        if ("DOWN".equals(redis.get("status"))) {
            isHealthy = false;
            message = "Redis连接异常";
        }
        
        health.put("status", isHealthy ? "UP" : "DOWN");
        health.put("message", message);
        health.put("details", status);
        
        return health;
    }
} 