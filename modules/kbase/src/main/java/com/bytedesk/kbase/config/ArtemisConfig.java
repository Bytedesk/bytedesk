/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import jakarta.jms.ConnectionFactory;

/**
 * Artemis配置类
 * 用于配置JMS相关的Bean
 */
@Configuration
@EnableJms
public class ArtemisConfig {

    /**
     * 配置消息转换器，使用Jackson将Java对象转换为JSON格式
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
    
    /**
     * 配置JMS监听器容器工厂
     */
    @Bean
    public JmsListenerContainerFactory<?> faqJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonJmsMessageConverter());
        // 配置并发消费者数量，动态调整
        factory.setConcurrency("3-10");
        // 使用事务管理
        factory.setSessionTransacted(true);
        // 设置消息确认模式
        factory.setSessionAcknowledgeMode(javax.jms.Session.CLIENT_ACKNOWLEDGE);
        // 设置恢复间隔，默认5000ms
        factory.setRecoveryInterval(5000L);
        return factory;
    }
    
    /**
     * 配置JMS模板
     */
    @Bean
    public JmsTemplate faqJmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(jacksonJmsMessageConverter());
        // 设置消息确认模式为客户端确认
        template.setSessionAcknowledgeMode(javax.jms.Session.CLIENT_ACKNOWLEDGE);
        // 设置默认目标名称
        template.setDefaultDestinationName("bytedesk.faq.index");
        return template;
    }
}
