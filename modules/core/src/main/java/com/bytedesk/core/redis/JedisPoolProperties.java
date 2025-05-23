/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 11:25:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:55:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(JedisProperties.CONFIG_PREFIX)
public class JedisPoolProperties {
    
    public static final String CONFIG_PREFIX = "bytedesk.redis.pool-config";

    private int maxIdle;

    private int maxTotal;

    private int minIdle;
    // private long maxWaitMillis;
}
