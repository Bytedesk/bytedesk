/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 14:02:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class McpServerRequest extends BaseRequest {

    private String name;

    private String description;

    private String serverType;

    private McpServerStatusEnum status;

    private Boolean enabled;

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
    private String serverConfig;

}
