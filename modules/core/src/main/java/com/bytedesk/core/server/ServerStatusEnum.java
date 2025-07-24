/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 19:56:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

/**
 * Server status enumeration
 * Defines different statuses of servers
 */
public enum ServerStatusEnum {
    ONLINE,      // 在线运行
    OFFLINE,     // 离线
    MAINTENANCE, // 维护中
    STARTING,    // 启动中
    STOPPING,    // 停止中
    RESTARTING,  // 重启中
    ERROR,       // 错误状态
    WARNING,     // 警告状态
    DEGRADED,    // 降级运行
    OVERLOADED,  // 过载
    UNKNOWN;     // 未知状态

    /**
     * Get Chinese name for the server status
     * @return Chinese name
     */
    public String getChineseName() {
        switch (this) {
            case ONLINE:
                return "在线运行";
            case OFFLINE:
                return "离线";
            case MAINTENANCE:
                return "维护中";
            case STARTING:
                return "启动中";
            case STOPPING:
                return "停止中";
            case RESTARTING:
                return "重启中";
            case ERROR:
                return "错误状态";
            case WARNING:
                return "警告状态";
            case DEGRADED:
                return "降级运行";
            case OVERLOADED:
                return "过载";
            case UNKNOWN:
                return "未知状态";
            default:
                return this.name();
        }
    }

    /**
     * Check if server is healthy (online and not in error/warning states)
     * @return true if healthy
     */
    public boolean isHealthy() {
        return this == ONLINE || this == DEGRADED;
    }

    /**
     * Check if server is operational (can serve requests)
     * @return true if operational
     */
    public boolean isOperational() {
        return this == ONLINE || this == DEGRADED || this == OVERLOADED;
    }

    /**
     * Convert from string value to enum
     * @param value string value
     * @return ServerStatusEnum
     */
    public static ServerStatusEnum fromValue(String value) {
        for (ServerStatusEnum status : ServerStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No ServerStatusEnum constant with value: " + value);
    }
} 