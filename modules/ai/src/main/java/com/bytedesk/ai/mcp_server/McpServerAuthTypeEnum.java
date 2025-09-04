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
package com.bytedesk.ai.mcp_server;

/**
 * MCP Server authentication type enumeration
 * Represents different authentication methods for MCP servers
 */
public enum McpServerAuthTypeEnum {
    
    /**
     * No authentication required
     */
    NONE("None", "No authentication required"),
    
    /**
     * Bearer token authentication
     */
    BEARER("Bearer", "Bearer token authentication (Authorization: Bearer <token>)"),
    
    /**
     * Basic authentication
     */
    BASIC("Basic", "Basic authentication (Authorization: Basic <base64(username:password)>)"),
    
    /**
     * API Key authentication
     */
    API_KEY("API Key", "API Key authentication (custom header or query parameter)"),
    
    /**
     * OAuth 2.0 authentication
     */
    OAUTH2("OAuth 2.0", "OAuth 2.0 authentication"),
    
    /**
     * JWT (JSON Web Token) authentication
     */
    JWT("JWT", "JSON Web Token authentication"),
    
    /**
     * Custom authentication
     */
    CUSTOM("Custom", "Custom authentication method"),
    
    /**
     * Mutual TLS authentication
     */
    MTLS("mTLS", "Mutual TLS certificate authentication"),
    
    /**
     * HMAC signature authentication
     */
    HMAC("HMAC", "HMAC signature authentication"),
    
    /**
     * Session-based authentication
     */
    SESSION("Session", "Session-based authentication with cookies");

    private final String displayName;
    private final String description;

    McpServerAuthTypeEnum(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get enum from string value (case insensitive)
     */
    public static McpServerAuthTypeEnum fromString(String authType) {
        if (authType == null || authType.trim().isEmpty()) {
            return BEARER;
        }
        
        try {
            return McpServerAuthTypeEnum.valueOf(authType.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try to match by display name
            for (McpServerAuthTypeEnum auth : values()) {
                if (auth.displayName.equalsIgnoreCase(authType)) {
                    return auth;
                }
            }
            // Try common variations
            String normalized = authType.toLowerCase().replace(" ", "_").replace("-", "_");
            switch (normalized) {
                case "bearer_token":
                case "bearer_auth":
                    return BEARER;
                case "basic_auth":
                case "basic_authentication":
                    return BASIC;
                case "api_key":
                case "apikey":
                    return API_KEY;
                case "oauth":
                case "oauth2":
                    return OAUTH2;
                case "json_web_token":
                    return JWT;
                case "mutual_tls":
                case "client_cert":
                    return MTLS;
                default:
                    return BEARER; // default fallback
            }
        }
    }

    /**
     * Check if authentication requires a token
     */
    public boolean requiresToken() {
        return this == BEARER || this == API_KEY || this == JWT || this == OAUTH2;
    }

    /**
     * Check if authentication requires username/password
     */
    public boolean requiresCredentials() {
        return this == BASIC || this == SESSION;
    }

    /**
     * Check if authentication requires certificates
     */
    public boolean requiresCertificate() {
        return this == MTLS;
    }

    /**
     * Check if authentication requires custom headers
     */
    public boolean requiresCustomHeaders() {
        return this == CUSTOM || this == API_KEY || this == HMAC;
    }

    /**
     * Check if authentication is secure
     */
    public boolean isSecure() {
        return this != NONE;
    }

    /**
     * Get the typical header name for this authentication type
     */
    public String getTypicalHeaderName() {
        switch (this) {
            case BEARER:
            case BASIC:
            case JWT:
                return "Authorization";
            case API_KEY:
                return "X-API-Key";
            case OAUTH2:
                return "Authorization";
            case HMAC:
                return "X-Signature";
            case CUSTOM:
                return "X-Auth-Token";
            case SESSION:
                return "Cookie";
            case MTLS:
                return null; // Certificate-based, no header
            case NONE:
            default:
                return null;
        }
    }

    /**
     * Get the authorization scheme prefix
     */
    public String getSchemePrefix() {
        switch (this) {
            case BEARER:
                return "Bearer ";
            case BASIC:
                return "Basic ";
            case JWT:
                return "Bearer ";
            case OAUTH2:
                return "Bearer ";
            default:
                return "";
        }
    }
}
