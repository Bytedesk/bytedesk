/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 */
package com.bytedesk.core.mq.rabbitmq;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = RabbitMqClusterProperties.CONFIG_PREFIX)
public class RabbitMqClusterProperties {

    public static final String CONFIG_PREFIX = "bytedesk.mq.rabbitmq.cluster";

    private boolean enabled = false;

    private List<String> nodes = new ArrayList<>();
}
