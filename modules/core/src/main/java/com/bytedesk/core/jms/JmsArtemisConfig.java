/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 14:54:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 15:44:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.jms;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;

// http://localhost:8161/console/auth/login
// https://spring.io/guides/gs/messaging-jms
// https://docs.spring.io/spring-boot/reference/messaging/jms.html
// https://activemq.apache.org/components/artemis/documentation/latest/index.html
@EnableJms
@Configuration
@RequiredArgsConstructor
public class JmsArtemisConfig {

	private final JmsErrorHandler jmsErrorHandler;
    
  	@Bean
	public JmsListenerContainerFactory<?> jmsArtemisQueueFactory(ConnectionFactory connectionFactory,
													DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all auto-configured defaults to this factory, including the message converter
		configurer.configure(factory, connectionFactory);
		// You could still override some settings if necessary.
		factory.setPubSubDomain(false);
		// 设置确认模式为客户端确认，确保消息处理成功后才被确认
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		// 设置错误处理器
        factory.setErrorHandler(jmsErrorHandler);
		return factory;
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsArtemisPubsubFactory(ConnectionFactory connectionFactory,
													DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all auto-configured defaults to this factory, including the message converter
		configurer.configure(factory, connectionFactory);
		// You could still override some settings if necessary.
		factory.setPubSubDomain(true);
		// factory.setConcurrency("3-10");
		// 设置错误处理器
        factory.setErrorHandler(jmsErrorHandler);
		return factory;
	}

	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		// ObjectMapper objectMapper， for transforming localDateTime to json
		// converter.setObjectMapper(objectMapper);
		return converter;
	}

	@Bean
    public DynamicDestinationResolver destinationResolver() {
        return new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException {
                pubSubDomain = destinationName.startsWith(JmsArtemisConstants.TOPIC_PREFIX);
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
            }
        };
    }
}
