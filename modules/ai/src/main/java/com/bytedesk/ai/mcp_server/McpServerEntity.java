/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 12:23:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

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
     * Type of MCP server
     */
    @Builder.Default
    @Column(name = "server_type")
    private String type = McpServerTypeEnum.KNOWLEDGE.name();

    /**
     * Server status (ACTIVE, INACTIVE, ERROR, CONNECTING, etc.)
     */
    @Builder.Default
    private String status = McpServerStatusEnum.INACTIVE.name();

    /**
     * Whether the server is enabled
     */
    @Builder.Default
    private Boolean enabled = false;

    /**
     * Category UID for organizing servers (optional)
     */
    private String categoryUid;

    /**
     * Server configuration in JSON format
     * Contains all server-specific configuration including:
     * - serverVersion, serverUrl, host, port, protocol
     * - authToken, authType, authHeaders
     * - connectionTimeout, readTimeout, maxRetries
     * - capabilities, availableTools, availableResources, availablePrompts
     * - environmentVars, healthCheckUrl, healthCheckInterval
     * - priority, tags, metadata, usageStats
     * - autoStart and other settings
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String serverConfig;

    // /**
    //  * Last health check time
    //  */
    // private ZonedDateTime lastHealthCheck;

    // /**
    //  * Last connection time
    //  */
    // private ZonedDateTime lastConnected;

    // /**
    //  * Last error message
    //  */
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private String lastError;

}
