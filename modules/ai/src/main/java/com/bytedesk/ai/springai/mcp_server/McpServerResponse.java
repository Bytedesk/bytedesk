/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 11:01:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.mcp_server;


import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class McpServerResponse extends BaseResponse {

    private String name;

    private String description;

    private String serverType;

    private String serverVersion;

    private String serverUrl;

    private String host;

    private Integer port;

    private String protocol;

    private String authType;

    private Integer connectionTimeout;

    private Integer readTimeout;

    private Integer maxRetries;

    private String capabilities;

    private String availableTools;

    private String availableResources;

    private String availablePrompts;

    private String configJson;

    private String environmentVars;

    private McpServerStatusEnum status;

    private Boolean enabled;

    private Boolean autoStart;

    private String healthCheckUrl;

    private Integer healthCheckInterval;

    private java.time.ZonedDateTime lastHealthCheck;

    private java.time.ZonedDateTime lastConnected;

    private String lastError;

    private Integer priority;

    private String tags;

    private String metadata;

    private String usageStats;

}
