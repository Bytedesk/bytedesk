/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 11:05:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.mcp_server;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * McpServer entity for Model Context Protocol server configuration and management
 * Stores MCP server connection details, capabilities, and runtime information
 * 
 * Database Table: bytedesk_ai_mcp_server
 * Purpose: Stores MCP server definitions, connection settings, and capability information
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({McpServerEntityListener.class})
@Table(name = "bytedesk_ai_mcp_server")
public class McpServerEntity extends BaseEntity {

    /**
     * MCP Server name
     */
    private String name;

    /**
     * MCP Server description
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of MCP server (THREAD, CUSTOMER, TICKET, etc.)
     */
    @Builder.Default
    private String serverType = McpServerTypeEnum.THREAD.name();

    /**
     * MCP Server version
     */
    @Builder.Default
    private String serverVersion = "1.0.0";

    /**
     * Connection URI/URL for the MCP server
     */
    private String serverUrl;

    /**
     * Server host/hostname
     */
    private String host;

    /**
     * Server port
     */
    private Integer port;

    /**
     * Connection protocol (http, https, websocket, etc.)
     */
    @Builder.Default
    private String protocol = "http";

    /**
     * Authentication token for server access
     */
    private String authToken;

    /**
     * Authentication type (bearer, basic, api_key, etc.)
     */
    @Builder.Default
    private String authType = "bearer";

    /**
     * Additional authentication headers in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String authHeaders;

    /**
     * Connection timeout in milliseconds
     */
    @Builder.Default
    private Integer connectionTimeout = 30000;

    /**
     * Read timeout in milliseconds
     */
    @Builder.Default
    private Integer readTimeout = 60000;

    /**
     * Maximum number of retry attempts
     */
    @Builder.Default
    private Integer maxRetries = 3;

    /**
     * Server capabilities in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String capabilities;

    /**
     * Available tools/functions in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String availableTools;

    /**
     * Available resources in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String availableResources;

    /**
     * Available prompts in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String availablePrompts;

    /**
     * Server configuration in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String configJson;

    /**
     * Environment variables for the server in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String environmentVars;

    /**
     * Server status (ACTIVE, INACTIVE, ERROR, CONNECTING, etc.)
     */
    @Builder.Default
    private String status = "INACTIVE";

    /**
     * Whether the server is enabled
     */
    @Builder.Default
    private Boolean enabled = false;

    /**
     * Whether to auto-start the server
     */
    @Builder.Default
    private Boolean autoStart = false;

    /**
     * Health check URL
     */
    private String healthCheckUrl;

    /**
     * Health check interval in seconds
     */
    @Builder.Default
    private Integer healthCheckInterval = 60;

    /**
     * Last health check time
     */
    private java.time.ZonedDateTime lastHealthCheck;

    /**
     * Last connection time
     */
    private java.time.ZonedDateTime lastConnected;

    /**
     * Last error message
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String lastError;

    /**
     * Priority for server selection (higher number = higher priority)
     */
    @Builder.Default
    private Integer priority = 0;

    /**
     * Tags for categorization and filtering
     */
    private String tags;

    /**
     * Additional metadata in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String metadata;

    /**
     * Usage statistics in JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String usageStats;

}
