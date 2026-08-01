/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 */
package com.bytedesk.core.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(RedisClusterSwitchProperties.CONFIG_PREFIX)
public class RedisClusterSwitchProperties {

    public static final String CONFIG_PREFIX = "bytedesk.redis.cluster";

    private boolean enabled = false;
}
