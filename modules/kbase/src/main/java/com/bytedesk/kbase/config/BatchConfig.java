/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-19 18:20:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

/**
 * Spring Batch配置类
 * 用于配置Spring Batch所需的基本组件
 */
@Configuration
@EnableBatchProcessing
@Description("Batch Processing Configuration - Spring Batch configuration for knowledge base data processing")
public class BatchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private Environment environment;

    @Value("${spring.batch.database-type:}")
    private String configuredDatabaseType;

    /**
     * 自定义 JobRepository，解决 KingbaseES 无法被 Spring Batch 自动识别的问题。
     * Kingbase 与 PostgreSQL 兼容，这里显式将 databaseType 指定为 POSTGRES。
     */
    @Bean
    @Primary
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factoryBean.setTablePrefix("BATCH_");
        factoryBean.setMaxVarCharLength(1000);

        // 1) 若通过配置显式指定了类型，则优先使用
        if (StringUtils.hasText(configuredDatabaseType)) {
            factoryBean.setDatabaseType(configuredDatabaseType.trim().toUpperCase());
        } else {
            // 2) 未指定时，针对 Kingbase 显式映射为 POSTGRES，其它交给默认自动检测
            String url = environment.getProperty("spring.datasource.url", "").toLowerCase();
            String driver = environment.getProperty("spring.datasource.driver-class-name", "").toLowerCase();
            if (url.contains("kingbase") || driver.contains("kingbase")) {
                factoryBean.setDatabaseType(DatabaseType.POSTGRES.name());
            }
        }

        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
