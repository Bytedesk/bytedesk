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

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch配置类
 * 用于配置Spring Batch所需的基本组件
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    // @Autowired
    // private DataSource dataSource;
    
    // @Autowired
    // private PlatformTransactionManager transactionManager;
    
    // @Autowired
    // private ResourceLoader resourceLoader;
    
    // @Value("${spring.batch.database-type:MYSQL}")
    // private String databaseType;
    
    // @Value("${spring.batch.jdbc.initialize-schema:always}")
    // private String initializeSchema;
    
    // /**
    //  * 初始化Spring Batch所需的数据库表
    //  */
    // public void initBatchSchema() {
    //     if (!"always".equals(initializeSchema)) {
    //         // 如果不是always，就不初始化表结构
    //         return;
    //     }
        
    //     String schemaLocation;
    //     try {
    //         // 根据数据库类型选择适当的schema脚本
    //         DatabaseType dbType = DatabaseType.valueOf(databaseType);
    //         String dbProductName = dbType.name().toLowerCase();
    //         schemaLocation = String.format("classpath:org/springframework/batch/core/schema-%s.sql", dbProductName);
    //     } catch (IllegalArgumentException e) {
    //         // 默认使用MySQL脚本
    //         schemaLocation = "classpath:org/springframework/batch/core/schema-mysql.sql";
    //         System.out.println("警告: 无法识别配置的数据库类型 '" + databaseType + "', 使用默认的MySQL schema");
    //     }
        
    //     try {
    //         Resource resource = resourceLoader.getResource(schemaLocation);
    //         if (resource.exists()) {
    //             ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
    //             populator.setContinueOnError(true); // 如果表已存在，继续执行不报错
    //             DatabasePopulatorUtils.execute(populator, dataSource);
    //             System.out.println("Spring Batch 表结构初始化成功");
    //         } else {
    //             System.err.println("无法找到Spring Batch schema脚本: " + schemaLocation);
    //         }
    //     } catch (Exception e) {
    //         System.err.println("初始化Spring Batch表结构时出错: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    // /**
    //  * 配置JobRepository
    //  * JobRepository用于存储和管理批处理元数据（如作业状态、步骤执行情况等）
    //  */
    // @Bean
    // @Primary
    // public JobRepository jobRepository() throws Exception {
    //     // 确保先初始化数据库表
    //     initBatchSchema();
        
    //     JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
    //     factoryBean.setDataSource(dataSource);
    //     factoryBean.setTransactionManager(transactionManager);
    //     factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
    //     factoryBean.setTablePrefix("BATCH_");
    //     factoryBean.setMaxVarCharLength(1000);
        
    //     // 添加incrementerFactory解决空指针异常
    //     DataFieldMaxValueIncrementerFactory incrementerFactory = new DefaultDataFieldMaxValueIncrementerFactory(dataSource);
    //     factoryBean.setIncrementerFactory(incrementerFactory);
        
    //     // 根据配置的数据库类型设置
    //     try {
    //         DatabaseType dbType = DatabaseType.valueOf(databaseType);
    //         factoryBean.setDatabaseType(dbType.getProductName());
    //     } catch (IllegalArgumentException e) {
    //         // 如果无法解析配置的数据库类型，使用默认的MYSQL类型并记录警告
    //         factoryBean.setDatabaseType(DatabaseType.MYSQL.getProductName());
    //         System.out.println("警告: 无法识别配置的数据库类型 '" + databaseType + "', 使用默认的MySQL类型");
    //     }
        
    //     // 确保所有属性设置完成后调用初始化方法
    //     factoryBean.afterPropertiesSet();
    //     return factoryBean.getObject();
    // }
    
    // /**
    //  * 配置JobLauncher
    //  * JobLauncher用于启动Job
    //  * Spring Boot 3.4.4 中使用TaskExecutorJobLauncher替代SimpleJobLauncher
    //  */
    // @Bean
    // public JobLauncher jobLauncher() throws Exception {
    //     TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
    //     jobLauncher.setJobRepository(jobRepository());
    //     jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
    //     jobLauncher.afterPropertiesSet();
    //     return jobLauncher;
    // }
}
