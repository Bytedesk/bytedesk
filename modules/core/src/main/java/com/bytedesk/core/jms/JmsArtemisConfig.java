/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 14:54:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 08:41:42
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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import lombok.extern.slf4j.Slf4j;

// http://localhost:8161/console/auth/login
// https://spring.io/guides/gs/messaging-jms
// https://docs.spring.io/spring-boot/reference/messaging/jms.html
// https://activemq.apache.org/components/artemis/documentation/latest/index.html
// 
// 配置说明：
// 1. embedded模式：Spring Boot自动启动内嵌Artemis broker，自动创建ConnectionFactory
// 2. native模式：连接外部Artemis broker，手动创建ConnectionFactory
// 3. 监听器工厂和JmsTemplate配置适用于两种模式
@EnableJms
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(ActiveMQConnectionFactory.class)
@Slf4j
public class JmsArtemisConfig {

	private final JmsErrorHandler jmsErrorHandler;

	@Bean
	public JmsListenerContainerFactory<?> jmsArtemisQueueFactory(ConnectionFactory connectionFactory,
													DefaultJmsListenerContainerFactoryConfigurer configurer) {
		log.info("Creating JMS Queue Listener Container Factory");
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all auto-configured defaults to this factory, including the message converter
		configurer.configure(factory, connectionFactory);
		// You could still override some settings if necessary.
		factory.setPubSubDomain(false);
		// 避免缓存 Session/Consumer：睡眠恢复后旧连接可能已关闭，缓存对象会导致反复 IllegalStateException
		factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_NONE);
		// 设置确认模式为自动确认，避免阻塞
		factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
		// 设置并发数
		factory.setConcurrency("5-20");
		// 设置错误处理器
        factory.setErrorHandler(jmsErrorHandler);
		// 禁用事务模式
		factory.setSessionTransacted(false);
		// 设置接收超时时间
		factory.setReceiveTimeout(30000L);
		// 设置恢复间隔
		factory.setRecoveryInterval(3000L);
		// 启用自动启动
		factory.setAutoStartup(true);
		// 设置每个任务处理的最大消息数
		factory.setMaxMessagesPerTask(10);
		return factory;
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsArtemisPubsubFactory(ConnectionFactory connectionFactory,
													DefaultJmsListenerContainerFactoryConfigurer configurer) {
		log.info("Creating JMS PubSub Listener Container Factory");
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all auto-configured defaults to this factory, including the message converter
		configurer.configure(factory, connectionFactory);
		// You could still override some settings if necessary.
		factory.setPubSubDomain(true);
		// 避免缓存 Session/Consumer：断链恢复时旧 Session 容易处于 closed 状态
		factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_NONE);
		// 设置并发数为1-3，允许适当的并发处理但避免过多实例
		factory.setConcurrency("1-3");
		// 设置确认模式为自动确认，避免阻塞
		factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
		// 设置错误处理器
        factory.setErrorHandler(jmsErrorHandler);
		// 禁用事务模式
		factory.setSessionTransacted(false);
		// 设置接收超时时间为30秒，增加超时时间避免过早断开
		factory.setReceiveTimeout(30000L);
		// 设置恢复间隔为3秒，减少恢复间隔
		factory.setRecoveryInterval(3000L);
		// 启用自动启动
		factory.setAutoStartup(true);
		// 设置每个任务处理的最大消息数，避免长时间占用
		factory.setMaxMessagesPerTask(10);
		return factory;
	}

	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		
		// NOTE: 我们不在这里定义类型映射，而是在消费者端使用自定义解析逻辑
		// 这样可以避免模块间的直接依赖
		
		// ObjectMapper objectMapper， for transforming ZonedDateTime to json
		// converter.setObjectMapper(objectMapper);
		return converter;
	}

	@Bean
	public DynamicDestinationResolver destinationResolver() {
		return new DynamicDestinationResolver() {
			@Override
			public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain)
					throws JMSException {
				boolean isTopicDestination = destinationName.startsWith(JmsArtemisConsts.TOPIC_PREFIX);
				return super.resolveDestinationName(session, destinationName, isTopicDestination);
			}
		};
	}

	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		log.info("Creating JmsTemplate with custom configuration");
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
		jmsTemplate.setDestinationResolver(destinationResolver());
		jmsTemplate.setReceiveTimeout(30000L);
		jmsTemplate.setTimeToLive(30000L);
		jmsTemplate.setSessionTransacted(false);
		jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
		jmsTemplate.setPriority(4);
		jmsTemplate.setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT);
		jmsTemplate.setExplicitQosEnabled(true);
		return jmsTemplate;
	}

}