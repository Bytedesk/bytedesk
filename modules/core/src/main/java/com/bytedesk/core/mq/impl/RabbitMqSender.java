/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-05 10:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-05 10:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.mq.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.bytedesk.core.mq.MqSendOptions;
import com.bytedesk.core.mq.MqSender;

@Component
@ConditionalOnProperty(name = "bytedesk.mq.type", havingValue = "rabbitmq")
public class RabbitMqSender implements MqSender {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(String destination, Object payload, MqSendOptions options) {
        rabbitTemplate.convertAndSend(destination, payload, amqpMessage -> {
            MessageProperties properties = amqpMessage.getMessageProperties();
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            properties.setContentEncoding(StandardCharsets.UTF_8.name());

            if (options != null) {
                if (options.getDelayMs() != null && options.getDelayMs() > 0) {
                    int delay = (int) Math.min(Integer.MAX_VALUE, options.getDelayMs());
                    properties.setHeader("x-delay", delay);
                }
                if (options.getPriority() != null) {
                    properties.setPriority(options.getPriority());
                }
                options.getHeadersOrEmpty().forEach(properties::setHeader);
            }

            return amqpMessage;
        });
    }
}