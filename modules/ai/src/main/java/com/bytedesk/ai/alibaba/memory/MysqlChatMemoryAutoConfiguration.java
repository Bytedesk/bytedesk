/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bytedesk.ai.alibaba.memory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

import jakarta.activation.DataSource;

/**
 * Auto-configuration for MySQL chat memory repository.
 */
@Slf4j
@AutoConfiguration(after = JdbcTemplateAutoConfiguration.class)
@ConditionalOnClass({ MysqlChatMemoryRepository.class, DataSource.class, JdbcTemplate.class })
@ConditionalOnProperty(prefix = "spring.ai.memory.mysql", name = "enabled", havingValue = "true",
		matchIfMissing = false)
@EnableConfigurationProperties(MysqlChatMemoryProperties.class)
public class MysqlChatMemoryAutoConfiguration {

	@Bean
	@Qualifier("mysqlChatMemoryRepository")
	@ConditionalOnMissingBean(name = "mysqlChatMemoryRepository")
	MysqlChatMemoryRepository mysqlChatMemoryRepository(JdbcTemplate jdbcTemplate) {
		log.info("Configuring MySQL chat memory repository");
		return MysqlChatMemoryRepository.mysqlBuilder().jdbcTemplate(jdbcTemplate).build();
	}

}
