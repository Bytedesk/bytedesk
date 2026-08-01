/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 15:51:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-25 11:28:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootTest(
	classes = AgentApplicationTests.TestApplication.class,
	webEnvironment = WebEnvironment.NONE,
	properties = {
		"spring.main.lazy-initialization=true",
		"spring.autoconfigure.exclude="
			+ "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,"
			+ "org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration",
		"application.version=test"
	})
class AgentApplicationTests {

	@SpringBootApplication(
		scanBasePackageClasses = AgentApplication.class,
		exclude = {
			DataSourceAutoConfiguration.class,
			HibernateJpaAutoConfiguration.class,
			RedisAutoConfiguration.class,
			RedisRepositoriesAutoConfiguration.class,
			ElasticsearchDataAutoConfiguration.class,
			ElasticsearchRepositoriesAutoConfiguration.class,
			ElasticsearchClientAutoConfiguration.class,
			JmsAutoConfiguration.class,
			RabbitAutoConfiguration.class,
			QuartzAutoConfiguration.class
		})
	@EntityScan(basePackages = "com.bytedesk")
	static class TestApplication {

		@Bean
		static BeanFactoryPostProcessor removeInitializerBeans() {
			return beanFactory -> {
				if (!(beanFactory instanceof org.springframework.beans.factory.support.BeanDefinitionRegistry registry)) {
					throw new BeansException("BeanFactory does not support bean definition removal") {
						private static final long serialVersionUID = 1L;
					};
				}
				Arrays.stream(beanFactory.getBeanDefinitionNames())
					.filter(name -> name.endsWith("Initializer"))
					.filter(registry::containsBeanDefinition)
					.forEach(registry::removeBeanDefinition);
			};
		}
	}

	@Test
	void contextLoads() {
	}

}
