/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-05 10:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-05 10:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.mq.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.core.mq.jms.JmsArtemisConsts;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConditionalOnProperty(name = "bytedesk.mq.type", havingValue = "rabbitmq")
@Slf4j
public class RabbitMqConfig {

    @Bean
    public MessageConverter rabbitmqMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitmqMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitmqMessageConverter);
        rabbitTemplate.setReplyTimeout(30000L);
        log.info("Creating RabbitTemplate with Jackson message converter");
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitmqMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitmqMessageConverter);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    @Bean
    public Queue memberBatchImportQueue() {
        return new Queue(JmsArtemisConsts.QUEUE_MEMBER_BATCH_IMPORT, true);
    }

    @Bean
    public Queue messageTopicQueue() {
        return new Queue(JmsArtemisConsts.TOPIC_STRING_NAME, true);
    }
}