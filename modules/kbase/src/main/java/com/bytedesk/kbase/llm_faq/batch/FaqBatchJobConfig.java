/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:48:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:48:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqExcel;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ批处理作业配置
 */
@Slf4j
@Configuration
public class FaqBatchJobConfig {

    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    @Autowired
    private FaqItemProcessor faqItemProcessor;
    
    @Autowired
    private FaqItemWriter faqItemWriter;
    
    /**
     * 配置FAQ导入作业
     */
    @Bean
    public Job importFaqJob() {
        return new JobBuilder("importFaqJob", jobRepository)
                .start(importFaqStep())
                .build();
    }
    
    /**
     * 配置FAQ导入步骤
     */
    @Bean
    public Step importFaqStep() {
        return new StepBuilder("importFaqStep", jobRepository)
                .<FaqExcel, FaqEntity>chunk(100, transactionManager)
                .reader(faqExcelReader(null))  // 读取器实例将在运行时设置
                .processor(faqItemProcessor)
                .writer(faqItemWriter)
                .build();
    }
    
    /**
     * 配置默认的Resource bean
     * 这个是一个占位符资源，实际使用时会被替换
     */
    @Bean
    public Resource defaultExcelResource() {
        // 提供一个默认的资源路径，可以根据实际情况调整
        // 这里假设在classpath下有一个templates目录，里面有一个空的模板文件
        // 如果没有这个文件，在运行时会尝试找到它并报错，但应用程序会正常启动
        return new ClassPathResource("templates/faq_template.xlsx");
    }
    
    /**
     * 配置FAQ Excel读取器
     * 
     * @param resource Excel文件资源，如果不手动指定，则使用defaultExcelResource
     * @return 配置好的Excel读取器
     */
    @Bean
    public FaqExcelReader faqExcelReader(Resource resource) {
        FaqExcelReader reader = new FaqExcelReader();
        if (resource != null) {
            reader.setResource(resource);
        }
        return reader;
    }
}
