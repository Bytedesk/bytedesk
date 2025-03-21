/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-22 19:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 16:32:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyReportJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dailyReportJob() {
        return new JobBuilder("dailyReportJob", jobRepository)
                .start(collectDataStep())
                .next(generateReportStep())
                .next(distributeReportStep())
                .build();
    }

    @Bean
    public Step collectDataStep() {
        return new StepBuilder("collectDataStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("执行数据收集步骤");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step generateReportStep() {
        return new StepBuilder("generateReportStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("执行报表生成步骤");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step distributeReportStep() {
        return new StepBuilder("distributeReportStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("执行报表分发步骤");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
} 
