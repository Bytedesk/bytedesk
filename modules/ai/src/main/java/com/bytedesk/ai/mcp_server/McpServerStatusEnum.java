/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-26 11:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 11:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

/**
 * MCP Server status enumeration
 * Represents the current operational status of MCP servers
 */
public enum McpServerStatusEnum {
    
    /**
     * Server is currently active and operational
     */
    ACTIVE("Server is active and responding"),
    
    /**
     * Server is inactive or not running
     */
    INACTIVE("Server is inactive"),
    
    /**
     * Server is in error state
     */
    ERROR("Server encountered an error"),
    
    /**
     * Server is currently connecting/initializing
     */
    CONNECTING("Server is establishing connection"),
    
    /**
     * Server is disconnecting
     */
    DISCONNECTING("Server is disconnecting"),
    
    /**
     * Server connection is in timeout state
     */
    TIMEOUT("Server connection timed out"),
    
    /**
     * Server is in maintenance mode
     */
    MAINTENANCE("Server is under maintenance"),
    
    /**
     * Server is disabled by configuration
     */
    DISABLED("Server is disabled"),
    
    /**
     * Server status is unknown
     */
    UNKNOWN("Server status is unknown");

    private final String description;

    McpServerStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get enum from string value (case insensitive)
     */
    public static McpServerStatusEnum fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return INACTIVE;
        }
        
        try {
            return McpServerStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    /**
     * Check if status indicates server is operational
     */
    public boolean isOperational() {
        return this == ACTIVE || this == CONNECTING;
    }

    /**
     * Check if status indicates server has issues
     */
    public boolean hasIssues() {
        return this == ERROR || this == TIMEOUT || this == UNKNOWN;
    }

    /**
     * Check if server can be started
     */
    public boolean canStart() {
        return this == INACTIVE || this == DISABLED || this == ERROR;
    }

    /**
     * Check if server can be stopped
     */
    public boolean canStop() {
        return this == ACTIVE || this == CONNECTING || this == ERROR;
    }
}
