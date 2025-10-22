/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-15 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-15 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.freeswitch;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSWITCH 数据源配置
 * 
 * 此配置类创建独立的数据源用于访问 FreeSWITCH 数据库
 * 不影响现有的主数据源配置
 * 
 * 配置说明：
 * 1. 通过 bytedesk.datasource.freeswitch.enabled=true 启用
 * 2. FreeSWITCH 实体类放在 com.bytedesk.call.freeswitch 包下
 * 3. 事务管理独立，使用 @Transactional("freeswitchTransactionManager")
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.bytedesk.call.freeswitch",  // FreeSWITCH 相关实体和 Repository 的包路径
    entityManagerFactoryRef = "freeswitchEntityManagerFactory",
    transactionManagerRef = "freeswitchTransactionManager"
)
@ConditionalOnProperty(
    prefix = "bytedesk.datasource.freeswitch",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false
)
@Description("FreeSWITCH DataSource Configuration - Separate datasource for FreeSWITCH database tables")
public class FreeSwitchDataSourceConfig {

    /**
     * FreeSWITCH 数据源属性配置
     */
    @Bean(name = "freeswitchDataSourceProperties")
    @ConfigurationProperties("bytedesk.datasource.freeswitch")
    public DataSourceProperties freeswitchDataSourceProperties() {
        log.info("Initializing FreeSWITCH DataSource Properties");
        return new DataSourceProperties();
    }

    /**
     * FreeSWITCH 数据源
     */
    @Bean(name = "freeswitchDataSource")
    @ConfigurationProperties("bytedesk.datasource.freeswitch.hikari")
    public DataSource freeswitchDataSource(
            @Qualifier("freeswitchDataSourceProperties") DataSourceProperties properties) {
        log.info("Creating FreeSWITCH DataSource: {}", properties.getUrl());
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("FreeSwitchHikariPool");
        return dataSource;
    }

    /**
     * FreeSWITCH EntityManagerFactory
     */
    @Bean(name = "freeswitchEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean freeswitchEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("freeswitchDataSource") DataSource dataSource,
            org.springframework.core.env.Environment env) {
        log.info("Creating FreeSWITCH EntityManagerFactory");

        // 允许通过属性覆盖，默认在启用时对 bytedesk_freeswitch 执行表结构创建/更新
        // 支持值：none|validate|update|create|create-drop
        String ddlAuto = env.getProperty("bytedesk.datasource.freeswitch.ddl-auto", "update");
        log.info("FreeSWITCH JPA ddl-auto={}", ddlAuto);

        java.util.Map<String, Object> jpaProps = new java.util.HashMap<>();
        jpaProps.put("hibernate.hbm2ddl.auto", ddlAuto);
        jpaProps.put("hibernate.show_sql", "false");
        jpaProps.put("hibernate.format_sql", "false");
        jpaProps.put("hibernate.jdbc.time_zone", "Asia/Shanghai");

        return builder
                .dataSource(dataSource)
                .packages("com.bytedesk.call.freeswitch")  // 仅扫描 freeswitch 实体
                .persistenceUnit("freeswitch")
                .properties(jpaProps)
                .build();
    }

    /**
     * FreeSWITCH 事务管理器
     */
    @Bean(name = "freeswitchTransactionManager")
    public PlatformTransactionManager freeswitchTransactionManager(
            @Qualifier("freeswitchEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        log.info("Creating FreeSWITCH Transaction Manager");
        return new JpaTransactionManager(entityManagerFactory);
    }
}
