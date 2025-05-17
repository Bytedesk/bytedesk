/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 16:15:57
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
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch配置类
 * 用于配置Spring Batch所需的基本组件
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    /**
     * 配置JobRepository
     * JobRepository用于存储和管理批处理元数据（如作业状态、步骤执行情况等）
     */
    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factoryBean.setTablePrefix("BATCH_");
        factoryBean.setMaxVarCharLength(1000);
        
        // 添加incrementerFactory解决空指针异常
        DataFieldMaxValueIncrementerFactory incrementerFactory = new DefaultDataFieldMaxValueIncrementerFactory(dataSource);
        factoryBean.setIncrementerFactory(incrementerFactory);
        
        // 根据数据库类型设置
        factoryBean.setDatabaseType(DatabaseType.MYSQL.getProductName());
        
        // 确保所有属性设置完成后调用初始化方法
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
    
    /**
     * 配置JobLauncher
     * JobLauncher用于启动Job
     * Spring Boot 3.4.4 中使用TaskExecutorJobLauncher替代SimpleJobLauncher
     */
    @Bean
    public JobLauncher jobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
