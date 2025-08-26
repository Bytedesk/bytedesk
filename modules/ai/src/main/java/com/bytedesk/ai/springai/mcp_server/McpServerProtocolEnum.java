/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-26 11:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 11:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.mcp_server;

/**
 * MCP Server protocol enumeration
 * Represents the communication protocol used by MCP servers
 */
public enum McpServerProtocolEnum {
    
    /**
     * HTTP protocol
     */
    HTTP("HTTP", "Standard HTTP protocol", 80),
    
    /**
     * HTTPS protocol (secure HTTP)
     */
    HTTPS("HTTPS", "Secure HTTP protocol", 443),
    
    /**
     * WebSocket protocol
     */
    WEBSOCKET("WebSocket", "WebSocket protocol for real-time communication", 80),
    
    /**
     * Secure WebSocket protocol
     */
    WSS("WSS", "Secure WebSocket protocol", 443),
    
    /**
     * Server-Sent Events (SSE)
     */
    SSE("SSE", "Server-Sent Events for streaming", 80),
    
    /**
     * gRPC protocol
     */
    GRPC("gRPC", "gRPC protocol for high-performance communication", 80),
    
    /**
     * TCP protocol
     */
    TCP("TCP", "Raw TCP socket connection", 8080),
    
    /**
     * STDIO protocol (for local processes)
     */
    STDIO("STDIO", "Standard input/output for local processes", 0);

    private final String displayName;
    private final String description;
    private final int defaultPort;

    McpServerProtocolEnum(String displayName, String description, int defaultPort) {
        this.displayName = displayName;
        this.description = description;
        this.defaultPort = defaultPort;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    /**
     * Get enum from string value (case insensitive)
     */
    public static McpServerProtocolEnum fromString(String protocol) {
        if (protocol == null || protocol.trim().isEmpty()) {
            return HTTP;
        }
        
        try {
            return McpServerProtocolEnum.valueOf(protocol.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try to match by display name
            for (McpServerProtocolEnum p : values()) {
                if (p.displayName.equalsIgnoreCase(protocol)) {
                    return p;
                }
            }
            return HTTP; // default fallback
        }
    }

    /**
     * Check if protocol is secure
     */
    public boolean isSecure() {
        return this == HTTPS || this == WSS;
    }

    /**
     * Check if protocol supports real-time communication
     */
    public boolean isRealTime() {
        return this == WEBSOCKET || this == WSS || this == SSE;
    }

    /**
     * Check if protocol is web-based
     */
    public boolean isWebBased() {
        return this == HTTP || this == HTTPS || this == WEBSOCKET || this == WSS || this == SSE;
    }

    /**
     * Check if protocol requires network connection
     */
    public boolean requiresNetwork() {
        return this != STDIO;
    }

    /**
     * Get URL scheme for the protocol
     */
    public String getScheme() {
        switch (this) {
            case HTTP:
                return "http";
            case HTTPS:
                return "https";
            case WEBSOCKET:
                return "ws";
            case WSS:
                return "wss";
            case GRPC:
                return "grpc";
            case TCP:
                return "tcp";
            case SSE:
                return "http"; // SSE uses HTTP
            case STDIO:
                return "stdio";
            default:
                return "http";
        }
    }
}
